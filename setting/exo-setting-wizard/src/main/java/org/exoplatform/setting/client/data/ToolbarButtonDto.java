package org.exoplatform.setting.client.data;

/**
 * Represents datas needed to display a button into screen
 * 
 * @author Clement
 *
 */
public class ToolbarButtonDto {

  private String text;
  private String link;
  private int toStepId;

  public ToolbarButtonDto() {}

  public ToolbarButtonDto(String text, int toStepId) {
    this.text = text;
    this.toStepId = toStepId;
  }
  
  public String getText() {
    return text;
  }
  public String getLink() {
    return link;
  }
  public int getToStepId() {
    return toStepId;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  public void setLink(String link) {
    this.link = link;
  }
  public void setToStepId(int toStepId) {
    this.toStepId = toStepId;
  }
  
  @Override
  public String toString() {
    return "ToolbarButtonDto [text=" + text + ", link=" + link + "]";
  }

}
