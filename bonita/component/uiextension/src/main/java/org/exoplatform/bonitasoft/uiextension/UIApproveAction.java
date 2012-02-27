/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.bonitasoft.uiextension;

import java.util.List;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;

import org.exoplatform.bonitasoft.services.process.ProcessManager;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.ecm.webui.component.explorer.UIJCRExplorer;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;

@ComponentConfig(template = "classpath:templates/approveAction/UIApproveAction.gtmpl", events = {
    @EventConfig(listeners = UIApproveAction.ApproveActionListener.class),
    @EventConfig(listeners = UIApproveAction.GetCommentActionListener.class) })
public class UIApproveAction extends UIComponent {
  private static Log logger = ExoLogger.getLogger(UIApproveAction.class);
  private static final String STATE_PROPERTY = "publication:currentState";
  private static final String DRAFT = "draft";

  public UIApproveAction() {}

  public String getState() throws Exception {
    Node node = getEditingNode();
    if (node.hasProperty("publication:currentState")) {
      return node.getProperty("publication:currentState").getString();
    } else {
      return "notPublicationCycle";
    }

  }

  public Node getEditingNode() throws Exception {
    return getAncestorOfType(UIJCRExplorer.class).getCurrentNode();
  }

  /**
   * return the allowed action on uiextension
   * 
   * @return
   */
  public String[] getAllowedActions() {
    String inlife = "false";
    try {
      Node node = this.getEditingNode();
      if (node.hasProperty("exo:bonitaEnrolledIn")) {
        inlife = node.getProperty("exo:bonitaEnrolledIn").getString();
      }
      if (node.hasProperty(STATE_PROPERTY)) {
        String state = node.getProperty(STATE_PROPERTY).getString();
        if (state.equals(DRAFT) && inlife.equals("false")) {
          return new String[] { "Approve" };
        }
      }
      return new String[0];
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        logger.debug(e.getStackTrace());
      }
      return new String[0];
    }

  }

  @SuppressWarnings("unchecked")
  public List<String> getMessages() {
    HttpServletRequest request = Util.getPortalRequestContext().getRequest();
    List<String> msglist = (List<String>) request.getAttribute("messages");
    return msglist;
  }

  public String getCommentsFromNode() {
    HttpServletRequest request = Util.getPortalRequestContext().getRequest();
    String comment = (String) request.getAttribute("comment");
    if (comment != null) {
      return comment;
    } else {
      return "";
    }
  }

  public static class ApproveActionListener extends EventListener<UIApproveAction> {
    public void execute(Event<UIApproveAction> event) throws Exception {
      if (logger.isDebugEnabled()) {
        logger.debug("### Starting Approve Action ...");
      }
      UIApproveAction uiApproveAction = (UIApproveAction) event.getSource();
      HttpServletRequest request = Util.getPortalRequestContext().getRequest();

      Node node = uiApproveAction.getEditingNode();
      ExoContainer container = PortalContainer.getInstance();
      ProcessManager processManager = (ProcessManager) container.getComponentInstanceOfType(ProcessManager.class);
      List<String> messages = processManager.startProcess(node, request.getRemoteUser());
      request.setAttribute("messages", messages);
    }
  }

  public static class GetCommentActionListener extends EventListener<UIApproveAction> {
    public void execute(Event<UIApproveAction> event) throws Exception {
      UIApproveAction uiApproveAction = (UIApproveAction) event.getSource();
      HttpServletRequest request = Util.getPortalRequestContext().getRequest();
      Node node = uiApproveAction.getEditingNode();
      String comment = "No Comments";
      if (node.hasProperty("exo:comment")) {
        comment = node.getProperty("exo:comment").getString();
      }
      request.setAttribute("comment", comment);
    }
  }

}
