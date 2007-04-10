/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.utils;

import static org.cubictest.model.ActionType.BLUR;
import static org.cubictest.model.ActionType.CHECK;
import static org.cubictest.model.ActionType.CLEAR_ALL_TEXT;
import static org.cubictest.model.ActionType.CLICK;
import static org.cubictest.model.ActionType.DBLCLICK;
import static org.cubictest.model.ActionType.ENTER_PARAMETER_TEXT;
import static org.cubictest.model.ActionType.ENTER_TEXT;
import static org.cubictest.model.ActionType.FOCUS;
import static org.cubictest.model.ActionType.GO_BACK;
import static org.cubictest.model.ActionType.KEY_PRESSED;
import static org.cubictest.model.ActionType.MOUSE_OUT;
import static org.cubictest.model.ActionType.MOUSE_OVER;
import static org.cubictest.model.ActionType.REFRESH;
import static org.cubictest.model.ActionType.SELECT;
import static org.cubictest.model.ActionType.UNCHECK;
import static org.cubictest.model.IdentifierType.CHECKED;
import static org.cubictest.model.IdentifierType.HREF;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.INDEX;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.NAME;
import static org.cubictest.model.IdentifierType.SELECTED;
import static org.cubictest.model.IdentifierType.SRC;
import static org.cubictest.model.IdentifierType.TITLE;
import static org.cubictest.model.IdentifierType.VALUE;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.model.ActionType;
import org.cubictest.model.FormElement;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.WebBrowser;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;


/**
 * Util class for Selenium export.
 * 
 * @author chr_schwarz
 */
public class SeleniumUtils {

	public static final String FIREEVENT = "fireEvent";
	
	/**
	 * Get the string that represents the Selenium locator-string for the element.
	 * @param element
	 * @return
	 */
	public static String getXPath(IActionElement element, ContextHolder contextHolder) {
		if (element instanceof WebBrowser) {
			return "";
		}
		PageElement pe = (PageElement) element;

		boolean hasLabel = false;
		for (Identifier id : pe.getNonIndifferentIdentifierts()) {
			if (id.getType().equals(LABEL)) {
				hasLabel = true;
			}
		}
		
		String index = "";
		if (pe.getIdentifier(INDEX) != null && pe.getIdentifier(INDEX).getProbability() != 0) {
			index = "[" + Integer.parseInt(pe.getIdentifier(INDEX).getValue()) + "]";
		}
		if (hasLabel) {
			String labelText = pe.getIdentifier(LABEL).getValue();
			String comparisonOperator = "=";
			if (pe.getIdentifier(LABEL).getProbability() < 0) {
				comparisonOperator = "!=";
			}

			if (element instanceof Text) {
				String axis = (contextHolder.getFullContext().equals("//")) ? "" : "descendant-or-self::";
				return axis + "*[contains(text(), \"" + labelText + "\")]";
			}
			else if (element instanceof Link || element instanceof Option) {
				return getHtmlElementType(pe) + index + "[text()" + comparisonOperator + "\"" + labelText + "\"" + getAttributeConstraints(pe, true) + "]";
			}
			else if (element instanceof Button) {
				return getHtmlElementType(pe) + index + "[(@type=\"button\" or @type=\"submit\") and @value" + comparisonOperator + "\"" + labelText + "\"" + getAttributeConstraints(pe, true) + "]";
			}
			else {
				//get first element that has "id" attribute equal to the "for" attribute of label with the specified text:
				return getHtmlElementType(pe) + index + "[@id" + comparisonOperator + "(//label[text()=\"" + labelText + "\"]/@for)" + getAttributeConstraints(pe, true) + "]";
			}
		}
		else {
			return getHtmlElementType(pe) + index + "[" + getAttributeConstraints(pe, false) + "]";

		}
	}
	

