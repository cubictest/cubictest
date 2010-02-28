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
package org.cubictest.exporters.selenium.runner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class SeleniumClasspathContainerInitializer extends
		ClasspathContainerInitializer {

	public static final String CUBICTEST_SELENIUM = "CUBICTEST_SELENIUM";

	@Override
	public void initialize(IPath containerPath, IJavaProject project)
			throws CoreException {
		
		final IPath libPath = JavaCore.getClasspathVariable(CUBICTEST_SELENIUM);
		final IClasspathEntry cubicCoreEntry = JavaCore.newLibraryEntry(
				libPath.append("/lib/cubictest-2.0.3.jar"), null,null);
		final IClasspathEntry cubicSeleniumExporterEntry = JavaCore.newLibraryEntry(
				libPath.append("/lib/selenium-exporter-2.0.3.jar"), 
				libPath.append("/lib/selenium-exporter-2.0.3.jar"), null);
		final IClasspathEntry cubicSeleniumExporterServerEntry = JavaCore.newLibraryEntry(
				libPath.append("/lib/cubictest-selenium-rc-2.0.3.jar"), 
				libPath.append("/lib/cubictest-selenium-rc-2.0.3.jar"), null);
		final IClasspathEntry jUnitEntry = JavaCore.newLibraryEntry(
				libPath.append("/lib/junit-4.4.jar"), libPath.append("/lib/junit-4.4-src.jar"), null);
		JavaCore.setClasspathContainer(
			new Path(CUBICTEST_SELENIUM), 
			new IJavaProject[]{ project }, // value for 'myProject'
			new IClasspathContainer[] {
				new IClasspathContainer() {
					public IClasspathEntry[] getClasspathEntries() {
						return new IClasspathEntry[]{cubicCoreEntry, jUnitEntry, 
								cubicSeleniumExporterEntry, cubicSeleniumExporterServerEntry};
					}
					public String getDescription() { return "CubicTest Selenium Library"; }
					public int getKind() { return IClasspathContainer.K_APPLICATION; }
					public IPath getPath() {return libPath; }
				}			
			}, 
			null);
	}
	
}
