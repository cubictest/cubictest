/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.ui.eclipse;

import java.io.File;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.utils.ModelUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.ResourceUtil;

/**
 * Updates subtests / extension start points in CubicTest test files with new path after a file move.
 * 
 * @author SK Skytteren
 * @author Christian Schwarz
 */
public abstract class TraverseTestFilesWorkspaceJob extends WorkspaceJob {

	private final IResource sourceResource;
	private IProgressMonitor monitor;


	public TraverseTestFilesWorkspaceJob(String name, IResource resource) {
		super(name);
		this.sourceResource = resource;
	}


	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		this.monitor = monitor;
		monitor.beginTask("Updating possible references to test file " + sourceResource.getFullPath(), 1);
		if (sourceResource.getProject().exists()) {
			traverseContainer(sourceResource.getProject());
			sourceResource.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		return Status.OK_STATUS;
	}
	
	/**
	 * Traverse project, folder or file and update subtests in test files with new path.
	 * @param container
	 */
	private void traverseContainer(IContainer container){
		try {
			for (IResource entry : container.members()) {
				File file = entry.getWorkspace().getRoot().getFile(entry.getFullPath()).getLocation().toFile();
				ResourceAttributes resourceAttr = ResourceAttributes.fromFile(file);
				if (resourceAttr.isHidden() || entry.getName().equals(".svn")) {
					//skip hidden / SVN files/folders
					continue;
				}

				if (entry.getType() == IResource.FOLDER) {
					traverseContainer((IContainer) entry);
				}
				else if(entry.getType() == IResource.FILE){
					// convert file if it is a test
					String fileName = entry.getName();
					IFile testFile = (IFile) entry;
					if (ModelUtil.isTestFile(fileName) && !entry.getFullPath().equals(sourceResource.getFullPath())) {
						boolean updated = false;
						//get editor if file is open
						for(IWorkbenchWindow window : CubicTestPlugin.getDefault().getWorkbench().getWorkbenchWindows()){
							IEditorPart editor = ResourceUtil.findEditor(window.getActivePage(), testFile);
							if (editor != null && editor instanceof GraphicalTestEditor) {
								GraphicalTestEditor gte = (GraphicalTestEditor) editor;
								if(updateTest(gte.getTest())){
									gte.doSave(monitor);
									updated = true;
								}
							}
						}
						if(!updated){
							//file was not open in editor, open it from file system:
							Test test = TestPersistance.loadFromFile(testFile);
							if (test != null){
								if(updateTest(test)){
									TestPersistance.saveToFile(test, testFile);
									testFile.touch(monitor);	
								}
							}
						}
					}
				}
			}
		} 
		catch (CoreException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow("Error when updating tests with new path after file move / rename.", e);
		}
	}

	
	public abstract boolean updateTest(Test testFile) throws CoreException;

}
