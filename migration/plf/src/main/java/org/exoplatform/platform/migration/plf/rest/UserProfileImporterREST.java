package org.exoplatform.platform.migration.plf.rest;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.impl.UserProfileImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

@Path("/userProfiles")
public class UserProfileImporterREST implements ResourceContainer{
  public OrganizationService organizationService = null;

  public UserProfileImporterREST(OrganizationService organizationService, InitParams initParams) {
    this.organizationService = organizationService;
  }

  @GET
  public Response importProfiles(@QueryParam("id") String id) throws Exception {
    StringBuffer responseStringBuffer = new StringBuffer();
    responseStringBuffer.append("<form action='/portal/rest/userProfiles/import/' method='POST'>");
    responseStringBuffer.append("  <input type='text' name='filePath'/>");
    responseStringBuffer.append("  <input type='submit'/>");
    responseStringBuffer.append("</form>");
    return Response.ok().entity(responseStringBuffer.toString()).build();
  }

  @POST
  @Path("/import/")
  public Response put(@QueryParam("filePath") String filePath) throws Exception {
    XStream xstream_ = new XStream(new XppDriver());
    xstream_.alias("user-profile", UserProfileImpl.class);

    FileInputStream fin = new FileInputStream(filePath);
    ZipInputStream zin = new ZipInputStream(fin);
    ZipEntry ze = null;
    while ((ze = zin.getNextEntry()) != null) {
      if (ze.getName().contains("_profile.xml")) {
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        for (int c = zin.read(); c != -1; c = zin.read()) {
          fout.write(c);
        }
        zin.closeEntry();

        UserProfileImpl userProfile = (UserProfileImpl) xstream_.fromXML(new String(fout.toByteArray()));
        System.out.println("userProfile = " + userProfile.getUserName());
        User user = organizationService.getUserHandler().findUserByName(userProfile.getUserName());
        if (user != null) {
          organizationService.getUserProfileHandler().saveUserProfile(userProfile, true);
        } else {
          System.out.println("==> WARNING: userProfile = " + userProfile.getUserName() + " doesn't exist ");
        }
      }
    }
    zin.close();
    return Response.ok().entity("Imported successfully !").build();
  }
}