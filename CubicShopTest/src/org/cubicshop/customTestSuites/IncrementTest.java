package org.cubicshop.customTestSuites;

import static junit.framework.Assert.assertEquals;

import org.cubictest.exporters.selenium.ElementContext;
import org.cubictest.exporters.selenium.SeleniumRunner;
import org.junit.Test;

public class IncrementTest {

	@Test
	public void testIncrement() {
		
		SeleniumRunner runner = new SeleniumRunner();
		runner.setUseNewBrowserInstanceForEachTest(true);
		
		ElementContext elementContext = new ElementContext();
		elementContext.put("counter", 100);
		runner.setCustomStepElementContext(elementContext);
		
		runner.runTest("/tests/increment.aat");
		assertEquals(103, elementContext.get("counter"));

		runner.runTest("/tests/increment.aat");
		assertEquals(106, elementContext.get("counter"));

	}
}
