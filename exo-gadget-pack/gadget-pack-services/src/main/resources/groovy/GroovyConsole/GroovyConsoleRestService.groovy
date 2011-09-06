
/***************************************************************************
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 ***************************************************************************/
 
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.groovy.control.CompilerConfiguration;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.RuntimeDelegate;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;
import java.util.regex.Matcher;


/**
 * Created by The eXo Platform SARL Author : Tung Vu Minh tungvm@exoplatform.com
 * May 13, 2011 8:49:21 PM
 */

/***********************************************************************************
 * GROOVY CONSOLE REST SERVICE
 ***********************************************************************************/  
 @Path("groovyconsole")
public class GroovyConsoleRestService implements ResourceContainer {
  private static final Log log = ExoLogger.getLogger(GroovyConsoleRestService.class);

  private static final CacheControl cacheControl;
  static {
    RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
    cacheControl = new CacheControl();
    cacheControl.setNoCache(true);
    cacheControl.setNoStore(true);
  }

  @GET
  @Path("/exec/{sessionId}/{script}")
  @Produces("application/json")
  public Response exec(@PathParam("sessionId") String sessionId, @PathParam("script") String script, @Context SecurityContext sc, @Context UriInfo uriInfo) throws Exception {
    try {
      script = script.replaceAll("%2B", "+").replaceAll("%2F", "/").replaceAll("%5C","\\\\"); // unescape '+', '/' and '\' character (URL's special characters)
      String output = "";
      String outputType = "result";
      
      try {
        GroovyConsole console = new GroovyConsole(getUserId(sc, uriInfo), sessionId);

        Matcher matcher;
        switch(script){
        case ~/\s*show imports\s*/:
          output = console.getImports();
          if(output.isEmpty()) output = "<empty>"
          output = "[" + sessionId + "]\n" + output;
          break;
        case ~/\s*show variables\s*/:
          output = console.getVariables();
          if(output.isEmpty()) output = "<empty>"
          output = "[" + sessionId + "]\n" + output;
          break;
        case ~/\s*clear session\s*/:
          console.clearSession();
          output = "Session cleared.";
          break;
        case {matcher = (it =~ /\s*list sessions(?:\s+(\S+))?\s*/)}:
          def match = matcher[0][1];
          String session = (match == null) ? "" : match; 
          output = console.listSessions(session);
          int numSessions = output.isEmpty() ? 0 : output.split("\n").length;
          output += "\nTotal: " + numSessions + " session" + (numSessions>1?"s":""); 
          break;
        case {matcher = (it =~ /\s*save session as\s+(\S+)\s*/)}:
          def match = matcher[0][1];
          String session = match == null ? "" : match;
          console.saveSessionAs(session);
          output = "Session was saved successfully to '" + session + "'";
          break;
        case {matcher = (it =~ /\s*load session\s+(\S+)\s*/)}:
          def match = matcher[0][1];
          String session = match == null ? "" : match;
          console.loadSession(session);
          output = "Session '" + session + "' was loaded successfully.";
          break;
        case {matcher = (it =~ /\s*remove session\s+(\S+)\s*/)}:
          def match = matcher[0][1];
          String session = match == null ? "" : match;
          console.removeSession(session);
          output = "Session '" + session + "' was removed successfully.";
          break;
        case {matcher = (it =~ /\s*remove all sessions(?:\s+(\S+)\s*)?\s*/)}:
          def match = matcher[0][1];
          String session = match == null ? "" : match; 
          int numSessions = console.removeAllSessions(session);
          output = numSessions + " session" + (numSessions>1?"s were":" was") + " removed.";
          break;
          
        default:
          output = console.exec(script);
        }
      } catch(Exception e) {
        outputType = "error";
        
        switch(e.getMessage()) {
          case "constructor.saveContext":
            output = "Error initializing GroovySession: " + e.getCause().getMessage();
            break;
          case ~/loadSession\..*/:
            output = "Error loading session: " + e.getCause().getMessage();
            break;
          case "saveSession.saveContext":
            output = "Error saving session: " + e.getCause().getMessage();
            break;
          case "clearSession.clearContext":
            output = "Error clearing session: " + e.getCause().getMessage();
            break;
          case "removeSession.clearContext":
            output = "Error removing session: " + e.getCause().getMessage();
            break;
          case "removeAllSessions":
            output = "Error removing sessions: " + e.getCause().getMessage();
            break;
          case "listSessions":
            output = "Error listing sessions: " + e.getCause().getMessage();
            break;
          case "exec":
            output = "Error executing script: " + e.getCause().getMessage();
            break;
  
          default:
            output = "Runtime exception: " + e.getMessage()
            log.debug(output, e);
        }
      }

      List<Object> consoleData = new ArrayList<Object>();
  
      consoleData.add(outputType);
      consoleData.add(output);
  
      MessageBean data = new MessageBean();
      data.setData(consoleData);
  
      return Response.ok(data, "application/json").cacheControl(cacheControl).build();
    }
    catch (Exception e) {
      log.debug("Exception in GroovyConsoleRestService.exec(sessionId, script): " + e.getMessage(), e);
      return Response.status(HTTPStatus.INTERNAL_ERROR).cacheControl(cacheControl).build();
    }
  }

