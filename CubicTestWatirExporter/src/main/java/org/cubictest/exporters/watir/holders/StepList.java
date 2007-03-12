/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.holders;

import org.cubictest.export.IResultHolder;


/**
 * Stores lines of watir (ruby) code in text format (StringBuffer).
 * Provides methods for adding steps to list and getting the final list. 
 * 
 * @see autat.common.interfaces.StepList
 * @author chr_schwarz
 */
public class StepList implements IResultHolder {
	
	private RubyBuffer rubyBuffer;
	private boolean browserStarted = false;
	private static final String URL_INVOKE_PATTERN = "ie.goto(";
	public static final String TEXT_ASSERTION_FAILURE = "TextAssertionFailure";
	public static final String ELEMENT_ASSERTION_FAILURE = "ElementAssertionFailure";
	private String prefix = "ie";
	
	/**
	 * Constructor that sets up the Watir script.
	 * @param testName
	 */
	public StepList() {
		this.rubyBuffer = new RubyBuffer();
		
		rubyBuffer.add("require 'rubygems'", 0);
		rubyBuffer.add("require 'watir'", 0);
		rubyBuffer.add("require 'test/unit'", 0);
		rubyBuffer.add("class " + TEXT_ASSERTION_FAILURE + " < RuntimeError", 0);
		rubyBuffer.add("end", 0);
		rubyBuffer.add("class " + ELEMENT_ASSERTION_FAILURE + " < RuntimeError", 0);
		rubyBuffer.add("end", 0);
		rubyBuffer.add("class TC_cubicTestWatirExport_" + System.currentTimeMillis() + " < Test::Unit::TestCase", 0);
		rubyBuffer.add("def test_exported", 1);
		rubyBuffer.add("failedSteps = 0", 2);
		rubyBuffer.add("passedSteps = 0", 2);
		rubyBuffer.add("ie = Watir::IE.new", 2);
		rubyBuffer.add("labelTargetId = \"\"", 2);
	}
	
	/**
	 * Adds step to the step list with the specified indent.
	 * Will check whether step already is decorated.
	 * If not, will decorate with console ouput, retry logic etc.
	 */
	public void add(TestStep testStep) {
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
			rubyBuffer.add("", 0);
			rubyBuffer.add(step, 0);
		}
		else {
			//Wrap in logic (decorate):
			rubyBuffer.add("", 2);
			rubyBuffer.add("begin", 2);
			rubyBuffer.add(step, 3);
			rubyBuffer.add("passedSteps += 1", 3);
			rubyBuffer.add("rescue => e", 2);
			rubyBuffer.add("puts(\"Step failed: " + ((TestStep)testStep).getDescription() + "\")", 3);
			rubyBuffer.add("failedSteps += 1", 3);
			rubyBuffer.add("end", 2);
		}
		
	}



	
	/**
	 * Get the String representation of the step list.
	 * This can be stored to file and excecuted.
	 */
	public String toResultString() {
		rubyBuffer.add("", 0);
		rubyBuffer.add("", 0);
		rubyBuffer.add("puts \"Done.\"", 2);
		rubyBuffer.add("puts \"\"", 2);
		
		rubyBuffer.add("if (failedSteps == 0)", 2);
		rubyBuffer.add("puts (passedSteps.to_s + \" steps passed, no steps failed.\")", 3);
		rubyBuffer.add("else", 2);
		rubyBuffer.add("puts(passedSteps.to_s + \" steps passed, \" + failedSteps.to_s + \" steps failed!\")", 3);
		rubyBuffer.add("end", 2);
		
		rubyBuffer.add("puts \"Press enter to exit\"", 2);
		rubyBuffer.add("gets", 2);
		rubyBuffer.add("ie.close()", 2);
		rubyBuffer.add("end", 1);
		rubyBuffer.add("end", 0);
		return rubyBuffer.toString(); 
	}

	
	/**
	 * Adds step to open an URL. 
	 * Will open browser if it is not started.
	 */
	private String addUrlInvokeStep(String invokeStep) {
		if (browserStarted) {
			rubyBuffer.add("ie.close()\n\n", 2);
			rubyBuffer.add("ie = Watir::IE.new", 2);
			rubyBuffer.add("ie.restore()", 2);
			browserStarted = true;
		}
		rubyBuffer.add(invokeStep, 2);
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