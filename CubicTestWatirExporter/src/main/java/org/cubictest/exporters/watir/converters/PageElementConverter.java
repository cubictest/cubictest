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
	public void handlePageElement(WatirHolder watirHolder, PageElement pe) {
		watirHolder.registerPageElement(pe);
		watirHolder.addSeparator();
		
		String not = pe.isNot() ? " not" : ""; 
		watirHolder.add("# asserting" + not + " present: " + pe.toString(), 2);
		watirHolder.add("begin", 2);

		if (watirHolder.requiresXPath(pe)) {
			PageElementAsserterXPath.handle(watirHolder, pe);
		}
		else {
			PageElementAsserterPlain.handle(watirHolder, pe);
		}

		watirHolder.add("puts \"" + WatirHolder.PASS + escape(watirHolder.getId(pe)) + "\"", 3);
		watirHolder.add("passedSteps += 1 ", 3);
	
		watirHolder.add("rescue " + WatirHolder.TEST_STEP_FAILED, 2);
		watirHolder.add("puts \"" + WatirHolder.FAIL + escape(watirHolder.getId(pe)) + "\"", 3);
		watirHolder.add("failedSteps += 1 ", 3);

		watirHolder.add("puts \"Step failed: Check" + not + " present: " + escape(pe.toString()) + "\"", 3);
		watirHolder.add("end", 2);
		watirHolder.add("STDOUT.flush", 2);
}

	
	private String escape(String s) { 
		return StringUtils.replace(s, "\"", "\\\"");
	}
}
