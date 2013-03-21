package org.exoplatform.platform.common.account.setup.web;

import org.exoplatform.commons.info.MissingProductInformationException;
import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 3/21/13
 */

public  class PingBackServlet extends HttpServlet {
    private static final Log LOG = ExoLogger.getExoLogger(PingBackServlet.class);
    private static final String pingBackUrl = "http://www.exoplatform.com/company/en/products/e-01011000-01101111";
    private static final long serialVersionUID = 6467955354840693802L;
    private static boolean loopfuseFormDisplayed = false;
    public static final String LOOP_FUSE_FORM_DISPLAYED = "formDisplayed";
    public static final String USER_HOME = System.getProperty("user.home");
    public static final String EXO_HOME_FOLDER = USER_HOME + "/.eXo";
    public static final String PING_BACK_FILE = "landingPage.xml";

    public static final String PRODUCT_NAME = "Platform";
    private static String productNameAndVersion;


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (isConnectedToInternet()) {
            loopfuseFormDisplayed = true;
            try {
                writePingBackFormDisplayed( loopfuseFormDisplayed);
            } catch (MissingProductInformationException e) {

            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    public static boolean isConnectedToInternet() {
        // computes the Platform server URL, format http://server/
        String pingServerURL = pingBackUrl.substring(0, pingBackUrl.indexOf("/", "http://url".length()));
        try {
            URL url = new URL(pingServerURL);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            return (HttpURLConnection.HTTP_NOT_FOUND != urlConn.getResponseCode());
        } catch (MalformedURLException e) {
            LOG.error("LeadCapture : Error creating HTTP connection to the server : " + pingServerURL);

        } catch (IOException e) {
            LOG.error("LeadCapture : Error creating HTTP connection to the server : " + pingServerURL);
        }
        return false;
    }

    public static void writeToFile(String key, String value, String fileLocation) {
        if (fileLocation == null || fileLocation.isEmpty()) {
            throw new IllegalArgumentException("Illegal empty file Location parameter.");
        }
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            Properties properties = new Properties();
            File file = new File(fileLocation);
            if (file.exists()) {
                inputStream = new FileInputStream(fileLocation);
                properties.loadFromXML(inputStream);
                inputStream.close();
            } else {
                verifyAndCreateParentFolder(fileLocation);
            }
            properties.put(key, value);
            outputStream = new FileOutputStream(fileLocation);
            properties.storeToXML(outputStream, "");
            outputStream.close();
        } catch (Exception exception) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ioException) {
                    LOG.error("Error during close outputStream ", ioException);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioException) {
                    LOG.error("Error during close inputStream ", ioException);
                }
            }
        }
    }

    private static void verifyAndCreateParentFolder(String fileLocation) {
        String parentFolderPath = fileLocation.replace("\\", "/");
        int parentFolderPathEndIndex = fileLocation.lastIndexOf("/");
        if (parentFolderPathEndIndex >= 0) {
            parentFolderPath = fileLocation.substring(0, parentFolderPathEndIndex);
        }
        if (!new File(parentFolderPath).exists()) {
            new File(parentFolderPath).mkdirs();
        }
    }

    public static void writePingBackFormDisplayed(boolean loopfuseFormDisplayed) throws MissingProductInformationException {
        PingBackServlet.loopfuseFormDisplayed = loopfuseFormDisplayed;
        writeToFile(LOOP_FUSE_FORM_DISPLAYED, Boolean.toString(loopfuseFormDisplayed), getPingBackFileLocation() );
    }
    public static String getPingBackFileLocation() throws MissingProductInformationException {
        ProductInformations productInformations = (ProductInformations) PortalContainer.getInstance().getComponentInstanceOfType(ProductInformations.class);
        productNameAndVersion = PRODUCT_NAME  + productInformations.getVersion().trim();
        return EXO_HOME_FOLDER +"/"+ productNameAndVersion +"/"+ PING_BACK_FILE;
    }

    public static String getPingBackUrl() {
        return pingBackUrl;
    }

    public static boolean isLandingPageDisplayed() throws MissingProductInformationException {
        String loopfuseFormDisplayedString = readFromFile(LOOP_FUSE_FORM_DISPLAYED, getPingBackFileLocation());
        if (loopfuseFormDisplayedString != null && !loopfuseFormDisplayedString.isEmpty()) {
            loopfuseFormDisplayed = Boolean.parseBoolean(loopfuseFormDisplayedString);
        }
        return loopfuseFormDisplayed;
    }

    public static String readFromFile(String key, String fileLocation) throws MissingProductInformationException {
        if (fileLocation != null && !fileLocation.isEmpty() && !new File(fileLocation).exists()) {
            writePingBackFormDisplayed(false);
            return "false";
        }
        try {
            Properties properties = new Properties();
            InputStream inputStream = new FileInputStream(fileLocation);
            properties.loadFromXML(inputStream);
            inputStream.close();
            return (String) properties.get(key);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}

