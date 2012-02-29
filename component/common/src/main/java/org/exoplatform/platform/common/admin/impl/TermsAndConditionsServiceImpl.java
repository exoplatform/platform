// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TermsAndConditionsServiceImpl.java

package org.exoplatform.platform.common.admin.impl;

import javax.jcr.Node;

import org.chromattic.api.ChromatticSession;
import org.exoplatform.commons.chromattic.ChromatticLifeCycle;
import org.exoplatform.commons.chromattic.ChromatticManager;
import org.exoplatform.platform.common.admin.TermsAndConditionsService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.hierarchy.NodeHierarchyCreator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * This service is used to manage JCR node of Terms and conditions
 * 
 * @author Clement
 *
 */
public class TermsAndConditionsServiceImpl implements TermsAndConditionsService {

  private static Log logger = ExoLogger.getLogger(TermsAndConditionsServiceImpl.class);
  private static final String CHROMATTIC_LIFECYCLE_NAME = "termsandconditions";
  private static final String TC_NODE_NAME = "TermsAndConditions";
  private ChromatticLifeCycle lifeCycle;
  private NodeHierarchyCreator nodeHierarchyCreator;
  private static final ThreadLocal<ChromatticSession> session = new ThreadLocal<ChromatticSession>();

  
  /*=======================================================================
   * Component access
   *======================================================================*/
  
  public ChromatticSession getSession() {
    if(session.get() == null) {
      session.set(lifeCycle.getChromattic().openSession());
    }
    return (ChromatticSession)session.get();
  }

  public TermsAndConditionsServiceImpl(ChromatticManager chromatticManager, NodeHierarchyCreator nodeHierarchyCreator) {
    this.lifeCycle = chromatticManager.getLifeCycle(CHROMATTIC_LIFECYCLE_NAME);
    this.nodeHierarchyCreator = nodeHierarchyCreator;
  }

  
  /*=======================================================================
   * API public methods
   *======================================================================*/
  
  public boolean isTermsAndConditionsChecked() {
    boolean isChecked = false;
    if(hasTermsAndConditions()) {
      isChecked = true;
    }
    return isChecked;
  }

  public void checkTermsAndConditions() {
    if (lifeCycle.getContext() == null) {
      lifeCycle.openContext();
    }
    
    if(! hasTermsAndConditions()) {
      createTermsAndConditions();
    }
    else {
      logger.debug("Terms and conditions: yet checked");
    }
  }

  
  /*=======================================================================
   * API private methods
   *======================================================================*/
  
  private void createTermsAndConditions() {
    try {
      Node publicApplicationNode = nodeHierarchyCreator.getPublicApplicationNode(SessionProvider.createSystemProvider());
      if(! publicApplicationNode.hasNode(TC_NODE_NAME)) {
        publicApplicationNode = publicApplicationNode.addNode(TC_NODE_NAME, "nt:folder");
        publicApplicationNode.addMixin("mix:referenceable");
        publicApplicationNode.getSession().save();
      }
    }
    catch(Exception e) {
      logger.error("Terms and conditions: cannot create node", e);
    }
  }

  private boolean hasTermsAndConditions() {
    boolean hasNode = false;
    try {
      Node publicApplicationNode = nodeHierarchyCreator.getPublicApplicationNode(SessionProvider.createSystemProvider());
      if(publicApplicationNode.hasNode(TC_NODE_NAME)) {
        hasNode = true;
      }
    }
    catch(Exception e) {
      logger.error("Terms and conditions: cannot get node", e);
    }
    return hasNode;
  }
}
