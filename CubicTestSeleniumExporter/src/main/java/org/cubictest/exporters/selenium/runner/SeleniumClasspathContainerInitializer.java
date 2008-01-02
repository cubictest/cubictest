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

	private static final String CUBICTEST_SELENIUM = "CUBICTEST_SELENIUM";

	@Override
	public void initialize(IPath containerPath, IJavaProject project)
			throws CoreException {
		
		final IPath libPath = JavaCore.getClasspathVariable(CUBICTEST_SELENIUM);
		final IClasspathEntry seleniumServerEntry = JavaCore.newLibraryEntry(
				libPath.append("/lib/selenium-server.jar"), null,null);
		final IClasspathEntry seleniumClientEntry = JavaCore.newLibraryEntry(
				libPath.append("/lib/selenium-java-client-driver.jar"), null,null);
		final IClasspathEntry commonsIoEntry = JavaCore.newLibraryEntry(
				libPath.append("/lib/commons-io-1.3.1.jar"), null,null);
		final IClasspathEntry commonsLangEntry = JavaCore.newLibraryEntry(
				libPath.append("/lib/commons-lang-2.3.jar"), null,null);
		final IClasspathEntry jdoomEntry = JavaCore.newLibraryEntry(
				libPath.append("/lib/jdom.jar"), null,null);
		final IClasspathEntry cubicTestSeleniumEntry = JavaCore.newLibraryEntry(
				libPath.append("/lib/CubicTestSelenium.jar"), null,null);
		JavaCore.setClasspathContainer(
			new Path(CUBICTEST_SELENIUM), 
			new IJavaProject[]{ project }, // value for 'myProject'
			new IClasspathContainer[] {
				new IClasspathContainer() {
					public IClasspathEntry[] getClasspathEntries() {
						return new IClasspathEntry[]{seleniumServerEntry,seleniumClientEntry,
								commonsIoEntry,commonsLangEntry,jdoomEntry,cubicTestSeleniumEntry};
					}
					public String getDescription() { return "CubicTest Selenium Library"; }
					public int getKind() { return IClasspathContainer.K_APPLICATION; }
					public IPath getPath() {return libPath; }
				}			
			}, 
			null);
	}
	
}
