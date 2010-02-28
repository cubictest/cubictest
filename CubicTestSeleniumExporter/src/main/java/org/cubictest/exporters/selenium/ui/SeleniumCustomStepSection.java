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
package org.cubictest.exporters.selenium.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.ui.command.ChangeCustomStepClassNameCommand;
import org.cubictest.exporters.selenium.ui.command.ChangeCustomStepWaitForPageToLoadCommand;
import org.cubictest.exporters.selenium.utils.SeleniumUtils;
import org.cubictest.model.customstep.data.CustomTestStepData;
import org.cubictest.model.customstep.data.CustomTestStepDataEvent;
import org.cubictest.model.customstep.data.ICustomTestStepDataListener;
import org.cubictest.ui.customstep.section.CustomStepSection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.ide.IDE;

public class SeleniumCustomStepSection extends CustomStepSection 
		implements ICustomTestStepDataListener{

	private Text classText;
	private Link newClassLink;
	private Button browseClassButton;
	private Label waitForPageToLoadLabel;
	private Button waitForPageToLoadButton;
	private CustomTestStepData data;
	private IProject project;
	
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(ColorConstants.white);
		
		FormLayout formLayout = new FormLayout();
		composite.setLayout(formLayout);
		
		createNewOrOpenClassLink(composite);
		createClassNameTextField(composite);
		createBrowseClassButton(composite);
		createWaitForPageToLoadCheckbox(composite);
	}

	
	private void createNewOrOpenClassLink(Composite composite) {
		newClassLink = new Link(composite, SWT.PUSH);
		newClassLink.setText("<A>CubicTest Selenium extension*: </A>");
		newClassLink.setBackground(ColorConstants.white);
		newClassLink.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				if(data.getPath() != null && data.getPath().length() > 0){
					
					IPath path = Path.fromPortableString(data.getPath());
					IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
					if(file.exists()){
						IWorkbenchPage page = SeleniumExporterPlugin.getDefault().getWorkbench().
							getActiveWorkbenchWindow().getActivePage();
						try {
							IDE.openEditor(page, file, true);
							return;
						} catch (PartInitException ex) {
							ErrorHandler.logAndRethrow(ex);
						}
					}
				}
				CustomStepWizard customStepWizard = new CustomStepWizard();
				customStepWizard.setProject(project);
				WizardDialog dialog = new WizardDialog(new Shell(),customStepWizard);
				if(dialog.open() == WizardDialog.OK){
					String className = customStepWizard.getClassName();
					ChangeCustomStepClassNameCommand command = 
		        		new ChangeCustomStepClassNameCommand();
		        	command.setCustomTestStepData(data);
		        	command.setPath(customStepWizard.getPath());
		        	command.setDisplayText(className);
		        	getCommandStack().execute(command);
				}
				
			}
		});
		
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(0,0);
		layoutData.width = STANDARD_LABEL_WIDTH;
		newClassLink.setLayoutData(layoutData);
	}
	
	
	private void createClassNameTextField(Composite composite) {
		classText = new Text(composite,SWT.BORDER);
		classText.setBackground(ColorConstants.white);
		classText.setText(data.getDisplayText());
		classText.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}
			public void focusLost(FocusEvent e) {
				ChangeCustomStepClassNameCommand command = new ChangeCustomStepClassNameCommand();
				command.setCustomTestStepData(data);
				command.setPath(data.getPath());
				command.setDisplayText(classText.getText());
				getCommandStack().execute(command);
			}
		});
		
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(newClassLink);
		layoutData.width = STANDARD_LABEL_WIDTH * 2;
		classText.setLayoutData(layoutData);
	}

	
	
	private void createBrowseClassButton(Composite composite) {
		browseClassButton = new Button(composite, SWT.PUSH);
		browseClassButton.setText("Browse...");
		browseClassButton.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				Shell shell = new Shell();
				SelectionDialog dialog;
				try {
					dialog = JavaUI.createTypeDialog(
					        shell, new ProgressMonitorDialog(shell),
					        SearchEngine.createWorkspaceScope(),
					        IJavaElementSearchConstants.CONSIDER_CLASSES, false);
					dialog.setTitle("Open ICustomTestStep implementation");
			        if (dialog.open() == SelectionDialog.OK){
			        	IType result = (IType) dialog.getResult()[0];
			        	
			        	ChangeCustomStepClassNameCommand command = 
			        		new ChangeCustomStepClassNameCommand();
			        	command.setCustomTestStepData(data);
			        	command.setPath(result.getPath().toPortableString());
			        	command.setDisplayText(result.getFullyQualifiedName());
			        	getCommandStack().execute(command);
			        }
				} catch (JavaModelException ex) {
					ErrorHandler.logAndShowErrorDialog(ex);
				}
		        
			}
		});
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(classText,5);
		browseClassButton.setLayoutData(layoutData);
	}
	
	
	private void createWaitForPageToLoadCheckbox(Composite composite) {
		waitForPageToLoadButton = new Button(composite,SWT.CHECK);
		waitForPageToLoadButton.setBackground(ColorConstants.white);
		
		boolean waitForPageToLoad = SeleniumUtils.getWaitForPageToLoadValue(data);
		
		waitForPageToLoadButton.setSelection(waitForPageToLoad);
		waitForPageToLoadButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				ChangeCustomStepWaitForPageToLoadCommand command = new ChangeCustomStepWaitForPageToLoadCommand();
				command.setCustomTestStepData(data);
				command.setNewWaitForPageToLoad(waitForPageToLoadButton.getSelection());
				getCommandStack().execute(command);
			}
		});
		
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(classText, 0, SWT.LEFT);
		layoutData.top = new FormAttachment(classText, 5, SWT.BOTTOM);
		waitForPageToLoadButton.setLayoutData(layoutData);
		
		layoutData = new FormData();
		waitForPageToLoadLabel = new Label(composite, SWT.NONE);
		waitForPageToLoadLabel.setText("Wait for page to load before entering Custom Test Step");
		waitForPageToLoadLabel.setBackground(ColorConstants.white);
		layoutData.left = new FormAttachment(waitForPageToLoadButton, 7, SWT.RIGHT);
		layoutData.top = new FormAttachment(classText, 5, SWT.BOTTOM);
		waitForPageToLoadLabel.setLayoutData(layoutData);
	
	}

	@Override
	public void setData(CustomTestStepData data) {
		this.data = data;
		data.addChangeListener(this);
	}

	public void handleEvent(CustomTestStepDataEvent event) {
		classText.setText(data.getDisplayText());
	}
	
	@Override
	public String getDataKey(){
		return "org.cubictest.seleniumexporter";
	}

	@Override
	public void setProject(IProject project) {
		this.project = project;
	}


}
