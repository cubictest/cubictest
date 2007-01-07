/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.coverters;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.converters.PageWalker;
import org.cubictest.common.converters.TreeTestWalker;
import org.cubictest.common.exception.UnknownExtensionPointException;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Page;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.testutils.AssertionList;
import org.cubictest.testutils.DummyConverter;
import org.eclipse.core.runtime.CoreException;
import org.junit.Before;

/**
 * Tests the TreeTestWalker, integration test style.
 * Uses .aat files created in CubicTest, and asserts that the corrent elements are being converted in the correct order.
 * Also checks that e.g. only paths to extension points have been converted (if subtest / extension start point mode).
 * 
 * @author chr_schwarz
 */
public class TreeTestWalkerTest {

	private AssertionList<String> assertionList;
	TreeTestWalker<AssertionList<String>> testWalker;
	
	@Before
	public void setUp() throws CoreException {
		
		PageWalker<AssertionList<String>> pw = new PageWalker<AssertionList<String>>(DummyConverter.class, DummyConverter.class);
		testWalker = new TreeTestWalker<AssertionList<String>>(DummyConverter.class, pw, 
				DummyConverter.class, DummyConverter.class);
			
		assertionList = new AssertionList<String>();
	}
	
	/**
	 * Test that all paths are converted in tree (as no target extension point is specified).
	 */
	@org.junit.Test
	public void testTraversesSimpleTree() {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleTree.aat";
		Test test = TestPersistance.loadFromFile(fileName);
		testWalker.convertTest(test, assertionList, null);
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("First Page --> Second Page");
		assertionList.assertContainsInOrder("Second");
		assertionList.resetCounter();
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
	}
	
	/**
	 * Test that only path to given extension point is converted.
	 */
	@org.junit.Test
	public void testTraversesSimpleTreeOnlyToExtensionPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleTreeExtensionPoint.aat";
		Test test = TestPersistance.loadFromFile(fileName);
		
