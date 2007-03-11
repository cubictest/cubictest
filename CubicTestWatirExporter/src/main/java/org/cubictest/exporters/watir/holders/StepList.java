/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.holders;


/**
 * Stores lines of watir (ruby) code in text format (StringBuffer).
 * Provides methods for adding steps to list and getting the final list. 
 * 
 * @see autat.common.interfaces.IStepList
 * @author chr_schwarz
 */
public class StepList implements IStepList {
	
	private StringBuffer buffer;
	private boolean browserStarted = false;
	private static final String URL_INVOKE_PATTERN = "ie.goto(";
	public static final String TEXT_ASSERTION_FAILURE = "TextAssertionFailure";
	private String prefix = "ie";
	
	/**
	 * Constructor that sets up the Watir script.
	 * @param testName
	 */
	public StepList() {
		this.buffer = new StringBuffer();
		
		add("require 'rubygems'", 0);
		add("require 'watir'", 0);
		add("require 'test/unit'", 0);
		add("class " + TEXT_ASSERTION_FAILURE + " < RuntimeError", 0);
		add("end", 0);
		add("class TC_cubicTestWatirExport_" + System.currentTimeMillis() + " < Test::Unit::TestCase", 0);
		add("def test_exported", 1);
		add("failedSteps = 0", 2);
		add("passedSteps = 0", 2);
		add("ie = Watir::IE.new", 2);
		add("labelTargetId = \"\"", 2);
	}
	
	/**
	 * Adds step to the step list with the specified indent.
	 * Will check whether step already is decorated.
	 * If not, will decorate with console ouput, retry logic etc.
	 */
	public void add(ITestStep testStep) {
		if (testStep == null) {
			System.out.println("Warning: Element was null.");
			return;
		}
		String step = testStep.getStep();
		
		if (isUrlInvoke(step)) {
			addUrlInvokeStep(step);
		}
		else if (((TestStep)testStep).isDecorated() && testStep instanceof TestStep){
			//step already contains wrapped logic (e.g. console output and "begin-rescue-end")
			append(buffer, "", 0);
			append(buffer, step, 0);
		}
		else {
			//Wrap in logic (decorate):
			append(buffer, "", 2);
			append(buffer, "begin", 2);
			append(buffer, step, 3);
			append(buffer, "passedSteps += 1", 3);
			append(buffer, "rescue => e", 2);
			append(buffer, "puts(\"Step failed: " + ((TestStep)testStep).getDescription() + "\")", 3);
			append(buffer, "failedSteps += 1", 3);
			append(buffer, "end", 2);
		}
		
	}

	
	/**
	 * Adds step to the step list with the specified indent.
	 */
	public void add(String step, int indent) {
		append(buffer, step, indent);
	}


	
	/**
	 * Get the String representation of the step list.
	 * This can be stored to file and excecuted.
	 */
	public String toString() {
		StringBuffer buff = new StringBuffer(buffer.toString());
		append(buff, "", 0);
		append(buff, "", 0);
		append(buff, "puts \"Done.\"", 2);
		append(buff, "puts \"\"", 2);
		
		append(buff, "if (failedSteps == 0)", 2);
		append(buff, "puts (passedSteps.to_s + \" steps passed, no steps failed.\")", 3);
		append(buff, "else", 2);
		append(buff, "puts(passedSteps.to_s + \" steps passed, \" + failedSteps.to_s + \" steps failed!\")", 3);
		append(buff, "end", 2);
		
		append(buff, "puts \"Press enter to exit\"", 2);
		append(buff, "gets", 2);
		append(buff, "ie.close()", 2);
		append(buff, "end", 1);
		append(buff, "end", 0);
		return buff.toString(); 
	}

	
	/**
	 * Adds step to open an URL. 
	 * Will open browser if it is not started.
	 */
	private String addUrlInvokeStep(String invokeStep) {
		if (browserStarted) {
			append(buffer, "ie.close()\n\n", 2);
			append(buffer, "ie = Watir::IE.new", 2);
			append(buffer, "ie.restore()", 2);
			browserStarted = true;
		}
		append(buffer, invokeStep, 2);
		return invokeStep;
	}
	
	
	/**
	 * Get whether this step is an "open URL" step.
	 */
	private boolean isUrlInvoke(String step) {
		int pos = step.indexOf(URL_INVOKE_PATTERN);
		if (pos >= 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * Utility method that appends the string to the passed 
	 * in StringBuffer with the specified indent.
	 */
	private void append(StringBuffer buff, String s, int indent) {
		if (!s.startsWith("\t")) {
			for (int i = 0; i < indent; i++) {
				buff.append("\t");			
			}
		}
		
		buff.append(s);
		if (!s.endsWith("\n"))
			buff.append("\n");
	}

	/**
	 * Set prefix to use (e.g. for contexts).
	 * Default prefix is "ie" (internet explorer root).
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Get prefix to use (e.g. for contexts).
	 * Default prefix is "ie" (internet explorer root).
	 */
	public String getPrefix() {
		return prefix;
	}

}