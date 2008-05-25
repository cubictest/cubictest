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

import static org.cubictest.model.Moderator.EQUAL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
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
		switch (a) {
		case CLICK:
			return "click";
		case CHECK:
			return "check";
		case UNCHECK:
			return "uncheck";
		case SELECT:
			return "select";
		case ENTER_TEXT:
			return "type";
		case ENTER_PARAMETER_TEXT:
			return "type";
		case CLEAR_ALL_TEXT:
			return "type";
		case KEY_PRESSED:
			return "keyPress";
		case MOUSE_OVER:
			return "mouseOver";
		case MOUSE_OUT:
			return "mouseOut";
		case DBLCLICK:
			return "doubleClick";
		case FOCUS:
			return FIREEVENT;
		case BLUR:
			return FIREEVENT;
		case GO_BACK:
			return "goBack";
		case REFRESH:
			return "reresh";
		case CLOSE:
			return "close";
		case SWITCH_WINDOW_BY_NAME:
			return "selectWindow";
		case DRAG_DROP:
			return "dragdrop";
		default:
			throw new ExporterException("Internal error: Could not get selenium command for action type " + a);
		}
	}

	
	/**
	 * Get a description of the command for the specified ActionType and element.
	 * @param a
	 * @param element
	 */
	public static String getCommandDescription(ActionType a, IActionElement element) {
		switch (a) {
		
		case CLICK:
			return "Clicking " + element;
		
		case CHECK:
			return "Checking " + element;
		
		case UNCHECK:
			return "Unchecking " + element;
		
		case SELECT:
			return "Selecting " + element;

		case ENTER_TEXT:
		case ENTER_PARAMETER_TEXT:
			return "Typing text " + element;
		
		case CLEAR_ALL_TEXT:
			return "Clearing text " + element;
		
		case KEY_PRESSED:
			return "Pressing key " + element;
		
		case MOUSE_OVER:
			return "Move mouse to " + element;
		
		case MOUSE_OUT:
			return "Remove mouse from " + element;
		
		case DBLCLICK:
			return "Doubleclicking on " + element;
		
		case FOCUS:
			return "Setting focus on " + element;
		
		case BLUR:
			return "Removing focus from " + element;
		
		case GO_BACK:
			return "Going back to previous page " + element;

		case REFRESH:
			return "Refreshing page " + element;
			
		case DRAG_DROP:
			return "Drag'n drop " + element;
			
		default:
			throw new ExporterException("Internal error: Could get command description for action type " + a);
		}
	}
	
	
	
	/**
	 * Get the value for a Selenium command (get value for third column in a Selenese row).
	 * @param userInteraction
	 */
	public static String getValue(UserInteraction userInteraction) {
		ActionType a = userInteraction.getActionType();
		switch (a){
			case ENTER_TEXT: 
			case ENTER_PARAMETER_TEXT:
				return userInteraction.getTextualInput();
			case SELECT:
				throw new ExporterException("getValue not supported for Options.");
			case FOCUS:
				return "focus";
			case BLUR:
				return "blur";
			case DRAG_DROP:
				return userInteraction.getValue();
			default:
				return "";
		}
	}
	
	
	/**
	 * Get the value for a Selenium command (get value for third column in a Selenese row).
	 * @param userInteraction
	 */
	public static boolean hasSeleniumInputColumn(UserInteraction userInteraction) {
		ActionType a = userInteraction.getActionType();
		switch (a){
			case ENTER_TEXT:
			case ENTER_PARAMETER_TEXT:
			case SELECT:
			case FOCUS:
			case BLUR:
			case DRAG_DROP:
				return true;
			default:
				return false;
		}
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
	
	
	public static void writeTextToFile(String directory, String fileName, String text){
		File textFile = new File(directory + File.separator
                + fileName + "_" + System.currentTimeMillis() + ".txt");
		try {
			FileWriter writer = new FileWriter(textFile);
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			Logger.error("Error writing text to file", e);
		}
	}
	
}
