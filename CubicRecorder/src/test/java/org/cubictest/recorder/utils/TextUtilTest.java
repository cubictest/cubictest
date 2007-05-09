/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder.utils;

import org.junit.Test;

import junit.framework.TestCase;


/**
 * Tests the Text util.
 * 
 * @author Christian Schwarz
 *
 */
public class TextUtilTest extends TestCase {

	
	@Test
	public void testSimple() {
		String s = "abc";
		assertEquals(s, TextUtil.normalize(s));
	}
	
	@Test
	public void testSimpleWithSpace() {
		String s = " abc ";
		String ex = "abc";
		assertEquals(ex, TextUtil.normalize(s));
	}
	
	
	@Test
	public void testSimpleWithMultipleSpace() {
		String s = " a     b  c    ";
		String ex = "a b c";
		assertEquals(ex, TextUtil.normalize(s));
	}
	
	@Test
	public void testSimpleWithLineBreakAndTab() {
		String s = " \ta   \n  b  c  \t\t\n  ";
		String ex = "a b c";
		assertEquals(ex, TextUtil.normalize(s));
	}
}
