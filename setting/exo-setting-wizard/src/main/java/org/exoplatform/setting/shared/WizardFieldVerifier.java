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
   * Screen super user
   *======================================================================*/
  
  public static boolean isValidSuperUserName(String name) {
    if (name == null) {
      return false;
    }
    return name.length() > 3;
  }

  public static boolean isValidSuperUserPassword(String password) {
    if (password == null) {
      return false;
    }
    return password.length() > 3;
  }

  public static boolean isValidSuperUserPassword2(String password, String password2) {
    if (password == null || password2 == null) {
      return false;
    }
    return password.equals(password2);
  }

  public static boolean isValidSuperUserEmail(String email) {
    if (email == null) {
      return false;
    }
    return email.matches("[a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}");
  }
}
