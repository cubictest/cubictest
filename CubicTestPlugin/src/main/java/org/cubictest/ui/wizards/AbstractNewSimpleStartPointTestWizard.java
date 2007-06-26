/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Test;
import org.cubictest.persistence.CubicTestXStream;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.cubictest.ui.utils.ModelUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.INewWizard;


/**
 * Wizard for creating new sub tests.
 * If the container resource (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target container.
 * 
 * @author Christian Schwarz 
 */

public abstract class AbstractNewSimpleStartPointTestWizard extends NewTestWizard implements INewWizard {

	/**
	 * Constructor for NewTestWizard.
	 */
	public AbstractNewSimpleStartPointTestWizard() {
		super();
	}
	
	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public abstract void addPages();	
	
	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
		final String containerName = testDetailsPage.getContainerName();
		final String fileName = testDetailsPage.getFileName();
		final String name = testDetailsPage.getName();
		final String description = testDetailsPage.getDescription();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, name, description, monitor);
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
			ErrorHandler.logAndShowErrorDialog(e, "Error creating test");
			return false;
		}
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
		String name,
		String description,
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
		final IFile file = container.getFile(new Path(fileName));
		
		try {
			Test emptyTest = createEmptyTest(name, description);
			String xml = new CubicTestXStream().toXML(emptyTest);
			FileUtils.writeStringToFile(file.getLocation().toFile(), xml, "ISO-8859-1");
			file.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);

		} catch (IOException e) {
			ErrorHandler.logAndRethrow(e);
		}
		monitor.worked(1);
		
		openFileForEditing(monitor, file);
	}
	

	public abstract Test createEmptyTest(String name, String description);


	
	@Override
	public boolean canFinish() {
		if(ModelUtil.isTestFile(testDetailsPage.getFileName()) && !testDetailsPage.getFileName().startsWith(".")) {
			return true;
		}
		return false;
	}
	
	
	@Override
	public void populateExtensionPointMap(IContainer resource, Map<ExtensionPoint,IFile> map, 
			IResourceMonitor monitor) throws CoreException {
		//not needed here
		return;
	}
		
}