/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.webui.ext.filter;

/**
 * All the existing types of filters
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          nicolas.filotto@exoplatform.com
 * 14 mai 2009  
 */
public enum UIExtensionFilterType {
  
  /**
   * This type of filter will be used to know if the
   * action related to the extension can be launched
   * and to know if the component related to the 
   * extension can be added to the webui tree
   * The filter is required to launch the action and
   * to add the component related to the extension
   * to the webui tree.
   * If it succeeds, we will check the other filters.
   * If it fails, we will stop.
   */
  MANDATORY(true, true, true),
  /**
   * This type of filter will be used to know if the
   * action related to the extension can be launched.
   * The filter is required to launch the action.
   * to the webui tree.
   * If it succeeds, we will check the other filters.
   * If it fails, we will stop.
   */
  REQUISITE(false, true, true),
  /**
   * This type of filter will only be used to know if the
   * action related to the extension can be launched.
   * The filter is required to launch the action.
   * If it succeeds or fails, we will check the other filters.
   * It can be used to add warnings
   */
  REQUIRED(false, false, true),
  /**
   * This type of filter will only be used to know if the
   * action related to the extension can be launched.
   * The filter is not required to launch the action.
   * If it succeeds or fails, we will check the other filters.
   * It can be used for auditing purpose
   */
  OPTIONAL(false, false, false);
  
  /**
   * Indicates if the filter allows to display the extension if it fails
   */
  private final boolean showExtensionOnlyIfOK;
 
  /**
   * Indicates if the other filters can be checked if it fails
   */
  private final boolean checkOtherFiltersOnlyIfOK;
 
  /**
   * Indicates if the filter allows to continue if it fails
   */
 private final boolean acceptOnlyIfOK;
 
 private UIExtensionFilterType(boolean showExtensionOnlyIfOK, boolean checkOtherFiltersOnlyIfOK, boolean acceptOnlyIfOK) {
   this.showExtensionOnlyIfOK = showExtensionOnlyIfOK;
   this.checkOtherFiltersOnlyIfOK = checkOtherFiltersOnlyIfOK;
   this.acceptOnlyIfOK = acceptOnlyIfOK;
 }

 /**
  * Indicates if the filter allows to display the extension if it fails
  */
  public boolean showExtensionOnlyIfOK() {
    return showExtensionOnlyIfOK;
  }

  /**
   * Indicates if the other filters can be checked if it fails
   */
  public boolean checkOtherFiltersOnlyIfOK() {
    return checkOtherFiltersOnlyIfOK;
  }

  /**
   * Indicates if the filter allows to continue if it fails
   */
  public boolean acceptOnlyIfOK() {
    return acceptOnlyIfOK;
  } 
}
