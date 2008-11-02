package customTestSuites;

import junit.framework.TestCase;

import org.cubictest.exporters.selenium.SeleniumRunner;

/**
 * Custom Test Suite.
 * Test set up and tear down logic can be put here. See JUnit documentation.
 */
public class CustomTestSuiteTest extends TestCase {

	public void test() {
		
		SeleniumRunner runner = new SeleniumRunner();
		
		//run all tests in the "/tests" folder:
		runner.runTests("/tests");
		
		
		//alternatively, run single tests, e.g: 
		//runner.runTest("/tests/myTest.aat");
	}
}

