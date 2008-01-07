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
		TestPersistance.loadFromFile(null, fileName);
		String dirName = "src/test/resources/org/cubictest/common/converters/";
		File dir = new File(dirName);
		for(File file : dir.listFiles()){
			if(file.getAbsolutePath().endsWith("aat")){
				System.out.println("Testing: " + file.getName());
				TestPersistance.loadFromFile(null, dirName + file.getName());
			}
		}
	}
}
