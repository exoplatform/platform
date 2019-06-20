package org.exoplatform.platform.common.branding;

import java.io.Serializable;

public class Branding implements Serializable {

  private static final long serialVersionUID = 625471892955717717L;
  private long id;
  private String companyName;
  private String topBarTheme;
  private Logo logo;
  private long lastUpdated;
  
  public Branding() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getTopBarTheme() {
    return topBarTheme;
  }

  public void setTopBarTheme(String topBarTheme) {
    this.topBarTheme = topBarTheme;
  }

  public long getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(long lastUpdated) {
    this.lastUpdated = lastUpdated;
  }
  
  public Logo getLogo() {
    return logo;
  }
  
  public void setLogo(Logo logo) {
    this.logo = logo;
  }

  @Override
  public String toString() {
    return "Branding [id=" + id + ", companyName=" + companyName + ", topBarTheme=" + topBarTheme + ", lastUpdated=" + lastUpdated
        + "]";
  }


  

}
