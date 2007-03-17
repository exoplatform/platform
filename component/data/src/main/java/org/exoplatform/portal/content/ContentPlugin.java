package org.exoplatform.portal.content;

import org.exoplatform.commons.utils.PageList;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.portal.content.model.ContentItem;
import org.exoplatform.portal.content.model.ContentNode;

public abstract class ContentPlugin extends BaseComponentPlugin {
  
	protected String type;	
  
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
	
  public abstract <T extends ContentItem>  PageList loadContentMeta(ContentNode node) throws Exception;

}
