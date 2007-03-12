package org.cubictest.export;

/**
 * Holder to put things into when exporting.
 * Has method for getting the final result of the export.
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
