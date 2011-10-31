package org.exoplatform.platform.gadget.services.ExoScriptingConsole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
	public static Map<String, ExoScriptingConsole> _consoles = new HashMap<String, ExoScriptingConsole>();
	public static Map<String, ConsoleTask> _consoleTasks = new HashMap<String, ConsoleTask>();
	private static Timer _timer = new Timer();
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
		ConsoleTask newConsoleTask = new ConsoleTask(consoleId, ConsoleTask.TIME_TO_LIVE);
		_consoleTasks.put(consoleId, newConsoleTask);
		_timer.schedule(newConsoleTask, ConsoleTask.PERIOD, ConsoleTask.PERIOD);
		return newConsole;
	}

	public static String runInConsole(String consoleId, String script) throws Exception{
		Collection<String> roles = ConversationState.getCurrent().getIdentity().getRoles();

		if(roles.isEmpty()) {
			throw new Exception("Session expired. Please login again.");
		}
		
		if(!(roles.contains("administrators") || roles.contains("developers"))) {
			throw new Exception("Permission denied: only administrators or developers are allowed to run console commands");
		}		
		
		ExoScriptingConsole console = getConsole(consoleId);
		_consoleTasks.get(consoleId).increaseTimeToLive(1);
		
		if(script.equals("history")) {
			String history = console.getHistory();
			if(history.isEmpty()) history = "<empty>";
			return history;
		} else if(script.equals("dump")){
			Bindings bindings = console.getVariables();
			StringBuilder builder = new StringBuilder();
			for(Map.Entry<String, Object> entry:bindings.entrySet()){
				builder.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
			}
			String variables = builder.toString();
			if(variables.isEmpty()) variables = "<empty>";
			return console.toString() + "\nVariables:\n" + variables;
		} else if(script.equals("refresh")){
			console.getVariables().clear();
			return "Session refreshed";
		} else if(script.equals("quit")){
			_consoles.remove(consoleId);
			return "Session terminated";
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
	@Path("history/{sessionId}")
	@Produces("application/json")
	public Response history(@PathParam("sessionId") String sessionId) {
		try {
			ExoScriptingConsole console = getConsole(ConversationState.getCurrent().getIdentity().getUserId() + DELIMITER + sessionId);
			return Response.ok(console.getHistory(), "application/json").cacheControl(cacheControl).build();
		} catch (Exception e) {
			return Response.ok("Error getting session history: " + e.getMessage(), "application/json").cacheControl(cacheControl).build();
		}
	}
	
	@GET
	@Path("languages")
	@Produces("application/json")
	public Response languages() {
		ArrayList<String> languages = new ArrayList<String>();
		ScriptEngineManager manager = new ScriptEngineManager();

		for (ScriptEngineFactory factory:manager.getEngineFactories()) {
			languages.add(factory.getLanguageName());
		}
		
		return Response.ok(languages, "application/json").cacheControl(cacheControl).build();
	}	
}

class ConsoleTask extends TimerTask {
	public static int TIME_TO_LIVE = 30; // timeout = TIME_TO_LIVE * PERIOD = 30 mins
	public static int PERIOD = 60000; // 60 secs = 1 min

	private String _consoleId;
	private int _timeToLive;
	private long _lastCheck;
	
	public ConsoleTask(String consoleId, int ttl){
		_consoleId = consoleId;
		_timeToLive = ttl;
	}
	
	public void increaseTimeToLive(int amount){
		long current = System.currentTimeMillis();
		if(current - _lastCheck > PERIOD){
			_timeToLive += amount;
			_lastCheck = current;
		}
	}
	
	public void run() {
		_timeToLive--;
		if(_timeToLive == 0) {
			ExoScriptingConsoleRestService._consoles.remove(_consoleId);
			ExoScriptingConsoleRestService._consoleTasks.remove(_consoleId);
			System.gc();
			this.cancel();
		}
	}
}

