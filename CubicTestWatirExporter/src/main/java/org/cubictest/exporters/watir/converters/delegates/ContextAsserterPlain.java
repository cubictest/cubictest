/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters.delegates;

import static org.cubictest.model.IdentifierType.ID;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Select;

/**
 * Page element converter that uses standard Watir without XPath.
 * 
 * @author Christian Schwarz
 */
public class ContextAsserterPlain {


	public static void handle(WatirHolder stepList, PageElement pe) {

		if (pe instanceof Select) {
			handleSelect(stepList, (Select) pe);
		}
		else if (pe instanceof AbstractContext) {
			handleAbstractContext(stepList, (AbstractContext) pe);
		}
	}

	
	/**
	 * Assert Context present and set steplist prefix.
	 */
	private static void handleAbstractContext(WatirHolder stepList, AbstractContext context) {
		
		if (!(context.getMainIdentifierType().equals(ID)))
			throw new ExporterException("Contexts must have identifier type = ID for Watir export. Context in error: " + context);

		String idValue = "\"" + StringUtils.replace(context.getMainIdentifierValue(),"\"", "\\\"") + "\"";
		String idType = WatirUtils.getIdType(context);

		//set prefix:
		stepList.setPrefix("(ie.div(" + idType + "," + idValue + "))");
		
		//assert present:
		stepList.add("if (ie.div(" + idType + "," + idValue + ") == nil)", 3);
		stepList.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
		stepList.add("end", 3);
	}


	/**
	 * Assert Select present, set prefix and if label, save selectListID in script to be able to select options
	 */
	private static void handleSelect(WatirHolder stepList, IContext ctx) {
		
		Select select = (Select) ctx;
		
		String idText = "\"" + select.getMainIdentifierValue() + "\"";
		String idType = WatirUtils.getIdType(select);


		if (select.getMainIdentifierType().equals(IdentifierType.LABEL)) {
			stepList.add(WatirUtils.getLabelTargetId(select));
			idText = "selectListId";
			idType = ":id";
			stepList.add("selectListId = labelTargetId", 3);
		}
		else if (select.getMainIdentifierType().equals(IdentifierType.NAME)) {
			idType = ":name";
		}
		else if (select.getMainIdentifierType().equals(IdentifierType.ID)) {
			idType = ":id";
		}
		//set prefix (context):
		stepList.setPrefix("ie.select_list(" + idType + ", " + idText + ")");
		
		//assert select box present:
		stepList.add("if (" + stepList.getPrefix() + " == nil)", 3);
		stepList.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
		stepList.add("end", 3);
	}

}