  private String getUserId(SecurityContext sc, UriInfo uriInfo) {
    try {
      return sc.getUserPrincipal().getName();
    }
    catch (NullPointerException e) {
      return getViewerId(uriInfo);
    }
    catch (Exception e) {
      log.debug("Fialed to get user id", e);
      return null;
    }
  }
  
  private String getViewerId(UriInfo uriInfo) {
    URI uri = uriInfo.getRequestUri();
    String requestString = uri.getQuery();
    if (requestString == null) return null;
    String[] queryParts = requestString.split("&");
    for (String queryPart : queryParts) {
      if (queryPart.startsWith("opensocial_viewer_id")) {
        return queryPart.substring(queryPart.indexOf("=") + 1, queryPart.length());
      }
    }
    return null;
  }  

}

// Utility class for wrapping REST data   
public class MessageBean {
  private List<Object> data;
  public void setData(List<Object> list) {
    this.data = list;
  }
  public List<Object> getData() {
    return data;
  }
}

/***********************************************************************************
 * GROOVY CONSOLE CORE CLASS
 ***********************************************************************************/  
public class GroovyConsole{
  private static final Log log = ExoLogger.getLogger(GroovyConsole.class);
  public static String GROOVY_CONSOLE_HOME = "../temp/"; // <Tomcat directory>/temp/
  public static String DELIMITER = "#~#";
  private String _userId;
  private String _sessionId;

  Set<String> _imports = new HashSet<String>();
  LinkedHashMap<String, String> _variables = new LinkedHashMap<String, String>();

  public GroovyConsole(String userId, String sessionId) throws Exception{
  this._userId = userId;
  this._sessionId = sessionId;
  
  // Load context data, create new if does not exist
  try{
    loadContext(_userId, _sessionId);
  } catch(Exception e) {
    try{
    saveContext(_userId, _sessionId);
    } catch(Exception sce){
    throw new Exception("constructor.saveContext", sce);
    }
  }
  }
  
