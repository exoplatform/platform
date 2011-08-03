package org.exoplatform.setting.shared;

import static org.junit.Assert.*;

import org.junit.Test;

public class WizardUtilityTest {

  @Test
  public void testBuildLocaleUrl() {
    String url = "http://toto:8080/";
    assertEquals("http://toto:8080/?locale=fr", WizardUtility.buildLocaleUrl(url, null, null, "fr"));
    
    url = "http://toto:8080/?titi=2";
    assertEquals("http://toto:8080/?titi=2&locale=fr", WizardUtility.buildLocaleUrl(url, "?titi=2", null, "fr"));
    
    url = "http://toto:8080/?titi=2&locale=en";
    assertEquals("http://toto:8080/?titi=2&locale=fr", WizardUtility.buildLocaleUrl(url, "?titi=2&locale=en", "en", "fr"));
    
    url = "http://toto:8080/?titi=2&locale=en&tutu=3";
    assertEquals("http://toto:8080/?titi=2&locale=fr&tutu=3", WizardUtility.buildLocaleUrl(url, "?titi=2&locale=fr&tutu=3", "en", "fr"));
    
    url = "http://toto:8080/?locale=en";
    assertEquals("http://toto:8080/?locale=fr", WizardUtility.buildLocaleUrl(url, "?locale=en", "en", "fr"));
    
    url = "http://toto:8080/?local=en";
    assertEquals("http://toto:8080/?local=en&locale=fr", WizardUtility.buildLocaleUrl(url, "?local=en", null, "fr"));
    
    url = "http://toto:8080/?titi=2&tutu=3&";
    assertEquals("http://toto:8080/?titi=2&tutu=3&locale=fr", WizardUtility.buildLocaleUrl(url, "?titi=2&tutu=3&", null, "fr"));
    
    url = "http://toto:8080/?";
    assertEquals("http://toto:8080/?locale=fr", WizardUtility.buildLocaleUrl(url, "?", null, "fr"));
  }
}
