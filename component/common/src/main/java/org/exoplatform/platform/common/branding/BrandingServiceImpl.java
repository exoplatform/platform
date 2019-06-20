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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
  
  private static final String BRANDING_COMPANY_NAME_SETTING_KEY = "exo.company.name";
  
  private static final String BRANDING_COMPANY_NAME_PARAM = "exo.company.name";
  
  private static final String BAR_NAVIGATION_STYLE_KEY = "bar_navigation_style";
  
  private static final String FILE_API_NAME_SPACE = "CompanyBranding";
  
  private static final String BRANDING_COMPANY_ID = "exo.branding.company.id";
  
  private SettingService        settingService;
  
  private FileService         fileService;
  
  private UploadService       uploadService;
  
  public static String     logo_name         = "logo.png";
  
  private String companyName  = "";
  
  private String barStyle = "Dark";
  
  private Long logoId = null;

  public BrandingServiceImpl(InitParams initParams,SettingService settingService, FileService fileService, UploadService uploadService) throws Exception {
    this.settingService = settingService;
    this.fileService = fileService;
    this.uploadService = uploadService;
    loadSettings(initParams);
  }
  
  
  /**
   * Load Branding Company Settings
   * @param initParams

   * @throws Exception 
   */
  private void loadSettings(InitParams initParams) throws Exception {
    SettingValue<String> brandingCompanyName = (SettingValue<String>) settingService.get(Context.GLOBAL, Scope.GLOBAL, BRANDING_COMPANY_NAME_SETTING_KEY);
    if(brandingCompanyName != null && !StringUtils.isBlank(brandingCompanyName.getValue())) {
      String companyNameValue = brandingCompanyName.getValue();
      this.companyName = companyNameValue;
    } else if (initParams != null) {
      ValueParam spacesAdministratorsParam = initParams.getValueParam(BRANDING_COMPANY_NAME_PARAM);
      String companyNameValue = spacesAdministratorsParam.getValue();
      this.companyName = companyNameValue;
    }
    
    SettingValue<String> barNavigationStyle = (SettingValue<String>) settingService.get(Context.GLOBAL, Scope.GLOBAL, BAR_NAVIGATION_STYLE_KEY);
    if (barNavigationStyle != null && !StringUtils.isBlank(barNavigationStyle.getValue())) {
      String barNavigationStyleValue = barNavigationStyle.getValue();
      this.barStyle = barNavigationStyleValue;
    }
    
    SettingValue<String> companyId = (SettingValue<String>) settingService.get(Context.GLOBAL, Scope.GLOBAL, BRANDING_COMPANY_ID);
    if (companyId != null && !StringUtils.isBlank(companyId.getValue())) {
      this.logoId = Long.parseLong(companyId.getValue());
    }
    
  }


  /**
   *  update company branding informations
   */
  @Override
  public void updateBranding(Branding branding) throws Exception {
    String name = branding.getCompanyName() != null ? branding.getCompanyName() : this.companyName;
    updateCompanyName(name);
    String style = branding.getTopBarTheme() != null ? branding.getTopBarTheme() : this.barStyle;
    updateBarStyle(style);
    uploadLogo(branding.getLogo());
  }
  
  /** 
   * retrieve company branding informations
   */
  @Override
  public Branding getBrandingInformation() {
    Branding brandng = new Branding();
    brandng.setCompanyName(getCompanyName());
    brandng.setTopBarTheme(getBarStyle());
    return brandng;
    
  }


  @Override
  public String getCompanyName() {
    return companyName;
  }
  
  @Override
  public void updateCompanyName(String companyName) {
    if(companyName == null) {
      throw new IllegalArgumentException("Company Name couldn't be null");
    }
    settingService.set(Context.GLOBAL, Scope.GLOBAL, BRANDING_COMPANY_NAME_SETTING_KEY, SettingValue.create(companyName));  
    this.companyName = companyName;
  }
  

  @Override
  public String getBarStyle() {
    return barStyle;
  }
  
  @Override
  public Long getLogoId() {
    return this.logoId;
  }
  

  @Override
  public void updateBarStyle(String style) {
    if(style == null) {
      throw new IllegalArgumentException("style couldn't be null");
    }
    settingService.set(Context.GLOBAL, Scope.GLOBAL, BAR_NAVIGATION_STYLE_KEY, SettingValue.create(style));   
    this.barStyle = style;
  }
  

  @Override
  public void uploadLogo(Logo logo) throws IOException, Exception {
    String currentUserId = getCurrentUserId();
    InputStream inputStream = getUploadDataAsStream(logo.getUploadId());
    FileItem fileItem = null;
    if (this.logoId == null) {
      fileItem = new FileItem(null,
                              logo_name,
                              "image/png",
                              FILE_API_NAME_SPACE,
                              logo.getSize(),
                              new Date(),
                              currentUserId,
                              false,
                              inputStream);
      fileItem = fileService.writeFile(fileItem);     
      settingService.set(Context.GLOBAL, Scope.GLOBAL, BRANDING_COMPANY_ID, SettingValue.create(fileItem.getFileInfo().getId()));
    } else {
      fileItem = new FileItem(this.logoId,
                              logo_name,
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
      try { // NOSONAR
        return new FileInputStream(new File(uploadResource.getStoreLocation()));
      } finally {
        uploadService.removeUploadResource(uploadId);
      }
    }
  }

  /**
   * 
   * @return
   */
  public static final String getCurrentUserId() {
    if (ConversationState.getCurrent() != null && ConversationState.getCurrent().getIdentity() != null) {
      return  ConversationState.getCurrent().getIdentity().getUserId();
    }
    return null;
  }

}
