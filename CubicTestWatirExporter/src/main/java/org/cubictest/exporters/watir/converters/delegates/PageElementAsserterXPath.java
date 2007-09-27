/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters.delegates;

import static org.cubictest.model.IdentifierType.LABEL;

import org.apache.commons.lang.StringUtils;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;

/**
 * Page element converter that uses XPath.
 * 
 * @author Christian Schwarz
 */
public class PageElementAsserterXPath {

	
	public static void handle(WatirHolder watirHolder, PageElement pe) {
		if (pe instanceof Title) {
			watirHolder.add("if (ie.title() != " + escape(pe.getIdentifier(LABEL).getValue()) + ")", 2);
			watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 3);
			watirHolder.add("end", 2);
		}
		else {
			//handle all other page elements:			
			watirHolder.add("pass = 0", 3);
			String xpath = escape(watirHolder.getFullContextWithAllElements(pe));
	
			if (pe instanceof TextField || pe instanceof TextArea) {
				//watir does not like type attribute for text input 
				xpath = StringUtils.replace(xpath, "[@type='text']", "");
			}
			
			if (pe instanceof Text || pe instanceof Option) {
				String not = pe.isNot() ? "not " : ""; 
				watirHolder.add("while " + not + "ie.element_by_xpath(\"" + xpath + "\") == nil do", 3);
			}
			else {
				String not = pe.isNot() ? "": "not "; 
				watirHolder.add("while " + not + "ie." + WatirUtils.getElementType(pe) + "(:xpath, \"" + xpath + "\").exists? do", 3);
			}
			watirHolder.add("if (pass > 20)", 4);
			watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 5);
			watirHolder.add("end", 4);
			watirHolder.add("sleep 0.1", 4);
			watirHolder.add("pass += 1", 4);
			watirHolder.add("end", 3);
	
		}
	}
	
	private static String escape(String s) { 
		return StringUtils.replace(s, "\"", "\\\"");
	}
}
