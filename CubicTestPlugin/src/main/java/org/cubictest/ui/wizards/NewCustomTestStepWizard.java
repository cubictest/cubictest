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

import java.lang.reflect.InvocationTargetException;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.customstep.CustomTestStep;
import org.cubictest.persistence.CustomTestStepPersistance;
import org.cubictest.ui.utils.ResourceNavigatorGetter;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class NewCustomTestStepWizard extends Wizard implements INewWizard {

	public NewCustomTestStepWizard() {
	}

	

	WizardNewCustomTestStepPage wizardNewCustomTestStepPage;
	ISelection selection;
	IProject project;
	String filePath;

	
	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public void addPages() {
		wizardNewCustomTestStepPage = new WizardNewCustomTestStepPage();
		addPage(wizardNewCustomTestStepPage);
	}
	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		final String containerName = wizardNewCustomTestStepPage.getContainerName();
		final String fileName = wizardNewCustomTestStepPage.getFileName();
		this.filePath = containerName + "/" + fileName;
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			ErrorHandler.logAndShowErrorDialog("Error creating custom test step", e);
			return false;
		}
		setPackageExplorerLinkingEnabled(false);
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */
	private void doFinish(
		String containerName,
		String fileName,
		IProgressMonitor monitor)
		throws CoreException {
		
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile customTestStepFile = container.getFile(new Path(fileName));
		
		CustomTestStep newCustomTestStep = WizardUtils.createEmptyCustomTestStep();

		CustomTestStepPersistance.saveToFile(newCustomTestStep, customTestStepFile);
		monitor.worked(1);
		
		customTestStepFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);

		openFileForEditing(monitor, customTestStepFile);
	}

	
	protected void openFileForEditing(IProgressMonitor monitor, final IFile file) {
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}

	protected void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "cubicTestPlugin", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	public boolean shouldPromptToSaveAllEditors() {
		return true;
	}
	
	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
		
	}

	protected void getWizardTitle() {
		setWindowTitle("New CubicTest Custom Test Step");
	}

	private void setPackageExplorerLinkingEnabled(boolean enabled) {
		// Set "Link with editor" preference to see the test to be created
		// If the desired part isn't available, getFromActivePerspective() returns null
		try {
			PackageExplorerPart.getFromActivePerspective().setLinkingEnabled(enabled);	
		} catch(NullPointerException e) {
			try {
				ResourceNavigatorGetter.getFromActivePerspective().setLinkingEnabled(enabled);			
			} catch(NullPointerException e1) {} 
		}
	}
	
	@Override
	public boolean canFinish() {
		String fileName = wizardNewCustomTestStepPage.getFileName();
		String containerName = wizardNewCustomTestStepPage.getContainerName();
		if(fileName.length() > 7 &&
				fileName.endsWith("custom") &&
				containerName.length() > 0) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = root.getFile(new Path(containerName + "/" + fileName));
			if(!resource.exists())
				return true;
		}
		return false;
	}
	
}
