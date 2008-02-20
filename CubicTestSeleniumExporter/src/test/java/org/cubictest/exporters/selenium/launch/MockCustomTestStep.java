package org.cubictest.exporters.selenium.launch;

import java.util.Map;

import org.cubictest.selenium.custom.ICustomTestStep;
import org.cubictest.selenium.custom.IElementContext;

import com.thoughtworks.selenium.Selenium;

public class MockCustomTestStep implements ICustomTestStep {
	
	public void execute(Map<String, String> arguments, IElementContext context,
			Selenium selenium) {
		if(arguments.size() == 0)
			selenium.fireEvent("locator", "event");
		else{
			String locator = (String)arguments.keySet().toArray()[0];
			selenium.fireEvent(locator, arguments.get(locator));
		}
		String[] results = selenium.getAllFields();
		if(results.length != 3){
			throw new IllegalArgumentException();
		}
		throw new RuntimeException();
	}

}