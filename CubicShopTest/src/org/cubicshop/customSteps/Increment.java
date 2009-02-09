package org.cubicshop.customSteps;
import java.util.Map;

import org.cubictest.selenium.custom.ICustomTestStep;
import org.cubictest.selenium.custom.IElementContext;

import com.thoughtworks.selenium.Selenium;


public class Increment implements ICustomTestStep {

	public void execute(Map<String, String> arguments, IElementContext context,
			Selenium selenium) throws Exception {
		Integer counter = (Integer) context.get("counter");
		if (counter == null) {
			counter = new Integer(0);
		}
		counter++;
		context.put("counter", counter);
		System.out.println("After increment: " + counter);
	}

}
