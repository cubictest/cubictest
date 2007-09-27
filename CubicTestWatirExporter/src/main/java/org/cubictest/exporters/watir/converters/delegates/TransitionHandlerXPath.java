/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters.delegates;

import org.apache.commons.lang.StringUtils;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;

/**
 * Page element converter that uses XPath.
 * 
 * @author Christian Schwarz
 */
public class TransitionHandlerXPath {

	
	public static void handle(WatirHolder watirHolder, UserInteraction userInteraction) {
		PageElement pe = (PageElement) userInteraction.getElement();

		//handle all other interaction types:
		String xpath = escape(watirHolder.getFullContextWithAllElements(pe));

		if (pe instanceof TextField || pe instanceof TextArea) {
			//watir does not like type attribute for text input 
			xpath = StringUtils.replace(xpath, "[@type='text']", "");
		}

		if (pe instanceof Option) {
			Select select = ((Option) pe).getParent();
			String selectXpath = escape(watirHolder.getFullContextWithAllElements(select));
			watirHolder.add("ie.select_list(\"/" + selectXpath + "\")." + WatirUtils.getInteraction(userInteraction), 3);
		}
		else {
			watirHolder.add("ie." + WatirUtils.getElementType(pe) + "(:xpath, \"" + xpath + "\")." + WatirUtils.getInteraction(userInteraction), 3);
		}
	
	}
	
	
	private static String escape(String s) { 
		return StringUtils.replace(s, "\"", "\\\"");
	}
}
