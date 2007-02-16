/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.holders;



/**
 * Watir test step (one or more lines of code).
 * Represents a step in a greater test, e.g. a user interaction or an assertion.

 * @see autat.common.interfaces.ITestStep
 * @author chr_schwarz
 */
public class TestStep implements ITestStep{

	private String step;
	private String description = "[no description]";
	private boolean isDecorated = false;
		
	public TestStep(String step) {
		if (step == null) {
			throw new IllegalArgumentException("Step passed in was null!");
		} 
		this.step = step;
	}


	public String getStep() {
		return step;
	}

	/**
	 * @param description The description to set.
	 */
	public TestStep setDescription(String description) {
		this.description = description;
		return this;
	}
	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * Set whether "begin-rescue-end" statement is inserted in the TestStep.
	 * @param decorated
	 */
	public void setDecorated(boolean isDecorated){
		this.isDecorated = isDecorated;
	}
	/**
	 * Get whether "begin-rescue-end" statement is already inserted in the TestStep.
	 * @return true if "begin-rescue-end" statement is already inserted in the TestStep.
	 */
	public boolean isDecorated(){
		return isDecorated;
	}

}
