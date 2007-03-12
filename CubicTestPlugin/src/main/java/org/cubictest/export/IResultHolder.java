package org.cubictest.export;

/**
 * Holds the exported test.
 * Has method for getting the final result of the export as a string.
 * 
 * @author chr_schwarz 
 *
 */
public interface IResultHolder {

	/**
	 * Get the final String representation of the result holder (after export is done).
	 */
	public String toResultString();
}
