package org.exoplatform.bonitasoft.services.rest.mail;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.mail.MailService;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.rest.resource.ResourceContainer;

@Path("mail")
public class ManageMail implements ResourceContainer {
  private static Log logger = ExoLogger.getLogger(ManageMail.class);
  private OrganizationService organizationService;
  private MailService mailService;
  String mailSender = "";

  private final String mailMessageBody = new StringBuffer()
      .append("<table cellspacing='0' cellpadding='0' border='0' width='550' ")
      .append("style='max-width: 550px; border-top: 4px solid #FFD400; font: 12px arial,sans-serif; margin: 0pt auto;'")
      .append("><tbody><tr><td> <div style='font:13px arial, sans-serif;width:540px'>")
      .append("<p> Dear @UserFullName;</p> Your Document  <a href='")
      .append(System.getProperty("org.exoplatform.runtime.conf.cas.server.name"))
      .append("/portal/private/intranet/editor?path=@Link'>@NodeName</a> is valid.")
      .append("<br/>See the last comments on your document :<br/><br/><FIELDSET>")
      .append("<LEGEND align=top><b>Last Comments</b></LEGEND>")
      .append("<div style=' max-height: 280px; max-width: 660px; overflow : auto;'>@Comments</div></FIELDSET><br/>")
      .append("<div style='margin-top: 15px; margin-bottom: 10px; border-bottom: 1px solid #FFD400; line-height: 1px;'>")
      .append("&nbsp;</div></td></tr></tbody></table>").toString();

  public ManageMail(OrganizationService organizationService, MailService mailService, InitParams initParams) {
    this.organizationService = organizationService;
    this.mailService = mailService;
    if (initParams != null) {
      if (initParams.containsKey("mail.sender")) {
        mailSender = initParams.getValueParam("mail.sender").getValue();
      } else {
        throw new IllegalStateException("init param 'mail.sender' not set");
      }
    } else {
      throw new IllegalStateException("init params not set");
    }
  }

  /**
   * send mail to inform user that the node is published
   * 
   * @param userName
   * @param link
   * @param commentaires
   * @throws Exception
   */
  @POST
  @Path("validate")
  public void sendMail(@FormParam("userName") String userName, @FormParam("link") String link,
      @FormParam("commentaires") String comments) throws Exception {
    String[] tab = link.split("/");
    User user = organizationService.getUserHandler().findUserByName(userName);
    String subject = "Document validation: " + tab[tab.length - 1];
    Session mailSession = mailService.getMailSession();
    MimeMessage msg = new MimeMessage(mailSession);
    String mailBody = this.mailMessageBody.replace("@UserFullName", user.getFullName());
    mailBody = mailBody.replace("@Link", link);
    mailBody = mailBody.replace("@NodeName", tab[tab.length - 1]);
    mailBody = mailBody.replace("@Comments", comments);
    msg.setFrom(new InternetAddress(mailSender));
    msg.setSubject(subject);
    msg.setContent(mailBody, "text/html ; charset=ISO-8859-1");
    String to = user.getEmail();
    msg.setRecipient(RecipientType.TO, new InternetAddress(to));
    mailService.sendMessage(msg);
    if (logger.isDebugEnabled()) {
      logger.debug("### send mail finish...");
    }
  }

}
