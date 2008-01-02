package org.cubictest.exporters.selenium.runner;

public interface ICubicTestRunner {

	String execute(String commandName, String locator, String inputValue);

	String execute(String commandName, String locator);

	String getTitle();

	String getText(String locator);

	String getValue(String locator);

	boolean isTextPresent(String text);

	void waitForPageToLoad(String string);

	void start();

	void setTimeout(String string);

	void open(String beginAt);

	void stop();

}
