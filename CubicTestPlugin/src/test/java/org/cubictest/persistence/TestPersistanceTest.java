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
package org.cubictest.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPersistanceTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetCharset() throws IOException {
		File file = File.createTempFile("TempTest", "aat");
		assertEquals("ISO-8859-1",TestPersistance.getCharset(file));
	}
	
	@Test
	public void testStoreTests() throws IOException{
		org.cubictest.model.Test test = new org.cubictest.model.Test();
		test.setName("NyTempTest");
		assertNotNull(test);
		File file = File.createTempFile("TempTest", "aat");
		TestPersistance.saveToFile(test, file);
		
		org.cubictest.model.Test persistedTest = TestPersistance.loadFromFile(file, null);
		assertEquals(test.toString(), persistedTest.toString() );
	}

}
