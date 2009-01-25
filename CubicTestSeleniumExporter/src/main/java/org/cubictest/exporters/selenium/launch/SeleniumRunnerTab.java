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
import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.utils.exported.RunnerUtils;
import org.cubictest.exporters.selenium.common.BrowserType;
import org.cubictest.exporters.selenium.common.SeleniumExporterProjectSettings;
import org.cubictest.model.Test;
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
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
	public static final String CUBIC_TEST_SELENIUM_SERVER_MULTI_WINDOW = "CubicTestSeleniumServerMultiWindow";
	public static final String CUBIC_TEST_SELENIUM_TAKE_SCREENSHOTS = "CubicTestSeleniumTakeScreenshots";
	public static final String CUBIC_TEST_SELENIUM_CAPTURE_HTML = "CubicTestSeleniumCaptureHtml";
	public static final String CUBIC_TEST_SELENIUM_SERVER_HOST = "CubicTestSeleniumServerHost";
	public static final String CUBIC_TEST_SELENIUM_SERVER_PORT = "CubicTestSeleniumServerPort";
	public static final String CUBIC_TEST_SELENIUM_SERVER_AUTO_HOST_AND_PORT = "CubicTestSeleniumServerAuto";

	private Text testName;
	private Button testBrowse;

	private Text projectName;
	private Button projectBrowse;
	
	private Button useCurrentTest;

	private Combo browserCombo;
	private BrowserType browserType;
	
	private Group miscSettingsGroup;
	private Button nameSpaceButton;
	private Label nameSpaceButtonLabel;

	private Label seleniumServerHostLabel;
	private Text seleniumServerHost;
	private Label seleniumServerPortLabel;
	private Text seleniumServerPort;
	
	private Label seleniumAutoHostAndPortLabel;
	private Button seleniumAutoHostAndPortButton;

	private Label seleniumServerMultiWindowLabel;
	private Button seleniumServerMultiWindowButton;

	private Label seleniumTakeScreenshotsLabel;
	private Button seleniumTakeScreenshotsButton;

	private Label seleniumCaptureHtmlLabel;
	private Button seleniumCaptureHtmlButton;

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
	
	
	private SelectionListener useCurrentTestListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent event) {
			IProject project = RunnerUtils.getProjectFromActiveEditorPage();
			Test test = RunnerUtils.getTestFromActiveEditorPage();
			
			if (project != null) {
				projectName.setText(project.getName());
			}
			if (test != null) {
				testName.setText(test.getFile().getProjectRelativePath().toPortableString());
			}				
			setDirty(true);
			updateLaunchConfigurationDialog();
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
	
	private SelectionListener selectionListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			setDirty(true);
			updateLaunchConfigurationDialog();
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
			groupLayout.numColumns = 3;
			
			Group group = new Group(composite, SWT.NONE);
			group.setText("Test to run: ");
			group.setLayout(groupLayout);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Label projectLabel = new Label(group, SWT.NONE);
			projectLabel.setText("Project: ");
			projectName = new Text(group, SWT.WRAP | SWT.BORDER);
			projectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			projectBrowse = new Button(group, SWT.PUSH);
			projectBrowse.setText("Browse...");
			projectBrowse.addSelectionListener(projectBrowseListener);
			
			Label testLabel = new Label(group, SWT.NONE);
			testLabel.setText("Test: ");
			testName = new Text(group, SWT.WRAP | SWT.BORDER);
			testName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			testBrowse = new Button(group, SWT.PUSH);
			testBrowse.setText("Browse...");
			testBrowse.addSelectionListener(testBrowseListener);

			useCurrentTest = new Button(group, SWT.PUSH);
			useCurrentTest.setText("Use test currently open in editor");
			useCurrentTest.addSelectionListener(useCurrentTestListener);
			GridData buttonData = new GridData();
			buttonData.horizontalSpan = 3;
			useCurrentTest.setLayoutData(buttonData);
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
			groupLayout.numColumns = 4;

			miscSettingsGroup = new Group(composite, SWT.NONE);
			miscSettingsGroup.setText("Selenium Runner:");
			miscSettingsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			miscSettingsGroup.setLayout(groupLayout);

			seleniumTakeScreenshotsLabel = new Label(miscSettingsGroup, SWT.NONE);
			seleniumTakeScreenshotsLabel.setText("Take screenshot of test failures:");
			seleniumTakeScreenshotsButton = new Button(miscSettingsGroup, SWT.CHECK);
			seleniumTakeScreenshotsButton.addSelectionListener(selectionListener);
			
			seleniumCaptureHtmlLabel = new Label(miscSettingsGroup, SWT.NONE);
			seleniumCaptureHtmlLabel.setText("Capture HTML of test failures:");
			seleniumCaptureHtmlButton = new Button(miscSettingsGroup, SWT.CHECK);
			seleniumCaptureHtmlButton.addSelectionListener(selectionListener);
			
			nameSpaceButtonLabel = new Label(miscSettingsGroup, SWT.NONE);
			nameSpaceButtonLabel.setText("Support namespaces in XHTML:");
			nameSpaceButton = new Button(miscSettingsGroup, SWT.CHECK);
			nameSpaceButton.addSelectionListener(selectionListener);
			GridData buttonData = new GridData();
			buttonData.horizontalSpan = 3;
			nameSpaceButton.setLayoutData(buttonData);
			
			seleniumServerMultiWindowLabel = new Label(miscSettingsGroup, SWT.NONE);
			seleniumServerMultiWindowLabel.setText("Multiwindow:");
			seleniumServerMultiWindowButton = new Button(miscSettingsGroup, SWT.CHECK);
			seleniumServerMultiWindowButton.setLayoutData(new GridData(100, SWT.DEFAULT));
			seleniumServerMultiWindowButton.addSelectionListener(selectionListener);

		}
		
		{
			GridLayout groupLayout = new GridLayout();
			groupLayout.numColumns = 5;

			Group group = new Group(composite, SWT.NONE);
			group.setText("Selenium Server");
			group.setLayout(groupLayout);
			group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			GridData layoutData;
			
			seleniumAutoHostAndPortLabel = new Label(group, SWT.NONE);
			seleniumAutoHostAndPortLabel.setText("Start new server on localhost (auto port):");
			layoutData = new GridData();
			layoutData.horizontalSpan = 2;
			seleniumAutoHostAndPortLabel.setLayoutData(layoutData);
			seleniumAutoHostAndPortButton = new Button(group, SWT.CHECK);
			seleniumAutoHostAndPortButton.addSelectionListener(selectionListener);
			seleniumAutoHostAndPortButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}
				public void widgetSelected(SelectionEvent e) {
					updateHostAndPortControls();
				}
			});
			layoutData = new GridData();
			layoutData.horizontalSpan = 3;
			seleniumAutoHostAndPortButton.setLayoutData(layoutData);

			seleniumServerHostLabel = new Label(group, SWT.NONE);
			seleniumServerHostLabel.setText("Use existing server at host:");
			seleniumServerHost = new Text(group, SWT.WRAP | SWT.BORDER);
			layoutData = new GridData();
			layoutData.horizontalSpan = 2;
			layoutData.widthHint = 200;
			seleniumServerHost.setLayoutData(layoutData);
			seleniumServerHost.addFocusListener(new FocusAdapter(){
				@Override
				public void focusLost(FocusEvent e) {
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});
			
			seleniumServerPortLabel = new Label(group, SWT.NONE);
			seleniumServerPortLabel.setText("port:");
			seleniumServerPort = new Text(group, SWT.WRAP | SWT.BORDER);
			layoutData = new GridData();
			layoutData.widthHint = 100;
			seleniumServerPort.setLayoutData(layoutData);
			seleniumServerPort.addFocusListener(new FocusAdapter(){
				@Override
				public void focusLost(FocusEvent e) {
					setDirty(true);
					updateLaunchConfigurationDialog();
				}
			});
		}
	}

	public String getName() {
		return "CubicTest";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			testName.setText(configuration.getAttribute(CUBIC_TEST_NAME, ""));
			projectName.setText(configuration.getAttribute(ATTR_PROJECT_NAME, ""));
			
			CubicTestProjectSettings settings = CubicTestProjectSettings.getInstanceFromActivePage();
			
			String defaultBrowser = SeleniumExporterProjectSettings.getPreferredBrowser(settings).getId();
			browserType = BrowserType.fromId(configuration.getAttribute(CUBIC_TEST_BROWSER, defaultBrowser));
			nameSpaceButton.setSelection(configuration.getAttribute(
					CUBIC_TEST_NAMESPACE_XPATH,	false));
			seleniumServerHost.setText(configuration.getAttribute(
					CUBIC_TEST_SELENIUM_SERVER_HOST, "localhost"));
			seleniumServerPort.setText(configuration.getAttribute(
					CUBIC_TEST_SELENIUM_SERVER_PORT, "4444"));
			seleniumServerMultiWindowButton.setSelection(configuration.getAttribute(
					CUBIC_TEST_SELENIUM_SERVER_MULTI_WINDOW, false));
			seleniumTakeScreenshotsButton.setSelection(configuration.getAttribute(
					CUBIC_TEST_SELENIUM_TAKE_SCREENSHOTS, false));
			seleniumCaptureHtmlButton.setSelection(configuration.getAttribute(
					CUBIC_TEST_SELENIUM_CAPTURE_HTML, false));
			seleniumAutoHostAndPortButton.setSelection(configuration.getAttribute(
					CUBIC_TEST_SELENIUM_SERVER_AUTO_HOST_AND_PORT, true));

			updateHostAndPortControls();

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


	private void updateHostAndPortControls() {
		if (seleniumAutoHostAndPortButton.getSelection()) {
			seleniumServerHost.setVisible(false);
			seleniumServerHostLabel.setVisible(false);
			seleniumServerPort.setVisible(false);
			seleniumServerPortLabel.setVisible(false);
		}
		else {
			seleniumServerHost.setVisible(true);
			seleniumServerHostLabel.setVisible(true);
			seleniumServerPort.setVisible(true);
			seleniumServerPortLabel.setVisible(true);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CUBIC_TEST_NAME, testName.getText());
		configuration.setAttribute(ATTR_PROJECT_NAME, projectName.getText());
		configuration.setAttribute(CUBIC_TEST_BROWSER, browserType.getId());
		configuration.setAttribute(CUBIC_TEST_NAMESPACE_XPATH, nameSpaceButton.getSelection());
		configuration.setAttribute(CUBIC_TEST_SELENIUM_SERVER_HOST, seleniumServerHost.getText());
		configuration.setAttribute(CUBIC_TEST_SELENIUM_SERVER_PORT, seleniumServerPort.getText());
		configuration.setAttribute(CUBIC_TEST_SELENIUM_SERVER_MULTI_WINDOW,  seleniumServerMultiWindowButton.getSelection());
		configuration.setAttribute(CUBIC_TEST_SELENIUM_TAKE_SCREENSHOTS,  seleniumTakeScreenshotsButton.getSelection());
		configuration.setAttribute(CUBIC_TEST_SELENIUM_CAPTURE_HTML,  seleniumCaptureHtmlButton.getSelection());
		configuration.setAttribute(CUBIC_TEST_SELENIUM_SERVER_AUTO_HOST_AND_PORT,  seleniumAutoHostAndPortButton.getSelection());
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
