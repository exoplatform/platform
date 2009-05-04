package org.exoplatform.portal.portlet;

import org.exoplatform.services.portletcontainer.PortletContainerException;

public interface PortletExceptionListener {
	public void handle(PortletContainerException e);
}
