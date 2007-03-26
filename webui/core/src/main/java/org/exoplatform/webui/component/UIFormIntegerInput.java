/***************************************************************************
 * Copyright 2001-2003 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.component;

import java.io.Writer;

import org.exoplatform.webui.application.WebuiRequestContext;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan08@users.sourceforge.net
 * Jun 6, 2006
 */
public class UIFormIntegerInput extends UIFormInputBase<Integer> {

  private Integer value ;

  public UIFormIntegerInput(String name, String bindingField, Integer value) {
    super(name, bindingField, Integer.class);
    this.value = value ;
  }

  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    value = Integer.parseInt(String.valueOf(context.getRequestParameter(getName())));
  }

  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ;
    w.append("<input class='integer' name='").append(getName());
    w.append("' value='").append(value.toString()).append("'/>") ;
  }
}
