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

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
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

/**
 * The "New" wizard page allows setting the container for
 * the new file as well as the file name. The page
 * will only accept file name without the extension OR
 * with the extension that matches the expected one (aat or one set).
 */

public class TestDetailsPage extends WizardPage {
	private String fileExt = ".aat";
	private Text containerText;
	private Text filenameText;
	private boolean updatingTestName;
	private boolean manualName;
	private Text testNameText;
	private Text descriptionText;
	private ISelection selection;
	private File selectedFile;
	private static final int STATUS_OK = 1;
	private static final int STATUS_INFO = 1;
	private static final int STATUS_ERROR = 2;
	private final boolean extentionPoint;
	private String newItemType;
	
	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}
	@Override
	public IWizardPage getNextPage() {
		if(extentionPoint)
			return getWizard().getPage(StartPointTypeSelectionPage.NAME);
		return getWizard().getPage(NewUrlStartPointPage.NAME);
	}

	/**
	 * Constructor for TestDetailsPage.
	 * @param extensionPointMap 
	 * @param pageName
	 */
	public TestDetailsPage(ISelection selection, boolean extentionPoint, String newItemType) {
		super("wizardPage");
		this.newItemType = newItemType;
		this.extentionPoint = extentionPoint;
		setTitle("Create a new " + newItemType);
		setDescription("Select location to create " + getNewItemTypeNoCapitalized() + ", and give it a filename.");
		this.selection = selection;

		IStructuredSelection iss = (IStructuredSelection) selection;
		IResource res = ((IResource) iss.getFirstElement());

		try {
			this.selectedFile = res.getRawLocation().toFile();
		}
		catch (NullPointerException e) {
			try {
				this.selectedFile = res.getProject().getLocation().toFile();
			}
			catch (NullPointerException e2) {
				this.selectedFile = null;
			}
		}
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("Location:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
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
		label = new Label(container, SWT.NULL);
		label.setText(getNewItemTypeCapitalized() + " name:");
		testNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		testNameText.setLayoutData(gd);
		testNameText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		label = new Label(container, SWT.NULL);
		
		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		filenameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		filenameText.setLayoutData(gd);
		filenameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!updatingTestName) {
					manualName = true;
					dialogChanged();
				}
			}
		});		
		label = new Label(container, SWT.NULL);
		
		label = new Label(container, SWT.TOP);
		label.setText("Description");
		
		descriptionText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
		gd = new GridData(GridData.FILL_BOTH);
		descriptionText.setLayoutData(gd);
		
		
		initialize();
		dialogChanged();
		setControl(container);
		testNameText.setFocus();
	}
	
	/**
	 * Tests if the current workbench selection is a suitable
	 * container to use.
	 */
	
	private void initialize() {
		if (selection!=null && selection.isEmpty()==false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection)selection;
			if (ssel.size()>1) return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer)obj;
				else
					container = ((IResource)obj).getParent();
				containerText.setText(container.getFullPath().toString());
			}
		}
	}
	
	private String getNextDefaultTestFilenameInDir(String baseName) {
		int highestNumber = 0;
		if (!selectedFile.isDirectory()) {
			selectedFile = selectedFile.getParentFile();
		}
		
		if (selectedFile != null && selectedFile.isDirectory()) {
			File[] inputContents = selectedFile.listFiles();
			for (int i = 0; i < inputContents.length; i++) {
				File file = inputContents[i];
				String existingFileName = file.getName();
				if (highestNumber < 1 && existingFileName.equals(baseName + fileExt))
				{
					highestNumber = 1;
				}
				else if (existingFileName.endsWith(fileExt) && existingFileName.startsWith(baseName)) {
					int fileNumber = 0;
					try {
						fileNumber = Integer.parseInt(existingFileName.substring(baseName.length(), existingFileName.indexOf((fileExt))));
					}
					catch (Exception e) {
						//no problem
					}
					if (fileNumber > highestNumber) {
						highestNumber = fileNumber;
					}
				}
			}
		}
		if (highestNumber < 1) {
			return baseName + fileExt;
		}
		else {
			return baseName + (highestNumber + 1) + fileExt;			
		}
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
				"Select " + getNewItemTypeNoCapitalized() + " location");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path)result[0]).toOSString());
			}
		}
	}
	
	/**
	 * Ensures that all text fields are set.
	 */

	private void dialogChanged() {
		String container = getContainerName();
		String fileName = getFileName();
		String testName = getName();
		
		if (container.length() == 0) {
			updateStatus(getNewItemTypeCapitalized() + " location must be specified", STATUS_ERROR);
			return;
		}
		
		if(testName.length() > 0 && !manualName) {
			updatingTestName = true;
			filenameText.setText(getNextDefaultTestFilenameInDir(testName));
			updatingTestName = false;
		}

		if (selectedFile != null && selectedFile.isDirectory()) {
			File[] inputContents = selectedFile.listFiles();
			for (int i = 0; i < inputContents.length; i++) {
				File file = inputContents[i];
				String existingFileName = file.getName();
				if (fileName.equals(existingFileName)) {
					updateStatus("File already exists", STATUS_ERROR);
					return;
				}
			}
		}
		
		if (fileName.length() == 0) {
			updateStatus("File name must be specified", STATUS_ERROR);
			return;
		}
		
		if (!fileName.endsWith(fileExt)) {
			updateStatus("File extension must be \"" + fileExt + "\"", STATUS_ERROR);
			return;
		}
		
		if (testName.length() == 0 ){
			updateStatus("Name must be specified", STATUS_INFO);
			return;
		}
		
		updateStatus(null, STATUS_OK);
	}

	private void updateStatus(String message, int severity) {
		if (severity == STATUS_ERROR) {
			setErrorMessage(message);
			setMessage(null);			
		}
		else {
			setErrorMessage(null);
			setMessage(message, IMessageProvider.WARNING);			
		}
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}
	public String getFileName() {
		return filenameText.getText();
	}
	@Override
	public String getName() {
		return testNameText.getText();
	}
	@Override
	public String getDescription() {
		return descriptionText.getText();
	}
	
	private String getNewItemTypeCapitalized() {
		return newItemType.substring(0, 1).toUpperCase() + newItemType.substring(1);
	}
	
	private String getNewItemTypeNoCapitalized() {
		return newItemType.substring(0, 1).toLowerCase() + newItemType.substring(1);
	}

}