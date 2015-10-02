package org.exoplatform.platform.common.software.register.service.impl;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.chromattic.api.ChromatticSession;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.platform.common.rest.PlatformInformationRESTService;
import org.exoplatform.platform.common.rest.PlatformInformationRESTService.JsonPlatformInfo;
import org.exoplatform.platform.common.software.register.model.SoftwareRegistration;
import org.exoplatform.platform.common.software.register.service.SoftwareRegistrationService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.json.JSONObject;

import javax.jcr.Node;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SEA
 * Author : eXoPlatform
 * toannh@exoplatform.com
 * On 9/30/15
 * Implement methods of SoftwareRegistrationService interface
 */
public class SoftwareRegistrationServiceImpl implements SoftwareRegistrationService {

  private static final Log LOG = ExoLogger.getLogger(SoftwareRegistrationServiceImpl.class);
  private static final String CHROMATTIC_LIFECYCLE_NAME = "softwareRegistration";
  private static final String SW_NODE_NAME = "SoftwareRegistration";
  private ChromatticLifeCycle lifeCycle;
  private NodeHierarchyCreator nodeHierarchyCreator;
  private static boolean hasSoftwareRegisteredNode = false;
  private static SettingService settingService;
  private PlatformInformationRESTService platformInformationRESTService;

  public ChromatticSession getSession() {
    return lifeCycle.getChromattic().openSession();
  }

  public SoftwareRegistrationServiceImpl(ChromatticManager chromatticManager,
                                         NodeHierarchyCreator nodeHierarchyCreator,
                                         SettingService settingService,
                                         PlatformInformationRESTService platformInformationRESTService) {
    this.lifeCycle = chromatticManager.getLifeCycle(CHROMATTIC_LIFECYCLE_NAME);
    this.nodeHierarchyCreator = nodeHierarchyCreator;
    this.settingService = settingService;
    this.platformInformationRESTService = platformInformationRESTService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SoftwareRegistration getAccessToken(String code, String returnURL) {
    String url = SOFTWARE_REGISTRATION_HOST +"/portal/accessToken";
    try {
      HttpClient client = new DefaultHttpClient();
      HttpPost post = new HttpPost(url);
      List<NameValuePair> urlParameters = new ArrayList<>();
      urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));
      urlParameters.add(new BasicNameValuePair("code", code));
      urlParameters.add(new BasicNameValuePair("redirect_uri", returnURL));
      urlParameters.add(new BasicNameValuePair("client_id", "x6iCo6YWmw"));
      urlParameters.add(new BasicNameValuePair("client_secret", "3XNzbpuTSx5HqJsBSwgl"));

      post.setEntity(new UrlEncodedFormEntity(urlParameters));
      HttpResponse response = client.execute(post);
      BufferedReader rd = new BufferedReader(
              new InputStreamReader(response.getEntity().getContent()));
      StringBuffer result = new StringBuffer();
      String line = "";
      while ((line = rd.readLine()) != null) {
        result.append(line);
      }

      SoftwareRegistration softwareRegistration = new SoftwareRegistration();
      JSONObject responseData = new JSONObject(result.toString());
      if (response.getStatusLine().getStatusCode() == HTTPStatus.OK) {
        String accessToken = responseData.getString("access_token");
        softwareRegistration.setAccess_token(accessToken);
      } else {
        String errorCode = responseData.getString("error_code");
        softwareRegistration.setError_code(errorCode);
      }

      return softwareRegistration;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateSkippedNumber() {
    int skippedNumber = getSkippedNumber();
    settingService.set(Context.GLOBAL, Scope.GLOBAL, SOFTWARE_REGISTRATION_SKIPPED,
            new SettingValue<Object>(String.valueOf(++skippedNumber)));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getSkippedNumber() {
    SettingValue settingValue = settingService.get(Context.GLOBAL, Scope.GLOBAL, SOFTWARE_REGISTRATION_SKIPPED);
    if (settingValue != null) {
      return Integer.parseInt(settingValue.getValue().toString());
    }
    settingService.set(Context.GLOBAL, Scope.GLOBAL, SOFTWARE_REGISTRATION_SKIPPED, new SettingValue<Object>("0"));
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSoftwareRegistered() {
    boolean isChecked = false;
    if (hasSoftwareRegistration()) {
      isChecked = true;
    }
    return isChecked;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void checkSoftwareRegistration() {
    if (lifeCycle.getContext() == null) {
      lifeCycle.openContext();
    }

    if (!hasSoftwareRegistration()) {
      createSoftwareRegistrationNode();
    } else {
      LOG.debug("Terms and conditions: yet checked");
    }
  }

  /**
   * Create software registration node
   */
  private void createSoftwareRegistrationNode() {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      Node publicApplicationNode = nodeHierarchyCreator.getPublicApplicationNode(sessionProvider);
      if (!publicApplicationNode.hasNode(SW_NODE_NAME)) {
        publicApplicationNode = publicApplicationNode.addNode(SW_NODE_NAME, "nt:folder");
        publicApplicationNode.addMixin("mix:referenceable");
        publicApplicationNode.getSession().save();
      }
    } catch (Exception e) {
      LOG.error("Software Registration: cannot create node", e);
    } finally {
      if (sessionProvider != null) {
        sessionProvider.close();
      }
    }
  }

  /**
   * Check existed software registration node
   *
   * @return
   */
  private boolean hasSoftwareRegistration() {
    SessionProvider sessionProvider = null;
    try {
      if (hasSoftwareRegisteredNode) {
        return hasSoftwareRegisteredNode;
      } else {
        try {
          sessionProvider = SessionProvider.createSystemProvider();
          Node publicApplicationNode = nodeHierarchyCreator.getPublicApplicationNode(sessionProvider);
          if (publicApplicationNode.hasNode(SW_NODE_NAME)) {
            hasSoftwareRegisteredNode = true;
          } else {
            hasSoftwareRegisteredNode = false;
          }
        } catch (Exception e) {
          LOG.error("Software Registration: cannot get node", e);
          hasSoftwareRegisteredNode = false;
        } finally {
          sessionProvider.close();
        }
        return hasSoftwareRegisteredNode;
      }
    } catch (Exception e) {
      LOG.error("Software Registration: cannot check node", e);
    }
    return hasSoftwareRegisteredNode;
  }

  /**
   * {@inheritDoc}
   */
  public boolean sendPlfInformation(String accessTokencode) {
    try {
      String url = SOFTWARE_REGISTRATION_HOST+"/portal/rest/registerLocalPlatformInformation/register";
      HttpClient client = new DefaultHttpClient();
      HttpPost httpPost = new HttpPost(url);

      JsonPlatformInfo jsonPlatformInfo = platformInformationRESTService.getJsonPlatformInfo();
      JSONObject jsonObj = new JSONObject(jsonPlatformInfo);

      String input = jsonObj.toString();

      httpPost.setHeader("Accept", "application/json");
      httpPost.setHeader("Content-type", "application/json");
      httpPost.setHeader("Authorization", "Bearer " + accessTokencode);
      httpPost.setEntity(new StringEntity(input));

      HttpResponse response = client.execute(httpPost);

      if (response.getStatusLine().getStatusCode() != 200) {
        LOG.warn("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        return false;
      }
      LOG.info("Output from Server .... \n");
      return true;
    } catch (Exception e) {
      LOG.warn("Can not send Platform information to eXo community", e);
      return false;
    }

  }

}
