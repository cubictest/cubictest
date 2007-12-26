/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.cubictest.common.exception.CubicException;
import org.cubictest.common.resources.UiText;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.SubTestStartPoint;
import org.cubictest.model.Test;
import org.cubictest.model.TestSuiteStartPoint;
import org.cubictest.persistence.CubicTestXStream;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.resources.ResourceMonitor;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.cubictest.ui.utils.ModelUtil;
import org.cubictest.ui.utils.ResourceNavigatorGetter;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;


/**
 * Wizard for creating new tests.
 * If the container resource (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target container.
 * 
 * @author skyt
 * @author chr_schwarz
 */

public class NewTestWizard extends Wizard implements INewWizard {
	TestDetailsPage testDetailsPage;
	StartPointTypeSelectionPage startPointTypeSelectionPage;
	ExtentionStartPointSelectorPage extentionStartPointSelectorPage;
	ISelection selection;
	NewUrlStartPointPage newUrlStartPointPage;
	Map<ExtensionPoint, IFile> extensionPointMap;
	IProject project;

	/**
	 * Constructor for NewTestWizard.
	 */
	public NewTestWizard() {
		super();
		setNeedsProgressMonitor(true);
		extensionPointMap = new HashMap<ExtensionPoint, IFile>();
	}
	
	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public void addPages() {
		testDetailsPage = new TestDetailsPage(selection, !extensionPointMap.isEmpty(), "test");
		addPage(testDetailsPage);
		startPointTypeSelectionPage = new StartPointTypeSelectionPage();
		addPage(startPointTypeSelectionPage);
		newUrlStartPointPage = new NewUrlStartPointPage(startPointTypeSelectionPage);
		addPage(newUrlStartPointPage);
		extentionStartPointSelectorPage = new ExtentionStartPointSelectorPage(extensionPointMap, project);
		addPage(extentionStartPointSelectorPage);
	}
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
		final String url = newUrlStartPointPage.getUrl();
		final ExtensionPoint extensionPoint = extentionStartPointSelectorPage.getExtensionPoint();
		final IFile file = extentionStartPointSelectorPage.getExtentionPointFile();
		final boolean useUrlStartPoint = startPointTypeSelectionPage.getNextPage().equals(newUrlStartPointPage);
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(containerName, fileName, name, description, url, extensionPoint, file, useUrlStartPoint, monitor);
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
			ErrorHandler.logAndShowErrorDialog("Error creating test", e);
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
		String name,
		String description,
		String url,
		ExtensionPoint extensionPoint,
		IFile startTestFile,
		boolean useUrlStartPoint,
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
		final IFile testFile = container.getFile(new Path(fileName));
		
		//create test:
		Test emptyTest = null;
		if (useUrlStartPoint)
			emptyTest = WizardUtils.createEmptyTest(name, description, url);
		else{
			emptyTest = WizardUtils.createEmptyTest("test" + System.currentTimeMillis(), 
					name, description, startTestFile, extensionPoint);
		}
		TestPersistance.saveToFile(emptyTest, testFile);
		monitor.worked(1);
		
		testFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);

		openFileForEditing(monitor, testFile);
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

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		getWizardTitle();

		setPackageExplorerLinkingEnabled(true);
		
		IStructuredSelection iss = (IStructuredSelection) selection;
		if (iss.getFirstElement() instanceof IResource) {
			IResource res = (IResource) iss.getFirstElement();
			project = res.getProject();
			IDE.saveAllEditors(new IResource[] {project}, true);
		}
		else {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			project = root.getProject();
			if (project == null) {
				if (root.getProjects().length == 0) {
					ErrorHandler.logAndShowErrorDialogAndThrow("A CubicTest Project has to be created " +
							"before a Test can be created.");
				}
			}
		}

		if (project == null) {
			ErrorHandler.logAndShowErrorDialogAndThrow("Could not create new test (Unable to get reference " +
					"to the CubicTest project). " +
					"Please retry the operation, and make sure a CubicTest project is target for the new test.");
		}
		
		try {
			IResourceMonitor monitor = new ResourceMonitor(project);
			populateExtensionPointMap(project, extensionPointMap, monitor);
		}		
		catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}

	protected void getWizardTitle() {
		setWindowTitle("New CubicTest test");
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
		if(newUrlStartPointPage.hasValidUrl() && startPointTypeSelectionPage.urlStartPointSelected == true) {
			return true;
		}
		else if(extentionStartPointSelectorPage.getExtensionPoint() != null && extentionStartPointSelectorPage.isPageShown() && 
				startPointTypeSelectionPage.extentionStartPointRadio.getSelection() == true) {
			return true;
		}
		return false;
	}
	
	public void populateExtensionPointMap(IContainer resource, Map<ExtensionPoint,IFile> map, 
			IResourceMonitor monitor) throws CoreException {
		traverseFolder(project, extensionPointMap, monitor);
	}
	
	protected void traverseFolder(IContainer resource, Map<ExtensionPoint,IFile> map, 
			IResourceMonitor monitor) throws CoreException{
		for (IResource entry : resource.members()) {
			if (entry.getType() == IResource.FOLDER) {
				traverseFolder((IFolder) entry, map, monitor);
			} else {
				// convert file if it is a test
				String fileName = entry.getName();

				if (ModelUtil.isTestFile(fileName)) {
					try {
						Test test = TestPersistance.loadFromFile((IFile)entry);
						if (test.getStartPoint() instanceof SubTestStartPoint) {
							continue;
						}
						else if (test.getStartPoint() instanceof TestSuiteStartPoint) {
							continue;
						}
						for(ExtensionPoint ep : test.getAllExtensionPoints()){
							map.put(ep, (IFile)entry);
						}
					}
					catch(Exception e) {
						MessageDialog.openWarning(new Shell(), UiText.APP_TITLE, 
								"Could not open test \"" + fileName + "\" for getting its extension points");
					}
				}
			}
		}
	}
	
}