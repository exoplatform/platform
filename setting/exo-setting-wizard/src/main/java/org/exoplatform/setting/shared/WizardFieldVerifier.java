package org.exoplatform.setting.shared;

/**
 * <p>
 * FieldVerifier validates that the name the user enters is valid.
 * </p>
 * <p>
 * This class is in the <code>shared</code> packing because we use it in both
 * the client code and on the server. On the client, we verify that the name is
 * valid before sending an RPC request so the user doesn't have to wait for a
 * network round trip to get feedback. On the server, we verify that the name is
 * correct to ensure that the input is correct regardless of where the RPC
 * originates.
 * </p>
 */
public class WizardFieldVerifier {

  /*=======================================================================
   * Generic
   *======================================================================*/
  
  public static boolean isValidTextField(String name) {
    if (name == null) {
      return false;
    }
    return name.length() > 3;
  }
  
  public static boolean isValidNumberField(String number) {
    if (number == null) {
      return false;
    }
    if(number.length() < 1) {
      return false;
    }
    return number.matches("[0-9]*");
  }

  public static boolean isValidPassword(String password) {
    if (password == null) {
      return false;
    }
    return password.length() >= 6;
  }

  public static boolean isValidPassword2(String password, String password2) {
    if (password == null || password2 == null) {
      return false;
    }
    return password.equals(password2);
  }

  public static boolean isValidEmail(String email) {
    if (email == null) {
      return false;
    }
    return email.matches("[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}");
  }
}
