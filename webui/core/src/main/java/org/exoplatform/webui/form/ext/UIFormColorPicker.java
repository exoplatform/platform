/**
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
 **/
package org.exoplatform.webui.form.ext;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.form.UIForm;
import org.exoplatform.webui.form.UIFormInput;
import org.exoplatform.webui.form.UIFormInputBase;
import org.exoplatform.webui.form.ext.UIFormColorPicker.Colors.Color;

/**
 * Created by The eXo Platform SAS
 * Author : Pham Tuan
 *          tuan.pham@exoplatform.com
 * Feb 29, 2008  
 */
public class UIFormColorPicker extends UIFormInputBase<String>  {

  /**
   * The size of the list (number of select options)
   */
  private int items_ = 10 ;
  /**
   * The list of options
   */
  //private List<SelectItemOption<String>> options_ ;

  /**
   * The javascript expression executed when an onChange event fires
   */
  private String onchange_;

  /**
   * The javascript expression executed when an client onChange event fires
   */
  public static final String ON_CHANGE = "onchange".intern();

  /**
   * The javascript expression executed when an client event fires
   */
  public static final String ON_BLUR = "onblur".intern();

  /**
   * The javascript expression executed when an client event fires
   */
  public static final String ON_FOCUS = "onfocus".intern();

  /**
   * The javascript expression executed when an client event fires
   */
  public static final String ON_KEYUP = "onkeyup".intern();

  /**
   * The javascript expression executed when an client event fires
   */
  public static final String ON_KEYDOWN = "onkeydown".intern();

  /**
   * The javascript expression executed when an client event fires
   */
  public static final String ON_CLICK = "onclick".intern();

  private Map<String, String> jsActions_ = new HashMap<String, String>() ;
  private Color[] colors_ = null ;

  public UIFormColorPicker(String name, String bindingExpression, String value) {
    super(name, bindingExpression, String.class);
    this.value_ = value ;
    setColors(Colors.COLORS) ;
  }

  public UIFormColorPicker(String name, String bindingExpression, Color[] colors) {
    super(name, bindingExpression, null);
    setColors(colors);
  }

  public void setJsActions(Map<String, String> jsActions) {
    if(jsActions != null) jsActions_ = jsActions;
  }

  public Map<String, String> getJsActions() {
    return jsActions_;
  }
  public void addJsActions(String action, String javaScript) {
    jsActions_.put(action, javaScript) ;
  }
  public UIFormColorPicker(String name, String bindingExpression, Color[] colors, Map<String, String> jsActions) {
    super(name, bindingExpression, null);
    setColors(colors) ;
    setJsActions(jsActions) ;
  }

  public UIFormColorPicker(String name, String value) {
    this(name, null, value);
  }
  /*final public UIFormColorPicker setColors(List<SelectItemOption<String>> options) { 
    options_ = options ; 
    if(options_ == null || options_.size() < 1) return this;
    value_ = options_.get(0).getValue();
    return this ;
  } */
  
  @SuppressWarnings("unused")
  public void decode(Object input, WebuiRequestContext context) throws Exception {
    value_ = (String) input;
    if(value_ != null && value_.trim().length() == 0) value_ = null ;
  }
  public void setOnChange(String onchange){ onchange_ = onchange; } 

  protected String renderOnChangeEvent(UIForm uiForm) throws Exception {
    return uiForm.event(onchange_, (String)null);
  }
  protected UIForm getUIform() {
    return getAncestorOfType(UIForm.class) ; 
  }

  private String renderJsActions() {
    StringBuffer sb = new StringBuffer("") ;
    for(String k : jsActions_.keySet()){
      if(sb != null && sb.length() > 0 ) sb.append(" ") ;
      if(jsActions_.get(k) != null) {
        sb.append(k).append("=\"").append(jsActions_.get(k)).append("\"") ;
      }  
    }
    return sb.toString() ;
  }

