/*
 * Copyright (C) 2003-2011 eXo Platform SAS.
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
package org.exoplatform.platform.component.ecms;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.platform.component.UIAdminToolbarContainer;
import org.exoplatform.portal.application.PortalRequestContext;
import org.exoplatform.portal.webui.util.Util;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.seo.PageMetadataModel;
import org.exoplatform.services.seo.SEOService;
import org.exoplatform.wcm.webui.Utils;
import org.exoplatform.wcm.webui.validator.FloatNumberValidator;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.input.UICheckBoxInput;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;

/**
 * Created by The eXo Platform SAS Author : eXoPlatform exo@exoplatform.com Jun 17, 2011
 */
@ComponentConfig(lifecycle = UIFormLifecycle.class, template = "app:/groovy/platformNavigation/portlet/UIAdminToolbarPortlet/UISEOForm.gtmpl", events = {
    @EventConfig(listeners = UISEOForm.SaveActionListener.class),
    @EventConfig(phase = Phase.DECODE, listeners = UISEOForm.CancelActionListener.class) })
public class UISEOForm extends UIForm {

  public static final String DESCRIPTION = "description";
  public static final String KEYWORDS = "keywords";
  public static final String ROBOTS = "robots";
  public static final String SITEMAP = "sitemap";
  public static final String ISINHERITED = "isInherited";
  public static final String SITEMAP_VISIBLE = "sitemapvisible";
  public static final String PRIORITY = "priority";
  public static final String FREQUENCY = "frequency";
  public static final String ROBOTS_INDEX = "index";
  public static final String ROBOTS_FOLLOW = "follow";
  public static final String FREQUENCY_DEFAULT_VALUE = "Always";

  String description = "";
  String keywords = "";
  String priority = "";
  String frequency = "";
  String index = "";
  String follow = "";
  boolean sitemap = true;
  boolean inherited = false;

  private static String contentPath = null;
  private boolean onContent = false;
  private boolean isInherited = false;
  private ArrayList<String> paramsArray = null;
  // private String pageParent = null;

  private static final Log LOG = ExoLogger.getLogger("seo.UISEOForm");

  public String getContentPath() {
    return this.contentPath;
  }

  public void setContentPath(String contentPath) {
    this.contentPath = contentPath;
  }

  public boolean getOnContent() {
    return this.onContent;
  }

  public void setOnContent(boolean onContent) {
    this.onContent = onContent;
  }

  public boolean getIsInherited() {
    return this.isInherited;
  }

  public void setIsInherited(boolean isInherited) {
    this.isInherited = isInherited;
  }

  public ArrayList<String> getParamsArray() {
    return this.paramsArray;
  }

  public void setParamsArray(ArrayList<String> params) {
    this.paramsArray = params;
  }

  /*
   * public String getPageParent() { if(pageParent != null && pageParent.length() > 0) return pageParent.trim(); return pageParent; } public void setPageParent(String pageParent) { this.pageParent = pageParent; }
   */

  public UISEOForm() throws Exception {
    setActions(new String[] { "Save", "Cancel" });
  }

