package org.exoplatform.setting.shared;

import static org.junit.Assert.*;

import org.junit.Test;


public class WizardFieldVerifierTest {

  @Test
  public void testIsValidTextField() {
    assertFalse(WizardFieldVerifier.isValidTextField(""));
    assertFalse(WizardFieldVerifier.isValidTextField(null));
    assertFalse(WizardFieldVerifier.isValidTextField("aaa"));
    assertTrue(WizardFieldVerifier.isValidTextField("aaaa"));
  }

  @Test
  public void testIsValidNumberField() {
    assertFalse(WizardFieldVerifier.isValidNumberField(""));
    assertFalse(WizardFieldVerifier.isValidNumberField(null));
    assertFalse(WizardFieldVerifier.isValidNumberField("sds"));
    assertTrue(WizardFieldVerifier.isValidNumberField("5"));
    assertTrue(WizardFieldVerifier.isValidNumberField("565656565"));
    assertFalse(WizardFieldVerifier.isValidNumberField("565dfdf656565"));
    assertFalse(WizardFieldVerifier.isValidNumberField("5.2"));
    assertFalse(WizardFieldVerifier.isValidNumberField("5,2"));
  }

  @Test
  public void testIsValidPassword() {
    assertFalse(WizardFieldVerifier.isValidPassword(""));
    assertFalse(WizardFieldVerifier.isValidPassword(null));
    assertFalse(WizardFieldVerifier.isValidPassword("aaa"));
    assertFalse(WizardFieldVerifier.isValidPassword("aaaa"));
    assertTrue(WizardFieldVerifier.isValidPassword("aaaaaa"));
  }
  
  @Test
  public void testIsValidPassword2() {
    assertFalse(WizardFieldVerifier.isValidPassword2(null, null));
    assertFalse(WizardFieldVerifier.isValidPassword2("", null));
    assertFalse(WizardFieldVerifier.isValidPassword2(null, ""));
    assertFalse(WizardFieldVerifier.isValidPassword2("aaaa", "aaa"));
    assertTrue(WizardFieldVerifier.isValidPassword2("aaaa", "aaaa"));
  }

  @Test
  public void testIsValidSuperUserEmail() {
    assertFalse(WizardFieldVerifier.isValidEmail("aaaa"));
    assertFalse(WizardFieldVerifier.isValidEmail("aaaa@aa"));
    assertFalse(WizardFieldVerifier.isValidEmail("aaaa@aa.a"));
    assertFalse(WizardFieldVerifier.isValidEmail("aaaa@aa.aaaaa"));
    assertTrue(WizardFieldVerifier.isValidEmail("aaaa@aa.aaa"));
  }
}
