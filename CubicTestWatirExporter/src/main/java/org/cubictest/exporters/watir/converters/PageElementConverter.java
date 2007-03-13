/*
 * Created on Apr 28, 2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.watir.converters;

import static org.cubictest.model.IdentifierType.LABEL;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.exporters.watir.holders.RubyBuffer;
import org.cubictest.exporters.watir.holders.StepList;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.formElement.Checkable;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;

/**
 * Converts a page element located on a page to a watir assertion.
 * 
 * @author chr_schwarz
 */
public class PageElementConverter implements IPageElementConverter<StepList> {	
	
	
	/**
	 * Converts a page element located on a page to a watir assertion.
	 */
	public void handlePageElement(StepList stepList, PageElement pe) {
		stepList.addSeparator();
		RubyBuffer rubyBuffer = new RubyBuffer();
		String idText = "\"" + StringUtils.replace(pe.getText(),"\"", "\\\"") + "\"";
		String idType = WatirUtils.getIdType(pe);
		
		if (WatirUtils.shouldExamineHtmlLabelTag(pe)) {
			stepList.add(WatirUtils.getLabelTargetId(pe));
			stepList.addSeparator();
			idText = "labelTargetId";
			idType = ":id";
		}
		
		String not = pe.isNot() ? " not" : ""; 
		rubyBuffer.add("# asserting " + pe.getType() + " with " + idType.substring(1) + " = " + idText + not + " present", 2);
		assertElementPresent(stepList, pe, rubyBuffer, idText, idType);

		assertInputElementContents(stepList, pe, rubyBuffer, idText, idType);

		handleAssertionFailure(stepList, pe, rubyBuffer);
		
		stepList.add(rubyBuffer.toString());
	}


	private void assertElementPresent(StepList stepList, PageElement pe, RubyBuffer rubyBuffer, String idText, String idType) {
		if (pe instanceof Title) {
			rubyBuffer.add("if (ie.title() != " + idText + ")", 2);
			rubyBuffer.add("raise " + StepList.TEST_STEP_FAILED, 3);
			rubyBuffer.add("end", 2);
		}
		else if (pe instanceof Option && pe.getIdentifierType().equals(LABEL)) {
			String selectList = stepList.getPrefix();
			rubyBuffer.add("optionFound = false", 2);
			rubyBuffer.add(selectList + ".getAllContents().each do |opt|", 2);
			rubyBuffer.add("if(opt.to_s() == \"" + pe.getText() + "\")", 3);
			rubyBuffer.add("optionFound = true", 4);
			rubyBuffer.add("end", 3);
			rubyBuffer.add("end", 2);
			rubyBuffer.add("if (!optionFound)", 2);
			rubyBuffer.add("puts \"Did not find option with text '" + pe.getText() + "' in select list " + selectList.replace("\"", "'") + "\"", 3);
			rubyBuffer.add("end", 2);
		}
		else {
			//handle all other page elements:			
			rubyBuffer.add("begin", 2);
			rubyBuffer.add("pass = 0", 3);
			if (pe instanceof Text) {
				String compare = pe.isNot() ? ">" : "<"; 
				rubyBuffer.add("while " + stepList.getPrefix() + ".text.index(" + idText + ") " + compare + " 0 do", 3);
			}
			else {
				String not = pe.isNot() ? "" : "not "; 
				rubyBuffer.add("while " + not + stepList.getPrefix() + "." + WatirUtils.getElementType(pe) + "(" + idType + ", " + idText + ").exists? do", 3);
			}
			rubyBuffer.add("if (pass > 20)", 4);
			rubyBuffer.add("raise " + StepList.TEST_STEP_FAILED, 5);
			rubyBuffer.add("end", 4);
			rubyBuffer.add("sleep 0.1", 4);
			rubyBuffer.add("pass += 1", 4);
			rubyBuffer.add("end", 3);
		}
	}

	

	private void assertInputElementContents(StepList stepList, PageElement pe, RubyBuffer rubyBuffer, String idText, String idType) {
		if (pe instanceof TextField || pe instanceof Password || pe instanceof TextArea){
			//Assert contents of field to be blank:
			rubyBuffer.add("if not (" + stepList.getPrefix() + "." + WatirUtils.getElementType(pe) + "(" + idType + ", " + idText + ").getContents() == \"\")", 3);
			rubyBuffer.add("raise " + StepList.TEST_STEP_FAILED, 4);
			rubyBuffer.add("end", 3);
		}
		else if (pe instanceof Checkable){
			String checked = ((Checkable)pe).isChecked() + "";
			//Assert checked status:
			rubyBuffer.add("if not (" + stepList.getPrefix() + "." + WatirUtils.getElementType(pe) + "(" + idType + ", " + idText + ").checked? == " + checked + ")", 3);
			rubyBuffer.add("raise " + StepList.TEST_STEP_FAILED, 3);
			rubyBuffer.add("end", 3);
		}
	}
	
	private void handleAssertionFailure(StepList stepList, PageElement pe, RubyBuffer rubyBuffer) {
		rubyBuffer.add("passedSteps += 1 ", 3);
		rubyBuffer.add("rescue " + StepList.TEST_STEP_FAILED, 2);
		rubyBuffer.add("failedSteps += 1 ", 3);

		String prefix = StringUtils.replace(stepList.getPrefix(),"\"", "\\\"");
		String contextInfo = "";
		if (!prefix.equalsIgnoreCase("ie")) {
			contextInfo = " (context: '" + prefix + "')";
		}
		
		String not = pe.isNot() ? " not" : ""; 
		rubyBuffer.add("puts \"Step failed: Check " + pe.getType() + not + " present: '+ pe.getText() + '" + contextInfo + "\"", 3);
		rubyBuffer.add("end", 2);
	}
	
	



	
}
