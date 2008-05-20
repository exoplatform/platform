/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.gadgets;

import org.apache.shindig.gadgets.spec.Auth;
import org.apache.shindig.gadgets.spec.Feature;
import org.apache.shindig.gadgets.spec.GadgetSpec;
import org.apache.shindig.gadgets.spec.LocaleSpec;
import org.apache.shindig.gadgets.spec.MessageBundle;
import org.apache.shindig.gadgets.spec.Preload;

import com.google.inject.Inject;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

/**
 * Primary gadget processing facility. Converts an input Context into an output
 * Gadget
 */
public class GadgetServer {
  private final Executor executor;
  private final GadgetFeatureRegistry registry;
  private final GadgetBlacklist blacklist;

  private ContentFetcherFactory preloadFetcherFactory;
  private ContentFetcher gadgetSpecFetcher;
  private ContentFetcher messageBundleFetcher;

  @Inject
  public GadgetServer(Executor executor,
      GadgetFeatureRegistry registry,
      GadgetBlacklist blacklist,
      ContentFetcherFactory preloadFetcherFactory,
      @GadgetSpecFetcher ContentFetcher gadgetSpecFetcher,
      @MessageBundleFetcher ContentFetcher messageBundleFetcher) {
    this.executor = executor;
    this.registry = registry;
    this.blacklist = blacklist;
    this.preloadFetcherFactory = preloadFetcherFactory;
    this.gadgetSpecFetcher = gadgetSpecFetcher;
    this.messageBundleFetcher = messageBundleFetcher;
  }

  /**
   * Process a single gadget.
   *
   * @param context
   * @return The processed gadget.
   * @throws GadgetException
   */
  public Gadget processGadget(GadgetContext context) throws GadgetException {
    if (blacklist.isBlacklisted(context.getUrl())) {
      throw new GadgetException(GadgetException.Code.BLACKLISTED_GADGET);
    }

    RemoteContentRequest request = RemoteContentRequest.getRequest(
        context.getUrl(), context.getIgnoreCache());
    RemoteContent response = gadgetSpecFetcher.fetch(request);
    if (response.getHttpStatusCode() != RemoteContent.SC_OK) {
      throw new GadgetException(
          GadgetException.Code.FAILED_TO_RETRIEVE_CONTENT,
          "Unable to retrieve gadget xml. HTTP error " +
          response.getHttpStatusCode());
    }
    GadgetSpec spec
        = new GadgetSpec(context.getUrl(), response.getResponseAsString());
    return createGadgetFromSpec(spec, context);
  }

  /**
   *
   * @param localeSpec
   * @param context
   * @return A new message bundle
   * @throws GadgetException
   */
  private MessageBundle getBundle(LocaleSpec localeSpec, GadgetContext context)
      throws GadgetException {
    URI bundleUrl = localeSpec.getMessages();
    RemoteContentRequest request
        = RemoteContentRequest.getRequest(bundleUrl, context.getIgnoreCache());
    RemoteContent response = messageBundleFetcher.fetch(request);
    if (response.getHttpStatusCode() != RemoteContent.SC_OK) {
      throw new GadgetException(
          GadgetException.Code.FAILED_TO_RETRIEVE_CONTENT,
          "Unable to retrieve message bundle xml. HTTP error " +
          response.getHttpStatusCode());
    }
    MessageBundle bundle
        = new MessageBundle(bundleUrl, response.getResponseAsString());
    return bundle;
  }

