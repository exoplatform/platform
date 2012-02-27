package org.exoplatform.bonitasoft.services.rest.social;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.activity.model.Activity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;

@Path("activitiesStreamManager")
public class ActivitiesStreamManager implements ResourceContainer {
  private static Log logger = ExoLogger.getLogger(ActivitiesStreamManager.class);
  private IdentityManager identityManager;
  private ActivityManager activityManager;

  public ActivitiesStreamManager(IdentityManager identityManager, ActivityManager activityManager) {
    this.identityManager = identityManager;
    this.activityManager = activityManager;
  }

  /**
   * when we use LeaveApplication process, an new entry is added to the
   * activity stream of logged user
   * 
   * @param userName
   * @param comment
   */
  @POST
  @Path("saveActivity")
  @Produces({ MediaType.APPLICATION_JSON })
  public void saveActivity(@FormParam("userName") String userName, @FormParam("comment") String comment) {
    logger.info("### Request for [" + userName + "] adding [" + comment + "] ...###");

    // Get existing user or create a new one
    Identity userIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, userName);

    // Create new activity for this user
    Activity activity = new Activity();
    activity.setUserId(userIdentity.getId());
    activity.setTitle(comment);
    // Save activity into JCR using ActivityManager
    activityManager.saveActivity(activity);

    logger.info("### Request for [" + userName + "] adding [" + comment + "] completed ###");
  }

}
