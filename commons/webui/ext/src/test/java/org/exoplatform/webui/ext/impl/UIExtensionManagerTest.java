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
package org.exoplatform.webui.ext.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;
import org.exoplatform.webui.ext.UIExtension;
import org.exoplatform.webui.ext.filter.UIExtensionFilter;
import org.exoplatform.webui.ext.UIExtensionManager;
import org.exoplatform.webui.ext.filter.UIExtensionFilterType;
import org.exoplatform.webui.ext.filter.UIExtensionFilters;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          nicolas.filotto@exoplatform.com
 * May 05, 2009  
 */
public class UIExtensionManagerTest extends TestCase {

  private UIExtensionManagerImpl manager;
  
  private static boolean OK; 
  
  public void setUp() throws Exception {
    StandaloneContainer.setConfigurationURL(Thread.currentThread().getContextClassLoader().getResource("conf/standalone/test-extension-configuration.xml").toString());
    StandaloneContainer container = StandaloneContainer.getInstance();
    manager = (UIExtensionManagerImpl) container.getComponentInstanceOfType(UIExtensionManager.class);
  }
  
  public void testGetUIExtensions() {
    List<UIExtension> extensions = manager.getUIExtensions(MyOwner.class.getName());
    assertEquals(extensions.size(), 36);    
  }
  
  public void testGetUIExtensions2() {
    UIExtension extension1 = new UIExtension();
    extension1.setType("test");
    extension1.setName("b");
    manager.registerUIExtension(extension1);
    UIExtension extension2 = new UIExtension();
    extension2.setType("test");
    extension2.setName("a1");
    manager.registerUIExtension(extension2);
    UIExtension extension3 = new UIExtension();
    extension3.setType("test");
    extension3.setName("a2");
    extension3.setCategory("b");
    manager.registerUIExtension(extension3);
    UIExtension extension4 = new UIExtension();
    extension4.setType("test");
    extension4.setName("a3");
    extension4.setCategory("a");
    manager.registerUIExtension(extension4);
    UIExtension extension5 = new UIExtension();
    extension5.setType("test");
    extension5.setName("a4");
    extension5.setCategory("a");
    extension5.setRank(2);
    manager.registerUIExtension(extension5);
    UIExtension extension6 = new UIExtension();
    extension6.setType("test");
    extension6.setName("a5");
    extension6.setCategory("a");
    extension6.setRank(1);
    manager.registerUIExtension(extension6);
    
    UIExtension[] result = {extension6, extension5, extension4, extension3, extension2, extension1};
    List<UIExtension> extensions = manager.getUIExtensions("test");
    assertEquals(extensions.size(), 6);    
    int i = 0;
    for (UIExtension extension : extensions) {
      assertEquals(extension, result[i++]);      
    }    
  }
  
  public void testGetUIExtension() {
    String extensionName = "Extension null-null";
    UIExtension extension = manager.getUIExtension(MyOwner.class.getName(), extensionName);
    assertEquals(extension.getName(), extensionName);
  }
  
  public void testRegisterUIExtension() {
    String extensionName = "My Custom Extension";
    UIExtension extension = new UIExtension();
    extension.setType(MyOwner.class.getName());
    extension.setName(extensionName);
    manager.registerUIExtension(extension);
    UIExtension extension2 = manager.getUIExtension(MyOwner.class.getName(), extensionName);
    assertEquals(extension, extension2);
  }
  
