/*
 * Created on Apr 28, 2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.exporters.watir.converters.delegates.PageElementAsserterPlain;
import org.cubictest.exporters.watir.converters.delegates.PageElementAsserterXPath;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.model.PageElement;

/**
 * Converts a page element located on a page to a watir assertion.
 * 
 * @author Christian Schwarz
 */
public class PageElementConverter implements IPageElementConverter<WatirHolder> {	
	
	
	/**
	 * Converts a page element located on a page to a watir assertion.
	 */
	public void handlePageElement(WatirHolder stepList, PageElement pe) {
		stepList.registerPageElement(pe);
		stepList.addSeparator();
		
		String not = pe.isNot() ? " not" : ""; 
		stepList.add("# asserting" + not + " present: " + pe.toString(), 2);
		stepList.add("begin", 2);

		
		if (stepList.requiresXPath(pe)) {
			PageElementAsserterXPath.handle(stepList, pe);
		}
		else {
			PageElementAsserterPlain.handle(stepList, pe);
		}

		
		stepList.add("puts \"" + WatirHolder.PASS + escape(stepList.getId(pe)) + "\"", 3);
		stepList.add("passedSteps += 1 ", 3);
	
		stepList.add("rescue " + WatirHolder.TEST_STEP_FAILED, 2);
		stepList.add("puts \"" + WatirHolder.FAIL + escape(stepList.getId(pe)) + "\"", 3);
		stepList.add("failedSteps += 1 ", 3);

		stepList.add("puts \"Step failed: Check" + not + " present: " + escape(pe.toString()) + "\"", 3);
		stepList.add("end", 2);
}

	
	private String escape(String s) { 
		return StringUtils.replace(s, "\"", "\\\"");
	}
}
