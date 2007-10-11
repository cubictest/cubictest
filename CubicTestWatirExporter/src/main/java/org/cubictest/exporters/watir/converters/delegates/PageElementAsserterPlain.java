/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters.delegates;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Page element converter that uses standard Watir without XPath.
 * 
 * @author Christian Schwarz
 */
public class PageElementAsserterPlain {

	public static void handle(WatirHolder watirHolder, PageElement pe) {
		String idValue = "\"" + StringUtils.replace(pe.getMainIdentifierValue(),"\"", "\\\"") + "\"";
		String idType = WatirUtils.getMainIdType(pe);



		if (pe instanceof Title) {
			watirHolder.add("if (ie.title() != " + idValue + ")", 2);
			watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 3);
			watirHolder.add("end", 2);
		}
		else if (pe instanceof Option) {
			Option option = (Option) pe;
			String selectList = watirHolder.getPrefix();
			Select select = (Select) option.getParent();
			if (select.getMainIdentifierType().equals(IdentifierType.LABEL)) {
				//If parent select list had label idType, assert that its label target ID was found:
				watirHolder.add("if (" + watirHolder.getVariableName(select) + " == nil)", 3);
				watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
				watirHolder.add("end", 3);
			}
			
			if (option.getMainIdentifierType().equals(IdentifierType.LABEL)) {
				watirHolder.add("optionFound = false", 3);
				watirHolder.add(selectList + ".getAllContents().each do |opt|", 3);
				watirHolder.add("if(opt == \"" + pe.getMainIdentifierValue() + "\")", 4);
				watirHolder.add("optionFound = true", 5);
				watirHolder.add("end", 4);
				watirHolder.add("end", 3);
				watirHolder.add("if (!optionFound)", 3);
				watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
				watirHolder.add("end", 3);
			}
			else if (option.getMainIdentifierType().equals(IdentifierType.VALUE)) {
				watirHolder.add("if (" + selectList + ".option(" + WatirUtils.getMainIdType(pe) + ", \"" + pe.getMainIdentifierValue() + "\") == nil)", 2);
				watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 3);
				watirHolder.add("end", 2);
			}
			else if (option.getMainIdentifierType().equals(IdentifierType.INDEX)) {
				watirHolder.add(watirHolder.getVariableName(option) + " = " + selectList + ".getAllContents()[" + (Integer.parseInt(pe.getMainIdentifierValue()) - 1) + "]", 3);
				watirHolder.add("if (" + watirHolder.getVariableName(option) + " == nil)", 3);
				watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
				watirHolder.add("end", 3);
			}			else {
				throw new ExporterException("Only label, value and index are supported identifierts " +
						"for options in select lists in Watir");
			}
		}
		else {
			//handle all other page elements:			

			if (WatirUtils.shouldGetLabelTargetId(pe)) {
				watirHolder.add(WatirUtils.getLabelTargetId(pe, watirHolder));
				watirHolder.addSeparator();
				idValue = watirHolder.getVariableName(pe);
				idType = ":id";
			}
			
			watirHolder.add("pass = 0", 3);
			if (pe instanceof Text) {
				String compare = pe.isNot() ? "!=" : "=="; 
				watirHolder.add("while " + watirHolder.getPrefix() + ".text.index(" + idValue + ") " + compare + " nil do", 3);
			}
			else {
				String not = pe.isNot() ? "" : "not "; 
				watirHolder.add("while " + not + "ie." + WatirUtils.getElementType(pe) + "(" + idType + ", " + idValue + ").exists? do", 3);
			}
			watirHolder.add("if (pass > 20)", 4);
			watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 5);
			watirHolder.add("end", 4);
			watirHolder.add("sleep 0.1", 4);
			watirHolder.add("pass += 1", 4);
			watirHolder.add("end", 3);

		}
	}
}
