package org.exoplatform.platform.common.rest.branding;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.file.services.FileService;

import org.exoplatform.platform.common.branding.Branding;
import org.exoplatform.platform.common.branding.BrandingRestResourcesV1;
import org.exoplatform.platform.common.branding.BrandingService;
import org.exoplatform.platform.common.rest.services.BaseRestServicesTestCase;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.test.mock.MockHttpServletRequest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrandingRestResourcesTest extends BaseRestServicesTestCase {

  private BrandingService brandingService;
  private FileService fileService;
  private SettingService settingService;

  protected Class<?> getComponentClass() {
    return BrandingRestResourcesV1.class;
  }
  
  public void  testbrandingInformations() throws Exception {
    // Given
    String path = "/v1/platform/branding/";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(path, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);

    brandingService = mock(BrandingService.class);
    fileService = mock(FileService.class);
    settingService = mock(SettingService.class);
    getContainer().registerComponentInstance("BrandingService", brandingService);
    getContainer().registerComponentInstance("FileService", fileService);
    getContainer().registerComponentInstance("SettingService", settingService);

    Branding branding = new Branding();
    branding.setCompanyName("test1");
    branding.setTopBarTheme("Dark");
    when(brandingService.getBrandingInformation()).thenReturn(branding);

    // When
    ContainerResponse resp = launcher.service("GET", path, "", null, null, envctx);

    // Then
    assertEquals(200, resp.getStatus());
    Object entity = resp.getEntity();
    assertNotNull(entity);
    assertTrue(entity instanceof Branding);
    Branding brandingResp = (Branding) entity;
    assertEquals("test1", brandingResp.getCompanyName());
    assertEquals("Dark", brandingResp.getTopBarTheme());
  }
   
}