  /**
   * Creates a Gadget from the specified gadget spec and context objects.
   * This performs message bundle substitution as well as feature processing.
   *
   * @param spec
   * @param context
   * @return The final Gadget, ready for consumption.
   * @throws GadgetException
   */
  private Gadget createGadgetFromSpec(GadgetSpec spec, GadgetContext context)
      throws GadgetException {
    LocaleSpec localeSpec
        = spec.getModulePrefs().getLocale(context.getLocale());
    MessageBundle bundle;
    String dir;
    if (localeSpec == null) {
      bundle = MessageBundle.EMPTY;
      dir = "ltr";
    } else {
      if (localeSpec.getMessages() != null &&
          localeSpec.getMessages().toString().length() > 0) {
        bundle = getBundle(localeSpec, context);
      } else {
        bundle = MessageBundle.EMPTY;
      }
      dir = localeSpec.getLanguageDirection();
    }

    Substitutions substituter = new Substitutions();
    substituter.addSubstitutions(
        Substitutions.Type.MESSAGE, bundle.getMessages());
    BidiSubstituter.addSubstitutions(substituter, dir);
    substituter.addSubstitution(Substitutions.Type.MODULE, "ID",
        Integer.toString(context.getModuleId()));
    UserPrefSubstituter.addSubstitutions(
        substituter, spec, context.getUserPrefs());
    spec = spec.substitute(substituter);

    Set<GadgetFeatureRegistry.Entry> features = getFeatures(spec);

    List<JsLibrary> jsLibraries = new LinkedList<JsLibrary>();
    Set<String> done = new HashSet<String>(features.size());

    Map<GadgetFeatureRegistry.Entry, GadgetFeature> tasks
        = new HashMap<GadgetFeatureRegistry.Entry, GadgetFeature>();

    do {
      for (GadgetFeatureRegistry.Entry entry : features) {
        if (!done.contains(entry.getName())
            && done.containsAll(entry.getDependencies())) {
          GadgetFeature feature = entry.getFeature().create();
          jsLibraries.addAll(feature.getJsLibraries(context));
          if (!feature.isJsOnly()) {
            tasks.put(entry, feature);
          }
          done.add(entry.getName());
        }
      }
    } while (done.size() != features.size());

    Gadget gadget = new Gadget(context, spec, bundle, jsLibraries);

    runTasks(gadget, tasks);
    return gadget;
  }

  /**
   * Processes tasks required for this gadget. Attempts to run as many tasks
   * in parallel as possible.
   *
   * @param gadget
   * @param tasks
   * @throws GadgetException
   */
  private void runTasks(Gadget gadget,
      Map<GadgetFeatureRegistry.Entry, GadgetFeature> tasks)
      throws GadgetException {

    // Immediately enqueue all the preloads. We don't block on preloads because
    // we want them to run in parallel
    RenderingContext renderContext = gadget.getContext().getRenderingContext();
    if (RenderingContext.GADGET.equals(renderContext)) {
      CompletionService<RemoteContent> preloadProcessor
          = new ExecutorCompletionService<RemoteContent>(executor);
      for (Preload preload : gadget.getSpec().getModulePrefs().getPreloads()) {
        // Cant execute signed/oauth preloads without the token
        if ((preload.getAuth() == Auth.NONE ||
            gadget.getContext().getToken() != null) &&
            (preload.getViews().size() == 0 ||
            preload.getViews().contains(gadget.getContext().getView()))) {
          PreloadTask task = new PreloadTask(gadget.getContext(), preload,
              preloadFetcherFactory);
          Future<RemoteContent> future = preloadProcessor.submit(task);
          gadget.getPreloadMap().put(preload, future);
        }
      }
    }

    // TODO: This seems pointless if nothing is actually using it.
    CompletionService<GadgetException> featureProcessor
        = new ExecutorCompletionService<GadgetException>(executor);
    // FeatureTask is OK has a hash key because we want actual instances, not
    // names.
    GadgetContext context = gadget.getContext();
    Set<FeatureTask> pending = new HashSet<FeatureTask>();
    for (Map.Entry<GadgetFeatureRegistry.Entry, GadgetFeature> entry
        : tasks.entrySet()) {
      FeatureTask task = new FeatureTask(entry.getKey().getName(),
          entry.getValue(), gadget, context, entry.getKey().getDependencies());
      pending.add(task);
    }

    Set<FeatureTask> running = new HashSet<FeatureTask>();
    Set<String> done = new HashSet<String>();
    do {
      for (FeatureTask task : pending) {
        if (task.depsDone(done)) {
          pending.remove(task);
          running.add(task);
          featureProcessor.submit(task);
        }
      }

      if (running.size() > 0) {
        try {
          Future<GadgetException> future;
          while ((future = featureProcessor.take()) != null) {
            GadgetException e = future.get();
            if (future.get() != null) {
              throw future.get();
            }
          }
        } catch (Exception e) {
          throw new GadgetException(
              GadgetException.Code.INTERNAL_SERVER_ERROR, e);
        }
      }

      for (FeatureTask task : running) {
        if (task.isDone()) {
          done.add(task.getName());
          running.remove(task);
        }
      }
    } while (pending.size() > 0 || running.size() > 0);
  }

