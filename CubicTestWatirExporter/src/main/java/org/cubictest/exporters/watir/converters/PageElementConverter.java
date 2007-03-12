/*
 * Created on Apr 28, 2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.watir.converters;

import static org.cubictest.model.IdentifierType.LABEL;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.exporters.watir.holders.StepList;
import org.cubictest.exporters.watir.holders.TestStep;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.FormElement;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkable;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;

/**
 * This class is responsible for creating Watir steps for each element that can be checked for on a page.
 * 
 * @author chr_schwarz
 * @author stinelil
 */
public class PageElementConverter implements IPageElementConverter<StepList> {	
	
	/**
	 * Converts one element located on a page to a list of Watir Steps.
	 * 
	 * @param pe The Page element to convert.
	 * @return An <code>java.util.List</code> with Watir Steps.
	 */
	public void handlePageElement(StepList steps, PageElement pe) {
		
		if (pe instanceof Title) {
			handleTitle(steps, (Title) pe);
		}
		else if (pe instanceof Link) {
			handleLink(steps, (Link) pe);
		}
		else if (pe instanceof Text) {
			handleText(steps, pe);
		}
		else if (pe instanceof FormElement) {
			handleFormElement(steps, (FormElement) pe);
		}
	}
	
	
	/**
	 * Creates a test step for verifying that the specified title is present.
	 * @param title The title to check for.
	 * @return A test step
	 */
	private void handleTitle(StepList steps, Title title) {
		String titleText = StringUtils.replace(title.getText(),"\"", "\\\"");
		String verify = "assert_equal(\"" + titleText + "\"," + steps.getPrefix() + ".title())";
		steps.add(new TestStep(verify).setDescription("Check title = '" + titleText + "'"));
	}
	

	/**
	 * Creates a Watir ITestElement testing for the presence of the given Text.
	 * Supports contexts.
	 * 
	 * @param text The text to check for.
	 * @return The Watir Step.
	 */
	private void handleText(StepList steps, PageElement text) {
		String txt = StringUtils.replace(text.getText(),"\"", "\\\"");
		
		StringBuffer str = new StringBuffer();
		
		append(str, "pass = 0", 2);
		
		if (text.isNot()){
			append(str, "textIndex = 0", 2);
			append(str, "while textIndex != nil and pass < 20 do", 2);
			append(str, "begin", 3);
			append(str, "textIndex = " + steps.getPrefix() + ".text.index(\"" + text.getText() + "\")", 4);
			append(str, "if(textIndex != nil)", 4);
			append(str, "raise " + StepList.TEXT_ASSERTION_FAILURE, 5);
			append(str, "end", 4);
		} else {
			append(str, "textIndex = nil", 2);
			append(str, "while textIndex == nil and pass < 20 do", 2);
			append(str, "begin", 3);
			append(str, "textIndex = "+ steps.getPrefix() +".text.index(\"" + text.getText() + "\")", 4);
			append(str, "if(textIndex == nil)", 4);
			append(str, "raise " + StepList.TEXT_ASSERTION_FAILURE, 5);
			append(str, "end", 4);
		}
		append(str, "passedSteps += 1 ", 4);
		append(str, "rescue " + StepList.TEXT_ASSERTION_FAILURE, 3);
		append(str, "pass += 1", 4);
		append(str, "sleep 0.1", 4);
		append(str, "if ( pass >= 20 ) then", 4);
		append(str, "failedSteps += 1 ", 5);
		String prefixEsc = StringUtils.replace(steps.getPrefix(),"\"", "\\\"");
		String ctxMessage = "";
		if (!prefixEsc.equalsIgnoreCase("ie")) {
			ctxMessage = "(context: '" + prefixEsc + "')";
		}
		if (text.isNot()){
			append(str, "puts \"Step failed: Check text NOT present: '"+ txt + "' " + ctxMessage + "\"", 5);
		} else {
			append(str, "puts \"Step failed: Check text present: '"+ txt + "' " + ctxMessage + "\"", 5);
		}
		append(str, "end", 4);
		append(str, "end", 3);
		append(str, "end", 2);
		
		TestStep step = new TestStep(str.toString());
		step.setDecorated(true);

		if (text.isNot())
		{
			step.setDescription("Check text NOT present: '" + txt + "', prefix " + steps.getPrefix());
		}
		else{
			step.setDescription("Check text present: '" + txt + "', prefix: " + steps.getPrefix());
		}
		
		steps.add(step);
	}
	
	
	/**
	 * Creates Watir ITestElement checking for the presence of a link.
	 * 
	 * @param link The link to check for.
	 * @return the Watir Step
	 */
	private void handleLink(StepList steps, Link link) {
		String idText = StringUtils.replace(link.getText(),"\"", "\\\"");
		String idType = WatirUtils.getIdType(link);
		TestStep step = null;
		
		if (link.isNot()) {
			step = new TestStep("assert(!ie.link(" + idType + ", \"" + idText + "/\").exists?)");
			step.setDescription("Check link NOT present with " + idType + " = '" + idText + "'");
		}
		else {
			StringBuffer buff = new StringBuffer();
			appendWaitStatement(buff, "link", idType, idText, steps.getPrefix());
			step = new TestStep(buff.toString()).setDescription("Check link present with " + idType + " = '" + idText + "'");			
		}
		
		steps.add(step);
	}
	

