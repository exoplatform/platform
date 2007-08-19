/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core;

import java.io.Writer;
import java.util.Locale;

import org.apache.commons.logging.Log;
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
  private Locale locale_ = Locale.ENGLISH  ;
  private long lastAccessApplication_ ;
  private UIPopupMessages uiPopupMessages_ ;

  public UIApplication() throws Exception {
    uiPopupMessages_ = createUIComponent(UIPopupMessages.class, null , null) ;
    uiPopupMessages_.setId(String.valueOf(uiPopupMessages_.hashCode())) ;
  }

  public String getOwner() { return owner ; }  
  public void   setOwner(String s) { owner = s ; }

  public Locale getLocale() {  return locale_ ; }  
  public void   setLocale(Locale locale) { locale_ = locale ; }

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