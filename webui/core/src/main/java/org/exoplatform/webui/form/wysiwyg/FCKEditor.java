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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * FCKeditor control class.<br>
 * 
 * It creates the html code for the FCKeditor based on the following things:
 * <ul>
 * <li>browser's capabilities</li>
 * <li>different properties settings managed by the {@link PropertiesLoader}</li>
 * <li>settings from the 'caller', eg. jsp-pages</li>
 * </ul>
 * 
 * @version $Id: FCKeditor.java 1682 2008-03-05 17:27:06Z th-schwarz $
 */
public class FCKEditor {
  
  private final static String DEFAULT_HEIGHT = "300".intern();
  private final static String DEFAULT_WIDTH = "100%".intern();
  private final static String DEFAULT_TOOLBAR = "Basic".intern();  
  private final static String DEFAULT_BASE_PATH = "/portal/fckeditor";
  
  private FCKEditorConfig config;
  private String instanceName;
  private String value;
  private String basePath;
  private HttpServletRequest request;
  
  private String toolbarSet; 
  private String width;
  private String height; 
  private String defaultBasePath; 

  /**
   * Main constructor.<br>
   * All important settings are done here and will be preset by there defaults taken from
   * {@link PropertiesLoader}.
   * 
   * @param request
   *            request object
   * @param instanceName
   *            unique name
   * @param width
   *            width
   * @param height
   *            height
   * @param toolbarSet
   *            toolbarSet name
   */
  public FCKEditor(final HttpServletRequest request, final String instanceName,
      final String width, final String height, final String toolbarSet, final String value,
      final String basePath) {
    this.request = request;
    this.instanceName = instanceName;		
    this.width = width;	
    this.height = height;		
    this.toolbarSet = toolbarSet;		
    this.value = value;		
    //this.basePath = request.getContextPath().concat(basePath);
    this.basePath = basePath;
    config = new FCKEditorConfig();
  }

  /**
   * Just a wrapper to {@link FCKEditor}.
   * 
   * @param request
   *            request object
   * @param instanceName
   *            unique name
   */

  public FCKEditor(final HttpServletRequest request, final String instanceName) {
    this(request, instanceName, DEFAULT_WIDTH,DEFAULT_HEIGHT,DEFAULT_TOOLBAR, null, DEFAULT_BASE_PATH);
  }

  /**
   * Set the unique name of the editor
   * 
   * @param value
   *            name
   */
  public void setInstanceName(final String value) {
    instanceName = value;
  }

  /**
   * Set the initial value to be edited.<br>
   * In HTML code
   * 
   * @param value
   *            value
   */
  public void setValue(final String value) {
    this.value = value;
  }

  /**
   * Set the dir where the FCKeditor files reside on the server.<br>
   * <b>Remarks</b>:<br>
   * Avoid using relative paths. It is preferable to set the base path starting from the root (/).<br>
   * Always finish the path with a slash (/).
   * 
   * @param value
   *            path
   */
  public void setBasePath(final String value) {
    basePath = value;
  }

  /**
   * Set the name of the toolbar to display
   * 
   * @param value
   *            toolbar name
   */
  public void setToolbarSet(final String value) {
    toolbarSet = value;
  }

  /**
   * Set the width of the textarea
   * 
   * @param value
   *            width
   */
  public void setWidth(final String value) {
    width = value;
  }

  /**
   * Set the height of the textarea
   * 
   * @param value
   *            height
   */
  public void setHeight(final String value) {
    height = value;
  }

  /**
   * Get the advanced configuation set.<br>
   * Adding element to this collection you can override the settings specified in the config.js
   * file.
   * 
   * @return configuration collection
   */
  public FCKEditorConfig getConfig() {
    return config;
  }

  /**
   * Set the advanced configuation set.
   * 
   * @param value
   *            configuration collection
   */
  public void setConfig(FCKEditorConfig value) {
    config = value;
  }

  private String escapeXml(String txt) {    
    txt = txt.replaceAll("&", "&#38;");
    txt = txt.replaceAll("<", "&#60;");
    txt = txt.replaceAll(">", "&#62;");
    txt = txt.replaceAll("\"", "&#34;");
    txt = txt.replaceAll("'", "&#39;");
    return txt;
  }

  /**
   * Minimum implementation, see ticket #27 for detailed information.
   */
  public String create() {
    return createHtml();
  }

