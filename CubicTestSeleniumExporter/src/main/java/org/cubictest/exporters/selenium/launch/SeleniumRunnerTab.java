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
import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.cubictest.exporters.selenium.ui.RunSeleniumRunnerAction;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;
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

	private Text seleniumServerHost;
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
			dialog.setTitle("Select a .aat file");
			filter.setPatterns(new String[]{"*.aat"});
			
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
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 2;
		
		{
			Group group = new Group(composite, SWT.NONE);
			group.setText("Project name: ");
			group.setLayout(topLayout);
			group.setLayoutData(gd);
			projectName = new Text(group, SWT.WRAP | SWT.BORDER);
			projectName.setLayoutData(gd);
			projectBrowse = new Button(group, SWT.PUSH);
			projectBrowse.setText("Browse...");
			projectBrowse.addSelectionListener(projectBrowseListener );
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		
		topLayout = new GridLayout();
		topLayout.numColumns = 2;
		
		{
			Group group = new Group(composite, SWT.NONE);
			group.setText("Test: ");
			group.setLayoutData(gd);
			group.setLayout(topLayout);
			testName = new Text(group, SWT.WRAP | SWT.BORDER);
			testName.setLayoutData(gd);
			testBrowse = new Button(group, SWT.PUSH);
			testBrowse.setText("Browse...");
			testBrowse.addSelectionListener(testBrowseListener);
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		
		topLayout = new GridLayout();
		topLayout.numColumns = 1;
		
		{
			Group group = new Group(composite, SWT.NONE);
			group.setText("Browser: ");
			group.setLayoutData(gd);
			group.setLayout(topLayout);
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
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		
		topLayout = new GridLayout();
		topLayout.numColumns = 2;
		
		{
			nameSpaceGroup = new Group(composite, SWT.NONE);
			nameSpaceGroup.setText("Use namespace in xpath execution: ");
			nameSpaceGroup.setLayoutData(gd);
			nameSpaceGroup.setLayout(topLayout);
			nameSpaceButton = new Button(nameSpaceGroup, SWT.CHECK);
			nameSpaceButton.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});
			
		}
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		
		topLayout = new GridLayout();
		topLayout.numColumns = 2;
		
		{
			Group group = new Group(composite, SWT.NONE);
			group.setText("SeleniumRC Server: ");
			group.setLayoutData(gd);
			group.setLayout(topLayout);
			
			seleniumServerHost = new Text(group, SWT.WRAP | SWT.BORDER);
			seleniumServerHost.setLayoutData(gd);
			
			seleniumServerPort = new Text(group, SWT.WRAP | SWT.BORDER);
			seleniumServerPort.setLayoutData(gd);
			
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
					CUBIC_TEST_SELENIUM_SERVER_HOST,""));
			seleniumServerPort.setText(configuration.getAttribute(
					CUBIC_TEST_SELENIUM_SERVER_PORT,""));
		} catch (CoreException e) {
			testName.setText("");
			projectName.setText("");
			browserType = BrowserType.FIREFOX;
			nameSpaceButton.setSelection(false);
			e.printStackTrace();
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
