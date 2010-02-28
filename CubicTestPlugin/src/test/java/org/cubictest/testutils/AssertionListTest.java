/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.testutils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import junit.framework.AssertionFailedError;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the AssertionList class.
 * 
 * @author chr_schwarz
 */
public class AssertionListTest {

	private AssertionList<String> list;
	
	@Before
	public void setUp() {
		list = new AssertionList<String>();
		list.add("First");
		list.add("Second");
		list.add("Third");
	}
	
	@Test
	public void shouldFailOnUnknownObject() {
		try {
			list.assertContainsInOrder("Unknown");
			fail("Should throw assertion error for unknown object");
		}
		catch (AssertionFailedError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void shouldPassForObjectInList() {
		list.assertContainsInOrder("First");
	}

	@Test
	public void shouldPassForCorrectOrder() {
		list.assertContainsInOrder("First");
		list.assertContainsInOrder("Second");
	}

	
	@Test
	public void shouldFailForWrongOrder() {
		try {
			list.assertContainsInOrder("Second");
			list.assertContainsInOrder("First");
			fail("Should throw assertion error for wrong order");
		}
		catch (AssertionFailedError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void shouldFailForSameElementTwice() {
		try {
			list.assertContainsInOrder("Second");
			list.assertContainsInOrder("Second");
			fail("Should throw assertion error for same element twice");
		}
		catch (AssertionFailedError e) {
			assertTrue(true);
		}
	}

	@Test
	public void shouldFailForSameElementTwiceEvenIfListContainsElementTwice() {
		list.add("Third");
		try {
			list.assertContainsInOrder("Third");
			list.assertContainsInOrder("Third");
			fail("Should throw assertion error for same element twice (because this is bad testing data)");
		}
		catch (AssertionFailedError e) {
			assertTrue(true);
		}
	}
	
	
	@Test
	public void shouldAssertNotContainsCorrectly() {
		list.assertNotContains("Not present");
		try {
			list.assertNotContains("First");
			fail("Should throw assertion error for element present");
		}
		catch (AssertionFailedError e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void shouldFailForWrongOrder2() {
		try {
			list.assertContainsInOrder("First");
			list.assertContainsInOrder("Third");
			list.assertContainsInOrder("Second");
			fail("Should throw assertion error for wrong order");
		}
		catch (AssertionFailedError e) {
			assertTrue(true);
		}
	}
	
	
	@Test
	public void shouldFailForWrongOrder3() {
		try {
			list.assertContainsInOrder("Third");
			list.assertContainsInOrder("First");
			list.assertContainsInOrder("Second");
			fail("Should throw assertion error for wrong order");
		}
		catch (AssertionFailedError e) {
			assertTrue(true);
		}
	}


	@Test
	public void shouldPassForCorrectOrder3() {
		list.assertContainsInOrder("First");
		list.assertContainsInOrder("Second");
		list.assertContainsInOrder("Third");
	}
}
