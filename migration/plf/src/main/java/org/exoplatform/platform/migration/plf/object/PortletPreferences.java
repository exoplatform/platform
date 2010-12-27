/**
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.platform.migration.plf.object;

import java.util.ArrayList;
import java.util.ListIterator;

public class PortletPreferences {

  private ArrayList<Preference> preferences;

  public PortletPreferences() {}

  public ArrayList<Preference> getPreferences() {
    return preferences;
  }

  public void setPreferences(ArrayList<Preference> l) {
    preferences = l;
  }

  public Preference getPreference(String name) {
    if (preferences != null) {
      for (Preference pref : preferences) {
        if (pref.getName().equals(name)) {
          return pref;
        }
      }
    }
    return null;
  }

  public void setPreference(Preference pref) {
    if (preferences == null) {
      preferences = new ArrayList<Preference>();
    }
    for (ListIterator<Preference> i = preferences.listIterator(); i.hasNext();) {
      Preference preference = i.next();
      if (preference.getName().equals(pref.getName())) {
        i.set(pref);
        return;
      }
    }
    preferences.add(pref);
  }

  static public class PortletPreferencesSet {
    private ArrayList<PortletPreferences> portlets;

    public ArrayList<PortletPreferences> getPortlets() {
      return portlets;
    }

    public void setPortlets(ArrayList<PortletPreferences> list) {
      portlets = list;
    }
  }
}
