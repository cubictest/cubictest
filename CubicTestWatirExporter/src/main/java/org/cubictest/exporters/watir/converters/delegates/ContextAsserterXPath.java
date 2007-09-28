/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters.delegates;

import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.PageElement;

/**
 * Asserts contexts present using XPath.
 * 
 * @author Christian Schwarz
 */
public class ContextAsserterXPath {

	
	public static void handle(WatirHolder watirHolder, PageElement pe) {
		// assert present:
		watirHolder.add("if ie.element_by_xpath(\"" + watirHolder.getFullContextWithAllElements(pe) + "\") == nil", 3);
		watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);		
		watirHolder.add("end", 3);
}
	
}
