package org.cubicshop.customSteps;

import java.util.Map;

import org.cubictest.selenium.custom.ICustomTestStep;
import org.cubictest.selenium.custom.IElementContext;

import com.thoughtworks.selenium.Selenium;

public class CloseWindow implements ICustomTestStep {

	public void execute(Map<String, String> arguments, IElementContext context,
			Selenium selenium) throws Exception {
		
		selenium.close();
	}

}
