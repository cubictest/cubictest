/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.cubictest.common.utils.ErrorHandler;
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
import org.eclipse.jdt.internal.core.JavaProject;
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
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
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
		namePage.setFileName(paramsName);
		namePage.setTitle("New CubicTest parameters");
		namePage.setDescription("Choose name and location of parameter file");
		namePage.setContainerName(defaultDestFolder);
		addPage(namePage);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		IStructuredSelection iss = (IStructuredSelection) selection;
		if (defaultDestFolder == null) {
			if (iss.getFirstElement() instanceof IResource) {
				IResource res = (IResource) iss.getFirstElement();
				this.defaultDestFolder = res.getFullPath().toPortableString();
			}
			else if (iss.getFirstElement() instanceof JavaProject) {
				JavaProject res = (JavaProject) iss.getFirstElement();
				this.defaultDestFolder = res.getPath().toPortableString();
			}
		}
	}
	

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, "cubicTestPlugin", IStatus.OK, message, null);
		throw new CoreException(status);
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
