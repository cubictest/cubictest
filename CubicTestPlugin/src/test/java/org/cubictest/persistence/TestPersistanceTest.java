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