  private Color[] getColors(){
    return colors_ ;
  }
  private void setColors(Color[] colors){
    colors_ = colors ;
    value_ = colors_[0].getName() ;
  }
  private int items() {return items_ ;}
  private int size() {return colors_.length ;}
  public void processRender(WebuiRequestContext context) throws Exception {
    Writer w =  context.getWriter() ; 
    w.write("<div class='UIFormColorPicker'>") ;
      w.write("<div class=\"UIColorPickerInput\" onclick=\"eXo.webui.UIColorPicker.show(this)\">") ;
      w.write("<span class=\" DisplayValue "+encodeValue(value_).toString()+"\"></span>") ;
      w.write("</div>") ;
      w.write("<div class=\"CalendarTableColor\" selectedColor=\""+encodeValue(value_).toString()+" \">") ;
      int i = 0 ;
      int count = 0 ;
      while(i <= size()/items())  {
        w.write("<div class='UIColorLine'>") ; 
        int j = 0 ;
        while(j <= items() && count < size()){
          Color color = getColors()[count] ; 
          String actionLink = "javascript:eXo.webui.UIColorPicker.setColor('"+color.getName()+"')" ;   
          w.write("<a href=\""+actionLink+"\" class=\""+color.getName()+" ColorCell \" onmousedown=\"event.cancelBubble=true\"><img src=\"/eXoResources/skin/sharedImages/Blank.gif\" /></a>") ;
          count++ ;
          j++;
        }
        w.write("</div>") ;  
        i++ ;
      }
      w.write("</div>") ;
      w.write("<input class='UIColorPickerValue' name='"+getId()+"' type='hidden'" + " id='"+getId()+"' " + renderJsActions());
      if(value_ != null && value_.trim().length() > 0) {      
        w.write(" value='"+value_+"'");
      }
      w.write(" />") ;
    w.write("</div>") ;
  }

  @Override
  public UIFormInput setValue(String arg0) {
    if(arg0 == null) arg0 = colors_[0].getName() ;
    return super.setValue(arg0);
  }

  private StringBuilder encodeValue(String value){
    char [] chars = {'\'', '"'};
    String [] refs = {"&#39;", "&#34;"};
    StringBuilder builder = new StringBuilder(value);
    int idx ;
    for(int i = 0; i < chars.length; i++){
      idx = indexOf(builder, chars[i], 0);
      while(idx > -1){
        builder = builder.replace(idx, idx+1, refs[i]);
        idx = indexOf(builder, chars[i], idx);
      }
    }    
    return builder;
  }

  private int indexOf(StringBuilder builder, char c, int from){
    int i = from;
    while(i < builder.length()){
      if(builder.charAt(i) == c) return i;
      i++;
    }
    return -1;
  }
  
  static public class Colors {

    public static final String H_OLIVE = "#808000".intern() ;
    public static final String H_ORANGE  ="#FFA500".intern() ;
    public static final String H_OLIVEDRAB = "#6B8E23".intern() ;
    public static final String H_ORANGERED = "#FF4500".intern() ;
    public static final String H_ORCHID = "#DA70D6".intern() ;
    public static final String H_PALEGOLDENROD = "#EEE8AA".intern() ;
    public static final String H_PALEGREEN = "#98FB98".intern() ;
    public static final String H_PALETURQUOISE = "#AFEEEE".intern() ;
    public static final String H_PALEVIOLETRED = "#D87093".intern() ;
    public static final String H_PAPAYAWHIP = "#FFEFD5".intern() ;
    public static final String H_PEACHPUFF = "#FFDAB9".intern() ;
    public static final String H_PERU = "#CD853F".intern() ;
    public static final String H_PINK = "#FFC0CB".intern() ;
    public static final String H_PLUM = "#DDA0DD".intern() ;
    public static final String H_POWDERBLUE = "#B0E0E6".intern() ;
    public static final String H_PURPLE = "#800080".intern() ;
    public static final String H_RED = "#FF0000".intern() ;
    public static final String H_ROSYBROWN = "#BC8F8F".intern() ;
    public static final String H_ROYALBLUE = "#4169E1".intern() ;
    public static final String H_SADDLEBROWN = "#8B4513".intern() ;
    public static final String H_SALMON = "#FA8072".intern() ;
    public static final String H_SANDYBROWN = "#F4A460".intern() ;
    public static final String H_SEAGREEN = "#2E8B57".intern() ;
    public static final String H_SEASHELL = "#FFF5EE".intern() ;
    public static final String H_SIANNA = "#A0522D".intern() ;
    public static final String H_SILVER = "#C0C0C0".intern() ;
    public static final String H_SKYBLUE = "#87CEEB".intern() ;
    public static final String H_THISTLE = "#D8BFD8".intern() ;
    public static final String H_TOMATO = "#FF6347".intern() ;
    public static final String H_TURQUOISE = "#40E0D0".intern() ;
    public static final String H_VIOLET = "#EE82EE".intern() ;
    public static final String H_WHEAT = "#F5DEB3".intern() ;
    public static final String H_YELLOW = "#FFFF00".intern() ;

