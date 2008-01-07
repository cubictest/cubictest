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
package org.cubictest.exporters.selenium.runner.converters;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.exceptions.UserInteractionException;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

import com.thoughtworks.selenium.SeleniumException;

/**
 * Class to convert transitions to selenium commands.
 * 
 * @author chr_schwarz
 */
public class TransitionConverter implements ITransitionConverter<SeleniumHolder> {
	
	
	/**
	 * Converts a user interactions transition to a list of Selenium commands.
	 * 
	 * @param transition The transition to convert.
	 */
	public void handleUserInteractions(SeleniumHolder seleniumHolder, UserInteractionsTransition transition) {
		boolean waitForPageToLoad = false;
		
		for (UserInteraction action : transition.getUserInteractions()) {
			IActionElement actionElement = action.getElement();
			
			if (actionElement == null) {
				Logger.warn("Action element was null. Skipping user interaction: " + action);
				continue;
			}
			String commandName = handleUserInteraction(seleniumHolder, action);
			if (!commandName.equals(SeleniumUtils.FIREEVENT)) {
				waitForPageToLoad = true;
			}
			seleniumHolder.addResult(null, TestPartStatus.PASS);

		}
		if (waitForPageToLoad) {
			int timeout = SeleniumUtils.getTimeout(seleniumHolder.getSettings());
			waitForPageToLoad(seleniumHolder, timeout);
		}
	}


	private void waitForPageToLoad(SeleniumHolder seleniumHolder, int seconds) {
		try {
			long millis = seconds * 1000;
			seleniumHolder.getSelenium().waitForPageToLoad(millis + "");
		}
		catch (SeleniumException e) {
			ErrorHandler.logAndThrow("Selenium error while waiting for page to load. Timeout used: " + seconds + " seconds");
		}
	}
	
	
	/**
	 * Converts a single user interaction to a Selenium command.
	 * @return the Selenium command name invoked. 
	 */
	private String handleUserInteraction(SeleniumHolder seleniumHolder, UserInteraction userInteraction) {

		IActionElement element = userInteraction.getElement();
		ActionType actionType = userInteraction.getActionType();
		
		//Getting selenium commands, locators and values:
		String commandName = SeleniumUtils.getCommandName(actionType);

		String locator = null;
		String inputValue = null;
		
		if (element instanceof Option) {
			Select select = ((Option) element).getParent();
			locator = "xpath=" + seleniumHolder.getFullContextWithAllElements(select);
			inputValue = SeleniumUtils.getOptionLocator((Option) element);
		}
		else {
			//all other elements
			if (element instanceof PageElement) {
				locator = "xpath=" + seleniumHolder.getFullContextWithAllElements((PageElement) element);
			}
			else {
				throw new ExporterException("Unsupported action element type");

			}
			inputValue = SeleniumUtils.getValue(userInteraction);
		}
		
		try {
			//invoke user interaction by reflection using command name from SeleniumUtil:
			
			if (SeleniumUtils.hasSeleniumInputColumn(userInteraction)) {
				//two parameters
				seleniumHolder.getSelenium().execute(commandName, locator, inputValue);
			}
			else {
				//one parameter only
				seleniumHolder.getSelenium().execute(commandName, locator);
			}
			return commandName;
		}
		catch (Exception e) {
			String msg = "Error invoking user interaction: " + userInteraction.toString() + ".";
			if (element instanceof PageElement) {
				PageElement pe = (PageElement) element;
				if (pe.getStatus().equals(TestPartStatus.FAIL)) {
					msg += "\n\nPage element " + pe.toString() + " not found.";
				}
				seleniumHolder.addResult(pe, TestPartStatus.EXCEPTION, pe.isNot());
			}
			Logger.error(msg, e);
			throw new UserInteractionException(msg);
		}
	}
}