		testWalker.convertTest(test, assertionList, setUpTargetExtensionPoint("page297923971162115959945_2"));
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		//Assert wrong paths in pre-test:
		assertionList.assertNotContains("Second");
		assertionList.assertNotContains("First Page --> Second Page");
	}

	
	/**
	 * Test exception is thrown on unknown extension point.
	 */
	@org.junit.Test
	public void testThrowsExceptionWhenInvalidExtensionPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleTreeExtensionPoint.aat";
		Test test = TestPersistance.loadFromFile(fileName);
		
		try {
			testWalker.convertTest(test, assertionList, setUpTargetExtensionPoint("not present"));
			fail("Should throw UnknownExtensionPointException when invalid extension point");
		}
		catch (UnknownExtensionPointException e) {
			assertTrue(true);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail("Should throw UnknownExtensionPointException, not exception " + e.toString());
		}
	}

	/**
	 * Test that whole medium test in converted, including form elements submittance.
	 * User actions and three-level tree.
	 */
	@org.junit.Test
	public void testTraversesMediumTree() {
		String fileName = "src/test/resources/org/cubictest/common/converters/MediumTreeExtensionPoint.aat";
		Test test = TestPersistance.loadFromFile(fileName);
		
		testWalker.convertTest(test, assertionList, null);
		
		//the order here might be fragile. Consider only using assertContains(..)
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("Second");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("Fifth");
		assertionList.assertContainsInOrder("Fourth");
	}
	
	/**
	 * Test that only path to given extension point is converted.
	 * Form elements, user actions and three-level tree.
	 */
	@org.junit.Test
	public void testTraversesMediumTreeOnlyToExtensionPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/MediumTreeExtensionPoint.aat";
		Test test = TestPersistance.loadFromFile(fileName);
		
		testWalker.convertTest(test, assertionList, setUpTargetExtensionPoint("page299655571162117362723"));
		
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("Third Page --> Fifth Page");
		assertionList.assertContainsInOrder("Fifth");
		//Assert wrong paths in pre-test:
		assertionList.assertNotContains("First Page --> Second Page");
		assertionList.assertNotContains("Second");
		assertionList.assertNotContains("Third Page --> Fouth Page");
		assertionList.assertNotContains("Fourth");
	}
	
	
	/**
	 * Test with subtest that only path to used extension point is converted.
	 */
	@org.junit.Test
	public void testTraversesSubTestToExtensionPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleUsingSubTest.aat";
		Test test = TestPersistance.loadFromFile(fileName);
		testWalker.convertTest(test, assertionList, null);
		
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("Alpha");
		assertionList.assertContainsInOrder("First Page --> SimpleTreeExtensionPoint (SimpleTreeExtensionPoint.aat)");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("SimpleTreeExtensionPoint (SimpleTreeExtensionPoint.aat) --> Second Page");
		assertionList.assertContainsInOrder("Beta");
		//Assert wrong paths in pre-test:
		assertionList.assertNotContains("Second");
		assertionList.assertNotContains("First Page --> Second Page");
	}

	
	/**
	 * Test with ExtensionStartPoint that only path to given extension point is converted.
	 */
	@org.junit.Test
	public void testTraversesPreTestToExtensionPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleUsingExtensionStartPoint.aat";
		Test test = TestPersistance.loadFromFile(fileName);
		testWalker.convertTest(test, assertionList, null);
		
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("SimpleTreeExtensionPoint (SimpleTreeExtensionPoint.aat) --> Fourth Page");
		assertionList.assertContainsInOrder("Fourth");
		//Assert wrong paths in pre-test:
		assertionList.assertNotContains("First Page --> Second Page"); 
		assertionList.assertNotContains("Second");
	}	
	
	
	/**
	 * Test that tree is traversed after extension start point.
	 */
	@org.junit.Test
	public void testTraversesTreeAfterExtensionStartPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/TreeUsingExtensionStartPoint.aat";
		Test test = TestPersistance.loadFromFile(fileName);
		testWalker.convertTest(test, assertionList, null);
		
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("SimpleTreeExtensionPoint (SimpleTreeExtensionPoint.aat) --> Fourth Page");
		assertionList.assertContainsInOrder("Fourth");
		assertionList.assertContainsInOrder("Fourth Page --> Fifth Page");
		assertionList.assertContainsInOrder("Fifth");
		assertionList.assertContainsInOrder("Fourth Page --> Sixth Page");
		assertionList.assertContainsInOrder("Sixth");
		//Assert wrong paths in pre-test:
		assertionList.assertNotContains("First Page --> Second Page"); 
		assertionList.assertNotContains("Second");
	}
	
	
	/**
	 * Test that tree is traversed after extension start point.
	 * Tree test with two levels and an extension point.
	 */
	@org.junit.Test
	public void testTraversesTreeWithExtensionPointAfterExtensionStartPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/TreeWithExtensionPointUsingExtensionStartPoint.aat";
		Test test = TestPersistance.loadFromFile(fileName);
		testWalker.convertTest(test, assertionList, null);
		
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("SimpleTreeExtensionPoint (SimpleTreeExtensionPoint.aat) --> Fourth Page");
		assertionList.assertContainsInOrder("Fourth");
		assertionList.assertContainsInOrder("Fourth Page --> Fifth Page");
		assertionList.assertContainsInOrder("Fifth");
		assertionList.assertContainsInOrder("Fourth Page --> Sixth Page");
		assertionList.assertContainsInOrder("Sixth");
		assertionList.assertContainsInOrder("Sixth Page --> Seventh Page");
		assertionList.assertContainsInOrder("Seventh");
		assertionList.assertContainsInOrder("Sixth Page --> Eighth Page");
		assertionList.assertContainsInOrder("Eighth");
		//Assert wrong paths in pre-test:
		assertionList.assertNotContains("First Page --> Second Page"); 
		assertionList.assertNotContains("Second");
	}
	
	/**
	 * Test with ExtensionStartPoint that only path to given extension point is converted.
	 */
	@org.junit.Test
	public void testTraversesPreTestToExtensionPoint2() {
		String fileName = "src/test/resources/org/cubictest/common/converters/purchase crate.aat";
		Test test = TestPersistance.loadFromFile(fileName);
		testWalker.convertTest(test, assertionList, null);
		
		assertionList.assertContainsInOrder("http://localhost:8080/cubicshop");
		assertionList.assertContainsInOrder("SearchLink"); 
		assertionList.assertContainsInOrder("WebshopLink"); 
		assertionList.assertContainsInOrder("First Page --> Webshop"); 
		assertionList.assertContainsInOrder("Webshop"); 
		assertionList.assertContainsInOrder("Shipping crate");
		//Assert wrong paths in pre-test:
		assertionList.assertNotContains("Search");
		assertionList.assertNotContains("First Page --> Search"); 
	}
	
	
	/**
	 * Util method that sets up transition from a page with the specified ID to a target extension point.
	 * @param pageId
	 * @return a target extension point hooked to a page with this ID.
	 */
	private ExtensionPoint setUpTargetExtensionPoint(String pageId) {
		ExtensionPoint point = new ExtensionPoint();
		Page page = new Page();
		page.setId(pageId);
		Transition transition = new SimpleTransition(page, point);
		List<Transition> transitions = new ArrayList<Transition>();
		transitions.add(transition);
		page.setOutTransitions(transitions);
		point.setInTransition(transition);
		return point;
	}
}