  // Execute a Groovy script
  public String exec(String script) throws Exception{
  StringWriter out = new StringWriter();
  StringWriter err = new StringWriter();
  PrintWriter stdout = new PrintWriter(out);
  PrintWriter stderr = new PrintWriter(err);

  CompilerConfiguration cc = new CompilerConfiguration();
  cc.setOutput(stdout);
  cc.setSourceEncoding("utf-8");

  // Load context data, create new if does not exist
  try{
    loadContext(_userId, _sessionId);
  } catch(Exception e) {
    try{
    saveContext(_userId, _sessionId);
    } catch(Exception sce){
    throw new Exception("exec.saveContext", sce);
    }
  }
  
  // Construct the GroovyShell
  Binding b = new Binding();
  b.setVariable("out",stdout);
  b.setVariable("err",stderr);
  GroovyShell shell = new GroovyShell(Thread.currentThread().getContextClassLoader(),b,cc);
  String result = "";
  try {
    // Construct the final script from input script and saved context data (including import and variable assignments statements)
    // TODO: parse class and function definitions to save to context data.
    String finalScript = getImports() + "\n";  // add import statements from the context
    finalScript += getVariables() + "\n";      // add variable assignments statements from the context
    script = removeComments(script);           // remove all comments from the input script
    finalScript += script;                     // add the script

    // Execute the final script
    Script parse = shell.parse(finalScript);
    parse.run();
    stdout.flush();
    result = out.toString();

    
    // At this point the execution is fine (no exception), this means the input script is valid
    // so we can parse it for new import statements and variable assignments
    
    //Parse imports
    Pattern pattern = Pattern.compile("import\\s+[^\\s]+.*?(;|\$)");
    Matcher matcher = pattern.matcher(script);
    while(matcher.find()){
    _imports.add(matcher.group());
    }

    //Parse variables
    pattern = Pattern.compile("(?:^|(?<=\\s))(\\w+)\\s*(?<!=)=(?!=)\\s*(.*\\w.*)");
    matcher = pattern.matcher(script);
    while(matcher.find()){
    _variables.put(matcher.group(1), matcher.group(2));
    }

    // Persist context data
    saveContext(_userId, _sessionId);

  } catch(Exception e) {
    throw new Exception("exec", e);
  } finally {
    out.close();
    err.close();
    stdout.close();
    stderr.close();
  }
  return result;
  }

  // List all saved sessions those have name matches the given regular expression 'regex'
  public String listSessions(String regex) throws Exception {
  String sessionNames = "";
  
  try {
    File homedir = new File(GROOVY_CONSOLE_HOME);

    if(regex.isEmpty()) regex = ".*";
    RegexNameFileFilter filter = new RegexNameFileFilter(_userId + DELIMITER + regex);
    
    File[] files = homedir.listFiles(filter);
    if (files != null) {
    Arrays.sort(files, new FileDateComperator("desc"));

    files.each {file ->
      String filename = file.getName();
      sessionNames += filename.substring(filename.indexOf(DELIMITER)+DELIMITER.length()) + "\n";
    }
    };
  } catch (Exception e) {
    throw new Exception("listSessions", e);
  }

  return sessionNames;
  }

  // Load the session with given sessionId
  public void loadSession(String sessionId) throws Exception {
  try {
    loadContext(_userId, sessionId);
    saveContext(_userId, _sessionId);
  } catch (Exception e) {
    if(e.getMessage().equals("loadContext")) throw new Exception("loadSession.loadContext", e.getCause());
    if(e.getMessage().equals("saveContext")) throw new Exception("loadSession.saveContext", e.getCause());
  }
  }

  // Save current session to a new session with given sessionId
  public void saveSessionAs(String sessionId) throws Exception {
  try {
    saveContext(_userId, sessionId);
  } catch (Exception e) {
    throw new Exception("saveSession.saveContext", e.getCause());
  }
  }
  
  // Remove the session with given sessionId
  public void removeSession(String sessionId) throws Exception {
  try {
    clearContext(_userId, sessionId);
  } catch (Exception e) {
    throw new Exception("removeSession.clearContext", e.getCause());
  }
  }

  // Remove all sessions those have name matches the given regular expression 'regex'
  public int removeAllSessions(String regex) throws Exception {
  try {
    File homedir = new File(GROOVY_CONSOLE_HOME);

    if(regex.isEmpty()) regex = ".*";
    RegexNameFileFilter filter = new RegexNameFileFilter(_userId + DELIMITER + regex);

    File[] files = homedir.listFiles(filter);
    if (files != null) {
    Arrays.sort(files, new FileDateComperator("desc"));
        
    files.each { file ->
      String filename = file.getName();
      String sessionId = filename.substring(filename.indexOf(DELIMITER)+DELIMITER.length());
      clearContext(_userId, sessionId);
    }
    return files.length;
    };
    return 0;
  } catch (Exception e) {
    throw new Exception("removeAllSessions", e);
  }
  }

