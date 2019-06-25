package org.exoplatform.platform.common.branding;

import org.apache.commons.io.IOUtils;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.file.model.FileInfo;
import org.exoplatform.commons.file.model.FileItem;
import org.exoplatform.commons.file.services.FileService;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.upload.UploadResource;
import org.exoplatform.upload.UploadService;
import org.junit.Test;

import org.exoplatform.commons.api.settings.SettingService;
import org.mockito.ArgumentCaptor;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BrandingServiceImplTest {

  @Test
  public void shouldGetDefaultBrandingInformationWhenNoUpdate() {
    // Given
    SettingService settingService = mock(SettingService.class);
    FileService fileService = mock(FileService.class);
    UploadService uploadService = mock(UploadService.class);

    InitParams initParams = new InitParams();
    ValueParam companyName = new ValueParam();
    companyName.setName(BrandingServiceImpl.BRANDING_COMPANY_NAME_INIT_PARAM);
    companyName.setValue("Default Company Name");
    initParams.addParam(companyName);

    BrandingService brandingService = new BrandingServiceImpl(initParams, settingService, fileService, uploadService);

    // When
    Branding brandingInformation = brandingService.getBrandingInformation();

    // Then
    assertNotNull(brandingInformation);
    assertEquals("Default Company Name", brandingInformation.getCompanyName());
  }

  @Test
  public void shouldGetUpdatedBrandingInformationWhenInformationUpdated() {
    // Given
    SettingService settingService = mock(SettingService.class);
    when(settingService.get(eq(Context.GLOBAL), eq(Scope.GLOBAL), eq(BrandingServiceImpl.BRANDING_COMPANY_NAME_SETTING_KEY))).thenReturn(new SettingValue("Updated Company Name"));
    FileService fileService = mock(FileService.class);
    UploadService uploadService = mock(UploadService.class);

    InitParams initParams = new InitParams();
    ValueParam companyName = new ValueParam();
    companyName.setName(BrandingServiceImpl.BRANDING_COMPANY_NAME_INIT_PARAM);
    companyName.setValue("Default Company Name");
    initParams.addParam(companyName);

    BrandingService brandingService = new BrandingServiceImpl(initParams, settingService, fileService, uploadService);

    // When
    Branding brandingInformation = brandingService.getBrandingInformation();

    // Then
    assertNotNull(brandingInformation);
    assertEquals("Updated Company Name", brandingInformation.getCompanyName());
  }

  @Test
  public void shouldUpdateCompanyNameAndTopBarThemeWhenInformationUpdated() throws Exception {
    // Given
    SettingService settingService = mock(SettingService.class);
    FileService fileService = mock(FileService.class);
    UploadService uploadService = mock(UploadService.class);

    InitParams initParams = new InitParams();
    ValueParam companyName = new ValueParam();
    companyName.setName(BrandingServiceImpl.BRANDING_COMPANY_NAME_INIT_PARAM);
    companyName.setValue("Default Company Name");
    initParams.addParam(companyName);

    BrandingService brandingService = new BrandingServiceImpl(initParams, settingService, fileService, uploadService);

    Branding newBranding = new Branding();
    newBranding.setCompanyName("New Company Name");
    newBranding.setTopBarTheme("Pink");

    ArgumentCaptor<Context> settingContext = ArgumentCaptor.forClass(Context.class);
    ArgumentCaptor<Scope> settingScope = ArgumentCaptor.forClass(Scope.class);
    ArgumentCaptor<String> settingKey = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<SettingValue> settingValue = ArgumentCaptor.forClass(SettingValue.class);

    // When
    brandingService.updateBrandingInformation(newBranding);

    // Then
    verify(settingService, times(2)).set(settingContext.capture(), settingScope.capture(), settingKey.capture(), settingValue.capture());
    List<Context> contexts = settingContext.getAllValues();
    List<Scope> scopes = settingScope.getAllValues();
    List<String> keys = settingKey.getAllValues();
    List<SettingValue> values = settingValue.getAllValues();
    assertEquals(Context.GLOBAL, contexts.get(0));
    assertEquals(Scope.GLOBAL, scopes.get(0));
    assertEquals(BrandingServiceImpl.BRANDING_COMPANY_NAME_SETTING_KEY, keys.get(0));
    assertEquals("New Company Name", values.get(0).getValue());
    assertEquals(Context.GLOBAL, contexts.get(1));
    assertEquals(Scope.GLOBAL, scopes.get(1));
    assertEquals(BrandingServiceImpl.BRANDING_TOPBAR_THEME_SETTING_KEY, keys.get(1));
    assertEquals("Pink", values.get(1).getValue());
  }

  @Test
  public void shouldUpdateLogoWhenLogoUpdatedByData() throws Exception {
    // Given
    SettingService settingService = mock(SettingService.class);
    FileService fileService = mock(FileService.class);
    FileInfo fileInfo = new FileInfo(1L, "myLogo", "image/png",
            BrandingServiceImpl.FILE_API_NAME_SPACE, "myLogo".getBytes().length, new Date(), "john", null, false);
    FileItem fileItem = new FileItem(fileInfo, null);
    when(fileService.writeFile(any(FileItem.class))).thenReturn(fileItem);
    when(fileService.getFileInfo(anyLong())).thenReturn(fileInfo);
    UploadService uploadService = mock(UploadService.class);

    InitParams initParams = new InitParams();
    ValueParam companyName = new ValueParam();
    companyName.setName(BrandingServiceImpl.BRANDING_COMPANY_NAME_INIT_PARAM);
    companyName.setValue("Default Company Name");
    initParams.addParam(companyName);

    BrandingService brandingService = new BrandingServiceImpl(initParams, settingService, fileService, uploadService);

    Branding newBranding = new Branding();
    Logo logo = new Logo();
    logo.setData("myLogo".getBytes());
    logo.setSize(logo.getData().length);
    newBranding.setLogo(logo);

    ArgumentCaptor<Context> settingContextArgumentCaptor = ArgumentCaptor.forClass(Context.class);
    ArgumentCaptor<Scope> settingScopeArgumentCaptor = ArgumentCaptor.forClass(Scope.class);
    ArgumentCaptor<String> settingKeyArgumentCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<SettingValue> settingValueArgumentCaptor = ArgumentCaptor.forClass(SettingValue.class);
    ArgumentCaptor<FileItem> fileItemArgumentCaptor = ArgumentCaptor.forClass(FileItem.class);

    // When
    brandingService.updateBrandingInformation(newBranding);

    // Then
    verify(settingService, times(1)).set(settingContextArgumentCaptor.capture(), settingScopeArgumentCaptor.capture(), settingKeyArgumentCaptor.capture(), settingValueArgumentCaptor.capture());
    verify(fileService, times(1)).writeFile(fileItemArgumentCaptor.capture());
    List<Context> contexts = settingContextArgumentCaptor.getAllValues();
    List<Scope> scopes = settingScopeArgumentCaptor.getAllValues();
    List<String> keys = settingKeyArgumentCaptor.getAllValues();
    List<SettingValue> values = settingValueArgumentCaptor.getAllValues();
    assertEquals(Context.GLOBAL, contexts.get(0));
    assertEquals(Scope.GLOBAL, scopes.get(0));
    assertEquals(BrandingServiceImpl.BRANDING_LOGO_ID_SETTING_KEY, keys.get(0));
    assertEquals("1", values.get(0).getValue());
    List<FileItem> fileItems = fileItemArgumentCaptor.getAllValues();
    assertEquals("myLogo", new String(fileItems.get(0).getAsByte()));
  }

  @Test
  public void shouldUpdateLogoWhenLogoUpdatedByUploadId() throws Exception {
    // Given
    SettingService settingService = mock(SettingService.class);
    FileService fileService = mock(FileService.class);
    FileInfo fileInfo = new FileInfo(2L, "myLogo", "image/png",
            BrandingServiceImpl.FILE_API_NAME_SPACE, "myLogo".getBytes().length, new Date(), "john", null, false);
    FileItem fileItem = new FileItem(fileInfo, null);
    when(fileService.writeFile(any(FileItem.class))).thenReturn(fileItem);
    when(fileService.getFileInfo(anyLong())).thenReturn(fileInfo);
    String uploadId = "1";
    UploadService uploadService = mock(UploadService.class);
    UploadResource uploadResource = new UploadResource(uploadId);
    URL resource = this.getClass().getResource("/branding/logo.png");
    uploadResource.setStoreLocation(resource.getPath());
    when(uploadService.getUploadResource(eq(uploadId))).thenReturn(uploadResource);

    InitParams initParams = new InitParams();
    ValueParam companyName = new ValueParam();
    companyName.setName(BrandingServiceImpl.BRANDING_COMPANY_NAME_INIT_PARAM);
    companyName.setValue("Default Company Name");
    initParams.addParam(companyName);

    BrandingService brandingService = new BrandingServiceImpl(initParams, settingService, fileService, uploadService);

    Branding newBranding = new Branding();
    Logo logo = new Logo();
    logo.setUploadId(uploadId);
    newBranding.setLogo(logo);

    ArgumentCaptor<Context> settingContextArgumentCaptor = ArgumentCaptor.forClass(Context.class);
    ArgumentCaptor<Scope> settingScopeArgumentCaptor = ArgumentCaptor.forClass(Scope.class);
    ArgumentCaptor<String> settingKeyArgumentCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<SettingValue> settingValueArgumentCaptor = ArgumentCaptor.forClass(SettingValue.class);
    ArgumentCaptor<FileItem> fileItemArgumentCaptor = ArgumentCaptor.forClass(FileItem.class);

    // When
    brandingService.updateBrandingInformation(newBranding);

    // Then
    verify(settingService, times(1)).set(settingContextArgumentCaptor.capture(), settingScopeArgumentCaptor.capture(), settingKeyArgumentCaptor.capture(), settingValueArgumentCaptor.capture());
    verify(fileService, times(1)).writeFile(fileItemArgumentCaptor.capture());
    List<Context> contexts = settingContextArgumentCaptor.getAllValues();
    List<Scope> scopes = settingScopeArgumentCaptor.getAllValues();
    List<String> keys = settingKeyArgumentCaptor.getAllValues();
    List<SettingValue> values = settingValueArgumentCaptor.getAllValues();
    assertEquals(Context.GLOBAL, contexts.get(0));
    assertEquals(Scope.GLOBAL, scopes.get(0));
    assertEquals(BrandingServiceImpl.BRANDING_LOGO_ID_SETTING_KEY, keys.get(0));
    assertEquals("2", values.get(0).getValue());
    List<FileItem> fileItems = fileItemArgumentCaptor.getAllValues();
    assertTrue(Arrays.equals(IOUtils.toByteArray(resource), fileItems.get(0).getAsByte()));
  }
}
