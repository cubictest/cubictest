package org.cubictest.selenium.custom;

import java.util.Map;

import com.thoughtworks.selenium.Selenium;

/**
 * Class for holding custom test code.
 * Implementing class is invoked from CubicTest during a test run.
 * 
 * @author SK Skytteren
 */
public interface ICustomTestStep {
		
	/**
	 * Execute the Custom Test Step. 
	 * The web page under test might not be fully loaded when this method is invoked, so use either 
	 * selenium.waitForPageToLoad(..) or when testing JavaScript/Ajax, wrap element assertions in 
	 * com.thoughtworks.selenium.Wait instances to wait for page elements to appear.
	 * See the CubicTest documentation for examples of how to use the Wait class.
	 * 
	 * We expect you to use JUnit type assertions like assertEquals. 
	 * In this way CubicTest can handle the CustomTestStep assertion failures correctly.
	 * You can throw other kinds of exceptions as well.
	 * 
	 * @param arguments (key-value pairs) Key names are from Custom Step definition and values from properties page in CubicTest.
	 * @param context Shared Custom Step Context. Makes it possible to send messages from one custom step to another instead of using static variables. 
	 * @param selenium The Selenium Remote Control object.
	 * @throws any type of exception or error. Handled by CubicTest. java.lang.AssertionError = test failed, others = test exception.
	 */
	public void execute(Map<String,String> arguments, IElementContext context, Selenium selenium) throws Exception;
}

