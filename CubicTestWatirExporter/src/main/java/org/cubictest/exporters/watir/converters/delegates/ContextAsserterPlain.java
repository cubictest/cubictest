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


	public static void handle(WatirHolder watirHolder, PageElement pe) {

		if (pe instanceof Select) {
			handleSelect(watirHolder, (Select) pe);
		}
		else if (pe instanceof AbstractContext) {
			handleAbstractContext(watirHolder, (AbstractContext) pe);
		}
	}

	
	/**
	 * Assert Context present and set watirHolder prefix.
	 */
	private static void handleAbstractContext(WatirHolder watirHolder, AbstractContext context) {
		
		if (!(context.getMainIdentifierType().equals(ID)))
			throw new ExporterException("Contexts must have identifier type = ID for Watir export. Context in error: " + context);

		String idValue = "\"" + StringUtils.replace(context.getMainIdentifierValue(),"\"", "\\\"") + "\"";
		String idType = WatirUtils.getMainIdType(context);

		//set prefix:
		watirHolder.setPrefix("(ie.div(" + idType + "," + idValue + "))");
		
		//assert present:
		watirHolder.add("if (ie.div(" + idType + "," + idValue + ") == nil)", 3);
		watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
		watirHolder.add("end", 3);
	}


	/**
	 * Assert Select present, set prefix and if label, save selectListID in script to be able to select options
	 */
	private static void handleSelect(WatirHolder watirHolder, IContext ctx) {
		
		Select select = (Select) ctx;
		
		String idText = "\"" + select.getMainIdentifierValue() + "\"";
		String idType = WatirUtils.getMainIdType(select);


		if (select.getMainIdentifierType().equals(IdentifierType.LABEL)) {
			watirHolder.add(WatirUtils.getLabelTargetId(select));
			idText = watirHolder.getWatirElementName(select);
			idType = ":id";
			watirHolder.add(watirHolder.getWatirElementName(select) + " = labelTargetId", 3);
		}
		else if (select.getMainIdentifierType().equals(IdentifierType.NAME)) {
			idType = ":name";
		}
		else if (select.getMainIdentifierType().equals(IdentifierType.ID)) {
			idType = ":id";
		}
		//set prefix (context):
		watirHolder.setPrefix("ie.select_list(" + idType + ", " + idText + ")");
		
		//assert select box present:
		watirHolder.add("if (" + watirHolder.getPrefix() + " == nil)", 3);
		watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
		watirHolder.add("end", 3);
	}

}
