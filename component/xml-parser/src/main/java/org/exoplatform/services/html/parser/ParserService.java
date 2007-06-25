/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.html.parser;

import java.util.List;

import org.exoplatform.services.common.ThreadSoftRef;
import org.exoplatform.services.html.HTMLDocument;
import org.exoplatform.services.html.Name;
import org.exoplatform.services.token.TokenParser;
import org.exoplatform.services.token.TypeToken;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          thuan.nhu@exoplatform.com
 * Sep 14, 2006  
 */
class ParserService {
  
  private static ThreadSoftRef<DOMParser> DOM_PARSER = new ThreadSoftRef<DOMParser>(DOMParser.class);
  
  private static ThreadSoftRef<NodeCreator> NODE_CREATOR = new ThreadSoftRef<NodeCreator>(NodeCreator.class);  
  
  private  static ThreadSoftRef<NodeCloser> NODE_CLOSER = new ThreadSoftRef<NodeCloser>(NodeCloser.class);
  
  private static ThreadSoftRef<NodeSetter> NODE_SETTER = new ThreadSoftRef<NodeSetter>(NodeSetter.class);
  
  private static ThreadSoftRef<TokenParser> TOKEN_PARSER = new ThreadSoftRef<TokenParser>(TokenParser.class);
  
  static private NodeImpl ROOT;
  
  static DOMParser getDOMParser () { return DOM_PARSER.getRef(); }
  
  static NodeCreator getNodeCreator () { return NODE_CREATOR.getRef(); }
  
  static NodeCloser getNodeCloser () { return NODE_CLOSER.getRef(); }
  
  static NodeSetter getNodeSetter () { return NODE_SETTER.getRef(); }
  
  static TokenParser getTokenParser () { return TOKEN_PARSER.getRef(); }

  static void parse(CharsToken tokens, HTMLDocument document){    
    ROOT = new NodeImpl(new char[]{'h', 't', 'm', 'l'}, Name.HTML, TypeToken.TAG);       
    document.setRoot(ROOT);
    List<NodeImpl> opens = NODE_CREATOR.getRef().getOpens();
    opens.clear();
    opens.add(ROOT);
    DOM_PARSER.getRef().parse(tokens);  
  }

  static NodeImpl createHeader(){
    NodeImpl node = new NodeImpl(new char[]{'h', 'e', 'a', 'd'}, Name.HEAD, TypeToken.TAG); 
    ROOT.getChildren().add(0, node);
    node.setParent(ROOT);
    return node;
  }

  static NodeImpl createBody(){
    NodeImpl node = new NodeImpl(new char[]{'b', 'o', 'd', 'y'}, Name.BODY, TypeToken.TAG);
    ROOT.getChildren().add(node);
    node.setParent(ROOT);
    return node;
  }

  static NodeImpl getRootNode() { return ROOT; }

  static void setRootNode(NodeImpl root) { ROOT = root; } 
  
}
