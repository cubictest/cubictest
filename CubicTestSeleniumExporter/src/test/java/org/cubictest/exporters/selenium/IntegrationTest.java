/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium;

import junit.framework.TestCase;

import org.cubictest.export.DirectoryWalker;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.exporters.selenium.converters.ContextConverter;
import org.cubictest.exporters.selenium.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.converters.PageElementConverter;
import org.cubictest.exporters.selenium.converters.TransitionConverter;
import org.cubictest.exporters.selenium.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.holders.SeleneseDocument;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;

/**
 * Integration test for Selenium export.
 * Exports a full test to selenese HTML, fires up Jetty, runs the tests and checks that all elements are found.
 * 
 * @author chr_schwarz
 *
 */
public class IntegrationTest extends TestCase {
	
	private DirectoryWalker<SeleneseDocument> dirWalker;
	@Before
	public void setUp() throws CoreException {
		
		TreeTestWalker<SeleneseDocument> testWalker = new TreeTestWalker<SeleneseDocument>(UrlStartPointConverter.class, 
				PageElementConverter.class, 
				ContextConverter.class,
				TransitionConverter.class, 
				CustomTestStepConverter.class);
			
		dirWalker = new DirectoryWalker<SeleneseDocument>(null, testWalker, ".html", SeleneseDocument.class);
	}

	
	
	@org.junit.Test
	public void testAssertElements() {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleTree.aat";
//		Test test = TestPersistance.loadFromFile(fileName);
//		DirectoryWalker<SeleneseDocument> walker = new DirectoryWalker<SeleneseDocument>();
//		walker.convertCubicTestFile(aatFile, outFolder, monitor);
		//fire up Jetty and run the tests

		
	}
}
