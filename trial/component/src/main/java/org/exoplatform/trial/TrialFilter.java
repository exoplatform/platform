package org.exoplatform.trial;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.container.PortalContainer;

public class TrialFilter implements Filter,org.exoplatform.web.filter.Filter {

	public static boolean unlocked;
	public static String calledUrl = "/portal";

	public void init(FilterConfig config) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (TrialFilter.unlocked || isIgnoredRequest(((HttpServletRequest) request).getRequestURI())) {
			chain.doFilter(request, response);
		} else {
			HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
			TrialFilter.calledUrl = "http://"+httpServletRequest.getServerName()+":"+httpServletRequest.getServerPort()+"/"+PortalContainer.getCurrentPortalContainerName();
			if(((HttpServletRequest) request).getRequestURI().endsWith(".js")){
				response.setContentType("text/javascript");
				response.getWriter().println("function readCookie(name) {");
				response.getWriter().println("	var nameEQ = name + '=';");
				response.getWriter().println("	var ca = document.cookie.split(';');");
				response.getWriter().println("	for(var i=0;i < ca.length;i++) {");
				response.getWriter().println("		var c = ca[i];");
				response.getWriter().println("		while (c.charAt(0)==' ') c = c.substring(1,c.length);");
				response.getWriter().println("		if (c.indexOf(nameEQ) == 0){ return c.substring(nameEQ.length,c.length);}");
				response.getWriter().println("	}");
				response.getWriter().println("	return null;");
				response.getWriter().println("}");
				response.getWriter().println("if(!readCookie('plf-lcf')) window.location.href='/trial/lcf/registration.jsp'; ");
				chain.doFilter(request, response);
				return;
			}
			((HttpServletResponse)response).sendRedirect("/trial/lcf/registration.jsp");
		}
	}

	private boolean isIgnoredRequest(String url) {
		return url.contains("UnlockServlet") || url.contains(".gif") || url.contains(".jpg") || url.contains(".png") || url.contains(".gif") || url.contains(".css");
	}

	public void destroy() {
	}

}
