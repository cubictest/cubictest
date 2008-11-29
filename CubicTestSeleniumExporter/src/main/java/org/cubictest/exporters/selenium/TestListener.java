package org.cubictest.exporters.selenium;

public interface TestListener {
	
	void handleTestFinished(TestEvent event);

	void handleTestFailed(TestEvent event);
	
	void handleTestStep(TestEvent event);
}
