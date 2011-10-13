package org.exoplatform.platform.gadget.services.ExoScriptingConsole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;

import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.services.security.ConversationState;

@Path("/console-manager")
public class ExoScriptingConsoleRestService implements ResourceContainer {
	private static String DELIMITER = "~";
	private static Map<String, ExoScriptingConsole> _consoles = new HashMap<String, ExoScriptingConsole>();  
	private static final CacheControl cacheControl;
	static {
		RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
		cacheControl = new CacheControl();
		cacheControl.setNoCache(true);
		cacheControl.setNoStore(true);
	}

	private static ExoScriptingConsole getConsole(String consoleId) throws Exception {
		if(_consoles.containsKey(consoleId)){
			return _consoles.get(consoleId);
		}
		
		ExoScriptingConsole newConsole = new ExoScriptingConsole(consoleId.split(DELIMITER)[2]);
		_consoles.put(consoleId, newConsole);
		return newConsole;
	}

	public static String runInConsole(String consoleId, String script) throws Exception{
		Collection<String> roles = ConversationState.getCurrent().getIdentity().getRoles();

		if(!(roles.contains("administrators") || roles.contains("developers"))) {
			throw new Exception("Permission denied: only administrators or developers are allowed to run console commands");
		}		
		
		ExoScriptingConsole console = getConsole(consoleId);
		if(script.equals("show variables")){
			Bindings bindings = console.getVariables();
			StringBuilder builder = new StringBuilder();
			for(Map.Entry<String, Object> entry:bindings.entrySet()){
				builder.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
			}
        	String variables = builder.toString();
        	if(variables.isEmpty()) variables = "<empty>";
        	return variables;
        } else if(script.equals("clear session")){
        	_consoles.remove(consoleId);
        	return "Session cleared";
        } else if(script.equals("quit")){
        	_consoles.remove(consoleId);
        	return "Session terminated";
        } else if(script.equals("list all sessions")){
        	if(!roles.contains("administrators")) throw new Exception("Permission denied: only administrators are allowed to run this command");
        	StringBuilder builder = new StringBuilder();
        	int i=0;
			for(Map.Entry<String, ExoScriptingConsole> entry:_consoles.entrySet()){
				String[] sessionInfo = entry.getKey().split(DELIMITER);
				builder.append(" " + (++i) + ". " + sessionInfo[0] + " @ " + new Date(Long.parseLong(sessionInfo[1])) + " (" + sessionInfo[2] + ")\n");
			}
        	return builder.toString();
        } else if(script.equals("remove all sessions")){
        	if(!roles.contains("administrators")) throw new Exception("Permission denied: only administrators are allowed to run this command");
        	_consoles.clear();
        	return "Sessions removed";
        } else {
			return console.run(script);
        }
	}

	@GET
	@Path("run/{sessionId}/{script}")
	@Produces("application/json")
	public Response run(@PathParam("sessionId") String sessionId, @PathParam("script") String script) {
		String outputType;
		String output;

		try{
			script = script.replaceAll("%2B", "+").replaceAll("%2F", "/").replaceAll("%5C","\\\\"); // unescape '+', '/' and '\' character (URL's special characters)
			output = runInConsole(ConversationState.getCurrent().getIdentity().getUserId() + DELIMITER + sessionId, script);
			outputType = "result";
		} catch (Exception e) {
			output = e.getMessage();
			outputType = "error";
		}

    	return Response.ok("{\"outputType\":\"" + outputType + "\", \"output\":\"" + output.replaceAll("\\r|\\n", "\\\\n") + "\"}", "application/json").cacheControl(cacheControl).build();
	}
	
	@GET
	@Path("languages")
	@Produces("application/json")
	public Response languages(@PathParam("sessionId") String sessionId, @PathParam("script") String script) {
		ArrayList<String> languages = new ArrayList<String>();
		ScriptEngineManager manager = new ScriptEngineManager();

        for (ScriptEngineFactory factory:manager.getEngineFactories()) {
            languages.add(factory.getLanguageName());
        }
		
    	return Response.ok(languages, "application/json").cacheControl(cacheControl).build();
	}

}