  @Override
  public String toString() {
    return createHtml();
  }

  /**
   * Generate the HTML Code for the editor. <br>
   * Evalute the browser capabilities and generate the editor if compatible, or a simple textarea
   * otherwise.
   * 
   * @return html code
   */
  public String createHtml() {
    StringBuffer strEditor = new StringBuffer();

    strEditor.append("<div>");
    String encodedValue = escapeXml(value.replaceAll("((\r?\n)+|\t*)", ""));

    if (check(request.getHeader("user-agent"))) {
      strEditor.append(createInputForVariable(instanceName, instanceName, encodedValue));
      // create config html
      String configStr = config.getUrlParams();
      if (configStr != null && configStr.length()>0) {
        configStr = configStr.substring(1);
        strEditor.append(createInputForVariable(null, instanceName.concat("___Config"),
            configStr)); 
      }       

      // create IFrame
      String sLink = basePath.concat("/editor/fckeditor.html?InstanceName=").concat(
          instanceName);
      if (toolbarSet != null && toolbarSet.length() > 0) {
        sLink += "&Toolbar=".concat(toolbarSet); 
      }      
      XHtmlTagTool iframeTag = new XHtmlTagTool("iframe", XHtmlTagTool.SPACE);
      iframeTag.addAttribute("id", instanceName.concat("___Frame"));
      iframeTag.addAttribute("src", sLink);
      iframeTag.addAttribute("width", width);
      iframeTag.addAttribute("height", height);
      iframeTag.addAttribute("frameborder", "no");
      iframeTag.addAttribute("scrolling", "no");
      strEditor.append(iframeTag);

    } else {
      XHtmlTagTool textareaTag = new XHtmlTagTool("textarea", encodedValue);
      textareaTag.addAttribute("name", instanceName);
      textareaTag.addAttribute("rows", "4");
      textareaTag.addAttribute("cols", "40");
      textareaTag.addAttribute("wrap", "virtual");
      textareaTag.addAttribute("style", "width: ".concat(width).concat("; height: ").concat(
          height));
    }
    strEditor.append("</div>");
    return strEditor.toString();
  }

  private String createInputForVariable(final String name, final String id, final String value) {
    XHtmlTagTool tag = new XHtmlTagTool("input");
    //if (Utils.isNotEmpty(id))
    tag.addAttribute("id", id);
    //if (Utils.isNotEmpty(name))
    tag.addAttribute("name", name);
    tag.addAttribute("value", value);
    tag.addAttribute("type", "hidden");
    return tag.toString();
  }

  public boolean check(final String userAgentString) {
    float version;

    // IE 5.5+, check special keys like 'Opera' and 'mac', because there are some
    // other browsers, containing 'MSIE' in there agent string!
    if (userAgentString.indexOf("Opera") < 0 && userAgentString.indexOf("mac") < 0) {
      version = getBrowserVersion(userAgentString, ".*MSIE ([\\d]+.[\\d]+).*");
      if (version != -1f && version >= 5.5f)
        return true;
    }
    // for mozilla only, because all firefox versions are supported
    version = getBrowserVersion(userAgentString, ".*Gecko/([\\d]+).*");
    if (version != -1f && version >= 20030210f)
      return true;

    // Opera 9.5+
    version = getBrowserVersion(userAgentString, "Opera/([\\d]+.[\\d]+).*");
    if (version != -1f && version >= 9.5f)
      return true;
    version = getBrowserVersion(userAgentString, ".*Opera ([\\d]+.[\\d]+)");
    if (version != -1f && version >= 9.5f)
      return true;

    // Safari 3+
    version = getBrowserVersion(userAgentString, ".*AppleWebKit/([\\d]+).*");
    if (version != -1f && version >= 522f)
      return true;

    return false;
  }  

  /**
   * Helper method to get the the browser version from 'userAgent' with the regular expression
   * 'regex'. The first group of the matches has to be the version number!
   * 
   * @param userAgent
   * @param regex
   * @return The browser version, or -1f, if version con't find out.
   */
  private float getBrowserVersion(final String userAgent, final String regex) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(userAgent);
    if (matcher.matches()) {
      try {
        return Float.parseFloat(matcher.group(1));
      } catch (NumberFormatException e) {
        return -1f;
      }
    }
    return -1f;
  }
}