	/**
	 * Get string to assert for all the page elements Identifier/HTML attribute values.
	 * E.g. [@id="someId"]
	 */
	private static String getAttributeConstraints(PageElement pe, boolean startWithAnd) {
		String result = "";
		if (startWithAnd) {
			result += " and ";
		}
		int i = 0;
		boolean attributeFound = false;
		for (Identifier id : pe.getNonIndifferentIdentifierts()) {
			if (id.getType().equals(LABEL) || id.getType().equals(INDEX)) {
				//label and index are not HTML attributes
				continue;
			}
			if (i > 0) {
				result += " and ";
			}
			String comparisonOperator = "=";
			if (id.getProbability() < 0) {
				comparisonOperator = "!=";
			}
			
			if (id.getType().equals(CHECKED) || id.getType().equals(SELECTED)) {
				//id type with no value
				if (id.getProbability() > 0) {
					result += "@" + getIdType(id)+ "=\"\"";
				}
				else {
					result += "not(@" + getIdType(id) + ")";
				}
			}
			else {
				//normal ID type (name, value)
				result += "@" + getIdType(id) + comparisonOperator + "\"" + id.getValue() + "\"";
			}
			attributeFound = true;
			i++;
		}
		return attributeFound ? result : "";
	}
	

	
	/**
	 * Get the HTML element type for the page element.
	 * @param pe
	 * @return
	 */
	public static String getHtmlElementType(PageElement pe) {
		if (pe instanceof Select)
			return "select";
		if (pe instanceof Option)
			return "option";
		if (pe instanceof FormElement)
			return "input";
		if (pe instanceof Link)
			return "a";
		if (pe instanceof Image)
			return "img";
		if (pe instanceof AbstractContext)
			return "div";
		if (pe instanceof Text)
			throw new ExporterException("Text is not a supported element type for identification.");
		
		throw new ExporterException("Unknown element type: " + pe);
	}

	
	/**
	 * Get the Selenium command name for the specified ActionType.
	 * @param a
	 */
	public static String getCommandName(ActionType a) {
		if (a.equals(CLICK))
			return "click";
		
		if (a.equals(CHECK))
			return "check";
		
		if (a.equals(UNCHECK))
			return "uncheck";
		
		if (a.equals(SELECT))
			return "select";
		
		if (a.equals(ENTER_TEXT))
			return "type";
		
		if (a.equals(ENTER_PARAMETER_TEXT))
			return "type";
		
		if (a.equals(CLEAR_ALL_TEXT))
			return "type";
		
		if (a.equals(KEY_PRESSED))
			return FIREEVENT;
		
		if (a.equals(MOUSE_OVER))
			return FIREEVENT;
		
		if (a.equals(MOUSE_OUT))
			return FIREEVENT;
		
		if (a.equals(DBLCLICK))
			return FIREEVENT;
		
		if (a.equals(FOCUS))
			return FIREEVENT;
		
		if (a.equals(BLUR))
			return FIREEVENT;
		
		if (a.equals(GO_BACK))
			return "goBack";

		if (a.equals(REFRESH))
			return "reresh";
		
		else
			throw new ExporterException("Internal error: Could not get selenium command for action type " + a);
	}

	
	/**
	 * Get a description of the command for the specified ActionType and element.
	 * @param a
	 * @param element
	 */
	public static String getCommandDescription(ActionType a, IActionElement element) {
		if (a.equals(CLICK))
			return "Clicking " + element;
		
		if (a.equals(CHECK))
			return "Checking " + element;
		
		if (a.equals(UNCHECK))
			return "Unchecking " + element;
		
		if (a.equals(SELECT))
			return "Selecting " + element;

		if (a.equals(ENTER_TEXT) || a.equals(ENTER_PARAMETER_TEXT))
			return "Typing text " + element;
		
		if (a.equals(CLEAR_ALL_TEXT))
			return "Clearing text " + element;
		
		if (a.equals(KEY_PRESSED))
			return "Pressing key " + element;
		
		if (a.equals(MOUSE_OVER))
			return "Move mouse to " + element;
		
		if (a.equals(MOUSE_OUT))
			return "Remove mouse from " + element;
		
		if (a.equals(DBLCLICK))
			return "Doubleclicking on " + element;
		
		if (a.equals(FOCUS))
			return "Setting focus on " + element;
		
		if (a.equals(BLUR))
			return "Removing focus from " + element;
		
		if (a.equals(GO_BACK))
			return "Going back to previous page " + element;

		if (a.equals(REFRESH))
			return "Refreshing page " + element;
		
		else
			throw new ExporterException("Internal error: Could get command description for action type " + a);
	}
	
	
	
	/**
	 * Get the value for a Selenium command (get value for third column in a Selenese row).
	 * @param userInteraction
	 */
	public static String getValue(UserInteraction userInteraction, ContextHolder contextHolder) {
		ActionType a = userInteraction.getActionType();
		
		if (a.equals(ENTER_TEXT) || a.equals(ENTER_PARAMETER_TEXT))
			return userInteraction.getTextualInput();
		if (a.equals(SELECT))
			throw new ExporterException("getValue not supported for Options.");
		if (a.equals(KEY_PRESSED))
			return "onkeypress";
		if (a.equals(MOUSE_OVER))
			return "onmouseover";
		if (a.equals(MOUSE_OUT))
			return "onmouseout";
		if (a.equals(DBLCLICK))
			return "ondblclick";
		if (a.equals(FOCUS))
			return "onfocus";
		if (a.equals(BLUR))
			return "onblur";
		else
			return "";
	}
	
	
	/**
	 * Get the Selenium ID type based on the specified Identifier.
	 * Also works for HTML ID's except for LABEL.
	 * @param id
	 * @return
	 */
	public static String getIdType(Identifier id) {
		if (id.getType().equals(LABEL)) {
			return "label";
		}
		else if (id.getType().equals(NAME)) {
			return "name";
		}
		else if (id.getType().equals(ID)) {
			return "id";
		}
		else if (id.getType().equals(VALUE)) {
			return "value";
		}
		else if (id.getType().equals(HREF)) {
			return "href";
		}
		else if (id.getType().equals(SRC)) {
			return "src";
		}
		else if (id.getType().equals(TITLE)) {
			return "title";
		}
		else if (id.getType().equals(CHECKED)) {
			return "checked";
		}
		else if (id.getType().equals(SELECTED)) {
			return "selected";
		}
		else if (id.getType().equals(INDEX)) {
			return "index";
		}
		return null;
	}

}
