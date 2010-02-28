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
package org.cubictest.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;

public class NewParamWizard extends Wizard implements INewWizard{

	private WizardNewParamCreationPage namePage;
	private String defaultDestFolder;
	private String paramsName;
	private String createdFilePath;
	private IProject project;

	public NewParamWizard(IProject project) {
		this.project = project;
	}
	
	public NewParamWizard(){
		setWindowTitle("CubicTest parameterisation file");
	}
	
	@Override
	public boolean performFinish() {	
		try {
			getContainer().run(false, false, new WorkspaceModifyOperation(null) {
				@Override
				protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
					IFile paramsFile = createParams(monitor, namePage.getFileName(), namePage.getContainerName());
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IDE.openEditor(page, paramsFile, true);
					createdFilePath = paramsFile.getFullPath().toPortableString();
				}
			});
		} catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		
		return true;
	}
	
	private IFile createParams(IProgressMonitor monitor, String fileName, String containerName) throws CoreException {
		monitor.beginTask("Creating " + fileName, 2);
		IResource resource = project.findMember(new Path(containerName));
		if (resource == null || !resource.exists() || !(resource instanceof IContainer)) {
			Path path = new Path(project.getLocation().removeLastSegments(1) + containerName);
			path.toFile().mkdirs();
			project.refreshLocal(Integer.MAX_VALUE, monitor);
			resource = project.findMember(new Path(containerName).removeFirstSegments(1));
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = new ByteArrayInputStream("".getBytes());
			if (file.exists()) {
				//file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
			ErrorHandler.logAndRethrow("Error creating params", e);
		}
		return file;
	}
	
	@Override
	public void addPages() {
		super.addPages();
		namePage = new WizardNewParamCreationPage("newCubicTestParamNamepage");
		if(paramsName != null)
			namePage.setFileName(paramsName);
		namePage.setTitle("New CubicTest parameters");
		namePage.setDescription("Choose name and location of parameter file");
		namePage.setContainerName(defaultDestFolder);
		addPage(namePage);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		if (project == null) {
			project = WizardUtils.getProjectFromSelectedResource(selection);
		}
		if (defaultDestFolder == null) {
			defaultDestFolder = WizardUtils.getPathFromSelectedResource(selection);
		}
	}
	

	public void setParamsName(String paramsName) {
		this.paramsName = paramsName;
	}

	public void setDefaultDestFolder(String defaultDestFolder) {
		this.defaultDestFolder = defaultDestFolder;
	}

	public String getCreatedFilePath() {
		return createdFilePath;
	}

}
