/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;
import junit.framework.TestCase;

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
		String text = "≈se∆se";
		String expected = "aseAse";
		assertEquals(expected, TextUtil.camel(text));
	}
	
	public void testNorwegian() {
		String text = "≈se ∆se";
		String expected = "aseAse";
		assertEquals(expected, TextUtil.camel(text));
	}

	public void testNorwegianOneWord() {
		String text = "≈se";
		String expected = "ase";
		assertEquals(expected, TextUtil.camel(text));
	}

	public void testNorwegianLong() {
		String text = "≈se ∆se dro til ÿsten";
		String expected = "aseAseDroTilOsten";
		assertEquals(expected, TextUtil.camel(text));
	}}
