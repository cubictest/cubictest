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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

public class NewPropertiesWizard extends Wizard implements INewWizard {

	private WizardNewPropertiesCreationPage namePage;
	private String defaultDestFolder;
	private String fileName;
	private String containerName;
	
	@Override
	public boolean performFinish() {	
		fileName = namePage.getFileName();
		containerName = namePage.getContainerName();
		try {
			getContainer().run(false, false, new WorkspaceModifyOperation(null) {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
					createProperties(monitor, namePage.getFileName(), namePage.getContainerName());
				}
			});
		} catch (InvocationTargetException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		} catch (InterruptedException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		return true;
	}
	
	private void createProperties(IProgressMonitor monitor, String fileName, String containerName) throws CoreException {
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			String initialContent = "#Format for internationalisation file:\n" +
					"#key=value\n" +
					"#Example:\n" +
					"#loginButton.label=Login\n";
			InputStream stream = new ByteArrayInputStream(initialContent.getBytes());
			if (file.exists()) {
				//file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
	}
	
	@Override
	public void addPages() {
		super.addPages();
		namePage = new WizardNewPropertiesCreationPage("newCubicTestPropertiesNamepage");
		namePage.setTitle("New CubicTest internationalisation file");
		namePage.setDescription("Choose name and location of internationalisation file");
		namePage.setContainerName(defaultDestFolder);
		addPage(namePage);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (defaultDestFolder == null) {
			defaultDestFolder = WizardUtils.getPathFromSelectedResource(selection);
		}
	}
	
	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "cubicTestPlugin", IStatus.OK, message, null);
		throw new CoreException(status);
	}
	
	public String getFileName(){
		return fileName;
	}

	public String getContainerName() {
		return containerName;
	}
}
