package org.exoplatform.platform.gadget.services.ExoScriptingConsole;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Scanner;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public class ExoScriptingConsole{
	ScriptEngine _engine;
	StringWriter _out, _err;
	PrintWriter _stdout, _stderr;
	
	public ExoScriptingConsole(String engineName) throws Exception{
		_out = new StringWriter();
		_err = new StringWriter();
		_stdout = new PrintWriter(_out);
		_stderr = new PrintWriter(_err);		

		_engine = new ScriptEngineManager().getEngineByName(engineName);
		if(_engine == null) throw new Exception("Cannot find script engine '" + engineName + "'");

		ScriptContext context =  _engine.getContext();
		context.setWriter(_stdout);
		context.setErrorWriter(_stderr);
	}
	
	public String run(String script) throws Exception{
		_out.getBuffer().setLength(0);
		_engine.eval(script);
		_stdout.flush();
		return _out.toString();
	}
	
	public void runInSystemTerminal(boolean isDebug) {
		String output = "";
		String outputType = "result";
		
		System.out.print("Type 'help' for available commands\n>> ");
		
		Scanner scn = new Scanner(System.in);
		while(scn.hasNextLine()){
			output = "";
			outputType = "result";

			try {
				String script = scn.nextLine();

				if(script.isEmpty()) {
					System.out.print(">> ");
					continue;
				}

				if(script.equals("quit")) {
					return;
				}

				if(script.equals("help")){
					output = "dump\tDisplay session state\nrefresh\tClear session state\nquit\tEnd session";
				} else if(script.equals("dump")){
					Bindings bindings = this.getVariables();
					StringBuilder builder = new StringBuilder();
					for(Map.Entry<String, Object> entry:bindings.entrySet()){
						builder.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
					}
		        	String variables = builder.toString();
		        	if(variables.isEmpty()) variables = "<empty>";
		        	output = this.toString() + "\nVariables:\n" + variables;
				} else if(script.equals("refresh")) {
					this.getVariables().clear();
					output = "Session refreshed";
				} else{
					output = this.run(script);
				}
			} catch(Exception e) {
				outputType = "error";
				output = "Runtime exception: " + e.getMessage();
				
				if(isDebug){
					e.printStackTrace();
				}
			}

			if(outputType.equals("error")){
				if(!isDebug){
					output = "ERROR: " + output;
					System.out.print(output + "\n");
					System.out.print(">> ");
				}
			} else{
				System.out.print(output + "\n");
				System.out.print(">> ");
			}
		}
	}
			
	// Get variables
	public Bindings getVariables(){
		return _engine.getBindings(ScriptContext.ENGINE_SCOPE);
	}
	
	public String toString(){
		ScriptEngineFactory factory = _engine.getFactory();
		String info = "Scripting engine: " + factory.getEngineName() + " (v" + factory.getEngineVersion() + ")\n";
		info += "Language version: " + factory.getLanguageName() + " " + factory.getLanguageVersion() + "\n";
		return info;
	}

	public static void main(String[] args) {
		try {
			ExoScriptingConsole console = new ExoScriptingConsole("groovy");
			System.out.println(console.toString());
			console.getVariables().put("x", 5);
			System.out.println("x + 1 = " + console.run("println x+1"));
			console.runInSystemTerminal(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