  // Clear current session's context data
  public void clearSession() throws Exception {
  try {
    _imports = new HashSet<String>();
    _variables = new LinkedHashMap<String, String>();
    clearContext(_userId, _sessionId);
  } catch (Exception e) {
    throw new Exception("clearSession.clearContext", e.getCause());
  }
  }

  // Get all import statements from current context
  public String getImports(){
  String imports = join(_imports, "\n");
  if(!imports.isEmpty()) imports += "\n";
  return imports;
  }

  // Get all variable assignments from current context
  public String getVariables(){
  String variables = "";
  _variables.each { key, value ->
    variables += key + " = " + value + "\n";
  }
  return variables;
  }

  // Load context data from the file named <userId>DELIMITER<sessionId>
  private void loadContext(String userId, String sessionId) throws Exception {
  FileInputStream is;
  ObjectInputStream ois;
  
  try{
    is = new FileInputStream(GROOVY_CONSOLE_HOME + userId + DELIMITER + sessionId);
    ois = new ObjectInputStream(is);

    _imports = (Set<String>)ois.readObject();
    _variables = (LinkedHashMap<String, String>)ois.readObject();
  } catch(Exception e){
    throw new Exception("loadContext", e);
  } finally {
    if(ois != null) ois.close();
    if(is != null) is.close();
  }
  }

  // Save context data to the file named <userId>DELIMITER<sessionId>
  private void saveContext(String userId, String sessionId) throws Exception {
  FileOutputStream os;
  ObjectOutputStream oos;
  
  try{
    os = new FileOutputStream(GROOVY_CONSOLE_HOME + userId + DELIMITER + sessionId);
    oos = new ObjectOutputStream(os);

    oos.writeObject(_imports);
    oos.writeObject(_variables);
  } catch(Exception e){
    throw new Exception("saveContext", e);
  } finally {
    if(oos != null ) oos.close();
    if(os != null) os.close();
  }
  }

  // Remove context data by removing the file named <userId>DELIMITER<sessionId>
  private void clearContext(String userId, String sessionId) throws Exception {
  try{
    FileInputStream is = new FileInputStream(GROOVY_CONSOLE_HOME + userId + DELIMITER + sessionId);
    is.close();
    (new File(GROOVY_CONSOLE_HOME + userId + DELIMITER + sessionId)).delete();
  }catch(Exception e){
    throw new Exception("clearContext", e);
  }
  }

  // Utility function to join a collection's elements to a string
  private static String join( Iterable< ? extends Object > pColl, String separator )
  {
  Iterator< ? extends Object > oIter;
  if ( pColl == null || ( !( oIter = pColl.iterator() ).hasNext() ) ) return "";
  StringBuilder oBuilder = new StringBuilder( String.valueOf( oIter.next() ) );
  while ( oIter.hasNext() ) oBuilder.append( separator ).append( oIter.next() );
  return oBuilder.toString();
  }

  // Utility function to remove comments from groovy source code
  private static String removeComments(String sourceCode){
  return sourceCode
  .replaceAll("(\".*)(//)(.*\")", "\$1__SLASHES_IN_STRING__\$3") // save   // in strings before replacement
  .replaceAll("//.*", "")                                        // remove //... comments
  .replaceAll("/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/","")             // remove /*...*/ comments
  .replaceAll("__SLASHES_IN_STRING__", "//");                    // return // to strings
  }

