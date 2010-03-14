/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.runner.converters;

import static org.cubictest.model.ActionType.SELECT;
import static org.cubictest.model.IdentifierType.MULTISELECT;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.exceptions.UserInteractionException;
import org.cubictest.exporters.selenium.runner.holders.CubicTestLocalRunner;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.WebBrowser;
import org.cubictest.model.context.Frame;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

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
		
		for (UserInteraction userInteraction : transition.getUserInteractions()) {
			IActionElement actionElement = userInteraction.getElement();
			
			if (actionElement == null) {
				Logger.warn("Action element was null. Skipping user interaction: " + userInteraction);
				continue;
			}
			
			int waitMillis = seleniumHolder.getNextPageElementTimeout() * 1000;
			int waitIntervalMillis = 100;
			int i = 0;
			while(true) {
				try {
					handleUserInteraction(seleniumHolder, userInteraction);
					break;
				}
				catch (UserInteractionException e) {
					if (i > waitMillis) {
						handleUserInteractionFailure(seleniumHolder, userInteraction, e);
					}
					try {
						Thread.sleep(waitIntervalMillis);
						i += waitIntervalMillis;
						Logger.warn("Retrying user interaction: " + userInteraction.toString() + " after error: " + ErrorHandler.getCause(e).toString());
					} catch (InterruptedException e2) {
						throw new ExporterException(e2.toString() + " came after " + e.toString(), e);
					}
				}
			}
			
			//increment the number of steps in test:
			seleniumHolder.addResult(null, TestPartStatus.PASS);
		}
		
		if (transition.hasCustomTimeout()) {
			Integer secondsToWaitForResult = transition.getSecondsToWaitForResult();
			Logger.info("Transition changed selenium timeout to " + secondsToWaitForResult + " seconds");
			seleniumHolder.setNextPageElementTimeout(secondsToWaitForResult);
		}
	}

	private void handleUserInteractionFailure(SeleniumHolder seleniumHolder,
			UserInteraction userInteraction, UserInteractionException e) {
		String msg = "Error invoking user interaction: " + userInteraction.toString() + ".";
		if (userInteraction.getElement() instanceof PageElement) {
			PageElement pe = (PageElement) userInteraction.getElement();
			if (pe.getStatus().equals(TestPartStatus.FAIL)) {
				msg += "\n\nPage element " + pe.toString() + " not found.";
			}
			seleniumHolder.addResultByIsNot(pe, TestPartStatus.EXCEPTION, pe.isNot());
		}
		Logger.error(msg, e);
		throw new UserInteractionException(msg);
	}

	/**
	 * Converts a single user interaction to a Selenium command.
	 * @return the Selenium command name invoked. 
	 */
	private String handleUserInteraction(SeleniumHolder seleniumHolder, UserInteraction userInteraction) {

		IActionElement element = userInteraction.getElement();
		ActionType actionType = userInteraction.getActionType();
		boolean withinFrame = false;
		if (element instanceof PageElement && seleniumHolder.isPageElementWithinFrame((PageElement)element)){
			//check if parent frame was found:
			if (TestPartStatus.FAIL == seleniumHolder.getParentFrame((PageElement)element).getStatus()) {
				ErrorHandler.logAndShowErrorDialogAndThrow("Cannot interact with element " + element + ":\n" + 
						"Parent frame " + seleniumHolder.getParentFrame((PageElement)element) + 
						" not found.");
			}
			withinFrame = true;
			getToRightFrame(seleniumHolder, seleniumHolder.getParentFrame((PageElement) element));
		}
		//Getting selenium commands, locators and values:
		String commandName = SeleniumUtils.getCommandName(actionType);

		String locator = null;
		String inputValue = null;
		
		if (element instanceof Option) {
			Select selectbox = ((Option) element).getParent();
			locator = "xpath=" + seleniumHolder.getFullContextWithAllElements(selectbox);
			inputValue = SeleniumUtils.getOptionLocator((Option) element);
			if (SELECT.equals(actionType) && selectbox.getIdentifier(MULTISELECT).getProbability() > 0) {
				commandName = "addSelection"; //appropriate for multi-selection
			}
		}
		else {
			//all other elements
			if (element instanceof PageElement) {
				locator = "xpath=" + seleniumHolder.getFullContextWithAllElements((PageElement) element);
			}
			else if(element instanceof WebBrowser){
				locator = userInteraction.getValue();
			}
			else {
				throw new ExporterException("Unsupported action element type");

			}
			inputValue = SeleniumUtils.getValue(userInteraction);
		}
		
		try {
			//invoke user interaction by reflection using command name from SeleniumUtil (legacy since Selenese exporter was written first):
			
			if (SeleniumUtils.hasSeleniumInputColumn(userInteraction)) {
				//two parameters
				seleniumHolder.getSelenium().execute(commandName, locator, inputValue);
			}
			else {
				//one parameter only
				seleniumHolder.getSelenium().execute(commandName, locator);
			}
		}
		catch (Throwable e) {
			throw new UserInteractionException(e);
		}

		if (withinFrame && commandName.equals(SeleniumUtils.FIREEVENT)) {
			upToParentFrame(seleniumHolder);
		}
		return commandName;
	}

	private void getToRightFrame(SeleniumHolder seleniumHolder, PageElement element) {
		Frame parent = seleniumHolder.getParentFrame(element);
		if(parent != null){
			getToRightFrame(seleniumHolder, parent);
		}
		String locator = "xpath=" + seleniumHolder.getFullContextWithAllElements(element);
		seleniumHolder.getSelenium().selectFrame(locator);
	}

	private void upToParentFrame(SeleniumHolder seleniumHolder) {
		final CubicTestLocalRunner seleniumRunner = seleniumHolder.getSelenium();
		seleniumRunner.selectFrame("relative=parent");
	}
}
