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
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;

public class JavascriptConfigService {

  private HashSet<String> availableScripts_;

  private String mergedJavascript = "";
  
  public JavascriptConfigService() {
    availableScripts_ = new HashSet<String>(5);
  }

  /**
   * TODO: should return a collection or list This method should return the
   * availables skin in the service
   * 
   * @return
   */
  public Iterator<String> getAvailableScripts() {
    return availableScripts_.iterator();
  }

  /**
   * 
   * @param module
   * @param skinName
   * @param cssPath
   */
  public void addJavascript(String module, String scriptPath, ServletContext scontext) {
    availableScripts_.add(module);
    
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
  
  public String getMergedJavascript() {
    return mergedJavascript;
  }
  
  public boolean isModuleLoaded(CharSequence module) {
    return availableScripts_.contains(module);
  }
  
  public String getJavascriptMergedURL() {
    return "/portal/javascript/merged.js";
  }

}