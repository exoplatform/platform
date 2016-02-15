package org.exoplatform.platform.portlet.juzu.whoisonline;

/**
 * @author <a href="rtouzi@exoplatform.com">rtouzi</a>
 */
public class User {

  /** . */

    private String position = "";
    private String fullName = "";
    private String id;
    private String profileUrl;
    private String activity = "";
    private String userName = "";
  /** . */

    private String avatar;
    private String status = "";
    private String identity = "";
    private String relationId = "";

    public User(final String userName) {
    this.userName = userName;
  }


    public void setPosition(final String position) {
        this.position = position;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setProfileUrl(final String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setActivity(final String activity) {
        this.activity = activity;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }
    public void setStatus(String status) {
        this.status=status;
    }

    public final String getStatus() {
        return status;
    }
    public final String getAvatar() {
        return avatar;
    }
    public final String getActivity() {
        return activity;
    }
    public final String getProfileUrl() {
        return profileUrl;
    }
    public final String getId() {
        return id;
    }
    public final String getFullName() {
        return fullName;
    }
    public final String getPosition() {
        return position;
    }


    public void setIdentity(String identity) {
        this.identity=identity;
    }
    public final String getIdentity() {
        return identity;
    }

    public void setRelationId(String relationId) {

        this.relationId=relationId;
    }
    public final String getRelationId() {
        return relationId;
    }

}
