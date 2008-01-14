package org.exoplatform.portal.webui.javascript;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.web.application.javascript.JavascriptConfigService;

public class JavascriptServlet extends HttpServlet {

  public void destroy() {
  }

  public ServletConfig getServletConfig() {
    return null;
  }

  public String getServletInfo() {
    return null;
  }

  public void init(ServletConfig arg0) throws ServletException {
  }

  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    JavascriptConfigService service = (JavascriptConfigService) ExoContainerContext
        .getCurrentContainer().getComponentInstanceOfType(
            JavascriptConfigService.class);
    response.setContentType("application/x-javascript");
    PrintWriter writer = response.getWriter();
    writer.print(service.getMergedJavascript());
  }

}