  // Standalone application for testing
  public static void main(String[] args){
  String userId = "tung_vuminh";
  String sessionId = "GroovySession@" + (new Date()).format("yyyy.MM.dd_HH'h'mm'm'ss's'");
  GroovyConsole console = new GroovyConsole(userId, sessionId);

  print ">> ";
  Scanner scn = new Scanner(System.in);
  while(scn.hasNextLine()){
    String output = "";
    String outputType = "result";
    
    try {
    String script = scn.nextLine();
    
    if(script.isEmpty()) {
      print ">> ";
      continue;
    }
      
    if(script.equals("exit")) break;

    Matcher matcher;
    switch(script){
      case ~/\s*show imports\s*/:
      output = console.getImports();
      if(output.isEmpty()) output = "<empty>"
      output = "[" + sessionId + "]\n" + output;
      break;
      case ~/\s*show variables\s*/:
      output = console.getVariables();
      if(output.isEmpty()) output = "<empty>"
      output = "[" + sessionId + "]\n" + output;
      break;
      case ~/\s*clear session\s*/:
      console.clearSession();
      output = "Session cleared.";
      break;
      case {matcher = (it =~ /\s*list sessions(?:\s+(\S+))?\s*/)}:
      def match = matcher[0][1];
      String session = (match == null) ? "" : match;
      output = console.listSessions(session);
      int numSessions = output.isEmpty() ? 0 : output.split("\n").length;
      output += "\nTotal: " + numSessions + " session" + (numSessions>1?"s":"");
      break;
      case {matcher = (it =~ /\s*save session as\s+(\S+)\s*/)}:
      def match = matcher[0][1];
      String session = match == null ? "" : match;
      console.saveSessionAs(session);
      output = "Session was saved successfully to '" + session + "'";
      break;
      case {matcher = (it =~ /\s*load session\s+(\S+)\s*/)}:
      def match = matcher[0][1];
      String session = match == null ? "" : match;
      console.loadSession(session);
      output = "Session '" + session + "' was loaded successfully.";
      break;
      case {matcher = (it =~ /\s*remove session\s+(\S+)\s*/)}:
      def match = matcher[0][1];
      String session = match == null ? "" : match;
      console.removeSession(session);
      output = "Session '" + session + "' was removed successfully.";
      break;
      case {matcher = (it =~ /\s*remove all sessions(?:\s+(\S+)\s*)?\s*/)}:
      def match = matcher[0][1];
      String session = match == null ? "" : match;
      int numSessions = console.removeAllSessions(session);
      output = numSessions + " session" + (numSessions>1?"s were":" was") + " removed.";
      break;
      
      default:
      output = console.exec(script);
    }
    } catch(Exception e) {
    outputType = "error";
    
    switch(e.getMessage()) {
      case "constructor.saveContext":
      output = "Error initializing GroovySession: " + e.getCause().getMessage();
      break;
      case ~/loadSession\..*/:
      output = "Error loading session: " + e.getCause().getMessage();
      break;
      case "saveSession.saveContext":
      output = "Error saving session: " + e.getCause().getMessage();
      break;
      case "clearSession.clearContext":
      output = "Error clearing session: " + e.getCause().getMessage();
      break;
      case "removeSession.clearContext":
      output = "Error removing session: " + e.getCause().getMessage();
      break;
      case "removeAllSessions":
      output = "Error removing sessions: " + e.getCause().getMessage();
      break;
      case "listSessions":
      output = "Error listing sessions: " + e.getCause().getMessage();
      break;
      case "exec":
      output = "Error executing script: " + e.getCause().getMessage();
      break;

      default:
      output = "Runtime exception: " + e.getMessage()
      e.printStackTrace();
    }
    }
    
    if(outputType.equals("error")) output = "ERROR: " + output;
    print output + "\n>> ";
  }
  }
}

// Utility filter for filtering files (under a directory) those have name matches a given regular expression
class RegexNameFileFilter implements FileFilter {
  String _regex;

  RegexNameFileFilter(String regex) {
  _regex = regex;
  }

  public boolean accept(File file) {
  return file.isFile() && file.getName().matches(_regex);
  }
}

// Utility comperator for sorting files by modified date
class FileDateComperator implements Comparator<File> {
  String _order;
  
  FileDateComperator(String order) {
  _order = order.equals("asc") ? order : "desc";
  }
  
  public int compare(File f1, File f2) {
  if(_order.equals("asc")) {
    if (f1.lastModified() < f2.lastModified()) {
    return -1;
    } else if (f1.lastModified() > f2.lastModified()) {
    return 1;
    } else {
    return 0;
    }
  } else {
    if (f1.lastModified() < f2.lastModified()) {
    return 1;
    } else if (f1.lastModified() > f2.lastModified()) {
    return -1;
    } else {
    return 0;
    }
  }
  }
}
