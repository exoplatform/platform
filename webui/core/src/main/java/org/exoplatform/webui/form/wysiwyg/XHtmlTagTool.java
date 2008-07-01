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
 * Tool to construct a XHTML-tag.<br>
 * <br>
 * Usage:
 * <pre>
 * XHtmlTagTool tag = XHtmlTagTool(&quot;a&quot;, &quot;link&quot;);
 * tag.addAttribute(&quot;href&quot;, &quot;http://google.com&quot;);
 * tag.toString();	: &lt;a href=&quot;http://google.com&quot;&gt;link&lt;/a&gt;
 * </pre>
 * 
 * Hint:
 * <ul>
 * <li>Attributes are not ordered.</li>
 * <li>If your tag shouldn't have a value but the tag has to close with '&lt;/[tagname]&gt;', set
 * the value to {@link XHtmlTagTool#SPACE}.</li>
 * </ul>
 * 
 * @version $Id: XHtmlTagTool.java 1719 2008-03-18 11:08:52Z mosipov $
 */
public class XHtmlTagTool {

	/** Name of the tag. */
	private String name;

	/** Container for the attributes. */
	private Map<String, String> attributes = new HashMap<String, String>();

	/** Value of the tag. */
	private String value = null;
	
	/** Indicator to uses non self-closing tag. */
	public static final String SPACE = " ";

	public XHtmlTagTool(final String name, final String value) {		
		this.name = name;
		this.value = value;
	}

	public XHtmlTagTool(final String name) {
		this(name, null);
	}

	/**
	 * Setter for the value of the tag.
	 * 
	 * @param value
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Adds an attribute to the tag.
	 * 
	 * @param key
	 * @param value
	 * @throws IllegalArgumentException if 'key' is empty.
	 */
	public void addAttribute(final String key, final String value) {		
		attributes.put(key, value);
	}

	/**
	 * Constructs the tag.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer tag = new StringBuffer();

		// open tag
		tag.append("<").append(name);

		// add attributes
		for (String key : attributes.keySet()) {
			String val = attributes.get(key);
			tag.append(' ').append(key).append('=').append('\"').append(val).append('\"');
		}

		// close the tag
		if (value != null && value.length() > 0) {
			tag.append(">").append(value).append("</").append(name).append('>');
		} else
			tag.append(" />");

		return tag.toString();
	}

	@Override
	public boolean equals(Object obj) {
		try {
			XHtmlTagTool tag = (XHtmlTagTool) obj;
			return value.equals(tag.value) && name.equals(tag.name)
					&& attributes.equals(tag.attributes);
		} catch (ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		
		return name.hashCode() + value.hashCode() + attributes.hashCode();
	}
}
