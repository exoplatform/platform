/*
 * Copyright 2004-2006 The  eXo Platform SARL        All rights reserved.
 *
 * Created on January 24, 2006, 7:48 PM
 */

package org.exoplatform.services.parser.attribute;

import org.exoplatform.services.parser.common.Node;
import org.exoplatform.services.parser.container.ThreadSoftRef;
/**
 *
 * @author nhuthuan
 * Email: nhudinhthuan@yahoo.com
 */
public final class AttributeParser {
  
  static ThreadSoftRef<AttrParser> PARSER = new ThreadSoftRef<AttrParser>(AttrParser.class);

  public static Attributes getAttributes(Node node) {
    return PARSER.getRef().getAttributes(node);
  }
}
