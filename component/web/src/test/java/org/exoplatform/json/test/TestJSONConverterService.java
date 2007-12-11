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
package org.exoplatform.json.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.json.BeanToJSONPlugin;
import org.exoplatform.json.JSONMap;
import org.exoplatform.json.JSONService;
import org.exoplatform.json.MapToJSONPlugin;
import org.exoplatform.test.BasicTestCase;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Mar 26, 2007  
 */
public class TestJSONConverterService extends BasicTestCase {
  
  private JSONService service_;
  
  public TestJSONConverterService(String name){
    super(name);
  }

  public void setUp() throws Exception {
    if(service_ != null) return;
    service_ = new JSONService() ;
  }
  
  public void teaDown() throws Exception {    
  }
  
  public void testConvert() throws Exception {
    Student bean  = new Student("Thuy", 24, true);
    bean.setScores(new int[]{34, 78, 56, 43, 90});
    bean.addAccount(new Account("ammi", "12'34"));
    bean.addAccount(new Account("sara", "3454\"3mf"));
    bean.addAccount(new Account("bim", "dsfd4"));
    bean.setSchools(new String[]{"school1", "school2"});
    
    service_.unregister(MapToJSONPlugin.class);
    
    System.out.println("\n\n");
    StringBuilder reflectJSONBuilder  = new StringBuilder();
    service_.toJSONScript(bean, reflectJSONBuilder, 0);
    System.out.println(reflectJSONBuilder);
    System.out.println("\n\n");
    
    service_.register(MapToJSONPlugin.class, new MapToJSONPlugin());
    System.out.println("\n\n MapPlugin ===> ");
    StringBuilder mapJSONBuilder  = new StringBuilder();
    service_.toJSONScript(bean, mapJSONBuilder, 0);
    System.out.println(mapJSONBuilder);
    System.out.println("\n\n");
    
    
    service_.register(Account.class, new AccountConverter());
    System.out.println("\n\n Custom Plugin ===> ");
    StringBuilder customJSONBuilder  = new StringBuilder();
    service_.toJSONScript(bean, customJSONBuilder, 0);
    System.out.println(customJSONBuilder);
    System.out.println("\n\n");
    
    List<Account> list = bean.getAccounts();
    System.out.println("\n\n arrays format ===> ");
    StringBuilder arrayJSONBuilder  = new StringBuilder();
    service_.toJSONScript(list, arrayJSONBuilder, 0);
    System.out.println(arrayJSONBuilder);
    System.out.println("\n\n");
    
    assertEquals(reflectJSONBuilder.toString(), customJSONBuilder.toString());
  }
  
  private class AccountConverter extends BeanToJSONPlugin<Account> {

    public void toJSONScript(Account account, StringBuilder builder, int indentLevel) throws Exception {
      appendIndentation(builder, indentLevel);
      builder.append('{').append('\n');
      
      String charValue = account.getUsername();
      charValue = charValue.replace("\'", "\\\\\'");
      charValue = charValue.replace("\"", "\\\"");
      
      appendIndentation(builder, indentLevel+1);
      builder.append('\'').append("username").append('\'').append(':').append(' ');
      builder.append('\'').append(charValue).append('\'').append(',').append('\n');
      
      charValue = account.getPassword();
      charValue = charValue.replace("\'", "\\\\\'");
      charValue = charValue.replace("\"", "\\\"");
      
      appendIndentation(builder, indentLevel+1);
      builder.append('\'').append("password").append('\'').append(':').append(' ');
      builder.append('\'').append(charValue).append('\'').append(',').append('\n');
      
      builder.deleteCharAt(builder.length()-2);
      builder.append('\n');
      appendIndentation(builder, indentLevel);
      builder.append('}'); 
    }
    
  }
  
  public class Student {
    
    private String name;
    private int[] scores;
    private int index;
    private boolean isPass;
    private String [] schools;
    private List<Account> accounts;
    private Double [] percents;
    private char [] chars = {'\'', '\"', 'k', 'j'};
    
    public Student(String name, int index, boolean isPass){
      this.name = name;
      this.index = index;
      this.isPass = isPass;
      accounts = new ArrayList<Account>();
      percents = new Double[]{23.0, 45.9};
    }
    
    public boolean isPass() { return isPass; }
    public void setPass(boolean isPass) {this.isPass = isPass; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int[] getScores() { return scores; }
    public void setScores(int[] scores) { this.scores = scores; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    
    public List<Account> getAccounts() { return accounts; }

    public void addAccount(Account account) { this.accounts.add(account); }

    public String[] getSchools() {return schools; }
    public void setSchools(String[] schools) { this.schools = schools; }
    
  }
  
  public class Account implements JSONMap {

    String username;
    String password;
    
    public Account(String user, String pass){
      username = user;
      password = pass;
    }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public Map<String, Object> getJSONMap() {
      Map<String, Object> map = new HashMap<String, Object>();
      map.put("username", username);
      map.put("password", password);
      return map;
    }

    public String getPassword() { return password; }
    
  }
}
