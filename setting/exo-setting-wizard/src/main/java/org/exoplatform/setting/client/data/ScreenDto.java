package org.exoplatform.setting.client.data;


/**
 * Represents datas needed to display a screen skeleton
 * 
 * @author Clement
 *
 */
public class ScreenDto {
  
  public ScreenDto() {}
  
  public ScreenDto(String title, String description) {
    this.title = title;
    this.description = description;
  }

  private String title;
  private String description;
  
  public String getTitle() {
    return title;
  }
  public String getDescription() {
    return description;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public void setDescription(String description) {
    this.description = description;
  }
}
