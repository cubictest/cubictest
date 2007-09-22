/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters.delegates;

import static org.cubictest.model.ActionType.SELECT;
import static org.cubictest.model.IdentifierType.LABEL;

import org.apache.commons.lang.StringUtils;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Page element converter that uses standard Watir without XPath.
 * 
 * @author Christian Schwarz
 */
public class TransitionHandlerPlain {


	public static void handle(WatirHolder stepList, UserInteraction userInteraction) {
		PageElement pe = (PageElement) userInteraction.getElement();
		String idType = WatirUtils.getIdType(pe);
		String idValue = "\"" + pe.getMainIdentifierValue() + "\"";

		stepList.add("# user interaction");
		stepList.add("begin");

		//Handle Label identifier:
		if (WatirUtils.shouldGetLabelTargetId(pe)) {
			stepList.add(WatirUtils.getLabelTargetId(pe));
			stepList.addSeparator();
			idValue = "labelTargetId";
			idType = ":id";
		}
		
		if (userInteraction.getActionType().equals(SELECT) && userInteraction.getElement() instanceof Option) {
			//select option in select list:
			selectOptionInSelectList(stepList, (Option) pe, idType, idValue);	
		}
		else {
			//handle all other interaction types:
			stepList.add("ie." + WatirUtils.getElementType(pe) + "(" + idType + ", " + idValue + ")." + WatirUtils.getInteraction(userInteraction), 3);
			stepList.add("passedSteps += 1 ", 3);

			stepList.add("rescue", 2);
			stepList.add("puts " + "\"" + WatirHolder.EXCEPTION + "$!" + "\"");
			stepList.add("failedSteps += 1 ", 3);
			String interactionType = StringUtils.replace(WatirUtils.getInteraction(userInteraction) ,"\"", "\\\"");
			stepList.add("puts \"Could not " + interactionType + " " + 
					WatirUtils.getElementType(pe) + " with " + pe.getMainIdentifierType() + " = '" + pe.getMainIdentifierValue() + "'\"", 3);
	
			stepList.add("end", 2);
		}
	}

		
		
	/**
	 * Selects the specified option in a select list.
	 */
	private static void selectOptionInSelectList(WatirHolder stepList, Option option, String idType, String idText) {
		Select select = (Select) option.getParent();
		String selectIdValue = "\"" + select.getMainIdentifierValue() + "\"";
		String selectIdType = WatirUtils.getIdType(select);
		
		if (select.getMainIdentifierType().equals(IdentifierType.LABEL)) {
			//Handle label:
			stepList.add(WatirUtils.getLabelTargetId(select));
			selectIdValue = "labelTargetId";
			selectIdType = ":id";
		}
		
		String selectList = "ie.select_list(" + selectIdType + ", " + selectIdValue + ")";
		
		//Select the option:
		if (option.getMainIdentifierType().equals(LABEL)) {
			stepList.add(selectList + ".select(" + idText + ")", 3);
		}
		else {
			stepList.add(selectList + ".option(" + idType + ", " + idText + ").select", 3);
		}
		
		stepList.add("rescue",2);
		stepList.add("failedSteps += 1 ",3);
		stepList.add("puts \"Step failed: Select option with " + idType + " = '" + option.getMainIdentifierValue() + "'\"", 3);		
		stepList.add("end", 2);

	}
		
}
