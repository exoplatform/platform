/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component.lifecycle;

import org.exoplatform.webui.application.RequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Aug 23, 2006  
 */
public class Decorator {
  //<%decorator.start(.., ..)%>
  final  public  void  start(String cssClass,  String style) throws Exception {
    RequestContext context = RequestContext.getCurrentInstance() ;
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
    RequestContext context = RequestContext.getCurrentInstance() ;
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