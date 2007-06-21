/***************************************************************************
 * Copyright 2003-2006 by eXoPlatform - All rights reserved.  *
 *    *
 **************************************************************************/
package org.exoplatform.services.html;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Aug 12, 2006
 */
public final class Group {

  public static interface Default {}

  public static interface Flow {}

  public static interface Block extends Flow {}

  public static interface Fontstyle extends Inline {}

  public static interface Formctrl extends Inline {}

  public static interface Heading extends Block {}

  public static interface List extends Block {}

  public interface Phrase extends Inline {}

  public static interface Special extends Inline {}

  public static interface Inline extends Flow { }  

  public static interface HtmlContent { }  

  public static interface HeadContent{ }  

  public static interface HeadMisc { }

  public static interface Table {}

  public static interface Preformatted extends Block { }
  
  
}
