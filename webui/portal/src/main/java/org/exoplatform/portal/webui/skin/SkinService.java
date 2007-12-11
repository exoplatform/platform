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
package org.exoplatform.portal.webui.skin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by The eXo Platform SARL
 * Author : Tuan Nguyen
 *          tuan.nguyen@exoplatform.com
 * Jan 19, 2007  
 */
public class SkinService {

  private Map<String, SkinConfig>  skinConfigs_ ;
  private HashSet<String>  availableSkins_ ;
  
  private Set<String> themeURLs_ ;
  private Map<String, Set<String>> portletThemes_ ;

  public SkinService() {
    skinConfigs_ = new  HashMap<String, SkinConfig>(20) ;
    availableSkins_ = new HashSet<String>(5) ;
  }

  /**
   * TODO: should return a collection or list
   * This method should return the availables  skin in the service
   * @return
   */
  public Iterator<String> getAvailableSkins() {
    return  availableSkins_.iterator() ;
  }

  /**
   * 
   * @param module
   * @param skinName
   * @param cssPath
   */
  public void addSkin(String module , String skinName, String cssPath) {
    addSkin(module, skinName, cssPath, false) ;
  }

  public void addSkin(String module , String skinName, String cssPath, boolean isPrimary) {
    availableSkins_.add(skinName);
    String key = module + "$" + skinName ;
    SkinConfig skinConfig = skinConfigs_.get(key);
    if(skinConfig == null || skinConfig.isPrimary() == false) skinConfigs_.put(key, new SkinConfig(module, skinName, cssPath, isPrimary));
  }

  public SkinConfig getSkin(String module, String skinName) {
    String key = module + "$" + skinName ;
    if(skinName.length() == 0)  key = module + "$Default" ;
    SkinConfig config = skinConfigs_.get(key);    
    return config ;    
  }
  
  public int size(){ return skinConfigs_.size(); }

  public SkinConfig getSkin(String key) {return skinConfigs_.get(key); }

  public void remove(String key) throws Exception {
    skinConfigs_.remove(key);
  }

  public void remove(String module, String skinName) throws Exception {
    String key = module + "$" + skinName ;
    if(skinName.length() == 0) key = module + "$Default" ;
    skinConfigs_.remove(key);
  }

  public void addThemeURL(String url){
	if(themeURLs_ == null) themeURLs_ = new HashSet<String>();
	themeURLs_.add(url);
  }

  public void addTheme(String categoryName, List<String> themesName){
	if(portletThemes_ == null) portletThemes_ = new HashMap<String, Set<String>>();
	if(!portletThemes_.containsKey(categoryName)) portletThemes_.put(categoryName, new HashSet<String>());
	Set<String> catThemes = portletThemes_.get(categoryName);
	for(String theme : themesName) catThemes.add(theme);
  }

  public void addCategoryTheme(String categoryName){
    if(portletThemes_ == null) portletThemes_ = new HashMap<String, Set<String>>();
	if(!portletThemes_.containsKey(categoryName)) portletThemes_.put(categoryName, new HashSet<String>());
  }
  
  public Set<String> getThemeURLs() { 
	if(themeURLs_ == null) themeURLs_ = new HashSet<String>();
	return themeURLs_; 
  }
  public void setThemeURLs(Set<String> themeURLs_) {this.themeURLs_ = themeURLs_; }

public Map<String, Set<String>> getPortletThemes() { return portletThemes_; }
public void setPortletThemes(Map<String, Set<String>> portletThemes_) {this.portletThemes_ = portletThemes_; }
}