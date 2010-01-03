/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.common.utils;
import junit.framework.TestCase;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

/**
 * Tests the TextUtil class.
 * 
 * @author chr_schwarz
 */
public class TextUtilTest extends TestCase {


	public void testCamelLowerCase() {
		String text = "this is a test";
		String expected = "thisIsATest";
		assertEquals(expected, TextUtil.camel(text));
	}

	public void testCamelUpperCase1() {
		String text = "This is a test";
		String expected = "thisIsATest";
		assertEquals(expected, TextUtil.camel(text));
	}

	public void testCamelUpperCase2() {
		String text = "This Is A TEST";
		String expected = "thisIsATest";
		assertEquals(expected, TextUtil.camel(text));
	}

	public void testCamelUpperCase3() {
		String text = "Book Query";
		String expected = "bookQuery";
		assertEquals(expected, TextUtil.camel(text));
	}
	
	public void testAlreadyCamelled() {
		String text = "bookQuery";
		String expected = "bookQuery";
		assertEquals(expected, TextUtil.camel(text));
	}
	
	public void testNorwegianAlreadyPartlyCammeled() {
		//we can't save in svn the characters we want, so we get it from unescaping html entities
		String text = StringEscapeUtils.unescapeHtml("&aring;se&Aring;se");
		String expected = "aseAse";
		assertEquals(expected, TextUtil.camel(text));
	}
	
	public void testNorwegian() {
		String text = StringEscapeUtils.unescapeHtml("&Aring;se &Aring;se");
		String expected = "aseAse";
		assertEquals(expected, TextUtil.camel(text));
	}

	public void testNorwegianOneWord() {
		String text = StringEscapeUtils.unescapeHtml("&Aring;se");
		String expected = "ase";
		assertEquals(expected, TextUtil.camel(text));
	}

	public void testNorwegianLong() {
		String text = StringEscapeUtils.unescapeHtml("&Aring;se &AElig;se dro til &oslash;sten");
		String expected = "aseAseDroTilOsten";
		assertEquals(expected, TextUtil.camel(text));
	}
	
	@Test
	public void testNormalizeSimple() {
		String s = "abc";
		assertEquals(s, TextUtil.normalizeAndTrim(s));
	}
	
	@Test
	public void testNormalizeSimpleWithSpace() {
		String s = " abc ";
		String ex = "abc";
		assertEquals(ex, TextUtil.normalizeAndTrim(s));
	}
	
	@Test
	public void testNormalizeSimpleWithMultipleSpace() {
		String s = " a     b  c    ";
		String ex = "a b c";
		assertEquals(ex, TextUtil.normalizeAndTrim(s));
	}
	
	@Test
	public void testNormalizeSimpleWithLineBreakAndTab() {
		String s = " \ta   \n  b  c  \t\t\n  ";
		String ex = "a b c";
		assertEquals(ex, TextUtil.normalizeAndTrim(s));
	}

	@Test
	public void testStripNoHtmlTags() {
		String s = "Test";
		String expected = "Test";
		assertEquals(expected, TextUtil.stripHtmlTags(s));
	}
	
	@Test
	public void testStripHtmlTags() {
		String s = "<i>Test</i>";
		String expected = "Test";
		assertEquals(expected, TextUtil.stripHtmlTags(s));
	}

	@Test
	public void testStripMultipleHtmlTags() {
		String s = "<i><b>Test</b></i>";
		String expected = "Test";
		assertEquals(expected, TextUtil.stripHtmlTags(s));
	}
	
	@Test
	public void testStripMultipleHtmlTags2() {
		String s = "<i><b>Test</b></i";
		String expected = "Test</i";
		assertEquals(expected, TextUtil.stripHtmlTags(s));
	}

	@Test
	public void testShouldKeepTrailingSpaceWhenNormalizingWithTrailingSpace() {
		String s = " \ta   \n  b  c ";
		String ex = "a b c ";
		assertEquals(ex, TextUtil.normalizeWithPossibleSingleTrailingSpace(s));		
	}
	
	@Test
	public void testShouldKeepTrailingSpaceWhenNormalizingWithMultipleTrailingSpaces() {
		String s = " \ta   \n  b  c  \t\t\n  ";
		String ex = "a b c ";
		assertEquals(ex, TextUtil.normalizeWithPossibleSingleTrailingSpace(s));		
	}
	
	@Test
	public void testShouldKeepNoTrailingSpaceWhenNormalizingWithNoTrailingSpace() {
		String s = " \ta   \n  b  c";
		String ex = "a b c";
		assertEquals(ex, TextUtil.normalizeWithPossibleSingleTrailingSpace(s));		
	}
	
}
