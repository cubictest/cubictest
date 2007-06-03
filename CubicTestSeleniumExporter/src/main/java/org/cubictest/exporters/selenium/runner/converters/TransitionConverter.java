/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.selenium.runner.converters;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.exceptions.ExporterException;
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
			try {
				String commandName = handleUserInteraction(seleniumHolder, action);
				if (!commandName.equals(SeleniumUtils.FIREEVENT)) {
					waitForPageToLoad = true;
				}
				seleniumHolder.addResult(null, TestPartStatus.PASS);
			}
			catch (SeleniumException e) {
				seleniumHolder.addResult(null, TestPartStatus.PASS);
				Logger.warn(e, "Test step failed");
			}
			catch (Exception e) {
				seleniumHolder.addResult(null, TestPartStatus.PASS);
				ErrorHandler.logAndRethrow(e, "Error invoking Selenium command.");
			}
		}
		if (waitForPageToLoad) {
			waitForPageToLoad(seleniumHolder, 30);
		}
	}


	private void waitForPageToLoad(SeleniumHolder seleniumHolder, int seconds) {
		try {
			long millis = seconds * 1000;
			seleniumHolder.getSelenium().waitForPageToLoad(millis + "");
		}
		catch (SeleniumException e) {
			Logger.error(e, "Error waiting for page to load");
		}
	}
	
	
	/**
	 * Converts a single user interaction to a Selenium command.
	 * @return the Selenium command name invoked. 
	 */
	private String handleUserInteraction(SeleniumHolder seleniumHolder, UserInteraction userInteraction) throws Exception {

		IActionElement element = userInteraction.getElement();
		ActionType actionType = userInteraction.getActionType();
		
		//Getting selenium commands, locators and values:
		String commandName = SeleniumUtils.getCommandName(actionType);

		String locator = null;
		String inputValue = null;
		
		if (element instanceof Option) {
			Option option = (Option) element;
			Identifier optionMainId = option.getMainIdentifier();
			Select select = option.getParent();
			//get locator of select-box:
			locator = "xpath=" + seleniumHolder.getFullContextWithAllElements(select);
			inputValue = SeleniumUtils.getIdType(optionMainId) + "=" + optionMainId.getValue();
		}
		else {
			//all other elements
			if (element instanceof PageElement) {
				locator = "xpath=" + seleniumHolder.getFullContextWithAllElements((PageElement) element);
			}
			else {
				throw new ExporterException("Unsupported action element type");

			}
			inputValue = SeleniumUtils.getValue(userInteraction, seleniumHolder);
		}
		
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
}
