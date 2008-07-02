/*
 * FCKeditor - The text editor for Internet - http://www.fckeditor.net
 * Copyright (C) 2003-2008 Frederico Caldeira Knabben
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 */

package org.exoplatform.webui.form.wysiwyg;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the configuration settings for the FCKEditor.<br>
 * Adding element to this collection you can override the settings specified in
 * the config.js file.
 * 
 * @version $Id: FCKeditorConfig.java 1905 2008-04-10 15:32:00Z th-schwarz $
 */
public class FCKEditorConfig extends HashMap<String, String> {

  private static final long serialVersionUID = -4831190504944866644L;  

  /**
   * Initialize the configuration collection
   */
  public FCKEditorConfig() {
    super();
  }

  /**
   * Generate the url parameter sequence used to pass this configuration to
   * the editor.
   * 
   * @return html endocode sequence of configuration parameters
   */
  public String getUrlParams() {
    StringBuffer osParams = new StringBuffer();    
    for (Map.Entry<String, String> entry : this.entrySet()) {				
      osParams.append("&");
      osParams.append(encodeConfig(entry.getKey()));
      osParams.append("=");
      osParams.append(encodeConfig(entry.getValue()));				
    }    
    return osParams.toString();
  }

  private String encodeConfig(String s) {
    s=s.replaceAll("&","%26");
    s=s.replaceAll("=","%3D");
    s=s.replaceAll("\"","%22");
    return s;
  }
}
