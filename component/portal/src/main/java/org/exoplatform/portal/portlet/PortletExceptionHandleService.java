package org.exoplatform.portal.portlet;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.services.portletcontainer.PortletContainerException;

public class PortletExceptionHandleService {

  private List<PortletExceptionListener> listeners;

  public void initListener(ComponentPlugin listener) throws Exception { 
    if(listener instanceof PortletExceptionListener) {
      if(listeners == null ) listeners = new ArrayList<PortletExceptionListener>();
      listeners.add((PortletExceptionListener) listener);
    }
    
  }

  public void handle(PortletContainerException ex) {
    for (PortletExceptionListener listener : listeners) {
      listener.handle(ex);
    }
  }
}
