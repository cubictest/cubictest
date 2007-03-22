package org.cubictest.ui.eclipse;

import org.cubictest.CubicTestPlugin;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.ResourceUtil;

public abstract class TraverseTestFilesWorkspaceJob extends WorkspaceJob {

	private final IResource resource;
	private IProgressMonitor monitor;


	public TraverseTestFilesWorkspaceJob(String name, IResource resource) {
		super(name);
		this.resource = resource;
	}


	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		traverseProject(resource.getProject());
		this.monitor = monitor;
		return Status.OK_STATUS;
	}
	
	public void traverseProject(IContainer container){
		try {
			for (IResource entry : container.members()) {
				if (entry.getType() == IResource.FOLDER) {
					traverseProject((IContainer) entry);
				} else if(entry.getType() == IResource.FILE){
					// convert file if it is a .aat test
					String fileName = entry.getName();
					IFile testFile = (IFile) entry;
					if (fileName.endsWith(".aat") && !entry.getFullPath().equals(resource.getFullPath())) {
						boolean updated = false;
						for(IWorkbenchWindow window : CubicTestPlugin.getDefault().getWorkbench().getWorkbenchWindows()){
							IEditorPart editor = ResourceUtil.findEditor(window.getActivePage(), testFile);
							if (editor instanceof GraphicalTestEditor) {
								GraphicalTestEditor gte = (GraphicalTestEditor) editor;
								if(updateTest(gte.getTest())){
									gte.doSave(monitor);
									updated = true;
								}
							}
						}
						if(!updated){
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
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	
	public abstract boolean updateTest(Test testFile) throws CoreException;

}
