package redempt.inputscripter2.script;

import java.util.Map;

import redempt.inputscripter2.gui.Indicator;
import redempt.inputscripter2.script.function.Function;
import redempt.inputscripter2.script.instruction.Instruction;
import redempt.inputscripter2.script.variable.Variable;

public class Script {
	
	public Instruction[] instructions;
	private ScriptLoader handler;
	private int lineNumber = 0;
	public Map<String, Variable> variables;
	public Map<String, Function> functions;
	private boolean terminate = false;
	private boolean running = false;
	private String source;
	private Indicator indicator = null;
	
	public Script(ScriptLoader handler, Instruction[] instructions, Map<String, Variable> variables, Map<String, Function> functions, String source) {
		this.source = source;
		this.instructions = instructions;
		this.handler = handler;
		this.variables = variables;
		this.functions = functions;
	}
	
	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}
	
	public Indicator getIndicator() {
		return indicator;
	}
	
	public String getSource() {
		return source;
	}
	
	public ScriptLoader getLoader() {
		return handler;
	}
	
	public void gotoLine(int line) {
		if (terminate && line < lineNumber) {
			return;
		}
		lineNumber = line;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public void kill() {
		lineNumber = instructions.length + 1;
		if (indicator != null) {
			indicator.dispose();
		}
		indicator = null;
	}
	
	public void terminate() {
		terminate = true;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public boolean isTerminated() {
		return terminate;
	}
	
	public void run() {
		if (running) {
			return;
		}
		running = true;
		while (lineNumber < instructions.length) {
			if (instructions[lineNumber] == null) {
				lineNumber++;
				continue;
			}
			try {
				instructions[lineNumber].run(this);
			} catch (Exception e) {
				System.out.println("Error on line " + lineNumber + ": " + e.getClass().getSimpleName());
				e.printStackTrace();
			}
			lineNumber++;
		}
		terminate = false;
		lineNumber = 0;
		running = false;
		if (indicator != null) {
			indicator.dispose();
		}
		indicator = null;
		functions.clear();
		getLoader().registerFunctions(functions);
	}
	
	public void runAsync() {
		Thread thread = new Thread() {
			
			@Override
			public void run() {
				Script.this.run();
			}
			
		};
		thread.start();
	}
	
}
