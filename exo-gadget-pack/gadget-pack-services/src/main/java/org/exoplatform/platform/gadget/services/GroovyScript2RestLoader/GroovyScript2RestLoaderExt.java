package org.exoplatform.platform.gadget.services.GroovyScript2RestLoader;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.ws.rs.Path;

import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.jcr.ext.resource.jcr.Handler;
import org.exoplatform.services.jcr.ext.script.groovy.GroovyScript2RestLoader;
import org.exoplatform.services.jcr.ext.script.groovy.GroovyScript2RestLoaderPlugin;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.ext.groovy.GroovyJaxrsPublisher;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.script.groovy.GroovyScriptInstantiator;

@Path("script/groovy")
public class GroovyScript2RestLoaderExt extends GroovyScript2RestLoader{
  private static final Log LOG = ExoLogger.getLogger(GroovyScript2RestLoaderExt.class);
    
    public GroovyScript2RestLoaderExt(ResourceBinder binder,
            GroovyScriptInstantiator groovyScriptInstantiator,
            RepositoryService repositoryService,
            ThreadLocalSessionProviderService sessionProviderService,
            ConfigurationManager configurationManager,
            RegistryService registryService, Handler jcrUrlHandler,
            InitParams params) {
        super(binder, groovyScriptInstantiator, repositoryService,
                sessionProviderService, configurationManager, registryService,
                jcrUrlHandler, params);
    }

    public GroovyScript2RestLoaderExt(ResourceBinder binder,
            GroovyScriptInstantiator groovyScriptInstantiator,
            RepositoryService repositoryService,
            ThreadLocalSessionProviderService sessionProviderService,
            ConfigurationManager configurationManager,
            RegistryService registryService,
            GroovyJaxrsPublisher groovyPublisher, Handler jcrUrlHandler,
            InitParams params) {
        super(binder, groovyScriptInstantiator, repositoryService,
                sessionProviderService, configurationManager, registryService,
                groovyPublisher, jcrUrlHandler, params);
    }

    public GroovyScript2RestLoaderExt(ResourceBinder binder,
            GroovyScriptInstantiator groovyScriptInstantiator,
            RepositoryService repositoryService,
            ThreadLocalSessionProviderService sessionProviderService,
            ConfigurationManager configurationManager, Handler jcrUrlHandler,
            InitParams params) {
        super(binder, groovyScriptInstantiator, repositoryService,
                sessionProviderService, configurationManager, jcrUrlHandler, params);
    }

    @Override
    public void start(){
        registryService = null; // ignore JCR registry service to force loading info from file
        super.start();
    }

    @Override
    protected void addScripts(){
        // Reload Groovy scripts from files if running in development mode
    	String isDevMode = System.getProperty("exo.product.developing");
        if(isDevMode != null && isDevMode.equalsIgnoreCase("true")){
            if (loadPlugins == null || loadPlugins.size() == 0){
                return;
            }

            for (GroovyScript2RestLoaderPlugin loadPlugin : loadPlugins){
                // If no one script configured then skip this item,
                // there is no reason to do anything.
                if (loadPlugin.getXMLConfigs().size() == 0){
                    continue;
                }

                Session session = null;
                try{
                    ManageableRepository repository = repositoryService.getRepository(loadPlugin.getRepository());
                    String workspace = loadPlugin.getWorkspace();
                    session = repository.getSystemSession(workspace);
                    String nodeName = loadPlugin.getNode();
                    Node node = null;
                    try{
                        node = (Node)session.getItem(nodeName);
                        node.remove();
                        session.save();
                    } catch (PathNotFoundException e){
                        LOG.info(nodeName + " not found", e);
                    }

                } catch (Exception e){
                    LOG.error("Failed remove scripts. ", e);
                } finally{
                    if (session != null){
                        session.logout();
                    }
                }
            }
        }
        
        super.addScripts();
    }
}
