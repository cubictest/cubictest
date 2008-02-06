/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
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
import static org.cubictest.model.Moderator.EQUAL;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Moderator;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.formElement.Option;


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
	public static String getOptionLocator(Option option) {
		Identifier optionMainId = option.getMainIdentifier();
		if (!optionMainId.getModerator().equals(EQUAL)) {
			throw new ExporterException("\n" + option.getParent() + " --> " + option + 
					":\nSelenium only supports \"be equal to\" identifiers for Options in SelectLists.");
		}
		String locator = "";
		if (optionMainId == null) {
			locator = "index=0";
		}
		else {
			locator = getOptionIdType(optionMainId) + "=" + optionMainId.getValue();
		}
		return locator;
	}
	
	
	
	private static String getOptionIdType(Identifier optionMainId) {
		if (optionMainId.getType().equals(IdentifierType.LABEL))
			return "label";
		else
			return ExportUtils.getHtmlIdType(optionMainId);
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
	public static String getValue(UserInteraction userInteraction) {
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
	 * Get the value for a Selenium command (get value for third column in a Selenese row).
	 * @param userInteraction
	 */
	public static boolean hasSeleniumInputColumn(UserInteraction userInteraction) {
		ActionType a = userInteraction.getActionType();
		
		if (a.equals(ENTER_TEXT) || a.equals(ENTER_PARAMETER_TEXT))
			return true;
		if (a.equals(SELECT))
			return true;
		if (a.equals(KEY_PRESSED))
			return true;
		if (a.equals(MOUSE_OVER))
			return true;
		if (a.equals(MOUSE_OUT))
			return true;
		if (a.equals(DBLCLICK))
			return true;
		if (a.equals(FOCUS))
			return true;
		if (a.equals(BLUR))
			return true;
		else
			return false;
	}
	

	/**
	 * Get user configured timeout in seconds.
	 * @param settings
	 * @return
	 */
	public static int getTimeout(CubicTestProjectSettings settings) {
		if(settings == null)
			return 60;
		return settings.getInteger(SeleniumUtils.getPluginPropertyPrefix(), "timeout", 60);
	}
	
	public static String getPluginPropertyPrefix() {
		return "SeleniumExporterPlugin";
	}
}
