package org.cubictest.exporters.selenium;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cubictest.export.IBuildPathSupporter;
import org.cubictest.exporters.selenium.runner.SeleniumClasspathContainerInitializer;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class SeleniumBuildPathSupporter implements IBuildPathSupporter {

	private static final String BUILD_PATH_PAGE_ID= "org.eclipse.jdt.ui.propertyPages.BuildPathsPropertyPage"; //$NON-NLS-1$

	public void addClassPathContainers(IJavaProject javaProject, Shell shell) {
		if(MessageDialog.openQuestion(new Shell(), "Add CubicTest Selenium to classpath", 
				"Would you like to add CubicTest and Selenium libraries to the classpath?")){
			putCubicSeleniumLibraryOnClasspath(javaProject, shell);
		}
	}

	public List<File> getFiles() {
		return new ArrayList<File>();
	}

	
	public static boolean putCubicSeleniumLibraryOnClasspath(
			IJavaProject javaProject, Shell shell) {
		IClasspathEntry newEntry = JavaCore.newContainerEntry(
				new Path(SeleniumClasspathContainerInitializer.CUBICTEST_SELENIUM));
		
		String id= BUILD_PATH_PAGE_ID;
		Map<String,Object> input= new HashMap<String,Object>();
		input.put("add_classpath_entry", newEntry);
		input.put("block_until_buildpath_applied", Boolean.TRUE);
		return PreferencesUtil.createPropertyDialogOn(shell, javaProject, id, new String[] { id }, input).open() == Window.OK;
	}
}
