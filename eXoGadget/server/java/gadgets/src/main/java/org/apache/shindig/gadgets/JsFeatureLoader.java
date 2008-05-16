/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.shindig.gadgets;

import org.apache.shindig.util.ResourceLoader;
import org.apache.shindig.util.XmlException;
import org.apache.shindig.util.XmlUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Provides a mechanism for loading a group of js features from a directory.
 *
 * All directories from the given input will be checked recursively for files
 * named "feature.xml"
 *
 * Usage:
 * GadgetFeatureRegistry registry = // get your feature registry.
 * JsFeatureLoader loader = new JsFeatureLoader(fetcher);
 * loader.loadFeatures("res://features/", registry);
 * loader.loadFeatures("/home/user/my-features/", registry);
 */
public class JsFeatureLoader {

  private final ContentFetcher fetcher;

  private static final Logger logger
      = Logger.getLogger("org.apache.shindig.gadgets");

  /**
   * Loads all of the gadgets in the directory specified by path. Invalid
   * features will not cause this to fail, but passing an invalid path will.
   *
   * @param path The file or directory to load the feature from. If feature.xml
   *    is passed in directly, it will be loaded as a single feature. If a
   *    directory is passed, any features in that directory (recursively) will
   *    be loaded. If res://*.txt is passed, we will look for named resources
   *    in the text file. If path is prefixed with res://, the file
   *    is treated as a resource, and all references are assumed to be
   *    resources as well.
   * @throws GadgetException
   */
  public void loadFeatures(String path, GadgetFeatureRegistry registry)
      throws GadgetException {
    List<ParsedFeature> features = new LinkedList<ParsedFeature>();
    try {
      if (path.startsWith("res://")) {
        path = path.substring(6);
        logger.info("Loading resources from: " + path);
        if (path.endsWith(".txt")) {
          loadResources(ResourceLoader.getContent(path).split("[\r\n]+"),
              features);
        } else {
          loadResources(new String[]{path}, features);
        }
      } else {
        logger.info("Loading files from: " + path);
        File file = new File(path);
        loadFiles(new File[]{file}, features);
      }
    } catch (IOException e) {
      throw new GadgetException(GadgetException.Code.INVALID_PATH, e);
    }

    for (ParsedFeature feature : features) {
      JsLibraryFeatureFactory factory
          = new JsLibraryFeatureFactory(feature.libraries);
      registry.register(feature.name, feature.deps, factory);
    }
  }

  /**
   * Parses and registers a single feature xml.
   * Used for testing.
   *
   * @param xml
   * @return The parsed feature.
   */
  public GadgetFeatureRegistry.Entry loadFeature(
      GadgetFeatureRegistry registry, String xml) throws GadgetException {
    ParsedFeature feature = parse(xml, "", false);

    JsLibraryFeatureFactory factory
        = new JsLibraryFeatureFactory(feature.libraries);
    return registry.register(feature.name, null, factory);
  }

  /**
   * Loads features from directories recursively.
   * @param files The files to examine.
   * @param features The set of all loaded features
   * @throws GadgetException
   */
  private void loadFiles(File[] files, List<ParsedFeature> features)
      throws GadgetException {
    for (File file : files) {
      if (file.isDirectory()) {
        loadFiles(file.listFiles(), features);
      } else if (file.getName().endsWith(".xml")) {
        ParsedFeature feature = processFile(file);
        if (feature != null) {
          features.add(feature);
        }
      }
    }
  }

  /**
   * Loads resources recursively.
   * @param paths The base paths to look for feature.xml
   * @param features The set of all loaded features
   * @throws GadgetException
   */
  private void loadResources(String[] paths, List<ParsedFeature> features)
      throws GadgetException {
    try {
      for (String file : paths) {
        logger.info("Processing resource: " + file);
        String content = ResourceLoader.getContent(file);
        String parent = file.substring(0, file.lastIndexOf('/') + 1);
        ParsedFeature feature = parse(content, parent, true);
        if (feature != null) {
          features.add(feature);
        } else {
          logger.warning("Failed to parse feature: " + file);
        }
      }
    } catch (IOException e) {
      throw new GadgetException(GadgetException.Code.INVALID_PATH, e);
    }
  }

  /**
   * Loads a single feature from a file.
   *
   * If the file can't be loaded, an error will be generated but no exception
   * will be thrown.
   *
   * @param file The file that contains the feature description.
   * @return The parsed feature.
   */
  private ParsedFeature processFile(File file) {
    logger.info("Loading file: " + file.getName());
    ParsedFeature feature = null;
    if (file.canRead()) {
      try {
        feature = parse(ResourceLoader.getContent(file),
                        file.getParent() + '/',
                        false);
      } catch (IOException e) {
        logger.warning("Error reading file: " + file.getAbsolutePath());
      } catch (GadgetException e) {
        logger.warning("Failed parsing file: " + file.getAbsolutePath());
      }
    } else {
      logger.warning("Unable to read file: " + file.getAbsolutePath());
    }
    return feature;
  }

