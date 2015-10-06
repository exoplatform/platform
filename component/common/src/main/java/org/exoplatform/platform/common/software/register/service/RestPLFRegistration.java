/**
 * Copyright (C) 2009 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.platform.common.software.register.service;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Path("/plf")
public class RestPLFRegistration implements ResourceContainer {
    private static final Log LOG = ExoLogger.getExoLogger(RestPLFRegistration.class);
    @GET
    @Path("checkConnection")
    @Produces("html/text")
    public Response checkConnection() throws Exception {
        String pingServerURL = SoftwareRegistrationService.SOFTWARE_REGISTRATION_HOST;
        try {
            URL url = new URL(pingServerURL);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            return Response.ok(String.valueOf(HttpURLConnection.HTTP_OK == urlConn.getResponseCode())).build();
        } catch (MalformedURLException e) {
            LOG.error("LeadCapture : Error creating HTTP connection to the server : " + pingServerURL);
        } catch (IOException e) {
            LOG.error("LeadCapture : Error creating HTTP connection to the server : " + pingServerURL);
        }
        return Response.ok(String.valueOf(false)).build();
    }
}
