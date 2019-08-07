package org.exoplatform.platform.common.branding;

import java.io.Serializable;

public class Branding implements Serializable {

  private static final long serialVersionUID = 625471892955717717L;

  private String companyName;

  private String topBarTheme;

  private Logo logo;

  public Branding() {
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
  
  public Logo getLogo() {
    return logo;
  }
  
  public void setLogo(Logo logo) {
    this.logo = logo;
  }

  @Override
  public String toString() {
    return "Branding [companyName=" + companyName + ", topBarTheme=" + topBarTheme + "]";
  }
}