    public static final String N_OLIVE = "Olive".intern() ;
    public static final String N_OLIVEDRAB = "OliveDrab".intern() ;
    public static final String N_ORANGERED = "OrangeRed".intern() ;
    public static final String N_ORCHID = "Orchid".intern() ;
    public static final String N_PALEGOLDENROD = "PaleGoldenRod".intern() ;
    public static final String N_PALEGREEN = "PaleGreen".intern() ;
    public static final String N_PALETURQUOISE = "PaleTurquoise".intern() ;
    public static final String N_PALEVIOLETRED = "PaleVioletRed".intern() ;
    public static final String N_PAPAYAWHIP = "PapayaWhip".intern() ;
    public static final String N_PEACHPUFF = "PeachPuff".intern() ;
    public static final String N_PERU = "Peru".intern() ;
    public static final String N_PINK = "Pink".intern() ;
    public static final String N_PLUM = "Plum".intern() ;
    public static final String N_POWDERBLUE = "PowderBlue".intern() ;
    public static final String N_PURPLE = "Purple".intern() ;
    public static final String N_RED = "Red".intern() ;
    public static final String N_ROSYBROWN = "RosyBrown".intern() ;
    public static final String N_ROYALBLUE = "RoyalBlue".intern() ;
    public static final String N_SADDLEBROWN = "SaddleBrown".intern() ;
    public static final String N_SALMON = "Salmon".intern() ;
    public static final String N_SANDYBROWN = "SandyBrown".intern() ;
    public static final String N_SEAGREEN = "SeaGreen".intern() ;
    public static final String N_SEASHELL = "SeaShell".intern() ;
    public static final String N_SIANNA = "Sienna".intern() ;
    public static final String N_SILVER = "Silver".intern() ;
    public static final String N_SKYBLUE = "SkyBlue".intern() ;
    public static final String N_THISTLE = "Thistle".intern() ;
    public static final String N_TOMATO = "Tomato".intern() ;
    public static final String N_TURQUOISE = "Turquoise".intern() ;
    public static final String N_VIOLET = "Violet".intern() ;
    public static final String N_WHEAT = "Wheat".intern() ;
    public static final String N_YELLOW = "Yellow".intern() ;


    public static final Color O_OLIVE = new Color(H_OLIVE, N_OLIVE) ; 
    public static final Color O_OLIVEDRAB = new Color(H_OLIVEDRAB, N_OLIVEDRAB) ; 
    public static final Color O_ORANGERED = new Color(H_ORANGERED, N_ORANGERED) ; 
    public static final Color O_ORCHID = new Color(H_ORCHID, N_ORCHID) ; 
    public static final Color O_PALEGOLDENROD = new Color(H_PALEGOLDENROD, N_PALEGOLDENROD) ; 
    public static final Color O_PALEGREEN = new Color(H_PALEGREEN, N_PALEGREEN) ; 
    public static final Color O_PALETURQUOISE = new Color(H_PALETURQUOISE, N_PALETURQUOISE) ; 
    public static final Color O_PALEVIOLETRED = new Color(H_PALEVIOLETRED, N_PALEVIOLETRED) ; 
    public static final Color O_PAPAYAWHIP = new Color(H_PAPAYAWHIP, N_PAPAYAWHIP) ; 
    public static final Color O_PEACHPUFF = new Color(H_PEACHPUFF, N_PEACHPUFF) ; 
    public static final Color O_PERU = new Color(H_PERU, N_PERU) ; 
    public static final Color O_PINK = new Color(H_PINK, N_PINK) ; 
    public static final Color O_PLUM = new Color(H_PLUM, N_PLUM) ; 
    public static final Color O_POWDERBLUE = new Color(H_POWDERBLUE, N_POWDERBLUE) ; 
    public static final Color O_PURPLE = new Color(H_PURPLE, N_PURPLE) ; 
    public static final Color O_RED = new Color(H_RED, N_RED) ; 
    public static final Color O_ROSYBROWN = new Color(H_ROSYBROWN, N_ROSYBROWN) ; 
    public static final Color O_ROYALBLUE = new Color(H_ROYALBLUE, N_ROYALBLUE) ; 
    public static final Color O_SADDLEBROWN = new Color(H_SADDLEBROWN, N_SADDLEBROWN) ; 
    public static final Color O_SALMON = new Color(H_SALMON, N_SALMON) ; 
    public static final Color O_SANDYBROWN = new Color(H_SANDYBROWN, N_SANDYBROWN) ; 
    public static final Color O_SEAGREEN = new Color(H_SEAGREEN, N_SEAGREEN) ; 
    public static final Color O_SEASHELL = new Color(H_SEASHELL, N_SEASHELL) ; 
    public static final Color O_SIANNA = new Color(H_SIANNA, N_SIANNA) ; 
    public static final Color O_SILVER = new Color(H_SILVER, N_SILVER) ; 
    public static final Color O_SKYBLUE = new Color(H_SKYBLUE, N_SKYBLUE) ; 
    public static final Color O_THISTLE = new Color(H_THISTLE, N_THISTLE) ; 
    public static final Color O_TOMATO = new Color(H_TOMATO, N_TOMATO) ; 
    public static final Color O_TURQUOISE = new Color(H_TURQUOISE, N_TURQUOISE) ; 
    public static final Color O_VIOLET = new Color(H_VIOLET, N_VIOLET) ; 
    public static final Color O_WHEAT = new Color(H_WHEAT, N_WHEAT) ; 
    public static final Color O_YELLOW = new Color(H_YELLOW, N_YELLOW) ; 

