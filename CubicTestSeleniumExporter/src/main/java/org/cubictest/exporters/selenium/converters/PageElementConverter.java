/*
 * Created on Apr 28, 2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.selenium.converters;

import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.exporters.selenium.holders.SeleneseDocument;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.FormElement;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;

/**
 * This class is responsible for converting PageElements to Selenese rows 
 * asserting that elements are present. 
 * 
 * @author chr_schwarz
 */
public class PageElementConverter implements IPageElementConverter<SeleneseDocument> {	

	
	/**
	 * Asserts that page element is present on HTML page. 
	 * @param pe The Page element to convert to Selenese row.
	 */
	public void handlePageElement(SeleneseDocument doc, PageElement pe) {
		
		if (pe instanceof Title) {
			doc.addCommand("verifyTitle", pe.getText()).setDescription("Check present = " + pe);
		}
		else if (pe instanceof Link) {
			String locator = SeleniumUtils.getLocator(pe);
			doc.addCommand("assertElementPresent", locator).setDescription("Check present: " + pe);
		}
		else if (pe instanceof Text) {
			doc.addCommand("waitForText", pe.getText()).setDescription("Check present: " + pe);
		}
		else if (pe instanceof FormElement) {
			String locator = SeleniumUtils.getLocator(pe);
			doc.addCommand("assertElementPresent", locator).setDescription("Check present: " + pe);
		}
	}
}
