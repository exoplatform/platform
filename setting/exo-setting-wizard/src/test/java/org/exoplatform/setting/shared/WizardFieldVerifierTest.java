package org.exoplatform.setting.shared;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

  @Test
  public void testIsValidIp() {
    assertTrue(WizardFieldVerifier.isValidIp("127.0.0.1"));
    assertTrue(WizardFieldVerifier.isValidIp("255.255.255.255"));
    assertFalse(WizardFieldVerifier.isValidIp("256.255.255.0"));
    assertFalse(WizardFieldVerifier.isValidIp("255.256.255.0"));
    assertFalse(WizardFieldVerifier.isValidIp("255.255.256.0"));
    assertFalse(WizardFieldVerifier.isValidIp("255.255.255.256"));
    assertFalse(WizardFieldVerifier.isValidIp("a.1.1.1"));
    assertFalse(WizardFieldVerifier.isValidIp("0.0.0."));
    assertFalse(WizardFieldVerifier.isValidIp("0.0.0"));
    assertFalse(WizardFieldVerifier.isValidIp("0007.0.0.1"));
    assertTrue(WizardFieldVerifier.isValidIp("001.1.1.2"));
    assertFalse(WizardFieldVerifier.isValidIp("1.1.1.2.5"));
  }
}
