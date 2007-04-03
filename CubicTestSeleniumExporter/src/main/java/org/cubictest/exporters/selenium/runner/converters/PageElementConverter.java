/*
 * Created on Apr 28, 2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.selenium.runner.converters;

import static org.cubictest.model.IdentifierType.LABEL;

import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.exporters.selenium.runner.RunnerUtils;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.Title;

/**
 * This class is responsible for converting PageElements to Selenese rows 
 * asserting that elements are present. 
 * 
 * @author chr_schwarz
 */
public class PageElementConverter implements IPageElementConverter<SeleniumHolder> {	

	
	/**
	 * Asserts that page element is present on HTML page. 
	 * @param pe The Page element to convert to Selenese row.
	 */
	public void handlePageElement(SeleniumHolder seleniumHolder, PageElement pe) {

		if (pe instanceof Title) {
			String actual = seleniumHolder.getSelenium().getTitle();
			String expected = pe.getIdentifier(LABEL).getValue();
			RunnerUtils.setStatus(pe, actual, expected);
		}
		else {
			//all other elements
			String locator = SeleniumUtils.getLocator(pe, seleniumHolder);
			String value = seleniumHolder.getSelenium().getValue(locator);
			String text = seleniumHolder.getSelenium().getText(locator);
			if (value == null && text == null) {
				pe.setStatus(TestPartStatus.FAIL);
			}
			else {
				pe.setStatus(TestPartStatus.PASS);
			}
		}
	}
}
