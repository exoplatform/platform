package org.exoplatform.platform.notification;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.services.rest.impl.EnvironmentContext;

public class LinkProviderUtils {
  
public static final String RESOURCE_URL = "rest/social/notifications";
  
  public static final String INVITE_TO_CONNECT = RESOURCE_URL + "/inviteToConnect";
  
  public static final String CONFIRM_INVITATION_TO_CONNECT = RESOURCE_URL + "/confirmInvitationToConnect";
  
  public static final String IGNORE_INVITATION_TO_CONNECT = RESOURCE_URL + "/ignoreInvitationToConnect";
  
  public static final String ACCEPT_INVITATION_JOIN_SPACE = RESOURCE_URL + "/acceptInvitationToJoinSpace";
  
  public static final String IGNORE_INVITATION_JOIN_SPACE = RESOURCE_URL + "/ignoreInvitationToJoinSpace";
  
  public static final String VALIDATE_REQUEST_JOIN_SPACE = RESOURCE_URL + "/validateRequestToJoinSpace";
  
  public static final String REPLY_ACTIVITY = RESOURCE_URL + "/replyActivity";
  
  public static final String VIEW_FULL_DISCUSSION = RESOURCE_URL + "/viewFullDiscussion";

  public static String getInviteToConnectUrl(String senderId, String receiverId) {
    String baseUrl = getBaseUrl();
    return String.format("%s/%s/%s/%s", baseUrl, INVITE_TO_CONNECT, senderId, receiverId);
  }
  
  public static String getConfirmInvitationToConnectUrl(String senderId, String receiverId) {
    String baseUrl = getBaseUrl();
    return String.format("%s/%s/%s/%s", baseUrl, CONFIRM_INVITATION_TO_CONNECT, senderId, receiverId);
  }
  
  public static String getIgnoreInvitationToConnectUrl(String senderId, String receiverId) {
    String baseUrl = getBaseUrl();
    return String.format("%s/%s/%s/%s", baseUrl, IGNORE_INVITATION_TO_CONNECT, senderId, receiverId);
  }
  
  public static String getAcceptInvitationToJoinSpaceUrl(String spaceId, String userId) {
    String baseUrl = getBaseUrl();
    return String.format("%s/%s/%s/%s", baseUrl, ACCEPT_INVITATION_JOIN_SPACE, spaceId, userId);
  }
  
  public static String getIgnoreInvitationToJoinSpaceUrl(String spaceId, String userId) {
    String baseUrl = getBaseUrl();
    return String.format("%s/%s/%s/%s", baseUrl, IGNORE_INVITATION_JOIN_SPACE, spaceId, userId);
  }
  
  public static String getValidateRequestToJoinSpaceUrl(String spaceId, String userId) {
    String baseUrl = getBaseUrl();
    return String.format("%s/%s/%s/%s", baseUrl, VALIDATE_REQUEST_JOIN_SPACE, spaceId, userId);
  }
  
  public static String getReplyActivityUrl(String activityId, String userId) {
    String baseUrl = getBaseUrl();
    return String.format("%s/%s/%s/%s", baseUrl, REPLY_ACTIVITY, activityId, userId);
  }
  
  public static String getViewFullDiscussionUrl(String activityId, String userId) {
    String baseUrl = getBaseUrl();
    return String.format("%s/%s/%s/%s", baseUrl, VIEW_FULL_DISCUSSION, activityId, userId);
  }
  
  private static String getBaseUrl() {
    HttpServletRequest currentServletRequest = getCurrentServletRequest();
    return currentServletRequest.getScheme() + "://" + currentServletRequest.getServerName() +
                                                 ":" + currentServletRequest.getServerPort();
  }
  
  private static HttpServletRequest getCurrentServletRequest() {
    EnvironmentContext environmentContext = EnvironmentContext.getCurrent();
    return (HttpServletRequest) environmentContext.get(HttpServletRequest.class);
  }

}
