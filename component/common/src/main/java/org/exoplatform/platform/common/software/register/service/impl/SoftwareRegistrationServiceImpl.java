package org.exoplatform.platform.common.software.register.service.impl;

import org.chromattic.api.ChromatticSession;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.platform.common.software.register.service.SoftwareRegistrationService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.jcr.Node;

/**
 * Created by The eXo Platform SEA
 * Author : eXoPlatform
 * toannh@exoplatform.com
 * On 9/30/15
 * Implement methods of SoftwareRegistrationService interface
 */
public class SoftwareRegistrationServiceImpl implements SoftwareRegistrationService{

  private static final Log LOG = ExoLogger.getLogger(SoftwareRegistrationServiceImpl.class);
  private static final String CHROMATTIC_LIFECYCLE_NAME = "softwareRegistration";
  private static final String SW_NODE_NAME = "SoftwareRegistration";
  private ChromatticLifeCycle lifeCycle;
  private NodeHierarchyCreator nodeHierarchyCreator;
  private static boolean hasSoftwareRegisteredNode = false;

  public ChromatticSession getSession() {
    return lifeCycle.getChromattic().openSession();
  }

  public SoftwareRegistrationServiceImpl(ChromatticManager chromatticManager, NodeHierarchyCreator nodeHierarchyCreator) {
    this.lifeCycle = chromatticManager.getLifeCycle(CHROMATTIC_LIFECYCLE_NAME);
    this.nodeHierarchyCreator = nodeHierarchyCreator;
  }


  /**
   *{@inheritDoc}
   */
  @Override
  public boolean isSoftwareRegistered() {
    boolean isChecked = false;
    if(hasTermsAndConditions()) {
      isChecked = true;
    }
    return isChecked;
  }

  /**
   *{@inheritDoc}
   */
  @Override
  public void checkSoftwareRegistration() {

  }

  /**
   * Create software registration node
   */
  private void createSoftwareRegistrationNode() {
    SessionProvider sessionProvider = SessionProvider.createSystemProvider();
    try {
      Node publicApplicationNode = nodeHierarchyCreator.getPublicApplicationNode(sessionProvider);
      if(! publicApplicationNode.hasNode(SW_NODE_NAME)) {
        publicApplicationNode = publicApplicationNode.addNode(SW_NODE_NAME, "nt:folder");
        publicApplicationNode.addMixin("mix:referenceable");
        publicApplicationNode.getSession().save();
      }
    } catch(Exception e) {
      LOG.error("Software Registration: cannot create node", e);
    } finally {
      if (sessionProvider != null) {
        sessionProvider.close();

      }
    }
  }

  /**
   * Check existed software registration node
   * @return
   */
  private boolean hasTermsAndConditions() {
    SessionProvider sessionProvider = null;
    try {
      if (hasSoftwareRegisteredNode)  {
        return hasSoftwareRegisteredNode;
      } else {
        try {
          sessionProvider = SessionProvider.createSystemProvider();
          Node publicApplicationNode = nodeHierarchyCreator.getPublicApplicationNode(sessionProvider);
          if(publicApplicationNode.hasNode(SW_NODE_NAME)) {
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
    } catch(Exception e) {
      LOG.error("Software Registration: cannot check node", e);
    }
    return hasSoftwareRegisteredNode;
  }
}