  public void testAccept() throws Exception {
    Map<String, Object> context = new HashMap<String, Object>();
    testAccept("Extension null-empty", context, true, true);
    testAccept("Extension null-empty", context, false, true);
    testAccept("Extension null-null", context, true, true);
    testAccept("Extension null-null", context, false, true);
    testAccept("Extension null-true", context, true, true);
    testAccept("Extension null-true", context, false, true);
    testAccept("Extension null-false", context, true, true);
    testAccept("Extension null-false", context, false, false);
    testAccept("Extension null-false2", context, true, true);
    testAccept("Extension null-false2", context, false, true);
    testAccept("Extension null-false3", context, true, true);
    testAccept("Extension null-false3", context, false, false);
    testAccept("Extension null-false4", context, true, false);
    testAccept("Extension null-false4", context, false, false);
    testAccept("Extension null-exception", context, true, true);
    testAccept("Extension null-exception", context, false, false);
    testAccept("Extension null-exception2", context, true, true);
    testAccept("Extension null-exception2", context, false, false);
    testAccept("Extension null-exception3", context, true, true);
    testAccept("Extension null-exception3", context, false, true);
    testAccept("Extension null-exception4", context, true, true);
    testAccept("Extension null-exception4", context, false, false);
    testAccept("Extension null-exception5", context, true, false);
    testAccept("Extension null-exception5", context, false, false);
    testAccept("Extension null-exception6", context, true, true);
    testAccept("Extension null-exception6", context, false, true);
    testAccept("Extension null-multiTrue", context, true, true);
    testAccept("Extension null-multiTrue", context, false, true);
    testAccept("Extension null-multiFalse", context, true, true);
    testAccept("Extension null-multiFalse", context, false, false);
    testAccept("Extension null-multiFalse2", context, true, true, Boolean.FALSE);
    testAccept("Extension null-multiFalse2", context, false, true, Boolean.TRUE);
    testAccept("Extension null-multiFalse3", context, true, true, Boolean.FALSE);
    testAccept("Extension null-multiFalse3", context, false, false, Boolean.FALSE);
    testAccept("Extension null-multiFalse4", context, true, false, Boolean.FALSE);
    testAccept("Extension null-multiFalse4", context, false, false, Boolean.FALSE);
    testAccept("Extension null-multiFalse5", context, true, true, Boolean.FALSE);
    testAccept("Extension null-multiFalse5", context, false, false, Boolean.TRUE);
    testAccept("Extension null-multiFalse6", context, true, true);
    testAccept("Extension null-multiFalse6", context, false, false);
    testAccept("Extension null-multiException", context, true, true);
    testAccept("Extension null-multiException", context, false, false);
    testAccept("Extension null-multiException2", context, true, true);
    testAccept("Extension null-multiException2", context, false, false);
    testAccept("Extension null-multiException3", context, true, true, Boolean.FALSE);
    testAccept("Extension null-multiException3", context, false, true, Boolean.TRUE);
    testAccept("Extension null-multiException4", context, true, true, Boolean.FALSE);
    testAccept("Extension null-multiException4", context, false, false, Boolean.FALSE);
    testAccept("Extension null-multiException5", context, true, false, Boolean.FALSE);
    testAccept("Extension null-multiException5", context, false, false, Boolean.FALSE);
    testAccept("Extension null-multiException6", context, true, true, Boolean.FALSE);
    testAccept("Extension null-multiException6", context, false, false, Boolean.TRUE);
    testAccept("Extension null-multiException7", context, true, true);
    testAccept("Extension null-multiException7", context, false, false);
    testAccept("Extension null-multiException8", context, true, true, Boolean.FALSE);
    testAccept("Extension null-multiException8", context, false, true, Boolean.TRUE);
    testAccept("Extension true-null", context, true, true);
    testAccept("Extension true-null", context, false, true);
    testAccept("Extension true-true", context, true, true);
    testAccept("Extension true-true", context, false, true);
    testAccept("Extension true-false", context, true, true);
    testAccept("Extension true-false", context, false, false);
    testAccept("Extension false-null", context, true, true);
    testAccept("Extension false-null", context, false, false);
    testAccept("Extension exception-null", context, true, true);
    testAccept("Extension exception-null", context, false, false);
    testAccept("Extension multiTrue-null", context, true, true);
    testAccept("Extension multiTrue-null", context, false, true);
    testAccept("Extension multiFalse-null", context, true, true);
    testAccept("Extension multiFalse-null", context, false, false);
    testAccept("Extension multiException-null", context, true, true);
    testAccept("Extension multiException-null", context, false, false);
  }
  
  private void testAccept(String extensionName, Map<String, Object> context, boolean checkOnly, boolean expected) throws Exception {
    testAccept(extensionName, context, checkOnly, expected, null);
  }
  
  private void testAccept(String extensionName, Map<String, Object> context, boolean checkOnly, boolean expected, Boolean bOKValue) throws Exception {
    String extensionType = MyOwner.class.getName();
    UIExtension extension = manager.getUIExtension(extensionType, extensionName);
    OK = false;
    assertEquals(expected, manager.accept(extension, context, checkOnly));
    if (bOKValue != null) {
      assertEquals(bOKValue.booleanValue(), OK);
    }
    // check if the result is the same
    OK = false;
    assertEquals(expected, manager.accept(extension, context, checkOnly));
    if (bOKValue != null) {
      assertEquals(bOKValue.booleanValue(), OK);
    }
    // check if the result is the same
    OK = false;
    assertEquals(expected, manager.accept(extension, context, checkOnly));
    if (bOKValue != null) {
      assertEquals(bOKValue.booleanValue(), OK);
    }
  }
  
  public static class MyUIContainer extends UIContainer {
    
  }
  
  public static class MyOwner extends UIContainer {
    
  }
  
  public static class MyUIExtensionComponentEmpty extends UIComponent {
  }
  
