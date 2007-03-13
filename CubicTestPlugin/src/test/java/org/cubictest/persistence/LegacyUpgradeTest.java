package org.cubictest.persistence;


import java.io.File;

import org.junit.Before;

public class LegacyUpgradeTest {

	@Before
	public void setUp() throws Exception {
	}

	@org.junit.Test
	public void testUpgradeTo5(){
		String fileName = "src/test/resources/org/cubictest/persistence/NotConvertedInto5.aat";
		System.out.println("Testing: NotConvertedInto5.aat");
		TestPersistance.loadFromFile(fileName);
		String dirName = "src/test/resources/org/cubictest/common/converters/";
		File dir = new File(dirName);
		for(File file : dir.listFiles()){
			if(file.getAbsolutePath().endsWith("aat")){
				System.out.println("Testing: " + file.getName());
				TestPersistance.loadFromFile(dirName + file.getName());
			}
		}
	}
}
