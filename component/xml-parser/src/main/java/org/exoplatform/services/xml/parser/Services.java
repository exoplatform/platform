/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.xml.parser;

import org.exoplatform.services.common.ThreadSoftRef;
import org.exoplatform.services.html.refs.RefsEncoder;
import org.exoplatform.services.token.TokenParser;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 14, 2006  
 */
public class Services {
  
  static ThreadSoftRef<TokenParser> TOKEN_PARSER = new ThreadSoftRef<TokenParser>(TokenParser.class);
  
  static ThreadSoftRef<RefsEncoder> ENCODER = new ThreadSoftRef<RefsEncoder>(RefsEncoder.class);

}
