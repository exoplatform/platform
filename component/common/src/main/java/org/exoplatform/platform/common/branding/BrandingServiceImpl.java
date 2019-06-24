/*
 * Copyright (C) 2003-2019 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.platform.common.branding;

import java.io.*;
import java.util.Date;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.commons.file.model.FileItem;
import org.apache.commons.lang3.StringUtils;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;

public class BrandingServiceImpl implements BrandingService {
  private static final Log LOG               = ExoLogger.getExoLogger(BrandingServiceImpl.class);

  public static final String BRANDING_COMPANY_NAME_INIT_PARAM = "exo.company.name";

  public static final String BRANDING_COMPANY_NAME_SETTING_KEY = "exo.company.name";

  public static final String BRANDING_TOPBAR_THEME_SETTING_KEY = "bar_navigation_style";

  public static final String BRANDING_LOGO_ID_SETTING_KEY = "exo.branding.company.id";

  public static final String FILE_API_NAME_SPACE = "CompanyBranding";

  private SettingService settingService;
  
  private FileService fileService;
  
  private UploadService uploadService;
  
  public static String LOGO_NAME = "logo.png";
  
  private String defaultCompanyName  = "";

  private String defaultTopbarTheme = "Dark";

  public BrandingServiceImpl(InitParams initParams,SettingService settingService, FileService fileService, UploadService uploadService) {
    this.settingService = settingService;
    this.fileService = fileService;
    this.uploadService = uploadService;

    this.loadInitParams(initParams);
  }
  
  /**
   * Load init params
   * @param initParams

   * @throws Exception 
   */
  private void loadInitParams(InitParams initParams) {
    if (initParams != null) {
      ValueParam companyNameParam = initParams.getValueParam(BRANDING_COMPANY_NAME_INIT_PARAM);
      if(companyNameParam != null) {
        this.defaultCompanyName = companyNameParam.getValue();
      }
    }
  }

  /**
   * Get all the branding information
   * @return The branding object containing all information
   */
  @Override
  public Branding getBrandingInformation() {
    Branding branding = new Branding();
    branding.setCompanyName(getCompanyName());
    branding.setTopBarTheme(getTopBarTheme());
    return branding;
  }

  /**
   * Update the branding information
   * Missing information in the branding object are not updated.
   * @param branding The new branding information
   * @throws Exception
   */
  @Override
  public void updateBrandingInformation(Branding branding) throws Exception {
    if(branding.getCompanyName() != null) {
      updateCompanyName(branding.getCompanyName());
    }

    if(branding.getTopBarTheme() != null) {
      updateTopBarTheme(branding.getTopBarTheme());
    }

    Logo logo = branding.getLogo();
    if(logo != null && (logo.getData() != null && logo.getData().length > 0 || StringUtils.isNotBlank(logo.getUploadId()))) {
      uploadLogo(branding.getLogo());
    }
  }

  @Override
  public String getCompanyName() {
    SettingValue<String> brandingCompanyName = (SettingValue<String>) settingService.get(Context.GLOBAL, Scope.GLOBAL, BRANDING_COMPANY_NAME_SETTING_KEY);
    if(brandingCompanyName != null && StringUtils.isNotBlank(brandingCompanyName.getValue())) {
      return brandingCompanyName.getValue();
    } else {
      return defaultCompanyName;
    }
  }
  
  @Override
  public void updateCompanyName(String companyName) {
    if(companyName == null) {
      throw new IllegalArgumentException("Company Name couldn't be null");
    }
    settingService.set(Context.GLOBAL, Scope.GLOBAL, BRANDING_COMPANY_NAME_SETTING_KEY, SettingValue.create(companyName));  
  }
  

  @Override
  public String getTopBarTheme() {
    SettingValue<String> topBarTheme = (SettingValue<String>) settingService.get(Context.GLOBAL, Scope.GLOBAL, BRANDING_TOPBAR_THEME_SETTING_KEY);
    if(topBarTheme != null && StringUtils.isNotBlank(topBarTheme.getValue())) {
      return topBarTheme.getValue();
    } else {
      return defaultTopbarTheme;
    }
  }
  
  @Override
  public Long getLogoId() {
    SettingValue<String> logoId = (SettingValue<String>) settingService.get(Context.GLOBAL, Scope.GLOBAL, BRANDING_LOGO_ID_SETTING_KEY);
    if(logoId != null && logoId.getValue() != null) {
      return Long.parseLong(logoId.getValue());
    } else {
      return null;
    }
  }

  @Override
  public void updateTopBarTheme(String topBarTheme) {
    if(topBarTheme == null) {
      throw new IllegalArgumentException("topBarTheme couldn't be null");
    }
    settingService.set(Context.GLOBAL, Scope.GLOBAL, BRANDING_TOPBAR_THEME_SETTING_KEY, SettingValue.create(topBarTheme));
  }

  /**
   * Update branding logo.
   * If the logo object contains the image data, they are used,
   * otherwise the uploadId is used to retrieve the uploaded resource
   * @param logo The logo object
   * @throws IOException
   * @throws Exception
   */
  @Override
  public void uploadLogo(Logo logo) throws Exception {
    InputStream inputStream;
    if(logo.getData() != null && logo.getData().length > 0) {
      inputStream = new ByteArrayInputStream(logo.getData());
    } else if(StringUtils.isNoneBlank(logo.getUploadId())) {
      inputStream = getUploadDataAsStream(logo.getUploadId());
    } else {
      throw new IllegalArgumentException("Cannot update branding logo, the logo object must contain the image data or an upload id");
    }
    String currentUserId = getCurrentUserId();
    FileItem fileItem;
    Long logoId = this.getLogoId();
    if (logoId == null) {
      fileItem = new FileItem(null,
                              LOGO_NAME,
                              "image/png",
                              FILE_API_NAME_SPACE,
                              logo.getSize(),
                              new Date(),
                              currentUserId,
                              false,
                              inputStream);
      fileItem = fileService.writeFile(fileItem);     
      settingService.set(Context.GLOBAL, Scope.GLOBAL, BRANDING_LOGO_ID_SETTING_KEY, SettingValue.create(fileItem.getFileInfo().getId()));
    } else {
      fileItem = new FileItem(logoId,
                              LOGO_NAME,
                              "image/png",
                              FILE_API_NAME_SPACE,
                              logo.getSize(),
                              new Date(),
                              currentUserId,
                              false,
                              inputStream);
      fileService.updateFile(fileItem);
    }
  }

  private InputStream getUploadDataAsStream(String uploadId) throws FileNotFoundException {
    UploadResource uploadResource = uploadService.getUploadResource(uploadId);
    if (uploadResource == null) {
      return null;
    } else {
      try {
        return new FileInputStream(new File(uploadResource.getStoreLocation()));
      } finally {
        uploadService.removeUploadResource(uploadId);
      }
    }
  }

  private String getCurrentUserId() {
    ConversationState conversationState = ConversationState.getCurrent();
    if (conversationState != null && conversationState.getIdentity() != null) {
      return  conversationState.getIdentity().getUserId();
    }
    return null;
  }

}
