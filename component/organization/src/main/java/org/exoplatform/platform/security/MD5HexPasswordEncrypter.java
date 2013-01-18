package org.exoplatform.platform.security;

import org.exoplatform.services.security.PasswordEncrypter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5HexPasswordEncrypter implements PasswordEncrypter {

  @Override
  public byte[] encrypt(byte[] plainPassword) {
    return md5AsHexString(new String(plainPassword)).getBytes();
  }
    public static byte[] md5(String text)
    {
        // arguments check
        if (text == null)
        {
            throw new NullPointerException("null text");
        }

        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            return md.digest();
        }
        catch (NoSuchAlgorithmException e)
        {

            throw new RuntimeException("Cannot find MD5 algorithm");
        }
    }
    /**
     * Computes an md5 hash and returns the result as a string in hexadecimal format.
     *
     * @param text the hashed string
     * @return the string hash
     * @throws NullPointerException if text is null
     */
    public static String md5AsHexString(String text)
    {
        return toHexString(md5(text));
    }

    /**
     * Returns a string in the hexadecimal format.
     *
     * @param bytes the converted bytes
     * @return the hexadecimal string representing the bytes data
     * @throws IllegalArgumentException if the byte array is null
     */
    public static String toHexString(byte[] bytes)
    {
        if (bytes == null)
        {
            throw new IllegalArgumentException("byte array must not be null");
        }
        StringBuffer hex = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++)
        {
            hex.append(Character.forDigit((bytes[i] & 0XF0) >> 4, 16));
            hex.append(Character.forDigit((bytes[i] & 0X0F), 16));
        }
        return hex.toString();
    }

}
