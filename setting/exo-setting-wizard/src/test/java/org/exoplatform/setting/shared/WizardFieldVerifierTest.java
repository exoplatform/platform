package org.exoplatform.setting.shared;

import static org.junit.Assert.*;

import org.junit.Test;


public class WizardFieldVerifierTest {

  @Test
  public void testIsValidSuperUserName() {
    assertFalse(WizardFieldVerifier.isValidSuperUserName(""));
    assertFalse(WizardFieldVerifier.isValidSuperUserName(null));
    assertFalse(WizardFieldVerifier.isValidSuperUserName("aaa"));
    assertTrue(WizardFieldVerifier.isValidSuperUserName("aaaa"));
  }

  @Test
  public void testIsValidSuperUserPassword() {
    assertFalse(WizardFieldVerifier.isValidSuperUserPassword(""));
    assertFalse(WizardFieldVerifier.isValidSuperUserPassword(null));
    assertFalse(WizardFieldVerifier.isValidSuperUserPassword("aaa"));
    assertTrue(WizardFieldVerifier.isValidSuperUserPassword("aaaa"));
  }
  
  @Test
  public void testIsValidSuperUserPassword2() {
    assertFalse(WizardFieldVerifier.isValidSuperUserPassword2(null, null));
    assertFalse(WizardFieldVerifier.isValidSuperUserPassword2("", null));
    assertFalse(WizardFieldVerifier.isValidSuperUserPassword2(null, ""));
    assertFalse(WizardFieldVerifier.isValidSuperUserPassword2("aaaa", "aaa"));
    assertTrue(WizardFieldVerifier.isValidSuperUserPassword2("aaaa", "aaaa"));
  }

  @Test
  public void testIsValidSuperUserEmail() {
    assertFalse(WizardFieldVerifier.isValidSuperUserEmail("aaaa"));
    assertFalse(WizardFieldVerifier.isValidSuperUserEmail("aaaa@aa"));
    assertFalse(WizardFieldVerifier.isValidSuperUserEmail("aaaa@aa.a"));
    assertFalse(WizardFieldVerifier.isValidSuperUserEmail("aaaa@aa.aaaaa"));
    assertTrue(WizardFieldVerifier.isValidSuperUserEmail("aaaa@aa.aaa"));
  }
}
