package org.exoplatform.platform.common.account.setup.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.commons.info.MissingProductInformationException;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 */
public  class PingBackServlet extends HttpServlet {

    private static final Log LOG = ExoLogger.getExoLogger(PingBackServlet.class);

    private static final long serialVersionUID = 6467955354840693802L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PingBackService pingBackService = PortalContainer.getInstance().getComponentInstanceOfType(PingBackService.class);
        if (pingBackService.isConnectedToInternet()) {
            try {
                pingBackService.writePingBackFormDisplayed(true);
            } catch (MissingProductInformationException e) {
                LOG.error("Product Information not found", e);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}

