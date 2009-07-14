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
package org.exoplatform.portal.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.services.portletcontainer.pci.model.ExoPortletPreferences;
/**
 * Jun 9, 2004 
 * @author: Tuan Nguyen
 * @email:   tuan08@users.sourceforge.net
 * @version: $Id: ExoPortletPreferences.java,v 1.1 2004/07/13 02:31:13 tuan08 Exp $
 */
public class PortletPreferences  {
 
  private String windowId;
  private String ownerType;
  private String ownerId;
  
  private String preferencesValidator ;
  
  private ArrayList<Preference> preferences ;
  
  public PortletPreferences() { }
  
  public PortletPreferences(ExoPortletPreferences prefs) {
    preferencesValidator = prefs.getPreferencesValidator() ;
    if(prefs.size() < 1)  return;
    preferences = new ArrayList<Preference>(prefs.size() + 2) ;
    Iterator i = prefs.values().iterator() ;
    while(i.hasNext()) {
      org.exoplatform.services.portletcontainer.pci.model.Preference pref = 
        (org.exoplatform.services.portletcontainer.pci.model.Preference )i.next() ;
      Preference tmp = new Preference() ;
      
      tmp.setName(pref.getName()) ;
      tmp.setReadOnly(pref.isReadOnly()) ;
      List values = pref.getValues() ;
      if(values == null) continue;
      for(int j = 0; j < values.size(); j++) {
        if(values.get(j) == null) continue;
        tmp.addValue((String)values.get(j)) ;
      }
      preferences.add(tmp) ;
    }    
  }
  
  public String getWindowId() { return windowId ; }
  public void   setWindowId(String s) { windowId = s ;}
  
  public String getPreferencesValidator() { return preferencesValidator; }
  public void setPreferencesValidator(String validator) { preferencesValidator = validator; }
  
  public ArrayList<Preference> getPreferences() { return preferences ; }
  public void setPreferences(ArrayList<Preference> l) { preferences = l ; }
  
  public String getOwnerId() { return ownerId; }
  public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

  public String getOwnerType() { return ownerType; }
  public void setOwnerType(String ownerType) { this.ownerType = ownerType; }
  
  public ExoPortletPreferences toExoPortletPreferences() {
    ExoPortletPreferences prefs = new ExoPortletPreferences() ;
    prefs.setPreferencesValidator(preferencesValidator) ;
    if(preferences == null) return prefs;
    for(int i = 0 ; i < preferences.size(); i++) {
      Preference pref = preferences.get(i) ;
      org.exoplatform.services.portletcontainer.pci.model.Preference tmp = 
        new org.exoplatform.services.portletcontainer.pci.model.Preference() ;
      tmp.setName(pref.getName()) ;
      tmp.setReadOnly(pref.isReadOnly()) ;
      List values = pref.getValues() ;
      if(values != null) {
        for(int j = 0; j < values.size(); j++) {
          tmp.addValue((String)values.get(j)) ;
        }
      }
      prefs.addPreference(tmp) ;
    }
    return  prefs ;
  }  
  
  static public class PortletPreferencesSet {
    private ArrayList<PortletPreferences> portlets ;
    
    public ArrayList<PortletPreferences> getPortlets() { return portlets ; }
    public void setPortlets(ArrayList<PortletPreferences> list) { portlets = list ; }
  }
}
