package org.exoplatform.platform.samples.website.extention.services;

import javax.jcr.Node;

public class NavigableNode {
	private Node node;
	private String navigationNode;
	private boolean clickable;
	private String listUri;
	private String listParam;
	private String detailUri;
	private String detailParam;
	/** allows to know if the node is viewable or not*/
	private boolean isViewableNode;
	
	public NavigableNode(Node node) {
		super();
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public String getNavigationNode() {
		return navigationNode;
	}
	public void setNavigationNode(String navigationNode) {
		this.navigationNode = navigationNode;
	}
	public boolean isClickable() {
		return clickable;
	}
	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
	public String getListUri() {
		return listUri;
	}
	public void setListUri(String listUri) {
		this.listUri = listUri;
	}
	public String getListParam() {
		return listParam;
	}
	public void setListParam(String listParam) {
		this.listParam = listParam;
	}
	public String getDetailUri() {
		return detailUri;
	}
	public void setDetailUri(String detailUri) {
		this.detailUri = detailUri;
	}
	public String getDetailParam() {
		return detailParam;
	}
	public void setDetailParam(String detailParam) {
		this.detailParam = detailParam;
	}

	 /**
   * Allows to know if the node is viewable or not
   * using {@link} WCMComposer}
   * @return true is the node is viewable, else false
	 * @throws Exception the exception
   */
  public boolean isViewableNode() throws Exception {
//    if (WCMCoreUtils.getService(WCMComposer.class).getContent(repository, workspace, getNode().getPath(), filters, WCMCoreUtils.getUserSessionProvider()) != null) {
//      isViewableNode = true;
//    } else {
//      isViewableNode = false;
//    }
    return isViewableNode;
  }

  public void setViewableNode(boolean isViewableNode) {
    this.isViewableNode = isViewableNode;
  }

}
