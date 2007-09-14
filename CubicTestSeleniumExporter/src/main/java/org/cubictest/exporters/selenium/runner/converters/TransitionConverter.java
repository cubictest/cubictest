/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.selenium.runner.converters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.exceptions.TestFailedException;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Identifier;
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
			ErrorHandler.logAndThrow("Error waiting for page to load");
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
			if (StringUtils.isBlank(inputValue)) {
				
				//one parameter only
				Method method = seleniumHolder.getSelenium().getClass().getMethod(commandName, new Class[] {String.class});
				method.invoke(seleniumHolder.getSelenium(), new Object[] {locator});
			}
			else {
				
				//two parameters
				Method method = seleniumHolder.getSelenium().getClass().getMethod(commandName, new Class[] {String.class, String.class});
				method.invoke(seleniumHolder.getSelenium(), new Object[] {locator, inputValue});
			}
			return commandName;
		}
		catch (Exception e) {
			Throwable ex = ErrorHandler.getCause(e);
			if (ex instanceof SeleniumException) {
				String msg = "Error invoking user interaction: " + userInteraction.toString() + ". Please verify identifiers and surrounding context.";
				Logger.error(msg);
				throw new TestFailedException(msg);
			}
			else {
				ErrorHandler.logAndRethrow(ex);
			}
			return null;
		}
	}
}