  public static class MyUIExtensionComponentNull extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return null;
    }
  }
  
  public static class MyUIExtensionComponentTrue extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilterTests() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterTrue()});
    }    
  }
  
  public static class MyUIExtensionComponentFalse extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterFalse()});
    }    
  }
  
  public static class MyUIExtensionComponentFalse2 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterFalse2()});
    }    
  }
  
  public static class MyUIExtensionComponentFalse3 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterFalse3()});
    }    
  }
  
  public static class MyUIExtensionComponentFalse4 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterFalse4()});
    }    
  }
  
  public static class MyUIExtensionComponentException extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException()});
    }    
  }
  
  public static class MyUIExtensionComponentException2 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException2()});
    }    
  }
  
  public static class MyUIExtensionComponentException3 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException3()});
    }    
  }
  
  public static class MyUIExtensionComponentException4 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException4()});
    }    
  }
  
  public static class MyUIExtensionComponentException5 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException5()});
    }    
  }
  
  public static class MyUIExtensionComponentException6 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException6()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiTrue extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterTrue(), new UIExtensionFilterTrue()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiFalse extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterTrue(), new UIExtensionFilterFalse()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiFalse2 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterFalse2(), new UIExtensionFilterTrue()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiFalse3 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterFalse3(), new UIExtensionFilterTrue()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiFalse4 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterFalse4(), new UIExtensionFilterTrue()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiFalse5 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterFalse(), new UIExtensionFilterTrue()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiFalse6 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterFalse(), new UIExtensionFilterTrue2()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiException extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterTrue(), new UIExtensionFilterException()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiException2 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterTrue(), new UIExtensionFilterException2()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiException3 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException3(), new UIExtensionFilterTrue()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiException4 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException4(), new UIExtensionFilterTrue()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiException5 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException5(), new UIExtensionFilterTrue()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiException6 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException(), new UIExtensionFilterTrue()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiException7 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException(), new UIExtensionFilterTrue2()});
    }    
  }
  
  public static class MyUIExtensionComponentMultiException8 extends UIComponent {
    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
      return Arrays.asList(new UIExtensionFilter[]{new UIExtensionFilterException6(), new UIExtensionFilterTrue()});
    }    
  }
  
  public static class UIExtensionFilterTrue implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) {
      OK = true;
      return true;
    }

    public void onDeny(Map<String, Object> context) throws Exception {
    }

    public UIExtensionFilterType getType() {
      return null;
    }
  }

  public static class UIExtensionFilterTrue2 implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) {
      return true;
    }

    public void onDeny(Map<String, Object> context) throws Exception {
    }

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.MANDATORY;
    }
  }

  public static class UIExtensionFilterFalse implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) {
      return false;
    }    

    public void onDeny(Map<String, Object> context) throws Exception {
    }    

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.REQUIRED;
    }
  }

  public static class UIExtensionFilterFalse2 implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) {
      return false;
    }    

    public void onDeny(Map<String, Object> context) throws Exception {
    }    

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.OPTIONAL;
    }
  }

  public static class UIExtensionFilterFalse3 implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) {
      return false;
    }    

    public void onDeny(Map<String, Object> context) throws Exception {
    }    

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.REQUISITE;
    }
  }

  public static class UIExtensionFilterFalse4 implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) {
      return false;
    }    

    public void onDeny(Map<String, Object> context) throws Exception {
    }    

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.MANDATORY;
    }
  }

  public static class UIExtensionFilterException implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) throws Exception {
      throw new Exception("My exception");
    }    

    public void onDeny(Map<String, Object> context) throws Exception {
    }    

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.REQUIRED;
    }
  }

  public static class UIExtensionFilterException2 implements UIExtensionFilter {

    private UIExtensionFilterException2() {}
    
    public boolean accept(Map<String, Object> context) throws Exception {
      throw new Exception("My exception 2");
    }    

    public void onDeny(Map<String, Object> context) throws Exception {
    }    

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.REQUIRED;
    }
  }
  
  public static class UIExtensionFilterException3 implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) throws Exception {
      throw new Exception("My exception 3");
    }    

    public void onDeny(Map<String, Object> context) throws Exception {
    }    

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.OPTIONAL;
    }
  }  
  
  public static class UIExtensionFilterException4 implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) throws Exception {
      throw new Exception("My exception 4");
    }    

    public void onDeny(Map<String, Object> context) throws Exception {
    }    

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.REQUISITE;
    }
  }  
  
  public static class UIExtensionFilterException5 implements UIExtensionFilter {

    public boolean accept(Map<String, Object> context) throws Exception {
      throw new Exception("My exception 5");
    }    

    public void onDeny(Map<String, Object> context) throws Exception {
    }    

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.MANDATORY;
    }
  }  
  
  public static class UIExtensionFilterException6 implements UIExtensionFilter {

    private UIExtensionFilterException6() {}
    
    public boolean accept(Map<String, Object> context) throws Exception {
      throw new Exception("My exception 6");
    }    

    public void onDeny(Map<String, Object> context) throws Exception {
    }    

    public UIExtensionFilterType getType() {
      return UIExtensionFilterType.OPTIONAL;
    }
  }  
}
