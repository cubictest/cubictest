/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.converters;

import org.cubictest.export.IResultHolder;
import org.cubictest.model.PageElement;


/**
 * Interface for converters of page elements of a page to test steps. 
 * The test steps produced should verify that the particular page element is present.
 *
 * @author chr_schwarz
*/
public interface IPageElementConverter<T extends IResultHolder> {

	/**
	 * Converts a page element to a list of test steps verifying that the page element is present.
	 * @param e The page element
	 * @return a list of test steps verifying that the page element is present.
	 */
	public void handlePageElement(T t, PageElement e);
	
}
