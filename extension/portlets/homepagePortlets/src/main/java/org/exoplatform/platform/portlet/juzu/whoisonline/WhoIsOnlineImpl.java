package org.exoplatform.platform.portlet.juzu.whoisonline;

import org.apache.commons.lang.StringEscapeUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.forum.service.ForumService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.common.RealtimeListAccess;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.manager.RelationshipManager;
import org.exoplatform.social.core.relationship.model.Relationship;
import org.exoplatform.social.core.service.LinkProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 * @date 07/12/12
 */
public class WhoIsOnlineImpl implements WhoIsOnline {

    private static Log log = ExoLogger.getLogger(WhoIsOnlineImpl.class);
    private static final String CONFIRMED = "confirmed";
    private static final String PENDING = "pending";
    private static final String RECEIVED = "received";
    private static final String IGNORED = "ignored";
    private static final String DEFAULT_ACTIVITY = "DEFAULT_ACTIVITY";
    private static final String LINK_ACTIVITY = "LINK_ACTIVITY";
    private static final String DOC_ACTIVITY = "DOC_ACTIVITY";
    private static final int COUNT = 10;
    private static final int MAX_CHAR = 115;
    private static final int INDEX_CHAR = 110;
    private static final String THREE_DOTS = "...";
    private static final int MAX_USER = 17;
    private static final int INDEX_USER = 18;
    private static final int MAX_DOC_CHAR = 25;

    public List<User> getFriends(String userId) {
        try {
            if (userId == null) {
                return null;
            }
            RelationshipManager relationshipManager = (RelationshipManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RelationshipManager.class);
            ForumService forumService = (ForumService) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ForumService.class);
            IdentityManager identityManager = (IdentityManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityManager.class);
            Identity myIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userId);
            List<String> users = forumService.getOnlineUsers();
            if (users.contains(userId)) {
                users.remove(userId);
            }
            Collections.reverse(users);
            if (users.size() > MAX_USER) {
                users = users.subList(0, INDEX_USER);
            }

            List<User> userOnLineList = new ArrayList<User>();
            String lastActivity = "";
            User userOnLine = null;
            String userStatus = "";

            for (String user : users) {
                userOnLine = new User(user);
                Identity userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, user);

                if (userIdentity.equals(myIdentity))
                    continue;

                lastActivity = getLastActivity(userIdentity);
                userStatus = getStatus(myIdentity, userIdentity);

                Profile userProfile = userIdentity.getProfile();
                String avatar = userProfile.getAvatarImageSource();
                if (avatar == null) {
                    avatar = LinkProvider.PROFILE_DEFAULT_AVATAR_URL;
                }

                String position = userProfile.getPosition();
                if (position == null) {
                    position = "";
                }
                String relation = "";
                if (userStatus.equals(RECEIVED)) {
                    relation = relationshipManager.getRelationship(myIdentity, userIdentity).getId();
                }

