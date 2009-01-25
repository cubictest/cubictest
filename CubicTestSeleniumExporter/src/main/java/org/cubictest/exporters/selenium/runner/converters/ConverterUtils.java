package org.cubictest.exporters.selenium.runner.converters;

import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.utils.CubicWait;

public class ConverterUtils {
	
	
	public static void waitForElement(final SeleniumHolder seleniumHolder, final String locator, final boolean isNot) {
		new CubicWait() {
			public boolean until() {
				if (isNot) {
					return !seleniumHolder.getSelenium().isElementPresent(locator);
				}
				else {
					return seleniumHolder.getSelenium().isElementPresent(locator);
				}
			}
		}.wait("Page element not found: " + locator, seleniumHolder.getNextPageElementTimeout() * 1000);
	}
}
