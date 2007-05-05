/***************************************************************************
 * Copyright 2001-2006 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.portal.content;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.portal.content.model.ContentItem;
import org.exoplatform.portal.content.model.ContentNode;

/**
 * Created by The eXo Platform SARL
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@exoplatform.com
 * Jul 21, 2006  
 */
public class DescriptionPlugin extends ContentPlugin {
  
  private List<DescItem> list;

  public DescriptionPlugin(){
    super();
    list = new ArrayList<DescItem>(1);
    list.add(new DescItem());
    type ="desc";
  }

  @SuppressWarnings("unchecked")
  public PageList loadContentMeta(ContentNode node) throws Exception {
    list.get(0).setTitle(node.getLabel());
    list.get(0).setDesc(node.getDescription());
    return new ContentPageList(list);
  } 

  static public class DescItem implements ContentItem {
    
    private String desc = "";
    private String title = "";
    
    public DescItem(){ }

    public String getDesc() { return desc; }
    public void setDesc(String desciption) {this.desc = desciption; }

    public String getLink() { return "#"; }
    @SuppressWarnings("unused")
    public void setLink(String url) {}

    public String getTime() { return null; }
    @SuppressWarnings("unused")
    public void setTime(String time) {}
    
    public String getImage() { return null; }

    @SuppressWarnings("unused")
    public void setImage(String image) {}

    public String getTitle() { return title;}
    public void setTitle(String title) { this.title = title;     }

    @SuppressWarnings("unused")
    public void setCreator(String creator){ }
    public String getCreator(){ return null; }

  }
  
}
