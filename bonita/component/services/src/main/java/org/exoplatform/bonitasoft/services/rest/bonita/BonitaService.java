package org.exoplatform.bonitasoft.services.rest.bonita;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

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
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

@Path("BonitaService")
public class BonitaService implements ResourceContainer {
  private static Log logger = ExoLogger.getLogger(BonitaService.class);

  /**
   * this method allow to get the list of deployed process or the list of
   * assigned task instance to logged user
   * 
   * @param serviceUrl
   * @param request
   * @return
   * @throws IOException
   */
  @GET
  @Path("sendList")
  public Response getList(@QueryParam("ServiceUrl") String serviceUrl, @Context HttpServletRequest request) throws IOException {

    if (logger.isDebugEnabled()) {
      logger.debug("### Starting getList Action ...");
    }

    PostMethod httpMethod = new PostMethod(serviceUrl);
    InputStream responseStream = getResponseAsInputStream(httpMethod, request.getRemoteUser());

    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
      String str;
      StringBuffer buffer = new StringBuffer();

      while ((str = reader.readLine()) != null) {

        // parse Response's bonita service to retrieve the instanceUUID of
        // process
        if (str.contains("<instanceUUID>")) {
          buffer.append(str);
          str = "";
          String str2 = reader.readLine();
          buffer.append(str2);
          str2 = str2.replaceAll("<value>", "");
          str2 = str2.replaceAll("</value>", "");
          str2 = str2.trim();
          if (str2.contains("PublicationProcess")) {
            // get direct link of document
            String documentURL = "/bonita-server-rest/API/queryRuntimeAPI/getProcessInstanceVariables/" + str2;
            try {
              PostMethod httpMethod2 = new PostMethod(documentURL);
              InputStream inputStream2 = getResponseAsInputStream(httpMethod2, request.getRemoteUser());
              BufferedReader reader2 = new BufferedReader(new InputStreamReader(inputStream2));
              String str3;

              String next = null;
              while ((str3 = reader2.readLine()) != null) {
                if (str3.contains("<string>link</string>")) {
                  next = reader2.readLine();
                  next = next.replaceAll("string", "doclink");
                  buffer.append(next);
                }
              }
            } catch (Exception e) {
              if (logger.isDebugEnabled()) {
                logger.debug(e.getStackTrace());
              }
            }
          }
        }
        buffer.append(str);
      }
      CacheControl cacheControl = new CacheControl();
      cacheControl.setNoCache(true);
      cacheControl.setNoStore(true);
      return Response.ok(buffer.toString(), "text/xml; charset=UTF-8").cacheControl(cacheControl).build();
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug(e.getStackTrace());
      }
      return null;
    }
  }

  /**
   * this method return the inputstreamresponse of called rest service
   * 
   * @param httpMethod
   * @param userName
   * @return
   */
  public InputStream getResponseAsInputStream(PostMethod httpMethod, String userName) {

    try {
      HostConfiguration hostConfiguration = new HostConfiguration();
      hostConfiguration.setHost(Constants.HOST, Constants.PORT);

      HttpConnectionManager connectionManager = new SimpleHttpConnectionManager();
      HttpConnectionManagerParams params = new HttpConnectionManagerParams();
      params.setMaxConnectionsPerHost(hostConfiguration, Constants.MAX_HOST_CONNECTIONS);
      connectionManager.setParams(params);

      Credentials credsCredentials = new UsernamePasswordCredentials(Constants.REST_USER, Constants.REST_PASS);

      HttpClient httpClient = new HttpClient(connectionManager);
      httpClient.getState().setCredentials(AuthScope.ANY, credsCredentials);
      httpClient.setHostConfiguration(hostConfiguration);

      httpMethod.setDoAuthentication(true);
      httpMethod.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=UTF-8");

      NameValuePair nameValuePair = new NameValuePair("options", "user:" + userName);
      NameValuePair[] nameValuePairs = new NameValuePair[1];
      nameValuePairs[0] = nameValuePair;
      httpMethod.setRequestBody(nameValuePairs);
      httpClient.executeMethod(httpMethod);

      String statusCode = "-1";
      if (httpMethod.getStatusCode() > 0) {
        statusCode = String.valueOf(httpMethod.getStatusCode());
      }
      String statusText = httpMethod.getStatusText();
      String responseString = httpMethod.getResponseBodyAsString();
      if (logger.isDebugEnabled()) {
        logger.debug("status CODE: " + statusCode + ", status TEXT: " + statusText + "\n response string: " + responseString);
      }
      return httpMethod.getResponseBodyAsStream();
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug(e.getStackTrace());
      }
      return null;
    } finally {
      httpMethod.releaseConnection();
    }
  }

}
