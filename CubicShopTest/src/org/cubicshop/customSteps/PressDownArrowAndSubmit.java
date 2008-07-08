package org.cubicshop.customSteps;

import java.util.Map;

import org.cubictest.selenium.custom.ICustomTestStep;
import org.cubictest.selenium.custom.IElementContext;

import com.thoughtworks.selenium.Selenium;


public class PressDownArrowAndSubmit implements ICustomTestStep {

	private static final String ENTER_KEY_CHAR_CODE = "\\13";
	private static final String DOWN_ARROW_CHAR_CODE = "\\40";

	public void execute(Map<String, String> arguments, IElementContext context,
			Selenium selenium) throws Exception {

		//first bring up the drop down list:
		selenium.keyDown("xpath=//input[@type='text' or not(@type)][@name='q']", DOWN_ARROW_CHAR_CODE);
		selenium.keyUp("xpath=//input[@type='text' or not(@type)][@name='q']", DOWN_ARROW_CHAR_CODE);

		//then select the first element:
		selenium.keyDown("xpath=//input[@type='text' or not(@type)][@name='q']", DOWN_ARROW_CHAR_CODE);
		selenium.keyUp("xpath=//input[@type='text' or not(@type)][@name='q']", DOWN_ARROW_CHAR_CODE);

		//submit:
		selenium.keyPress("xpath=//input[@type='text' or not(@type)][@name='q']", ENTER_KEY_CHAR_CODE);
		
		selenium.waitForPageToLoad("10000");
	}

}
