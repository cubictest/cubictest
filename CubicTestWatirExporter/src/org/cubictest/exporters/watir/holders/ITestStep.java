/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.holders;

/**
 * Interface for representing a step in a larger test.
 *  
 * @author chr_schwarz
 */
public interface ITestStep {
	
	public String getStep();
	
	public String getDescription();
}
