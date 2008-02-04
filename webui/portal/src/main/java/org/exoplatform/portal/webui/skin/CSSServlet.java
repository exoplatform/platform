package org.exoplatform.portal.webui.skin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.ExoContainerContext;

public class CSSServlet extends HttpServlet {

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
    SkinService service = (SkinService) ExoContainerContext
        .getCurrentContainer().getComponentInstanceOfType(
            SkinService.class);
    response.setContentType("text/css");
    String css = service.getMergedCSS(request.getRequestURI());
    PrintWriter writer = response.getWriter();
    writer.print(css);
  }

}