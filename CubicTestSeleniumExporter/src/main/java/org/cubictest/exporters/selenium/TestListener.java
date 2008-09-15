package org.cubictest.exporters.selenium;

public interface TestListener {
	
	void handleTestFinnished(TestEvent event);

	void handleTestFailed(TestEvent event);
	
	void handleTestStep(TestEvent event);
}
