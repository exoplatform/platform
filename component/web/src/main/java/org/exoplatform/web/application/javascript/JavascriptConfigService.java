/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.web.application.javascript;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;

public class JavascriptConfigService {

  private Collection<String> availableScripts_;
  private Collection<String> availableScriptsPaths_;

  private String mergedJavascript = "";
  
  private ByteArrayOutputStream jsStream_ = null;
  
  public JavascriptConfigService() {
    availableScripts_ = new ArrayList<String>();
    availableScriptsPaths_ = new ArrayList<String>();
  }

  /**
   * return a collection  list This method should return the
   * availables scripts in the service
   * 
   * @return
   */
  public Collection<String> getAvailableScripts() {
    return availableScripts_;
  }
  
  public Collection<String> getAvailableScriptsPaths() {
    return availableScriptsPaths_;
  }  

  /**
   * 
   * @param module
   * @param skinName
   * @param cssPath
   */
  public void addJavascript(String module, String scriptPath, ServletContext scontext) {
    String servletContextName = scontext.getServletContextName();
    availableScripts_.add(module);
    availableScriptsPaths_.add("/" + servletContextName + scriptPath);
    StringBuffer sB = new StringBuffer();
    String line = ""; 
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(scontext
          .getResourceAsStream(scriptPath)));
      try {
        while ((line = reader.readLine()) != null) {
          sB.append(line + "\n");
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      } finally {
        try {
          reader.close();
        } catch (Exception ex) {
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    sB.append("\n");
    mergedJavascript = mergedJavascript.concat(sB.toString());
  }
  
  public byte[] getMergedJavascript() {
    if(jsStream_ == null) {
      jsStream_ = new ByteArrayOutputStream();
      ByteArrayInputStream input = new ByteArrayInputStream(mergedJavascript.getBytes());
      JSMin jsMin = new JSMin(input,jsStream_);
      try {
        jsMin.jsmin();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return jsStream_.toByteArray();
  }
  
  public boolean isModuleLoaded(CharSequence module) {
    return getAvailableScripts().contains(module);
  }
  
}