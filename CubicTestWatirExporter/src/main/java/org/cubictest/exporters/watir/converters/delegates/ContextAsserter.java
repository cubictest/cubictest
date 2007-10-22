/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters.delegates;

import org.cubictest.export.utils.exported.XPathBuilder;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.PageElement;
import org.cubictest.model.formElement.Select;

/**
 * Asserts contexts present using XPath.
 * 
 * @author Christian Schwarz
 */
public class ContextAsserter {

	
	public static void handle(WatirHolder watirHolder, PageElement pe) {

		watirHolder.add("pass = 0", 3);

		if (pe instanceof Select) {
			String xpath = "//" + XPathBuilder.getXPath(pe); //cannot assert all child elements, as watir does not support it for options
			watirHolder.add(watirHolder.getVariableName(pe) + " = ie." + WatirUtils.getElementType(pe) + "(:xpath, \"" + xpath + "\")", 3);
			String not = pe.isNot() ? "": "not "; 
			watirHolder.add("while " + not + watirHolder.getVariableName(pe) + ".exists? do", 3);
		}
		else if (WatirUtils.getElementType(pe).equals("*")){
			String xpath = watirHolder.getFullContextWithAllElements(pe);
			watirHolder.add(watirHolder.getVariableName(pe) + " = ie.element_by_xpath(\"" + xpath + "\")", 3);
			String not = pe.isNot() ? "" : "not "; 
			watirHolder.add("while " + not + watirHolder.getVariableName(pe) + ".methods.member?(\"ole_get_methods\")", 3);
		}
		else {
			//context elements that have class in watir
			String xpath = watirHolder.getFullContextWithAllElements(pe);
			watirHolder.add(watirHolder.getVariableName(pe) + " = ie." + WatirUtils.getElementType(pe) + "(:xpath, \"" + xpath + "\")", 3);
			String not = pe.isNot() ? "" : "not "; 
			watirHolder.add("while " + not + watirHolder.getVariableName(pe) + ".methods.member?(\"display\")", 3);
		}
		
		watirHolder.add("if (pass > 20)", 4);
		watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 5);
		watirHolder.add("end", 4);
		watirHolder.add("sleep 0.1", 4);
		watirHolder.add("pass += 1", 4);
		watirHolder.add("end", 3);
	}
}
