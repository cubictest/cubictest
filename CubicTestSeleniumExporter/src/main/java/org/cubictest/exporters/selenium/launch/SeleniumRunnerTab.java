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

import org.apache.commons.lang.ArrayUtils;
import org.cubictest.common.utils.Logger;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;

public class SeleniumRunnerTab extends AbstractLaunchConfigurationTab {

	public static final String CUBIC_TEST_NAME= "CubicTestSeleniumName";
	public static final String CUBIC_TEST_BROWSER = "CubicTestSeleniumBrowser";
	public static final String CUBIC_TEST_NAMESPACE_XPATH = "CubicTestSeleniumNamespaceXpath";
	public static final String CUBIC_TEST_SELENIUM_SERVER_HOST = "CubicTestSeleniumServerHost";
	public static final String CUBIC_TEST_SELENIUM_SERVER_PORT = "CubicTestSeleniumServerPort";

	private Text projectName;
	private Button projectBrowse;
	//IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME
	
	private Text testName;
	private Button testBrowse;

	private Combo browserCombo;
	private BrowserType browserType;
	
	private Group nameSpaceGroup;
	private Button nameSpaceButton;
	private Label nameSpaceButtonLabel;

	private Label seleniumServerHostLabel;
	private Text seleniumServerHost;
	private Label seleniumServerPortLabel;
	private Text seleniumServerPort;

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
				if(!name.equals(projectName.getText())) {
					testName.setText("");
				}
				projectName.setText(name);
				
				setDirty(true);
				updateLaunchConfigurationDialog();
			}			
			
		}
	};

	ResourcePatternFilter filter = new ResourcePatternFilter(){
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IFolder)
				return true;
			if (element instanceof IProject && ((IProject)element).getName().equals(projectName.getText()))
				return true;
			return !super.select(viewer, parentElement, element);
		}
	};
	
	private SelectionListener testBrowseListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			ElementTreeSelectionDialog dialog =
				new ElementTreeSelectionDialog(new Shell(), 
							new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			dialog.setTitle("Select an .aat / .ats file");
			filter.setPatterns(new String[]{"*.aat", "*.ats"});
			
			dialog.addFilter(filter);
			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
			dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
			if(dialog.open() == Window.OK){
				String testFile = ((IResource) dialog.getResult()[0]).getProjectRelativePath().toPortableString();
				testName.setText(testFile);
				setDirty(true);
				updateLaunchConfigurationDialog();
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
	

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		composite.setFont(parent.getFont());
		
		{
			GridLayout groupLayout = new GridLayout();
			groupLayout.numColumns = 2;
			
			Group group = new Group(composite, SWT.NONE);
			group.setText("Test: ");
			group.setLayout(groupLayout);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			testName = new Text(group, SWT.WRAP | SWT.BORDER);
			testName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			testBrowse = new Button(group, SWT.PUSH);
			testBrowse.setText("Browse...");
			testBrowse.addSelectionListener(testBrowseListener);
		}

		{
			GridLayout groupLayout = new GridLayout();
			groupLayout.numColumns = 2;

			Group group = new Group(composite, SWT.NONE);
			group.setText("Project: ");
			group.setLayout(groupLayout);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			projectName = new Text(group, SWT.WRAP | SWT.BORDER);
			projectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			projectBrowse = new Button(group, SWT.PUSH);
			projectBrowse.setText("Browse...");
			projectBrowse.addSelectionListener(projectBrowseListener );
		}
		
		
		{
			GridLayout groupLayout = new GridLayout();
			groupLayout.numColumns = 1;
			
			Group group = new Group(composite, SWT.NONE);
			group.setText("Browser: ");
			group.setLayout(groupLayout);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			browserCombo = new Combo(group, SWT.NONE | SWT.READ_ONLY);
			for (BrowserType browserType : BrowserType.values()) {
				browserCombo.add(browserType.getDisplayName());
			}
		
			browserCombo.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					browserType = BrowserType.values()[browserCombo.getSelectionIndex()];
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});
			int storedBrowserTypeIndex = ArrayUtils.indexOf(BrowserType.values(), browserType);
			browserCombo.select(storedBrowserTypeIndex);
			setControl(composite);
		}
				
		{
			GridLayout groupLayout = new GridLayout();
			groupLayout.numColumns = 2;

			nameSpaceGroup = new Group(composite, SWT.NONE);
			nameSpaceGroup.setText("Support for namespaces in XHTML:");
			nameSpaceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			nameSpaceGroup.setLayout(groupLayout);
			
			nameSpaceButtonLabel = new Label(nameSpaceGroup, SWT.NONE);
			nameSpaceButtonLabel.setText("Use namespace in XPath execution:");
			
			nameSpaceButton = new Button(nameSpaceGroup, SWT.CHECK);
			nameSpaceButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});
			
		}
		
		{
			GridLayout groupLayout = new GridLayout();
			groupLayout.numColumns = 2;

			Group group = new Group(composite, SWT.NONE);
			group.setText("Selenium RC Server: ");
			group.setLayout(groupLayout);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			seleniumServerHostLabel = new Label(group, SWT.NONE);
			seleniumServerHostLabel.setText("Host:");
			seleniumServerHost = new Text(group, SWT.WRAP | SWT.BORDER);
			seleniumServerHost.setLayoutData(new GridData(200, SWT.DEFAULT));
			
			seleniumServerPortLabel = new Label(group, SWT.NONE);
			seleniumServerPortLabel.setText("Port:");
			seleniumServerPort = new Text(group, SWT.WRAP | SWT.BORDER);
			seleniumServerPort.setLayoutData(new GridData(100, SWT.DEFAULT));
		}
	}

	public String getName() {
		return "CubicTest";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			testName.setText(configuration.getAttribute(CUBIC_TEST_NAME, ""));
			projectName.setText(configuration.getAttribute(ATTR_PROJECT_NAME, ""));
			browserType = BrowserType.fromId(configuration.getAttribute(CUBIC_TEST_BROWSER, 
					BrowserType.FIREFOX.getId()));
			nameSpaceButton.setSelection(configuration.getAttribute(
					CUBIC_TEST_NAMESPACE_XPATH,	false));
			seleniumServerHost.setText(configuration.getAttribute(
					CUBIC_TEST_SELENIUM_SERVER_HOST, "localhost"));
			seleniumServerPort.setText(configuration.getAttribute(
					CUBIC_TEST_SELENIUM_SERVER_PORT, "4545"));
		} catch (CoreException e) {
			testName.setText("");
			projectName.setText("");
			browserType = BrowserType.FIREFOX;
			nameSpaceButton.setSelection(false);
			Logger.warn(e.getMessage(), e);
		}
		int storedBrowserTypeIndex = ArrayUtils.indexOf(BrowserType.values(), browserType);
		browserCombo.select(storedBrowserTypeIndex);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CUBIC_TEST_NAME, testName.getText());
		configuration.setAttribute(ATTR_PROJECT_NAME, projectName.getText());
		configuration.setAttribute(CUBIC_TEST_BROWSER, browserType.getId());
		configuration.setAttribute(CUBIC_TEST_NAMESPACE_XPATH, nameSpaceButton.getSelection());
		configuration.setAttribute(CUBIC_TEST_SELENIUM_SERVER_HOST, seleniumServerHost.getText());
		configuration.setAttribute(CUBIC_TEST_SELENIUM_SERVER_PORT, seleniumServerPort.getText());
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
