/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.holders;

import org.cubictest.export.IResultHolder;


/**
 * Stores the watir test as a series of test steps.
 * Provides methods for adding steps to list and getting the final list. 
 * 
 * @author chr_schwarz
 */
public class StepList implements IResultHolder {
	
	private RubyBuffer rubyBuffer;
	private boolean browserStarted = false;
	public static final String TEST_STEP_FAILED = "TestStepFailed";
	
	/** Initial prefix */
	private String prefix = "ie";
	
	/**
	 * Constructor that sets up the Watir script.
	 * @param testName
	 */
	public StepList() {
		this.rubyBuffer = new RubyBuffer();
		
		setUpWatirTest();
	}

	
	/**
	 * Adds string to the step list.
	 * If not indented, adds with default indent.
	 */
	public void add(String s) {
		if (!s.startsWith("\t")) {
			//use default indent:
			rubyBuffer.add(s, 2);
		}
		else {
			rubyBuffer.add(s);
		}
	}

	/**
	 * Adds string to the step list with the secified indent.
	 */
	public void add(String s, int indent) {
		rubyBuffer.add(s, indent);
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
		rubyBuffer.add("ie.close", 2);
		rubyBuffer.add("end", 1);
		rubyBuffer.add("end", 0);
		return rubyBuffer.toString(); 
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

	private void setUpWatirTest() {
		rubyBuffer.add("require 'rubygems'", 0);
		rubyBuffer.add("require 'watir'", 0);
		rubyBuffer.add("require 'test/unit'", 0);
		rubyBuffer.add("class " + TEST_STEP_FAILED + " < RuntimeError", 0);
		rubyBuffer.add("end", 0);
		rubyBuffer.add("class TC_cubicTestWatirExport_" + System.currentTimeMillis() + " < Test::Unit::TestCase", 0);
		rubyBuffer.add("def test_exported", 1);
		rubyBuffer.add("failedSteps = 0", 2);
		rubyBuffer.add("passedSteps = 0", 2);
		rubyBuffer.add("labelTargetId = \"\"", 2);
	}


	public boolean isBrowserStarted() {
		return browserStarted;
	}


	public void setBrowserStarted(boolean browserStarted) {
		this.browserStarted = browserStarted;
	}
	
	public void addSeparator() {
		rubyBuffer.add("\n");
	}
}