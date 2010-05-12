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
package org.exoplatform.webui.ext;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.ext.filter.UIExtensionFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilterType;
import org.exoplatform.webui.ext.filter.UIExtensionFilters;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          nicolas.filotto@exoplatform.com
 * May 04, 2009  
 */
public class UIExtensionTest extends TestCase {

  public void testCompareTo() {
    UIExtension extension1 = new UIExtension();
    extension1.setName("b");
    UIExtension extension1bis = new UIExtension();
    extension1bis.setName("b");
    UIExtension extension2 = new UIExtension();
    extension2.setName("a");
    UIExtension extension3 = new UIExtension();
    extension3.setName("a");
    extension3.setCategory("b");
    UIExtension extension3bis = new UIExtension();
    extension3bis.setName("a");
    extension3bis.setCategory("b");
    UIExtension extension4 = new UIExtension();
    extension4.setName("a");
    extension4.setCategory("a");
    UIExtension extension5 = new UIExtension();
    extension5.setName("a");
    extension5.setCategory("a");
    extension5.setRank(2);
    UIExtension extension5bis = new UIExtension();
    extension5bis.setName("a");
    extension5bis.setCategory("a");
    extension5bis.setRank(2);
    UIExtension extension6 = new UIExtension();
    extension6.setName("a");
    extension6.setCategory("a");
    extension6.setRank(1);
    
    UIExtension[] extensions = {extension1, extension2, extension3, extension4, extension5, extension6};
    Arrays.sort(extensions);
    UIExtension[] result = {extension6, extension5, extension4, extension3, extension2, extension1};
    assertTrue(Arrays.equals(extensions, result));
    assertTrue(extension1.compareTo(extension2) > 0);
    assertTrue(extension2.compareTo(extension1) < 0);
    assertTrue(extension1.compareTo(extension3) > 0);
    assertTrue(extension3.compareTo(extension1) < 0);
    assertTrue(extension1.compareTo(extension5) > 0);
    assertTrue(extension5.compareTo(extension1) < 0);
    assertTrue(extension1.compareTo(extension1bis) == 0);
    assertTrue(extension1bis.compareTo(extension1) == 0);
    assertTrue(extension3.compareTo(extension4) > 0);
    assertTrue(extension4.compareTo(extension3) < 0);
    assertTrue(extension3.compareTo(extension5) > 0);
    assertTrue(extension5.compareTo(extension3) < 0);
    assertTrue(extension3.compareTo(extension3bis) == 0);
    assertTrue(extension3bis.compareTo(extension3) == 0);
    assertTrue(extension5.compareTo(extension6) > 0);
    assertTrue(extension6.compareTo(extension5) < 0);
    assertTrue(extension5.compareTo(extension5bis) == 0);
    assertTrue(extension5bis.compareTo(extension5) == 0);    
  }
  
  public void testGetComponent() {
    UIExtension extension = new UIExtension();
    extension.setComponent("an.unknon.ClassName");
    try {
      extension.getComponent();
      assertTrue("Should throw an exception", true);
    } catch (IllegalArgumentException e) {
      // do nothing
    }
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest");
    try {
      extension.getComponent();
      assertTrue("Should throw an exception", true);
    } catch (ClassCastException e) {
      // do nothing
    }
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent");
    extension.getComponent();    
  }
  
  public void testComponentFiltersNIsEnable() {
    UIExtension extension = new UIExtension();
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent1");
    assertEquals(null, extension.getComponentFilters());
    assertEquals(true, extension.isEnable());
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent2");
    assertEquals(1, extension.getComponentFilters().size());
    assertEquals(true, extension.isEnable());
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent3");
    assertEquals(2, extension.getComponentFilters().size());
    assertEquals(true, extension.isEnable());
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent4");
    assertEquals(null, extension.getComponentFilters());
    assertEquals(false, extension.isEnable());
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent5");
    assertEquals(null, extension.getComponentFilters());
    assertEquals(false, extension.isEnable());
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent6");
    assertEquals(null, extension.getComponentFilters());
    assertEquals(false, extension.isEnable());
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent7");
    assertEquals(null, extension.getComponentFilters());
    assertEquals(false, extension.isEnable());
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent8");
    assertEquals(null, extension.getComponentFilters());
    assertEquals(false, extension.isEnable());    
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent9");
    assertEquals(null, extension.getComponentFilters());
    assertEquals(false, extension.isEnable());
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUIExtensionComponent10");
    assertEquals(null, extension.getComponentFilters());
    assertEquals(false, extension.isEnable());
  }
  
  public void testAnnotationInheritance() {
    UIExtension extension = new UIExtension();
    extension.setComponent("org.exoplatform.webui.ext.UIExtensionTest$MyTestUISubExtensionComponent");
    assertEquals(1, extension.getComponentFilters().size());
    assertEquals(true, extension.isEnable());
  }
  
  public static class MyTestUISubExtensionComponent extends MyTestUISuperExtensionComponent {
  }
  
  public static abstract class MyTestUISuperExtensionComponent extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new MyTestUIExtensionFilter1()});
    }    
  }
  
  public static class MyTestUIExtensionComponent extends UIComponent {
  }
  
  public static class MyTestUIExtensionComponent1 extends UIComponent {
  }
  
  public static class MyTestUIExtensionComponent2 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new MyTestUIExtensionFilter1()});
    }    
  }
  
  public static class MyTestUIExtensionComponent3 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new MyTestUIExtensionFilter1(), new MyTestUIExtensionFilter1()});
    }    
  }
  
  public static class MyTestUIExtensionComponent4 extends UIComponent {
    private MyTestUIExtensionComponent4(){}    
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new MyTestUIExtensionFilter1()});
    }    
  }
  
  public static class MyTestUIExtensionComponent5 extends UIComponent {
    @SuppressWarnings("unused")
    @UIExtensionFilters
    private List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new MyTestUIExtensionFilter1()});
    }    
  }
  
  public static class MyTestUIExtensionComponent6 extends UIComponent {    
    @UIExtensionFilters
    public void dummy() {
    }    
  }
  
  public static class MyTestUIExtensionComponent7 extends UIComponent {    
    @UIExtensionFilters
    public String[] getValues() {
      return new String[]{"a"};
    }    
  }  
  
  public static class MyTestUIExtensionComponent8 extends UIComponent {    
    @UIExtensionFilters
    public List<String> getValues() {
      return Arrays.asList(new String[]{"a"});
    }    
  }  
 
  public static class MyTestUIExtensionComponent9 extends UIComponent {    
    @UIExtensionFilters
    public List<UIExtensionFilter> getValues(String arg) {
      return Arrays.asList(new UIExtensionFilter[]{new MyTestUIExtensionFilter1()});
    }    
  } 
  
  public static class MyTestUIExtensionComponent10 extends UIComponent {    
    @UIExtensionFilters
    public List<? extends UIExtensionFilter> getValues() {
      return Arrays.asList(new UIExtensionFilter[]{new MyTestUIExtensionFilter1()});
    }    
  } 
  
  public static class MyTestUIExtensionFilter1 implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) throws Exception {
      return false;
    }

    public UIExtensionFilterType getType() {
      return null;
    }

    public void onDeny(Map<String, Object> context) throws Exception {
    }    
  }
}
