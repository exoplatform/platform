package org.exoplatform.platform.gadget.services.ExoScriptingConsole;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Scanner;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
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
		
		System.out.print(">> ");
		
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

				if(script.equals("show variables")){
					Bindings bindings = this.getVariables();
					StringBuilder builder = new StringBuilder();
					for(Map.Entry<String, Object> entry:bindings.entrySet()){
						builder.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
					}
		        	output = builder.toString();
		        	if(output.isEmpty()) output = "<empty>";
				} else {
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
	

	public static void main(String[] args) {
		try {
			ExoScriptingConsole console = new ExoScriptingConsole("groovy");
			console.getVariables().put("x", "5");
			System.out.println(console.run("println x"));
			console.runInSystemTerminal(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