  public void initSEOForm(PageMetadataModel pageModel) throws Exception {

    if (pageModel != null) {
      description = pageModel.getDescription();
      keywords = pageModel.getKeywords();
      frequency = pageModel.getFrequency();
      if (pageModel.getPriority() >= 0)
        priority = String.valueOf(pageModel.getPriority());
      if (pageModel.getRobotsContent() != null && pageModel.getRobotsContent().length() > 0) {
        index = pageModel.getRobotsContent().split(",")[0];
        follow = pageModel.getRobotsContent().split(",")[1];
      }
      sitemap = pageModel.getSitemap();
    }

    ExoContainer container = ExoContainerContext.getCurrentContainer();
    SEOService seoService = (SEOService) container.getComponentInstanceOfType(SEOService.class);

    UIFormTextAreaInput uiDescription = new UIFormTextAreaInput(DESCRIPTION, DESCRIPTION, null);
    uiDescription.setValue(description);
    addUIFormInput(uiDescription);

    UIFormTextAreaInput uiKeywords = new UIFormTextAreaInput(KEYWORDS, KEYWORDS, null);
    uiKeywords.setValue(keywords);
    addUIFormInput(uiKeywords);

    if (!onContent) {
      /*
       * if(pageParent != null) { if(seoService.getPageMetadata(pageParent) != null && pageModel == null) { setIsInherited(true); UIFormCheckBoxInput<Boolean> isInherited = new UIFormCheckBoxInput<Boolean>(ISINHERITED, ISINHERITED, null); isInherited.setChecked(inherited); addUIFormInput(isInherited); } }
       */
      List<SelectItemOption<String>> robotIndexItemOptions = new ArrayList<SelectItemOption<String>>();
      String robotsindexOptions = seoService.getRobotsIndexOptions();
      String robotsfollowOptions = seoService.getRobotsFollowOptions();
      String frequencyOptions = seoService.getFrequencyOptions();

      if (robotsindexOptions != null && robotsindexOptions.length() > 0) {
        String[] arrOptions = robotsindexOptions.split(",");
        for (int i = 0; i < arrOptions.length; i++) {
          robotIndexItemOptions.add(new SelectItemOption<String>((arrOptions[i])));
        }
      }
      UIFormSelectBox robots_index = new UIFormSelectBox(ROBOTS_INDEX, null, robotIndexItemOptions);
      if (index != null && index.length() > 0)
        robots_index.setValue(index);
      else
        robots_index.setValue(ROBOTS_INDEX);
      addUIFormInput(robots_index);

      List<SelectItemOption<String>> robotFollowItemOptions = new ArrayList<SelectItemOption<String>>();
      if (robotsfollowOptions != null && robotsfollowOptions.length() > 0) {
        String[] arrOptions = robotsfollowOptions.split(",");
        for (int i = 0; i < arrOptions.length; i++) {
          robotFollowItemOptions.add(new SelectItemOption<String>((arrOptions[i])));
        }
      }
      UIFormSelectBox robots_follow = new UIFormSelectBox(ROBOTS_FOLLOW, null, robotFollowItemOptions);
      if (follow != null && follow.length() > 0)
        robots_follow.setValue(follow);
      else
        robots_follow.setValue(ROBOTS_FOLLOW);
      addUIFormInput(robots_follow);

      UICheckBoxInput visibleSitemapCheckbox = new UICheckBoxInput (SITEMAP, SITEMAP, null);
      visibleSitemapCheckbox.setChecked(sitemap);
      addUIFormInput(visibleSitemapCheckbox);

      UIFormStringInput uiPrority = new UIFormStringInput(PRIORITY, null);
      if (priority == null || priority.length() == 0) {
        WebuiRequestContext rc = WebuiRequestContext.getCurrentInstance();
        priority = rc.getApplicationResourceBundle().getString("UISEOForm.tip.priority");
      }
      uiPrority.setValue(priority);
      addUIFormInput(uiPrority.addValidator(FloatNumberValidator.class));

      List<SelectItemOption<String>> frequencyItemOptions = new ArrayList<SelectItemOption<String>>();
      if (frequencyOptions != null && frequencyOptions.length() > 0) {
        String[] arrOptions = frequencyOptions.split(",");
        for (int i = 0; i < arrOptions.length; i++) {
          frequencyItemOptions.add(new SelectItemOption<String>(arrOptions[i], (arrOptions[i])));
        }
      }
      UIFormSelectBox frequencySelectbox = new UIFormSelectBox(FREQUENCY, null, frequencyItemOptions);
      if (frequency != null && frequency.length() > 0)
        frequencySelectbox.setValue(frequency);
      else
        frequencySelectbox.setValue(FREQUENCY_DEFAULT_VALUE);
      addUIFormInput(frequencySelectbox);
    }
  }

  public static class SaveActionListener extends EventListener<UISEOForm> {

