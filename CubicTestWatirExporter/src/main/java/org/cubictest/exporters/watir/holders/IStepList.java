/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.holders;



/**
 * Interface representing a list of steps.
 * Has methods for adding steps to the list.
 * Used by the various delegates adding test steps when walking through the model.
 * 
 * It contains an list of steps where the sequence is important.
 * Together, the steps make up the complete test.
 * 
 * Implementing classes will have to represent the list appropriate to the exporter / runner used.
 * StepLists also include the necessary test setup / configuration (the "root" element).

 * @author chr_schwarz
*/
public interface IStepList{

	public void add(ITestStep testElement);

	/**
	 * Gets the root element of the step list.
	 * The root element will be a test specification representing the complete exported / runnable test.
	 * 
	 * This root element represents an end product that a consumer can process. 
	 * @return an object representing a complete test specification.
	 */
	public String toString();
	
	
	/** Set prefix for contexts (search for elements within this prefix) */
	public void setPrefix(String prefix);

	/** Get prefix for contexts (search for elements within this prefix) */
	public String getPrefix();
}
