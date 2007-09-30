/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.holders;

import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.SubTest;

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
	

	/**
	 * Update status on the passed in sub test.
	 * @param subTest
	 */
	public void updateStatus(SubTest subTest, boolean hadException, ConnectionPoint targetConnectionPoint);
}
