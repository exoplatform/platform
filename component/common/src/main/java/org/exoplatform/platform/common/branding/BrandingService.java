package org.exoplatform.platform.common.branding;

import java.io.IOException;

public interface BrandingService {

  void updateBranding(Branding branding) throws Exception;

  Branding getBrandingInformation();

  String getCompanyName();

  void updateCompanyName(String companyName);

  String getBarStyle();

  Long getLogoId();

  void updateBarStyle(String style);

  void uploadLogo(Logo logo) throws IOException, Exception;

}
