package org.exoplatform.platform.common.branding;

import java.io.IOException;

public interface BrandingService {

  Branding getBrandingInformation();

  void updateBrandingInformation(Branding branding) throws Exception;

  String getCompanyName();

  void updateCompanyName(String companyName);

  String getTopBarTheme();

  Long getLogoId();

  Logo getLogo();

  void updateTopBarTheme(String style);

  void updateLogo(Logo logo) throws IOException, Exception;

}
