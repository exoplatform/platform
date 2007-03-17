/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.xml;

import org.exoplatform.services.common.DataBuffer;
import org.exoplatform.services.parser.common.TokenParser;
import org.exoplatform.services.parser.container.ThreadSoftRef;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 14, 2006  
 */
public class Services {
  
  static ThreadSoftRef<TokenParser> TOKEN_PARSER = new ThreadSoftRef<TokenParser>(TokenParser.class);
  
  static ThreadSoftRef<DataBuffer> DATA_BUFFER= new ThreadSoftRef<DataBuffer>(DataBuffer.class);

}
