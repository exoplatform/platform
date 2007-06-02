package org.exoplatform.portal.config.serialize;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jibx.runtime.IAliasable;
import org.jibx.runtime.IMarshaller;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshaller;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.impl.MarshallingContext;
import org.jibx.runtime.impl.UnmarshallingContext;

public class JibxHashMapper implements IMarshaller, IUnmarshaller, IAliasable {

  private static final String SIZE_ATTRIBUTE_NAME = "size";
  private static final String ENTRY_ELEMENT_NAME = "entry";
  private static final String KEY_ATTRIBUTE_NAME = "key";
  private static final String KEY_ATTRIBUTE_VALUE = "value";
  private static final int DEFAULT_SIZE = 10;

  private String marshalURI;
  private int marshallIndex;
  private String marshallName;

  public JibxHashMapper() {
    marshalURI = null;
    marshallIndex = 0;
    marshallName = "hashmap";
  }

  public JibxHashMapper(String uri, int index, String name) {
    marshalURI = uri;
    marshallIndex = index;
    marshallName = name;
  }

  @SuppressWarnings("unused")
  public boolean isExtension(int index) { return false; }

  public void marshal(Object obj, IMarshallingContext ictx) throws JiBXException {
    if (!(obj instanceof HashMap)) throw new JiBXException("Invalid object type for marshaller");
    if (!(ictx instanceof MarshallingContext)) throw new JiBXException("Invalid object type for marshaller");
    
    MarshallingContext ctx = (MarshallingContext)ictx;
    HashMap map = (HashMap)obj;
    MarshallingContext mContext = ctx.startTagAttributes(marshallIndex, marshallName);
    mContext.attribute(marshallIndex, SIZE_ATTRIBUTE_NAME, map.size()).closeStartContent();

    Iterator iter = map.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      ctx.startTagAttributes(marshallIndex, ENTRY_ELEMENT_NAME);
      if(entry.getKey() != null) {
        ctx.attribute(marshallIndex, KEY_ATTRIBUTE_NAME, entry.getKey().toString());
        ctx.attribute(marshallIndex, KEY_ATTRIBUTE_VALUE, entry.getValue().toString());
      }
      ctx.closeStartContent();
      ctx.endTag(marshallIndex, ENTRY_ELEMENT_NAME);
    }

    ctx.endTag(marshallIndex, marshallName);
  }

  public boolean isPresent(IUnmarshallingContext ctx) throws JiBXException {
    return ctx.isAt(marshalURI, marshallName);
  }

  @SuppressWarnings("unchecked")
  public Object unmarshal(Object obj, IUnmarshallingContext ictx) throws JiBXException {
    UnmarshallingContext ctx = (UnmarshallingContext)ictx;
    if (!ctx.isAt(marshalURI, marshallName)) ctx.throwStartTagNameError(marshalURI, marshallName);

    int size = ctx.attributeInt(marshalURI, SIZE_ATTRIBUTE_NAME, DEFAULT_SIZE);
    HashMap<String, String> map = (HashMap<String, String>)obj;
    if (map == null) map = new HashMap<String, String>(size);

    ctx.parsePastStartTag(marshalURI, marshallName);
    while (ctx.isAt(marshalURI, ENTRY_ELEMENT_NAME)) {
      Object key = ctx.attributeText(marshalURI, KEY_ATTRIBUTE_NAME, null);
      Object value = ctx.attributeText(marshalURI, KEY_ATTRIBUTE_VALUE, null);
      map.put(key.toString(), value.toString());
      ctx.parsePastEndTag(marshalURI, ENTRY_ELEMENT_NAME);
    }
    ctx.parsePastEndTag(marshalURI, marshallName);
    return map;
  }
}