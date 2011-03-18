package org.exoplatform.trial;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PingBackServlet extends HttpServlet {

  private static final long serialVersionUID = 6467955354840693802L;

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (isConnectedToInternet()) {
      Utils.loopfuseFormDisplayed = true;
      Utils.writePingBackFormDisplayed();
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  public static boolean isConnectedToInternet() {
    // computes the Platform server URL, format http://server/
    String pingServerURL = Utils.pingBackUrl.substring(0,
        Utils.pingBackUrl.indexOf("/", "http://url".length()));
    try {
      URL url = new URL(pingServerURL);
      HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
      urlConn.connect();
      return (HttpURLConnection.HTTP_OK == urlConn.getResponseCode());
    } catch (MalformedURLException e) {
      System.err.println("LeadCapture : Error creating HTTP connection to the server : " + pingServerURL);
    } catch (IOException e) {
      System.err.println("LeadCapture : Error creating HTTP connection to the server : " + pingServerURL);
    }
    return false;
  }
}