	/**
	 * Creates a test element verifying that the form element in the argument is present.
	 */
	private void handleFormElement(StepList steps, FormElement fe) {
		String elementType = WatirUtils.getElementType(fe);
		String idType = WatirUtils.getIdType(fe);
		String idText = fe.getText();
		String value = "";

		StringBuffer buff = new StringBuffer();

		if (fe.getIdentifierType().equals(LABEL) && !(fe instanceof Button) && !(fe instanceof Option)) {
			WatirUtils.appendGetLabelTargetId(buff, fe, fe.getDescription());
			idText = "\" + labelTargetId + \"";
			idType = ":id";
		}
		
		if (fe instanceof TextField || fe instanceof Password || fe instanceof TextArea){
			appendWaitStatement(buff, elementType, idType, idText, steps.getPrefix());
			//Assert contents of field:
			append(buff, "assert_equal(\"" + value + "\", " + steps.getPrefix() + "." + elementType + "(" + idType + ", \"" + idText + "\").getContents())", 3);
		}
		else if (fe instanceof Option && fe.getIdentifierType().equals(LABEL)) {
			WatirUtils.appendCheckOptionPresent(buff, steps.getPrefix(), fe.getText());
		}
		else if (fe instanceof Checkable){
			value = ((Checkable)fe).isChecked() + "";
			appendWaitStatement(buff, elementType, idType, idText, steps.getPrefix());
			//assert checked status:
			append(buff, "assert(" + steps.getPrefix() + "." + elementType + "(" + idType + ", \"" + idText + "\").checked? == " + value + ")", 3);
		}
		else {
			appendWaitStatement(buff, elementType, idType, idText, steps.getPrefix());
		}
		
		if (StringUtils.isNotBlank(buff.toString())) {
			String desc = "Check " + fe.getType() + " present with " + idType + " = '" + idText + "'";
			if (!(fe instanceof Option)) {
				desc += " and value = '" + value + "'";
			}
			
			TestStep step = new TestStep(buff.toString()).setDescription(desc);
			if (fe instanceof Option) {
				step.setDecorated(true); //do not wrap in retry logic
			}
			steps.add(step);
		}
	}


	
	/**
	 * @param elementType The element that are checked to be present. 
	 * @param idType The type of the value representing the element in Watir (name, value, etc)
	 * @param idText The value representing the element on the wep-page.
	 * @return A Watir statement that will wait for the element, maximum 2 secounds. 
	 */
	private void appendWaitStatement(StringBuffer buff, String elementType, String idType, String idText, String prefix) {
		
		append(buff, "pass = 0", 3);
		append(buff, "while not " + prefix + "." + elementType + "(" + idType + ", \"" + idText + "\").exists? do", 3);
		append(buff, "sleep 0.1", 4);
		append(buff, "pass += 1", 4);
		append(buff, "if (pass > 20)", 4);
		append(buff, "raise " + StepList.ELEMENT_ASSERTION_FAILURE, 5);
		append(buff, "end", 4);
		append(buff, "end", 3);
	}
	
	private void append(StringBuffer buff, String s, int indent) {
		for (int i = 0; i < indent; i++) {
			buff.append("\t");			
		}
		buff.append(s);
		buff.append("\n");
	}


	
}
