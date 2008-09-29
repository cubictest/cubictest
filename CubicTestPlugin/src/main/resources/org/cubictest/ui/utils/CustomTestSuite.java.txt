package org.cubicshop.customTestSuites;

import junit.framework.TestCase;

import org.cubictest.exporters.selenium.SeleniumRunner;

/**
* Custom setUp and tearDown logic can be used in this class, see JUnit documentation.
*/
public class CustomTestSuiteTest extends TestCase {

	public void test() {
		
		SeleniumRunner runner = new SeleniumRunner();
		
		//run all tests in the "/test suites" folder:
		runner.runTests("/test suites");
		
		
		//alternatively, run single tests, e.g: 
		//runner.runTest("/tests/myTest.aat");
	}
}
