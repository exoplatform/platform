/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.parser.html.parser;

import java.util.List;

import org.exoplatform.services.parser.common.TokenParser;
import org.exoplatform.services.parser.common.TypeToken;
import org.exoplatform.services.parser.container.ThreadSoftRef;
import org.exoplatform.services.parser.html.HTMLDocument;
import org.exoplatform.services.parser.html.Name;
import org.exoplatform.services.parser.io.DataBuffer;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 14, 2006  
 */
class Services {
  
  private static ThreadSoftRef<DOMParser> DOM_PARSER = new ThreadSoftRef<DOMParser>(DOMParser.class);
  
  static ThreadSoftRef<NodeCreator> OPEN_NODE = new ThreadSoftRef<NodeCreator>(NodeCreator.class);  
  
  static ThreadSoftRef<NodeCloser> CLOSE_NODE = new ThreadSoftRef<NodeCloser>(NodeCloser.class);
  
  static ThreadSoftRef<NodeSetter> ADD_NODE = new ThreadSoftRef<NodeSetter>(NodeSetter.class);
  
  static ThreadSoftRef<TokenParser> TOKEN_PARSER = new ThreadSoftRef<TokenParser>(TokenParser.class);
  
  static ThreadSoftRef<DataBuffer> DATA_BUFFER= new ThreadSoftRef<DataBuffer>(DataBuffer.class);

  static NodeImpl ROOT;

  static void parse(CharsToken tokens, HTMLDocument document){    
    ROOT = new NodeImpl(new char[]{'h', 't', 'm', 'l'}, HTML.getConfig(Name.HTML), TypeToken.TAG);       
    document.setRoot(ROOT);
    List<NodeImpl> opens = OPEN_NODE.getRef().getOpens();
    opens.clear();
    opens.add(ROOT);
    DOM_PARSER.getRef().parse(tokens);  
  }

  static NodeImpl createHeader(){
    NodeImpl node = new NodeImpl(new char[]{'h', 'e', 'a', 'd'}, HTML.getConfig(Name.HEAD), TypeToken.TAG); 
    ROOT.getChildren().add(0, node);
    node.setParent(ROOT);
    return node;
  }

  static NodeImpl createBody(){
    NodeImpl node = new NodeImpl(new char[]{'b', 'o', 'd', 'y'}, HTML.getConfig(Name.BODY), TypeToken.TAG);
    ROOT.getChildren().add(node);
    node.setParent(ROOT);
    return node;
  } 
  
}
