package org.exoplatform.portal.gadget.core;

/**
 * Created by IntelliJ IDEA.
 * User: jeremi
 * Date: Oct 6, 2008
 * Time: 10:53:13 AM
 * To change this template use File | Settings | File Templates.
 */
public interface SecurityTokenGenerator {

  String createToken(String gadgetURL, Long moduleId);
}
