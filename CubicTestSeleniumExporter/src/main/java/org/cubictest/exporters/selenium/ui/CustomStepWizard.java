/*******************************************************************************
 * Copyright (c) 2005, 2008  Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.ui;

import java.util.HashMap;
import java.util.Map;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.exporters.selenium.runner.SeleniumClasspathContainerInitializer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class CustomStepWizard extends Wizard {

	private static final String BUILD_PATH_PAGE_ID= "org.eclipse.jdt.ui.propertyPages.BuildPathsPropertyPage"; //$NON-NLS-1$
	private static final String TEST_SUPERCLASS_NAME = "org.cubictest.selenium.custom.ICustomTestStep";
	private NewClassWizardPage classWizard;
	private String name;
	private IProject project;
	
	public CustomStepWizard() {
		setWindowTitle("Create new CustomTestStep");
	}
	
	@Override
	public void addPages() {
		classWizard = new NewClassWizardPage();
		IJavaProject javaProject = JavaCore.create(project);
		IResource pageFragmentRoot = null;
		try {
			for(IJavaElement element : javaProject.getChildren())
				if(element.getElementType()==IJavaElement.PACKAGE_FRAGMENT_ROOT){
					pageFragmentRoot = element.getResource();
					break;
				}
		} catch (JavaModelException e) {
			Logger.error(e.getMessage(), e);
		}
		if(pageFragmentRoot != null)
			classWizard.setPackageFragmentRoot(
					javaProject.getPackageFragmentRoot(pageFragmentRoot), true);
		classWizard.addSuperInterface("org.cubictest.selenium.custom.ICustomTestStep");
		classWizard.setMethodStubSelection(false, false, true, true);
		addPage(classWizard);
	}
	
	@Override
	public boolean performFinish() {
		name = classWizard.getTypeName();
		if(name != null && name.length() > 0){
			try {
				IJavaProject javaProject = JavaCore.create(project);
				if (javaProject.findType(TEST_SUPERCLASS_NAME) == null) {
					if(MessageDialog.openQuestion(new Shell(), "Add CubicTest Selenium to classpath", 
							"Would you like to add CubicTest and Selenium libraries to the classpath?")){
						IClasspathEntry newEntry = JavaCore.newContainerEntry(
								new Path(SeleniumClasspathContainerInitializer.CUBICTEST_SELENIUM));
						
						String id= BUILD_PATH_PAGE_ID;
						Map<String,Object> input= new HashMap<String,Object>();
						input.put("add_classpath_entry", newEntry);
						input.put("block_until_buildpath_applied", Boolean.TRUE);
						PreferencesUtil.createPropertyDialogOn(getShell(), javaProject, id, new String[] { id }, input).open();
					}
				}
				IProgressMonitor monitor = new NullProgressMonitor();
				classWizard.createType(monitor);
				return true;
			} catch (CoreException e) {
				ErrorHandler.logAndShowErrorDialog(e);
			} catch (InterruptedException e) {
				ErrorHandler.logAndShowErrorDialog(e);
			}
		}
		return false;
	}
	
	public String getClassName(){
		String packageText = classWizard.getPackageText();
		String typeTypeName = classWizard.getTypeName();
		return packageText + (packageText.length() > 0 ? "." : "") + typeTypeName;
	}

	public String getPath() {
		return classWizard.getModifiedResource().getFullPath().toPortableString();
	}

	public void setProject(IProject project) {
		this.project = project;
	}

}
