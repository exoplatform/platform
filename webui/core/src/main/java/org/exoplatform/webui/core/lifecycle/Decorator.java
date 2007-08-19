/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core.lifecycle;

import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Created by The eXo Platform SAS
 * Aug 23, 2006  
 * 
 * Writes a decorator template with the given css class style
 */
public class Decorator {
  //<%decorator.start(.., ..)%>
  final  public  void  start(String cssClass,  String style) throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    context.getWriter().
      append("<div class=\"").append(cssClass).append("\">").
        append("<div class=\"").append(style).append("\" style=\"margin: 0px\">").
          append("<div class=\"TopLeftCornerBoxDecorator\">").
            append("<div class=\"TopRightCornerBoxDecorator\">").
              append("<div class=\"TopCenterBoxDecorator\"><span></span></div>").
            append("</div>").
          append("</div>"). 
          append("<div class=\"MiddleLeftSideBoxDecorator\">").
            append("<div class=\"MiddleRightSideBoxDecorator\">").
              append("<div class=\"DecoratorBackground\">");
              
  }
  
  final  public  void  end() throws Exception {
    WebuiRequestContext context = WebuiRequestContext.getCurrentInstance() ;
    context.getWriter().
              append("</div>").
            append("</div>").
          append("</div>").  
          append("<div class=\"BottomLeftCornerBoxDecorator\">").
            append("<div class=\"BottomRightCornerBoxDecorator\">").
              append("<div class=\"BottomCenterBoxDecorator\"><span></span></div>").
            append("</div>").
          append("</div>").    
        append("</div>").
      append("</div>");
  }
}