                userOnLine.setAvatar(avatar);
                userOnLine.setPosition(position);
                userOnLine.setFullName(userProfile.getFullName());
                userOnLine.setId(userProfile.getId());
                userOnLine.setProfileUrl(LinkProvider.getUserActivityUri(user));
                userOnLine.setIdentity(userIdentity.getId());
                userOnLine.setActivity(lastActivity);
                userOnLine.setStatus(userStatus);
                userOnLine.setRelationId(relation);
                userOnLineList.add(userOnLine);
            }
            return userOnLineList;

        } catch (Exception e) {
            log.error("Error in who's online  service: " + e.getMessage(), e);
            return null;
        }
    }


    private String getStatus(Identity identity1, Identity identity2) {
        String status = "";
        RelationshipManager relationshipManager = (RelationshipManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(RelationshipManager.class);
        if (relationshipManager.getStatus(identity1, identity2) == null) {
            status = "";
        } else {
            if (relationshipManager.getStatus(identity1, identity2).equals(Relationship.Type.CONFIRMED)) {
                status = CONFIRMED;
            } else {
                if (relationshipManager.getStatus(identity1, identity2).equals(Relationship.Type.PENDING)) {

                    if (relationshipManager.getRelationship(identity2, identity1).getSender().equals(identity1)) {
                        status = PENDING;
                    } else {
                        status = RECEIVED;
                    }
                } else {
                    status = IGNORED;
                }
            }
        }

        return status;
    }

    private String getLastActivity(Identity identity) {
        String activity = "";
        int count = COUNT;
        int i = 0;
        ActivityManager activityManager = (ActivityManager) ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(ActivityManager.class);
        RealtimeListAccess<ExoSocialActivity> activityList = activityManager.getActivitiesWithListAccess(identity);
        for (ExoSocialActivity act : activityList.loadAsList(i, count)) {
            i++;
            activity = act.getTitle().replaceAll("<br/>", " ").replaceAll("<br />", " ").replaceAll("<br>", " ").replaceAll("</br>", " ").trim();
            activity = StringEscapeUtils.unescapeHtml(activity);
            activity = activity.replaceAll("\"", "'");
            if (act.getType().equals(DEFAULT_ACTIVITY) || act.getType().equals(LINK_ACTIVITY) || act.getType().equals(DOC_ACTIVITY)) {

                if (activity.length() > MAX_CHAR && act.getType().equals(DEFAULT_ACTIVITY)) {
                    String maxBody = activity.substring(0, MAX_CHAR);
                    int tagEnterLocation = maxBody.indexOf('<', 0);
                    if (tagEnterLocation != -1) {
                        if (tagEnterLocation == 0) {
                            if (maxBody.indexOf("<", tagEnterLocation) == 0) {
                                int endtag = activity.indexOf(">", tagEnterLocation);
                                int tagend = activity.indexOf("<", endtag);
                                int tagend2 = activity.indexOf(">", tagend);
                                String linktitle = activity.substring(endtag + 1, tagend);
                                if (linktitle.length() > MAX_CHAR) {
                                    linktitle = linktitle.substring(0, MAX_CHAR);
                                    activity = activity.substring(0, endtag + 1) + linktitle + activity.substring(tagend, tagend2 + 1);
                                } else {
                                    activity = activity.substring(0, tagend2);
                                }
                            }

                            activity = activity + "<span class='truncate_ellipsis'>" + THREE_DOTS + "</span>";
                        } else {
                            int tagEndLocation = maxBody.indexOf("<", tagEnterLocation + 1);
                            int tagLocationEnd = maxBody.indexOf("/>", tagEnterLocation);
                            if ((tagEndLocation == -1 && tagLocationEnd == -1)) {
                                String str1 = maxBody.substring(0, tagEnterLocation - 1);
                                activity = str1 + "<span class='truncate_ellipsis'>" + THREE_DOTS + "</span>";
                            }
                            if (tagEndLocation != -1) {

                                if (tagEndLocation > MAX_CHAR - 3) {
                                    String charRest = activity.substring(0, tagEndLocation + 3);
                                    activity = charRest + "<span class='truncate_ellipsis'>" + THREE_DOTS + "</span>";
                                } else {
                                    if (tagEndLocation <= MAX_CHAR - 3) {
                                        activity = maxBody + "<span class='truncate_ellipsis'>" + THREE_DOTS + "</span>";
                                    }
                                }
                            }
                            if (tagLocationEnd != -1) {
                                activity = maxBody + "<span class='truncate_ellipsis'>" + THREE_DOTS + "</span>";
                            }
                        }
                    } else {
                        activity = maxBody + "<span class='truncate_ellipsis'>" + THREE_DOTS + "</span>";
                    }
                }

                if (act.getType().equals(DOC_ACTIVITY)) {
                    if ((activity.split(">")[1].split("<")[0]).length() > MAX_DOC_CHAR) {
                        String docName = activity.split(">")[1].split("<")[0].substring(0, MAX_DOC_CHAR).concat(THREE_DOTS);
                        String docUrl = activity.split(">")[0].split("=")[1].replace("\"", "'");
                        activity = "Shared a Document <a class='ColorLink' target='_blank' href=" + docUrl + "title='" + activity.split(">")[1].split("<")[0] + "'>" + docName + "</a>";
                    }
                }

                if (act.getType().equals(LINK_ACTIVITY)) {

                        if(activity.indexOf("<",0)!=-1){
                            activity=activity.substring(activity.indexOf(">",0)+1,activity.indexOf("<",activity.indexOf(">",0)));
                        }
                    if (activity.length() > MAX_CHAR) {
                        activity=activity.substring(0,MAX_CHAR);
                    }

                    activity = "<a class='ColorLink' target='_blank' href='" + act.getUrl() + "'>" + activity + "</a>";
                }
                break;
            }
            if (i == 9 && activity.equals("")) {
                count += COUNT;
            }
        }

        return activity;

    }

}