  /**
   * Parses the input into a dom tree.
   * @param xml
   * @param path The path the file was loaded from.
   * @param isResource True if the file was a resource.
   * @return A dom tree representing the feature.
   * @throws GadgetException
   */
  private ParsedFeature parse(String xml, String path, boolean isResource)
      throws GadgetException {
    Element doc;
    try {
      doc = XmlUtil.parse(xml);
    } catch (XmlException e) {
      throw new GadgetException(GadgetException.Code.MALFORMED_XML_DOCUMENT, e);
    }

    ParsedFeature feature = new ParsedFeature();

    feature.basePath = path;
    feature.isResource = isResource;

    NodeList nameNode = doc.getElementsByTagName("name");
    if (nameNode.getLength() != 1) {
      throw new GadgetException(GadgetException.Code.MALFORMED_XML_DOCUMENT,
          "No name provided");
    }
    feature.name = nameNode.item(0).getTextContent();

    NodeList gadgets = doc.getElementsByTagName("gadget");
    for (int i = 0, j = gadgets.getLength(); i < j; ++i) {
      processContext(feature, (Element)gadgets.item(i),
          RenderingContext.GADGET);
    }

    NodeList containers = doc.getElementsByTagName("container");
    for (int i = 0, j = containers.getLength(); i < j; ++i) {
      processContext(feature, (Element)containers.item(i),
          RenderingContext.CONTAINER);
    }

    NodeList dependencies = doc.getElementsByTagName("dependency");
    for (int i = 0, j = dependencies.getLength(); i < j; ++i) {
      feature.deps.add(dependencies.item(i).getTextContent());
    }

    return feature;
  }

  /**
   * Processes <gadget> and <container> tags and adds new libraries
   * to the feature.
   * @param feature
   * @param context
   * @param renderingContext
   * @throws GadgetException
   */
  private void processContext(ParsedFeature feature, Element context,
                              RenderingContext renderingContext)
      throws GadgetException {
    String container = XmlUtil.getAttribute(context, "container",
        ContainerConfig.DEFAULT_CONTAINER);
    NodeList libraries = context.getElementsByTagName("script");
    for (int i = 0, j = libraries.getLength(); i < j; ++i) {
      Element script = (Element)libraries.item(i);
      boolean inlineOk = XmlUtil.getBoolAttribute(script, "inline", true);
      String source = XmlUtil.getAttribute(script, "src");
      String content;
      JsLibrary.Type type;
      if (source == null) {
        type = JsLibrary.Type.INLINE;
        content = script.getTextContent();
      } else {
        content = source;
        if (content.startsWith("http://")) {
          type = JsLibrary.Type.URL;
        } else if (content.startsWith("//")) {
          type = JsLibrary.Type.URL;
          content = content.substring(1);
        } else if (content.startsWith("res://")) {
          content = content.substring(6);
          type = JsLibrary.Type.RESOURCE;
        } else if (feature.isResource) {
          // Note: Any features loaded as resources will assume that their
          // paths point to resources as well.
          content = feature.basePath + content;
          type = JsLibrary.Type.RESOURCE;
        } else {
          content = feature.basePath + content;
          type = JsLibrary.Type.FILE;
        }
      }
      JsLibrary library = JsLibrary.create(
          type, content, feature.name, inlineOk ? fetcher : null);
      for (String cont : container.split(",")) {
        feature.addLibrary(renderingContext, cont.trim(), library);
      }
    }
  }

  /**
   * @param fetcher
   */
  public JsFeatureLoader(ContentFetcher fetcher) {
    this.fetcher = fetcher;
  }
}

/**
 * Temporary structure to represent the intermediary parse state.
 */
class ParsedFeature {
  public String name = "";
  public String basePath = "";
  public boolean isResource = false;
  final Map<RenderingContext, Map<String, List<JsLibrary>>> libraries;
  final List<String> deps;

  public ParsedFeature() {
    libraries = new EnumMap<RenderingContext, Map<String, List<JsLibrary>>>(
        RenderingContext.class);
    deps = new LinkedList<String>();
  }

  public void addLibrary(RenderingContext ctx, String cont, JsLibrary library) {
    Map<String, List<JsLibrary>> ctxLibs = libraries.get(ctx);
    if (ctxLibs == null) {
      ctxLibs = new HashMap<String, List<JsLibrary>>();
      libraries.put(ctx, ctxLibs);
    }

    List<JsLibrary> containerLibs = ctxLibs.get(cont);
    if (containerLibs == null) {
      containerLibs = new LinkedList<JsLibrary>();
      ctxLibs.put(cont, containerLibs);
    }

    containerLibs.add(library);
  }
}
