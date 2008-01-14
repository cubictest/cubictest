/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.launch;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.launching.JavaMigrationDelegate;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;

public class SeleniumRunnerTab extends AbstractLaunchConfigurationTab {

	private Text projectName;
	//IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME
	
	private Text testName;
	private Button projectBrowse;

	private Button testBrowse;
	private SelectionListener projectBrowseListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent event) {
			IJavaProject[] projects;
			try {
				projects= JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
			} catch (JavaModelException e) {
				projects= new IJavaProject[0];
			}
			
			ILabelProvider labelProvider= new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT);
			ElementListSelectionDialog dialog= new ElementListSelectionDialog(getShell(), labelProvider);
			dialog.setTitle("CubicTest Project Selection"); 
			dialog.setMessage("Select the CubicProject to look for test in:"); 
			dialog.setElements(projects);
			
			IJavaProject javaProject = getJavaProject();
			if (javaProject != null) {
				dialog.setInitialSelections(new Object[] { javaProject });
			}
			if (dialog.open() == Window.OK) {			
				IJavaProject project = (IJavaProject) dialog.getFirstResult();
				String name = project.getElementName();
				projectName.setText(name);
			}			
			
		}
	};

	private SelectionListener testBrowseListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(new Shell());
			dialog.setFilterExtensions(new String[]{".aat"});
			dialog.setFilterPath(projectName.getText());
			if(dialog.open() == Window.OK + ""){
				String testFile = dialog.getFileName();
				testName.setText(testFile);
			}
		}
	};
	
	private IJavaProject getJavaProject() {
		String projectName = this.projectName.getText().trim();
		if (projectName.length() < 1) {
			return null;
		}
		return JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProject(projectName);		
	}
	
	public final static String CUBIC_TEST_NAME= "CubicUnitTestName";

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		composite.setFont(parent.getFont());

		Group group = new Group(composite, SWT.NONE);
		group.setText("Project name: ");
		//group.setVisible(true);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		GridLayout topLayout = new GridLayout();
		group.setLayout(topLayout);
		topLayout.numColumns = 2;
		projectName = new Text(group, SWT.WRAP | SWT.BORDER);
		projectName.setLayoutData(gd);
		
		projectBrowse = new Button(group, SWT.PUSH);
		projectBrowse.setText("Browse...");
		projectBrowse.addSelectionListener(projectBrowseListener );
		
		group = new Group(composite, SWT.NONE);
		group.setText("Test: ");
		//group.setVisible(true);
		group.setLayoutData(gd);
		group.setLayout(topLayout);
		testName = new Text(group, SWT.WRAP | SWT.BORDER);
		testName.setLayoutData(gd);
		testBrowse = new Button(group, SWT.PUSH);
		testBrowse.setText("Browse...");
		testBrowse.addSelectionListener(testBrowseListener);
		
	}

	public String getName() {
		return "CubicTest";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			testName.setText(configuration.getAttribute(CUBIC_TEST_NAME, ""));
			projectName.setText(configuration.getAttribute(ATTR_PROJECT_NAME, ""));
		} catch (CoreException e) {
			testName.setText("");
			projectName.setText("");
			e.printStackTrace();
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CUBIC_TEST_NAME, testName.getText());
		configuration.setAttribute(ATTR_PROJECT_NAME, projectName.getText());
		mapResources(configuration);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CUBIC_TEST_NAME, testName != null ? testName.getText(): "");
		configuration.setAttribute(ATTR_PROJECT_NAME, projectName != null ? projectName.getText(): "");
	}
	
	protected void mapResources(ILaunchConfigurationWorkingCopy config)  {
		try {
		//CONTEXTLAUNCHING
			IJavaProject javaProject = getJavaProject();
			if (javaProject != null && javaProject.exists() && javaProject.isOpen()) {
				JavaMigrationDelegate.updateResourceMapping(config);
			}
		} catch(CoreException ce) {
			setErrorMessage(ce.getStatus().getMessage());
		}
	}
}
