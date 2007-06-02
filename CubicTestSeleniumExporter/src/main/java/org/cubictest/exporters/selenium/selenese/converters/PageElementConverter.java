/*
 * Created on Apr 28, 2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.selenium.selenese.converters;

import static org.cubictest.model.IdentifierType.LABEL;

import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.exporters.selenium.selenese.holders.SeleneseDocument;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;

/**
 * This class is responsible for converting PageElements to Selenese rows 
 * asserting that elements are present. 
 * 
 * @author Christian Schwarz
 */
public class PageElementConverter implements IPageElementConverter<SeleneseDocument> {	

	
	/**
	 * Asserts that page element is present on HTML page. 
	 * @param pe The Page element to convert to Selenese row.
	 */
	public void handlePageElement(SeleneseDocument doc, PageElement pe) {

		if (pe instanceof Title) {
			doc.addCommand("verifyTitle", pe.getIdentifier(LABEL).getValue()).setDescription("Check present = " + pe);
		}
		else if (doc.isInRootContext() && pe instanceof Text) {
			//texts in root context have bug in firefox xpath, use selenium's own function:
			doc.addCommand("waitForText", pe.getText()).setDescription("Check present: " + pe);
		}
		else {
			//all other elements
			String locator = "xpath=" + doc.getXPathWithFullContextAndPreviousElements(pe);
			doc.addCommand("waitForElementPresent", locator).setDescription("Check present: " + pe);
		}
	}
}
