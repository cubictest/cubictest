package org.cubictest.runner.selenium.server;

import java.util.Map;

import com.thoughtworks.selenium.Selenium;

public interface ICustomTestStep {
		
	/**
	 * Execute the test this element represents
	 */
	public void execute(Map<String,String> arguments, IElementContext context, Selenium selenium);
}

