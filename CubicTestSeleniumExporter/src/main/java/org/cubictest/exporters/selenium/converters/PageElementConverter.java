/*
 * Created on Apr 28, 2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
*/
package org.cubictest.exporters.selenium.converters;

import static org.cubictest.model.IdentifierType.LABEL;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.exporters.selenium.holders.SeleneseDocument;
import org.cubictest.exporters.selenium.holders.Command;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.FormElement;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkable;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;

/**
 * This class is responsible for creating Selenium doc for each element that can be checked for on a page.
 * 
 * @author chr_schwarz
 */
public class PageElementConverter implements IPageElementConverter<SeleneseDocument> {	
	
	/**
	 * Converts one element located on a page to a list of Selenese rows. 
	 * @param pe The Page element to convert.
	 */
	public void handlePageElement(SeleneseDocument doc, PageElement pe) {
		
		if (pe instanceof Title) {
			handleTitle(doc, (Title) pe);
		}
		else if (pe instanceof Link) {
			handleLink(doc, (Link) pe);
		}
		else if (pe instanceof Text) {
			handleText(doc, (Text) pe);
		}
		else if (pe instanceof FormElement) {
			handleFormElement(doc, (FormElement) pe);
		}
	}
	
	
	/**
	 * Appends a Selenese row verifying that the specified title is present.
	 * @param title The title to check for.
	 */
	private void handleTitle(SeleneseDocument doc, Title title) {
		String titleText = StringUtils.replace(title.getText(),"\"", "\\\"");
		
		doc.addCommand("verifyTitle", titleText).setDescription("Check title = '" + titleText + "'");
	}
	

	/**
	 * Appends a Selenese row testing for the presence of the given Text.
	 * @param text The text to check for.
	 */
	private void handleText(SeleneseDocument doc, Text text) {
		String txt = StringUtils.replace(text.getText(),"\"", "\\\"");
		
		doc.addCommand("waitForText", txt).setDescription("Check text present: '" + text + "'");
		
	}
	
	
	/**
	 * Creates Selenium ITestElement checking for the presence of a link.
	 * 
	 * @param link The link to check for.
	 * @return the Selenium Step
	 */
	private void handleLink(SeleneseDocument doc, Link link) {
		String idText = StringUtils.replace(link.getText(),"\"", "\\\"");
		String idType = SeleniumUtils.getIdType(link);
		
		doc.addCommand("assertElementPresent", idType + idText).setDescription("Check link present: " + idType + idText);
	}
	

	/**
	 * Creates a test element verifying that the form element in the argument is present.
	 */
	private void handleFormElement(SeleneseDocument doc, FormElement fe) {
		String elementType = SeleniumUtils.getElementType(fe);
		String idType = SeleniumUtils.getIdType(fe);
		String idText = fe.getText();
		String value = "";

		StringBuffer buff = new StringBuffer();

		if (fe.getIdentifierType().equals(LABEL) && !(fe instanceof Button) && !(fe instanceof Option)) {
			SeleniumUtils.appendGetLabelTargetId(buff, fe, fe.getDescription());
			idText = "\" + labelTargetId + \"";
			idType = ":id";
		}
		
		if (fe instanceof TextField || fe instanceof Password || fe instanceof TextArea){
			appendWaitStatement(buff, elementType, idType, idText, doc.getPrefix());
			append(buff, "assert_equal(\"" + value + "\", " + doc.getPrefix() + "." + elementType + "(" + idType + ", \"" + idText + "\").getContents())", 3);
		}
		else if (fe instanceof Option) {
			if (fe.getIdentifierType().equals(LABEL)) {
				SeleniumUtils.appendCheckOptionPresent(buff, doc.getPrefix(), fe.getText());
			}
			else {
				appendWaitStatement(buff, elementType, idType, idText, doc.getPrefix());
				append(buff, doc.getPrefix() + "." + elementType + "(" + idType + ", \"" + idText + "\").to_s()", 3);
			}
		}
		else if (fe instanceof Checkable){
			value = ((Checkable)fe).isChecked() + "";
			appendWaitStatement(buff, elementType, idType, idText, doc.getPrefix());
			append(buff, "assert(" + doc.getPrefix() + "." + elementType + "(" + idType + ", \"" + idText + "\").checked? == " + value + ")", 3);
		}
		else if (fe instanceof Button) {
			appendWaitStatement(buff, elementType, idType, idText, doc.getPrefix());
			append(buff, doc.getPrefix() + "." + elementType + "(" + idType + ", \"" + idText + "\").to_s()", 3);
		}
		else if (fe instanceof Select){
			appendWaitStatement(buff, elementType, idType, idText, doc.getPrefix());
			append(buff, doc.getPrefix() + "." + elementType + "(" + idType + ", \"" + idText + "\").to_s()", 3);
		}
		
		if (StringUtils.isNotBlank(buff.toString())) {
			String desc = "Check " + fe.getType() + " present with " + idType + " = '" + idText + "'";
			if (!(fe instanceof Option)) {
				desc += " and value = '" + value + "'";
			}
			
			Command step = new Command(buff.toString()).setDescription(desc);
			if (fe instanceof Option) {
				step.setDecorated(true); //do not wrap in retry logic
			}
		}
}



	
	/**
	 * @param element The element that are checked to be present. 
	 * @param type The type of the value representing the element in Selenium (name, value, etc)
	 * @param value The value representing the element on the wep-page.
	 * @return A Selenium statement that will wait for the element, maximum 2 secounds. 
	 */
	private void appendWaitStatement(StringBuffer buff, String element, String type, String value, String prefix) {
		
		append(buff, "count = 0", 3);
		append(buff, "while not " + prefix + "." + element + "(" + type + ", \"" + value + "\").exists? and count < 20 do", 3);
		append(buff, "sleep 0.1", 4);
		append(buff, "count += 1", 4);
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
