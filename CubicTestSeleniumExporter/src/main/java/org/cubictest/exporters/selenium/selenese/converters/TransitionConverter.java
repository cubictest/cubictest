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
package org.cubictest.exporters.selenium.selenese.converters;

import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.selenese.holders.SeleneseDocument;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.PageElement;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Class to convert transitions to selenium commands.
 * 
 * @author chr_schwarz
 */
public class TransitionConverter implements ITransitionConverter<SeleneseDocument> {
	
	
	/**
	 * Converts a user interactions transition to Selenium commands (Selenese steps).
	 */
	public void handleUserInteractions(SeleneseDocument doc, UserInteractionsTransition transition) {
		for (UserInteraction action : transition.getUserInteractions()) {
			IActionElement actionElement = action.getElement();
			
			if (actionElement == null) {
				Logger.warn("Action element was null. Skipping user interaction: " + action);
				continue;
			}
			handleUserInteraction(doc, action);
		}
		
		if (transition.hasCustomTimeout()) {
			int timeout = transition.getSecondsToWaitForResult();
			doc.addCommand("setTimeout", (timeout * 1000) + "");
		}
	}
	
	
	/**
	 * Converts a single user interaction to a Selenium command (selenese row).
	 */
	private String handleUserInteraction(SeleneseDocument doc, UserInteraction userInteraction) {

		IActionElement element = userInteraction.getElement();
		ActionType actionType = userInteraction.getActionType();
		
		//Getting selenium commands, locators and values:
		String commandName = SeleniumUtils.getCommandName(actionType);
		String commandDesc = SeleniumUtils.getCommandDescription(actionType, element);

		String locator = "";
		String inputValue = "";
		
		if (element instanceof Option) {
			Select select = ((Option) element).getParent();
			locator = "xpath=" + doc.getFullContextWithAllElements(select);
			inputValue = SeleniumUtils.getOptionLocator((Option) element);
		}
		else {
			//all other elements
			if (element instanceof PageElement) {
				locator = "xpath=" + doc.getFullContextWithAllElements((PageElement) element);
			}
			else {
				throw new ExporterException("Unsupported action element type");
			}
			inputValue = SeleniumUtils.getValue(userInteraction);
		}
		
		doc.addCommand(commandName, locator, inputValue).setDescription(commandDesc);
		return commandName;
	}

	
}
