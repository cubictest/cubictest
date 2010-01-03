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
package org.cubictest.ui.wizards;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

public abstract class WizardNewFileCreationPage extends WizardPage{
	
	private Text fileText;
	private Text containerText;
	private String defaultContainerName;
	private String initFileName = "";
	
	private static final String fileNameEmptyWarning = 
		"File name must be set";
	private static final String fileExtWarning = 
		"File name has to end with ";
	private static final String pathWarning = 
		"Location for the file must be set";
	private static final String fileAlreadyExists =
		"File already exists";
	
	protected WizardNewFileCreationPage(String pageName) {
		super(pageName);
		setTitle(pageName);

	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		
		Label label = new Label(container, SWT.NULL);
		label.setText("File name:");
		
		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		fileText.setText(initFileName + getFileExtention());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("Location:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		if (defaultContainerName != null) {
			containerText.setText(defaultContainerName);
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		
		
		setPageComplete(false);
		dialogChanged();
		setControl(container);
		fileText.setFocus();
	}

	public String getFileName() {
		return fileText.getText();
	}
	
	public void setFileName(String name) {
		initFileName = name;
	}
	
	public String getContainerName(){
		return containerText.getText();
	}
	
	public void setContainerName(String name){
		this.defaultContainerName = name;
	}
	
	/**
	 * Uses the standard container selection dialog to
	 * choose the new value for the container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog =
			new ContainerSelectionDialog(
				getShell(),
				ResourcesPlugin.getWorkspace().getRoot(),
				false,
				"Select location");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path)result[0]).toPortableString());
			}
		}
	}
	
	private void dialogChanged() {
		String fileName = getFileName();
		String containerName = getContainerName();
		if (StringUtils.isBlank(fileName) || fileName.equals(getFileExtention())) {
			setErrorMessage(fileNameEmptyWarning);
			setPageComplete(false);
		}else if (!fileName.endsWith(getFileExtention())) {
			setErrorMessage(fileExtWarning + getFileExtention());
			setPageComplete(false);
		}else if("".equals(containerName)){
			setErrorMessage(pathWarning);
			setPageComplete(false);
		}else if(ResourcesPlugin.getWorkspace().getRoot().
				getFile(new Path(containerName + "/" + fileName)).exists()){
			setErrorMessage(fileAlreadyExists);
			setPageComplete(false);
		}else{
			setErrorMessage(null);
			setPageComplete(true);
		}
	}

	protected abstract String getFileExtention();

}
