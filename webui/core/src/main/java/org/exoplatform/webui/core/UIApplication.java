/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.webui.core;

import java.io.Writer;
import java.util.Locale;

import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.exception.MessageException;

/**
 * Created by The eXo Platform SAS
 * May 8, 2006
 */
abstract public class UIApplication  extends  UIContainer {
  
  protected static Log log = ExoLogger.getLogger("portal:UIApplication"); 

  private String owner ;
  private long lastAccessApplication_ ;
  private UIPopupMessages uiPopupMessages_ ;

  public UIApplication() throws Exception {
    uiPopupMessages_ = createUIComponent(UIPopupMessages.class, null , null) ;
    uiPopupMessages_.setId("_" + uiPopupMessages_.hashCode()) ;
  }

  public String getOwner() { return owner ; }  
  public void   setOwner(String s) { owner = s ; }

  public UIPopupMessages getUIPopupMessages() { return uiPopupMessages_ ; }

  public void addMessage(ApplicationMessage message) {
    uiPopupMessages_.addMessage(message) ;
  }

  public void clearMessages() { uiPopupMessages_.clearMessages() ; }

  public long getLastAccessApplication() { return lastAccessApplication_ ; }  
  public void setLastAccessApplication(long time) { lastAccessApplication_ = time; } ;

  public String getUIComponentName() { return "uiapplication" ; }
  
  @SuppressWarnings("unchecked")
  public <T extends UIComponent> T findComponentById(String lookupId) {    
    if(uiPopupMessages_.getId().equals(lookupId)) return (T)uiPopupMessages_;
    return (T)super.findComponentById(lookupId) ;
  }

  public void renderChildren()  throws Exception {
    super.renderChildren();   
    if(uiPopupMessages_ == null)  return ;
    WebuiRequestContext  context =  WebuiRequestContext.getCurrentInstance() ;
    uiPopupMessages_.processRender(context);    
  }
  
  public void processAction(WebuiRequestContext context) throws Exception {
    try {
      super.processAction(context) ;
    } catch (MessageException ex) {
      uiPopupMessages_.addMessage(ex.getDetailMessage()) ;
    } catch(Throwable t) {
      Object[] args = { t.getMessage()} ;
      ApplicationMessage msg = 
        new ApplicationMessage("UIApplication.msg.unknown-error", args, ApplicationMessage.ERROR) ;
      uiPopupMessages_.addMessage(msg) ;
      log.error("Error during the processAction phase", t);
    }
  }
  
  public void renderBlockToUpdate(UIComponent uicomponent, WebuiRequestContext context, Writer w) throws Exception {
    w.write("<div class=\"BlockToUpdate\">") ;
    w.  append("<div class=\"BlockToUpdateId\">").append(uicomponent.getId()).append("</div>");
    w.  write("<div class=\"BlockToUpdateData\">") ;
    uicomponent.processRender(context) ;  
    w.  write("</div>");
    w.write("</div>") ;
  }
}