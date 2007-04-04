/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.exporters.selenium.runner.converters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.formElement.Option;

import com.thoughtworks.selenium.SeleniumException;

/**
 * Class to convert transitions to selenium commands.
 * 
 * @author chr_schwarz
 */
public class TransitionConverter implements ITransitionConverter<SeleniumHolder> {
	
	
	/**
	 * Converts a user interactions transition to a list of Watir doc.
	 * 
	 * @param transition The transition to convert.
	 */
	public void handleUserInteractions(SeleniumHolder seleniumHolder, UserInteractionsTransition transition) {
		List actions = transition.getUserInteractions();
		Iterator it = actions.iterator();
		while(it.hasNext()) {
			UserInteraction action = (UserInteraction) it.next();
			IActionElement actionElement = action.getElement();
			
			if (actionElement == null) {
				Logger.warn("Action element was null. Skipping user interaction: " + action);
				continue;
			}
			try {
				handleUserInteraction(seleniumHolder, action);
			} catch (SeleniumException e) {
				Logger.warn(e, "Test step failed");
				ErrorHandler.showWarnDialog("Test step failed: Could not " + SeleniumUtils.getCommandDescription(action.getActionType(), actionElement));
			} catch (Exception e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e, "Error invoking Selenium command.");
			}
		}
		waitForPageToLoad(seleniumHolder, 5);
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
	 * Converts a single user interaction to a Selenium command (selenese row).
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private void handleUserInteraction(SeleniumHolder seleniumHolder, UserInteraction userInteraction) throws Exception {

		IActionElement element = userInteraction.getElement();
		ActionType actionType = userInteraction.getActionType();
		
		//Getting selenium commands, locators and values:
		String commandName = SeleniumUtils.getCommandName(actionType);

		String locator = null;
		String inputValue = null;
		
		if (element instanceof Option) {
			Option option = (Option) element;
			Identifier optionId = option.getMainIdentifier();
			//get locator of select-box:
			locator = SeleniumUtils.getLocator(option.getParent(), seleniumHolder);
			inputValue = SeleniumUtils.getIdType(optionId) + "=" + optionId.getValue();
		}
		else {
			locator = SeleniumUtils.getLocator(element, seleniumHolder);
			inputValue = SeleniumUtils.getValue(userInteraction, seleniumHolder);
		}
		
		//invoke user interaction by reflection:
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
	}

	
	
}