  /**
   * Constructs a set of dependencies from the given spec.
   *
   * @return The dependencies that are requested in the spec and are also
   *     supported by this server.
   * @throws GadgetException If the spec requires a feature that is not
   *     supported by this server.
   */
  private Set<GadgetFeatureRegistry.Entry> getFeatures(GadgetSpec spec)
      throws GadgetException {
    // Check all required features for the gadget.
    Map<String, Feature> features = spec.getModulePrefs().getFeatures();

    Set<GadgetFeatureRegistry.Entry> dependencies
        = new HashSet<GadgetFeatureRegistry.Entry>(features.size());
    Set<String> unsupported = new HashSet<String>();
    registry.getIncludedFeatures(features.keySet(), dependencies, unsupported);

    for (String missing : unsupported) {
      Feature feature = features.get(missing);
      if (feature.getRequired()) {
        throw new GadgetException(GadgetException.Code.UNSUPPORTED_FEATURE,
            missing);
      }
    }

    return dependencies;
  }
}

/**
 * Provides a task for processing non-trival features (anything that is not
 * js only)
 */
class FeatureTask implements Callable<GadgetException> {
  private final Set<String> dependencies;
  public boolean depsDone(Set<String> deps) {
    return deps.containsAll(dependencies);
  }
  private final String name;
  public String getName() {
    return name;
  }
  private final GadgetFeature feature;
  private final Gadget gadget;
  private final GadgetContext context;

  private boolean done = false;
  public boolean isDone() {
    return done;
  }

  public GadgetException call() {
    try {
      feature.process(gadget, context);
      done = true;
      return null;
    } catch (GadgetException e) {
      return e;
    } catch (Exception e) {
      return new GadgetException(GadgetException.Code.INTERNAL_SERVER_ERROR, e);
    }
  }

  public FeatureTask(String name, GadgetFeature feature, Gadget gadget,
      GadgetContext context, Set<String> dependencies) {
    this.name = name;
    this.feature = feature;
    this.gadget = gadget;
    this.context = context;
    this.dependencies = dependencies;
  }
}

/**
 * Provides a task for preloading data into the gadget content
 */
class PreloadTask implements Callable<RemoteContent> {
  private final Preload preload;
  private final ContentFetcherFactory preloadFetcherFactory;
  private final GadgetContext context;

  public RemoteContent call() {
    RemoteContentRequest request = new RemoteContentRequest(preload.getHref());
    request.getOptions().ownerSigned = preload.isSignOwner();
    request.getOptions().viewerSigned = preload.isSignViewer();
    try {
      switch (preload.getAuth()) {
        case NONE:
          return preloadFetcherFactory.get().fetch(request);
        case SIGNED:
          return preloadFetcherFactory.getSigningFetcher(context.getToken())
              .fetch(request);
        default:
          return RemoteContent.ERROR;
      }
    } catch (GadgetException e) {
      return RemoteContent.ERROR;
    }
  }

  public PreloadTask(GadgetContext context, Preload preload,
      ContentFetcherFactory preloadFetcherFactory) {
    this.preload = preload;
    this.preloadFetcherFactory = preloadFetcherFactory;
    this.context = context;
  }
}