/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters.delegates;

import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.PageElement;

/**
 * Page element converter that uses XPath.
 * 
 * @author Christian Schwarz
 */
public class ContextAsserterXPath {

	
	public static void handle(WatirHolder stepList, PageElement pe) {
		// assert present:
		stepList.add("if ie.element_by_xpath(\"" + stepList.getFullContextWithAllElements(pe) + "\") == nil", 3);
		stepList.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);		
		stepList.add("end", 3);
}
	
}