    public void execute(Event<UISEOForm> event) throws Exception {
      UISEOForm uiForm = event.getSource();
      UIApplication uiApp = uiForm.getAncestorOfType(UIApplication.class);
      String description = uiForm.getUIFormTextAreaInput(DESCRIPTION).getValue();
      String keywords = uiForm.getUIFormTextAreaInput(KEYWORDS).getValue();
      PortalRequestContext portalRequestContext = Util.getPortalRequestContext();
      String portalName = portalRequestContext.getPortalOwner();
      String uri = portalRequestContext.getRequestURI();
      String fullStatus = null;
      String pageReference = Util.getUIPortal().getSelectedUserNode().getPageRef();

      if (!uiForm.onContent) {
        String robots_index = uiForm.getUIFormSelectBox(ROBOTS_INDEX).getValue();
        String robots_follow = uiForm.getUIFormSelectBox(ROBOTS_FOLLOW).getValue();
        String rebots_content = robots_index + ", " + robots_follow;
        boolean isVisibleSitemap = uiForm.getUICheckBoxInput(SITEMAP).isChecked();
        float priority = -1;
        if (uiForm.getUIStringInput(PRIORITY).getValue() != null && uiForm.getUIStringInput(PRIORITY).getValue().length() > 0) {
          priority = Float.parseFloat(uiForm.getUIStringInput(PRIORITY).getValue());
          if (priority < 0.0 || priority > 1.0) {
            uiApp.addMessage(new ApplicationMessage("FloatNumberValidator.msg.Invalid-number", null, ApplicationMessage.WARNING));
            event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
            return;
          }
        }
        String frequency = uiForm.getUIFormSelectBox(FREQUENCY).getValue();
        try {
          PageMetadataModel metaModel = new PageMetadataModel();
          metaModel.setDescription(description);
          metaModel.setFrequency(frequency);
          metaModel.setKeywords(keywords);
          metaModel.setPriority(priority);
          metaModel.setRobotsContent(rebots_content);
          metaModel.setSiteMap(isVisibleSitemap);
          metaModel.setUri(uri);
          metaModel.setPageReference(pageReference);
          if (description != null && keywords != null && priority != -1)
            fullStatus = "Full";
          else
            fullStatus = "Partial";
          metaModel.setFullStatus(fullStatus);
          /*
           * if(uiForm.isInherited) { if(uiForm.getUIFormCheckBoxInput(ISINHERITED).isChecked()) metaModel.setPageParent(uiForm.pageParent); }
           */

          SEOService seoService = uiForm.getApplicationComponent(SEOService.class);
          seoService.storePageMetadata(metaModel, portalName, uiForm.onContent);
          UIAdminToolbarContainer uiAdminToolbar = uiForm.getAncestorOfType(UIAdminToolbarContainer.class);
          if (uiAdminToolbar != null) {
            Utils.closePopupWindow(uiAdminToolbar, UIAdminToolbarContainer.SEO_POPUP_WINDOW);
          }
        } catch (Exception ex) {
          LOG.error("Unexpected error ", ex);
          uiApp.addMessage(new ApplicationMessage("UISEOForm.msg.repository-exception", null, ApplicationMessage.ERROR));
          event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
          return;
        }
      } else {
        try {
          PageMetadataModel metaModel = new PageMetadataModel();
          metaModel.setDescription(description);
          metaModel.setKeywords(keywords);
          metaModel.setUri(uri);
          metaModel.setPageReference(pageReference);
          if (description != null && keywords != null)
            fullStatus = "Full";
          else
            fullStatus = "Partial";
          metaModel.setFullStatus(fullStatus);
          SEOService seoService = uiForm.getApplicationComponent(SEOService.class);
          Node contentNode = null;
          for (int i = 0; i < uiForm.paramsArray.size(); i++) {
            String contentPath = uiForm.paramsArray.get(i).toString();
            contentNode = seoService.getContentNode(contentPath);
            if (contentNode != null)
              break;
          }
          metaModel.setUri(contentNode.getUUID());
          seoService.storePageMetadata(metaModel, portalName, uiForm.onContent);
          UIAdminToolbarContainer uiAdminToolbar = uiForm.getAncestorOfType(UIAdminToolbarContainer.class);
          if (uiAdminToolbar != null) {
            Utils.closePopupWindow(uiAdminToolbar, UIAdminToolbarContainer.SEO_POPUP_WINDOW);
          }
        } catch (Exception ex) {
          LOG.error("Unexpected error ", ex);
          uiApp.addMessage(new ApplicationMessage("UISEOForm.msg.repository-exception", null, ApplicationMessage.ERROR));
          event.getRequestContext().addUIComponentToUpdateByAjax(uiApp.getUIPopupMessages());
          return;
        }
      }

    }
  }

  public static class CancelActionListener extends EventListener<UISEOForm> {
    public void execute(Event<UISEOForm> event) throws Exception {
      UISEOForm uiSEO = event.getSource();
      UIAdminToolbarContainer uiAdminToolbar = uiSEO.getAncestorOfType(UIAdminToolbarContainer.class);
      if (uiAdminToolbar != null) {
        Utils.closePopupWindow(uiAdminToolbar, UIAdminToolbarContainer.SEO_POPUP_WINDOW);
      }

    }
  }
}
