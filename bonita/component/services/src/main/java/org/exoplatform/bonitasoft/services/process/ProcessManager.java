package org.exoplatform.bonitasoft.services.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.exoplatform.bonitasoft.services.utils.Constants;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.jcr.impl.core.RepositoryImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import com.thoughtworks.xstream.XStream;

public class ProcessManager {
  private static final String BONITA_SERVER_REST_API_PATH = "/bonita-server-rest/API/runtimeAPI/instantiateProcessWithVariables";
  private static final String VAR_LINK = "link";
  private static final Log LOG = ExoLogger.getLogger(ProcessManager.class);
  private String publicationProcessPath = null;
  private String publicationProcessURL = null;

  public ProcessManager(InitParams initParams) {
    if (initParams != null) {
      if (initParams.containsKey("publication.process.path")) {
        publicationProcessPath = initParams.getValueParam("publication.process.path").getValue();
      } else {
        throw new IllegalStateException("init param 'publication.process.path' not set");
      }
    } else {
      throw new IllegalStateException("init params not set");
    }
    publicationProcessURL = new StringBuffer().append(BONITA_SERVER_REST_API_PATH).append(publicationProcessPath).toString();
  }

  /**
   * start an instance of publicationProcess
   * 
   * @param node
   * @param request
   * @return List of messages
   * @throws Exception
   */
  public List<String> startProcess(Node node, String userId) throws Exception {

    PostMethod httpMethod = new PostMethod(publicationProcessURL);
    List<String> messages = new ArrayList<String>();
    try {
      HttpClient httpClient = null;
      HostConfiguration hostConfiguration = new HostConfiguration();
      hostConfiguration.setHost(Constants.HOST, Constants.PORT);

      HttpConnectionManager connectionManager = new SimpleHttpConnectionManager();
      HttpConnectionManagerParams params = new HttpConnectionManagerParams();
      params.setMaxConnectionsPerHost(hostConfiguration, Constants.MAX_HOST_CONNECTIONS);
      connectionManager.setParams(params);

      httpClient = new HttpClient(connectionManager);

      Credentials credsCredentials = new UsernamePasswordCredentials(Constants.REST_USER, Constants.REST_PASS);
      httpClient.getState().setCredentials(AuthScope.ANY, credsCredentials);
      httpClient.setHostConfiguration(hostConfiguration);

      httpMethod.setDoAuthentication(true);
      httpMethod.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=UTF-8");

      NameValuePair nameValuePair = new NameValuePair("options", "user:" + userId);
      NameValuePair[] nameValuePairs = new NameValuePair[2];
      nameValuePairs[0] = nameValuePair;

      // Declare process variables
      Map<String, Object> variables = new HashMap<String, Object>();
      StringBuffer href = new StringBuffer();

      RepositoryImpl repositoryImpl = (RepositoryImpl) node.getSession().getRepository();
      href.append("/").append(repositoryImpl.getName());
      href.append("/" + node.getSession().getWorkspace().getName());
      href.append(node.getPath());
      variables.put(VAR_LINK, href.toString());

      XStream xstream = new XStream();
      NameValuePair nameValuePair2 = new NameValuePair("variables", xstream.toXML(variables));
      nameValuePairs[1] = nameValuePair2;

      httpMethod.setRequestBody(nameValuePairs);
      httpClient.executeMethod(httpMethod);
      messages.add("ok");
    } catch (Exception e) {
      LOG.error("Error when starting an instance of publicationProcess on node " + node.getPath(), e);
      messages.add("ko");
    } finally {
      httpMethod.releaseConnection();
    }
    return messages;
  }

}
