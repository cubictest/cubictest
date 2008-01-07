/*******************************************************************************
 * Copyright (c) 2005, 2008 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - bug fixes and usability improvements
 *******************************************************************************/
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
