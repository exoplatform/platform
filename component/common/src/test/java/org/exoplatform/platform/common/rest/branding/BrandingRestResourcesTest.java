package org.exoplatform.platform.common.rest.branding;

import javax.servlet.http.HttpServletRequest;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.file.services.FileService;

import org.exoplatform.platform.common.branding.Branding;
import org.exoplatform.platform.common.branding.BrandingRestResourcesV1;
import org.exoplatform.platform.common.branding.BrandingService;
import org.exoplatform.platform.common.rest.services.BaseRestServicesTestCase;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.EnvironmentContext;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;
import org.exoplatform.services.test.mock.MockHttpServletRequest;
import org.json.JSONObject;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.mockito.Mockito.*;

public class BrandingRestResourcesTest extends BaseRestServicesTestCase {

  private BrandingService brandingService;
  private FileService fileService;
  private SettingService settingService;

  protected Class<?> getComponentClass() {
    return BrandingRestResourcesV1.class;
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();

    brandingService = mock(BrandingService.class);
    fileService = mock(FileService.class);
    settingService = mock(SettingService.class);
    getContainer().registerComponentInstance("BrandingService", brandingService);
    getContainer().registerComponentInstance("FileService", fileService);
    getContainer().registerComponentInstance("SettingService", settingService);
  }

  @Override
  public void tearDown() throws Exception {
    getContainer().unregisterComponent("BrandingService");
    getContainer().unregisterComponent("FileService");
    getContainer().unregisterComponent("SettingService");
    super.tearDown();
  }

  public void testGetBrandingInformation() throws Exception {
    // Given
    String path = "/v1/platform/branding/";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(path, null, 0, "GET", null);
    envctx.put(HttpServletRequest.class, httpRequest);

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

  public void testUpdateBrandingInformation() throws Exception {
    // Given
    String path = "/v1/platform/branding/";
    EnvironmentContext envctx = new EnvironmentContext();
    HttpServletRequest httpRequest = new MockHttpServletRequest(path, null, 0, "PUT", null);
    envctx.put(HttpServletRequest.class, httpRequest);

    Branding branding = new Branding();
    branding.setCompanyName("test1");
    branding.setTopBarTheme("Dark");
    JSONObject jsonBranding = new JSONObject();
    jsonBranding.put("companyName", branding.getCompanyName());
    jsonBranding.put("topBarTheme", branding.getTopBarTheme());

    ArgumentCaptor<Branding> brandingArgumentCaptor = ArgumentCaptor.forClass(Branding.class);

    Map<String, List<String>> headers = new HashMap<>();
    headers.put("Content-Type", Arrays.asList("application/json"));

    // When
    ContainerResponse resp = launcher.service("PUT", path, "", headers, jsonBranding.toString().getBytes(), envctx);

    // Then
    assertEquals(200, resp.getStatus());
    Object entity = resp.getEntity();
    assertNull(entity);

    verify(brandingService, times(1)).updateBrandingInformation(brandingArgumentCaptor.capture());
    assertNotNull(brandingArgumentCaptor);
    Branding caturedBranding = brandingArgumentCaptor.getValue();
    assertEquals("test1", caturedBranding.getCompanyName());
    assertEquals("Dark", caturedBranding.getTopBarTheme());
  }
}
