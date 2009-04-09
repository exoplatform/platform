package org.exoplatform.portal.gadget.core;

import java.io.File;
import java.io.IOException;

import org.apache.shindig.auth.BlobCrypterSecurityToken;
import org.apache.shindig.common.crypto.BasicBlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypterException;
import org.apache.shindig.common.util.TimeSource;
import org.exoplatform.web.application.RequestContext;

public class ExoDefaultSecurityTokenGenerator implements SecurityTokenGenerator {
  private String           containerKey;

  private final TimeSource timeSource;

  public ExoDefaultSecurityTokenGenerator() throws Exception {
    // TODO should be moved to config
    // generateKeys("RSA", 1024);
    this.containerKey = "key.txt";
    this.timeSource = new TimeSource();
  }

//  private static void generateKeys(String keyAlgorithm, int numBits) {
//    FileOutputStream keyFile = null;
//    try {
//      keyFile = new FileOutputStream("exokey.pem");
//
//      // RSA private key
//
//      CertAndKeyGen cakg = new CertAndKeyGen(keyAlgorithm, "SHA1WithRSA");
//      cakg.generate(1024);
//
//      PrivateKey privateKey = cakg.getPrivateKey();
//
//      keyFile.write("-----BEGIN RSA PRIVATE KEY-----\n".getBytes());
//      // wrap at 64
//      int wrapIndex = 64;
//      StringBuffer sb = new StringBuffer(new String(Base64.encode(privateKey.getEncoded())));
//      for (int i = wrapIndex; i < sb.length(); i = i + wrapIndex + 1) {
//        sb.insert(i, "\n");
//      }
//      keyFile.write((sb.toString()).getBytes());
//      keyFile.write("\n-----END RSA PRIVATE KEY-----\n".getBytes());
//
//      X500Name name = new X500Name("One", "Two", "Three", "Four", "Five", "Six");
//
//      X509Certificate certificate = cakg.getSelfCertificate(name, 2000000);
//      System.out.println("\n CN: " + certificate.getSubjectDN());
//      keyFile.write("-----BEGIN CERTIFICATE-----\n".getBytes());
//      // wrap at 64
//      wrapIndex = 64;
//      sb = new StringBuffer(new String(Base64.encode(certificate.getEncoded())));
//      for (int i = wrapIndex; i < sb.length(); i = i + wrapIndex + 1) {
//        sb.insert(i, "\n");
//      }
//      keyFile.write(sb.toString().getBytes());
//      keyFile.write("\n-----END CERTIFICATE-----".getBytes());
//    } catch (Exception e) {
//      e.printStackTrace();
//    } finally {
//      Safe.close(keyFile);
//    }
//  }

  protected String createToken(String gadgetURL,
                               String owner,
                               String viewer,
                               Long moduleId,
                               String container) {
    try {
      BlobCrypterSecurityToken t = new BlobCrypterSecurityToken(getBlobCrypter(this.containerKey),
                                                                container,
                                                                null);

      t.setAppUrl(gadgetURL);
      t.setModuleId(moduleId);
      t.setOwnerId(owner);
      t.setViewerId(viewer);
      t.setTrustedJson("trusted");

      return t.encrypt();
    } catch (IOException e) {
      e.printStackTrace(); // To change body of catch statement use File |
                           // Settings | File Templates.
    } catch (BlobCrypterException e) {
      e.printStackTrace(); // To change body of catch statement use File |
                           // Settings | File Templates.
    }
    return null;
  }

  public String createToken(String gadgetURL, Long moduleId) {
    RequestContext context = RequestContext.getCurrentInstance();
    // context.get
    String rUser = context.getRemoteUser();
    String viewer = rUser;

    return createToken(gadgetURL, viewer, rUser, moduleId, "default");
  }

  private BlobCrypter getBlobCrypter(String fileName) throws IOException {
    BasicBlobCrypter c = new BasicBlobCrypter(new File(fileName));
    c.timeSource = timeSource;
    return c;
  }

}
