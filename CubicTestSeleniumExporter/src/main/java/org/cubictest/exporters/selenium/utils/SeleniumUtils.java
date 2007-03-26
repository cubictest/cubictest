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
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.NAME;
import static org.cubictest.model.IdentifierType.VALUE;

import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.holders.SeleneseDocument;
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

	
	/**
	 * Get the string that represents the Selenium locator-string for the element.
	 * @param element
	 * @return
	 */
	public static String getLocator(IActionElement element, SeleneseDocument doc) {
		if (element instanceof WebBrowser) {
			return "";
		}
		PageElement pe = (PageElement) element;

		IdentifierType idType = pe.getMostSignificantIdentifier().getType();
		String idText = getIdText(pe);
		String context = doc.getFullContext();
		
		if (idType.equals(ID)) {
			return "xpath=" + context + getHtmlElementType(pe) + "[@id=\"" + idText + "\"]";
		}
		if (idType.equals(NAME)) {
			return "xpath=" + context + getHtmlElementType(pe) + "[@name=\"" + idText + "\"]";
		}
		if (idType.equals(VALUE)) {
			throw new ExporterException("\"Value\" identifier type not supported as locator.");
		}
		if (idType.equals(LABEL)) {
			if (element instanceof Text) {
				String axis = (context.equals("//")) ? "" : "descendant-or-self::";
				return "xpath=" + context + axis + "*[contains(text(), \"" + idText + "\")]";
			}
			else if (element instanceof Link) {
				return "xpath=" + context + getHtmlElementType(pe) + "[text()=\"" + idText + "\"]";
			}
			else if (element instanceof Option) {
				return "label=" + idText;
			}
			else if (element instanceof Button) {
				return "xpath=" + context + "input[(@type=\"button\" or @type=\"submit\") and @value=\"" + idText + "\"]";
			}
			else {
				//get first element that has "id" attribute equal to the "for" attribute of label with the specified text:
				return "xpath=" + context + getHtmlElementType(pe) + "[@id=(//label[text()=\"" + idText + "\"]/@for)]";
			}
		}
		else {
			throw new ExporterException("Identifier type not recognized.");
		}
	}
	
	
	/**
	 * Get the identifier value of the most significant identifier.
	 * @param pe
	 * @return
	 */
	public static String getIdText(PageElement pe) {
		return pe.getMostSignificantIdentifier().getValue();
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
			return "fireEvent";
		
		if (a.equals(MOUSE_OVER))
			return "fireEvent";
		
		if (a.equals(MOUSE_OUT))
			return "fireEvent";
		
		if (a.equals(DBLCLICK))
			return "fireEvent";
		
		if (a.equals(FOCUS))
			return "fireEvent";
		
		if (a.equals(BLUR))
			return "fireEvent";
		
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
	public static String getValue(UserInteraction userInteraction, SeleneseDocument doc) {
		ActionType a = userInteraction.getActionType();
		
		if (a.equals(ENTER_TEXT) || a.equals(ENTER_PARAMETER_TEXT))
			return userInteraction.getTextualInput();
		if (a.equals(SELECT))
			return getLocator((Option) userInteraction.getElement(), doc);
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

}
