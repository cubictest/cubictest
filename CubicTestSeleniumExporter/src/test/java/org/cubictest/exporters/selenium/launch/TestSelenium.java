package org.cubictest.exporters.selenium.launch;

import com.thoughtworks.selenium.DefaultSelenium;

public class TestSelenium extends DefaultSelenium {
	
	private String testLocator;
	private String testEventName;

	public TestSelenium() {
		super(null);
	}

	@Override
	public void fireEvent(String locator, String eventName) {
		testLocator = locator;
		testEventName = eventName;
	}
	
	@Override
	public String[] getAllFields() {
		return new String[]{"en","to","tre"};
	}

	public String getTestLocator() {
		return testLocator;
	}

	public String getTestEventName() {
		return testEventName;
	}

	
}