    public static final Color[] COLORS = {
      O_POWDERBLUE,O_ORCHID,O_PALEGOLDENROD,O_PALEGREEN,
      O_OLIVE,O_OLIVEDRAB,O_ORANGERED,O_PALETURQUOISE,O_PALEVIOLETRED,O_PAPAYAWHIP,O_PEACHPUFF,
      O_PERU,O_PINK,O_PLUM,O_PURPLE,O_RED,O_ROSYBROWN,O_ROYALBLUE,O_SADDLEBROWN,O_SALMON,
      O_SANDYBROWN,O_SEAGREEN,O_SEASHELL,O_SIANNA,O_SILVER,O_SKYBLUE,O_THISTLE,O_TOMATO,O_TURQUOISE,
      O_VIOLET,O_WHEAT,O_YELLOW } ;
     
    
    public static final String[] COLORNAMES = {
      N_POWDERBLUE,N_ORCHID,N_PALEGOLDENROD,N_PALEGREEN,
      N_OLIVE,N_OLIVEDRAB,N_ORANGERED,N_PALETURQUOISE,N_PALEVIOLETRED,N_PAPAYAWHIP,N_PEACHPUFF,
      N_PERU,N_PINK,N_PLUM,N_PURPLE,N_RED,N_ROSYBROWN,N_ROYALBLUE,N_SADDLEBROWN,N_SALMON,
      N_SANDYBROWN,N_SEAGREEN,N_SEASHELL,N_SIANNA,N_SILVER,N_SKYBLUE,N_THISTLE,N_TOMATO,N_TURQUOISE,
      N_VIOLET,N_WHEAT,N_YELLOW 
    } ;
    
    public static final String[] CODES = {
      H_POWDERBLUE,H_ORCHID,H_PALEGOLDENROD,H_PALEGREEN,
      H_OLIVE,H_OLIVEDRAB,H_ORANGERED,H_PALETURQUOISE,H_PALEVIOLETRED,H_PAPAYAWHIP,H_PEACHPUFF,
      H_PERU,H_PINK,H_PLUM,H_PURPLE,H_RED,H_ROSYBROWN,H_ROYALBLUE,H_SADDLEBROWN,H_SALMON,
      H_SANDYBROWN,H_SEAGREEN,H_SEASHELL,H_SIANNA,H_SILVER,H_SKYBLUE,H_THISTLE,H_TOMATO,H_TURQUOISE,
      H_VIOLET,H_WHEAT,H_YELLOW 
    } ;
    
    
  static public class Color {
      
      int R = 0;
      int G = 0;
      int B = 0;
      String code_ ;
      String name_ ;

      public Color(String code){
        setCode(code) ;
      }
      public Color(int r, int g, int b){
        R = r ;G = g ;B = b ;
      }
      public Color(String code,String name){
        setCode(code) ;
        setName(name) ;
      }
      
      public String getCode() {return code_ ;}
      public void setName(String name) {name_ = name;}
      public String getName() {return name_ ;}

      public void setCode(String code) {
        code_ = code ;
        /*R =  Integer.parseInt(code.substring(1,2), 16) ;
        G =  Integer.parseInt(code.substring(3,2), 16) ;
        B =  Integer.parseInt(code.substring(5,2), 16) ;*/
      }
    }

  }
   
}