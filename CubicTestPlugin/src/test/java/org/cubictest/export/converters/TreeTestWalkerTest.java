/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.converters;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

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
		
		testWalker = new TreeTestWalker<AssertionList<String>>(DummyConverter.class, DummyConverter.class,
				DummyConverter.class, DummyConverter.class, DummyConverter.class);
			
		assertionList = new AssertionList<String>();
	}
	
	
	/**
	 * Test that all paths are converted in tree (as no target extension point is specified).
	 */
	@org.junit.Test
	public void testTraversesSimpleTree() {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleTree.aat";
		Test test = TestPersistance.loadFromFile(null, fileName);
		testWalker.convertTest(test, assertionList);
		
		//first path:
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Second Page");
		assertionList.assertContainsInOrder("Second");
		
		//second path:
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");

		assertionList.verifySize();
}
	
	/**
	 * Test that only path to given extension point is converted.
	 */
	@org.junit.Test
	public void testTraversesSimpleTreeOnlyToExtensionPoint() throws InstantiationException, IllegalAccessException {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleTreeExtensionPoint.aat";
		Test test = TestPersistance.loadFromFile(null, fileName);
		
		//only convert path to extension point:
		testWalker.convertTransitionNode(assertionList, test.getStartPoint(), setUpTargetExtensionPoint("page297923971162115959945_2"));
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		
		//Assert did not convert elements on wrong path:
		assertionList.assertNotContains("Second");
		assertionList.assertNotContains("First Page --> Second Page");

		assertionList.verifySize();
	}

	
	/**
	 * Test exception is thrown on unknown extension point.
	 */
	@org.junit.Test
	public void testThrowsExceptionWhenInvalidExtensionPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleTreeExtensionPoint.aat";
		Test test = TestPersistance.loadFromFile(null, fileName);
		
		try {
			System.out.println("\n== Testing traversal to unknown extension point. Error(s) about unknown extension point should follow ==");
			testWalker.convertTransitionNode(assertionList, test.getStartPoint(), setUpTargetExtensionPoint("Dummy, should not be present"));
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
		Test test = TestPersistance.loadFromFile(null, fileName);
		
		testWalker.convertTest(test, assertionList);
		
		//the order here might be fragile. Consider only using assertContains(..)
		
		//first path:
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Second Page");
		assertionList.assertContainsInOrder("Second");
		
		//second path:
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("InputThird");
		assertionList.assertContainsInOrder("ButtonThird");
		assertionList.assertContainsInOrder("Third Page --> Fifth Page");
		assertionList.assertContainsInOrder("Fifth");
		
		//third path:
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("InputThird");
		assertionList.assertContainsInOrder("ButtonThird");
		assertionList.assertContainsInOrder("Third Page --> Fourth Page");
		assertionList.assertContainsInOrder("Fourth");

		assertionList.verifySize();
	}
	
	/**
	 * Test that only path to given extension point is converted.
	 * Form elements, user actions and three-level tree.
	 */
	@org.junit.Test
	public void testTraversesMediumTreeOnlyToExtensionPoint() throws InstantiationException, IllegalAccessException {
		String fileName = "src/test/resources/org/cubictest/common/converters/MediumTreeExtensionPoint.aat";
		Test test = TestPersistance.loadFromFile(null, fileName);
		
		//only convert path to extension point:
		testWalker.convertTransitionNode(assertionList, test.getStartPoint(), setUpTargetExtensionPoint("page299655571162117362723"));
		
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("InputThird");
		assertionList.assertContainsInOrder("ButtonThird");
		assertionList.assertContainsInOrder("Third Page --> Fifth Page");
		assertionList.assertContainsInOrder("Fifth");
		
		//Assert did not convert elements on wrong path:
		assertionList.assertNotContains("First Page --> Second Page");
		assertionList.assertNotContains("Second");
		assertionList.assertNotContains("Third Page --> Fouth Page");
		assertionList.assertNotContains("Fourth");

		assertionList.verifySize();
	}
	
	
	/**
	 * Test with subtest that only path to used extension point is converted.
	 */
	@org.junit.Test
	public void testTraversesSubTestToExtensionPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleUsingSubTest.aat";
		Test test = TestPersistance.loadFromFile(null, fileName);
		testWalker.convertTest(test, assertionList);
		
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("Alpha");
		assertionList.assertContainsInOrder("First Page --> SimpleTreeExtensionPoint (SimpleTreeExtensionPoint.aat)");
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("Beta");

		//Assert did not convert elements on wrong path in sub test:
		assertionList.assertNotContains("Second");
		assertionList.assertNotContains("First Page --> Second Page");

		assertionList.verifySize();
	}

	
	/**
	 * Test with Extension*Start*Point that only path to given extension point is converted.
	 */
	@org.junit.Test
	public void testTraversesPreTestToExtensionPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/SimpleUsingExtensionStartPoint.aat";
		Test test = TestPersistance.loadFromFile(null, fileName);
		testWalker.convertTest(test, assertionList);
		
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("Fourth");
		
		//Assert did not convert elements on wrong path:
		assertionList.assertNotContains("First Page --> Second Page"); 
		assertionList.assertNotContains("Second");

		assertionList.verifySize();
	}	
	
	
	/**
	 * Test that complete tree is traversed after extension start point.
	 */
	@org.junit.Test
	public void testTraversesTreeAfterExtensionStartPoint() {
		String fileName = "src/test/resources/org/cubictest/common/converters/TreeUsingExtensionStartPoint.aat";
		Test test = TestPersistance.loadFromFile(null, fileName);
		testWalker.convertTest(test, assertionList);
		
		//first path:
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("Fourth");
		assertionList.assertContainsInOrder("LinkToSixth");
		assertionList.assertContainsInOrder("LinkToFifth");
		assertionList.assertContainsInOrder("Fourth Page --> Fifth Page");
		assertionList.assertContainsInOrder("Fifth");
		
		//second path:
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("Fourth");
		assertionList.assertContainsInOrder("LinkToSixth");
		assertionList.assertContainsInOrder("LinkToFifth");
		assertionList.assertContainsInOrder("Fourth Page --> Sixth Page");
		assertionList.assertContainsInOrder("Sixth");

		//Assert did not convert elements on wrong path in test from start point:
		assertionList.assertNotContains("First Page --> Second Page"); 
		assertionList.assertNotContains("Second");

		assertionList.verifySize();
	}
	
	
	/**
	 * Test that tree is traversed only to extension point after extension start point.
	 * Tree test with two levels and an extension point.
	 */
	@org.junit.Test
	public void testTraversesTreeWithExtensionPointAfterExtensionStartPoint() throws InstantiationException, IllegalAccessException {
		String fileName = "src/test/resources/org/cubictest/common/converters/MediumTreeWithExtensionPointUsingExtensionStartPoint.aat";
		Test test = TestPersistance.loadFromFile(null, fileName);
		testWalker.convertTransitionNode(assertionList, test.getStartPoint(), setUpTargetExtensionPoint("page8469831163802373773"));
		
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");
		assertionList.assertContainsInOrder("Fourth");
		assertionList.assertContainsInOrder("LinkToSixth");
		assertionList.assertContainsInOrder("LinkToFifth");
		assertionList.assertContainsInOrder("Fourth Page --> Sixth Page");
		assertionList.assertContainsInOrder("Sixth");
		assertionList.assertContainsInOrder("LinkToSeventh");
		assertionList.assertContainsInOrder("LinkToEighth");
		assertionList.assertContainsInOrder("Sixth Page --> Seventh Page");
		assertionList.assertContainsInOrder("Seventh");

		//Assert did not convert elements on wrong path:
		assertionList.assertNotContains("First Page --> Second Page"); 
		assertionList.assertNotContains("Second");
		assertionList.assertNotContains("Fourth Page --> Fifth Page");
		assertionList.assertNotContains("Fifth");
		assertionList.assertNotContains("Sixth Page --> Eighth Page");
		assertionList.assertNotContains("Eighth");

		assertionList.verifySize();
	}

	
	
	/**
	 * Test that tree is traversed only to extension point after extension start point.
	 * Tree test with two levels and an extension point.
	 */
	@org.junit.Test
	public void testTraversesUrlInEachPathFromUrlStartPoint() throws InstantiationException, IllegalAccessException {
		String fileName = "src/test/resources/org/cubictest/common/converters/TreeFromUrlStartPoint.aat";
		Test test = TestPersistance.loadFromFile(null, fileName);
		testWalker.convertTest(test, assertionList);
		
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("FirstLink");
		assertionList.assertContainsInOrder("First Page --> Second Page");
		assertionList.assertContainsInOrder("SecondText");
		//new path, should invoke URL:
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("ThirdLink");
		assertionList.assertContainsInOrder("Third Page --> Fourth Page");
		assertionList.assertContainsInOrder("FourthText");

		assertionList.verifySize();
	}


	/**
	 * Test that subtest after ExtensionStartPoint works as expected.
	 */
	@org.junit.Test
	public void testTraversesSubTestAfterExtensionStartPoint() throws InstantiationException, IllegalAccessException {
		String fileName = "src/test/resources/org/cubictest/common/converters/ExtensionStartPointWithSubTest.aat";
		Test test = TestPersistance.loadFromFile(null, fileName);
		testWalker.convertTest(test, assertionList);
		
		//pre-test
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");

		//subtest
		assertionList.assertContainsInOrder("www.test.org");
		assertionList.assertContainsInOrder("First");
		assertionList.assertContainsInOrder("LinkToSecond");
		assertionList.assertContainsInOrder("LinkToThird");
		assertionList.assertContainsInOrder("First Page --> Third Page");
		assertionList.assertContainsInOrder("Third");

		//the main test:
		assertionList.assertContainsInOrder("Fourth");
		assertionList.assertContainsInOrder("LinkToFifth");
		assertionList.assertContainsInOrder("Fourth Page --> Fifth Page");
		assertionList.assertContainsInOrder("Fifth");

		assertionList.verifySize();
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
