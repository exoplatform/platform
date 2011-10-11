package org.exoplatform.platform.security;

import org.exoplatform.services.security.PasswordEncrypter;
import org.picketlink.idm.impl.api.PasswordCredential;

public class MD5HexPasswordEncrypter implements PasswordEncrypter {

  @Override
  public byte[] encrypt(byte[] plainPassword) {
    return PasswordCredential.md5AsHexString(new String(plainPassword)).getBytes();
  }

}
