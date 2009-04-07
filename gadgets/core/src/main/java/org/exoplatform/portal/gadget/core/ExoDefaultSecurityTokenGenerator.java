package org.exoplatform.portal.gadget.core;

import org.apache.shindig.auth.BlobCrypterSecurityToken;
import org.apache.shindig.common.crypto.BlobCrypterException;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.crypto.BasicBlobCrypter;
import org.apache.shindig.common.util.TimeSource;
import org.exoplatform.web.application.RequestContext;

import java.io.File;
import java.io.IOException;




public class ExoDefaultSecurityTokenGenerator implements SecurityTokenGenerator{
    private final TimeSource timeSource;


  public ExoDefaultSecurityTokenGenerator() {
    this.timeSource = new TimeSource();
  }

  protected String createToken(String gadgetURL, String owner, String viewer, Long moduleId, String container) {
      try {
        BlobCrypterSecurityToken t = new BlobCrypterSecurityToken(
          getBlobCrypter(ExoContainerConfig.getKeyPath()), container, null);

        t.setAppUrl(gadgetURL);
        t.setModuleId(moduleId);
        t.setOwnerId(owner);
        t.setViewerId(viewer);
        t.setTrustedJson("trusted");

        return t.encrypt();
    } catch (IOException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (BlobCrypterException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
      return null;
  }

  public String createToken(String gadgetURL, Long moduleId) {
    RequestContext context = RequestContext.getCurrentInstance();
    String rUser = context.getRemoteUser();


    return createToken(gadgetURL, "Anonymous", rUser, moduleId, "default");
  }

  private BlobCrypter getBlobCrypter(String fileName) throws IOException {
    BasicBlobCrypter c = new BasicBlobCrypter(new File(fileName));
    c.timeSource = timeSource;
    return c;
  }
    
}

