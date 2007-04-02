/*
 * Created on 28.mar.2007
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.utils.ErrorHandler;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * Delete and updates sub-test paths and closes editors if necessary.
 * 
 * @author SK Skytteren
 * @author Christian Schwarz
 *
 */
public class PostChangeCubicTestDeltaVisitor implements IResourceDeltaVisitor {

	public boolean visit(IResourceDelta delta) throws PartInitException {
		IResource resource = delta.getResource();
		if(delta.getKind() == IResourceDelta.REMOVED){
			if("aat".equals(resource.getFileExtension())) {
				if (delta.getMovedToPath() == null){
					//delete
					closeOpenEditorsOfRemovedFiles(resource, false);
				}
				else {
					//move
					closeOpenEditorsOfRemovedFiles(resource, true);
					
					UpdateTestsSubTests operation = new UpdateTestsSubTests(resource,delta.getMovedToPath());
					operation.setRule(resource.getProject());
					operation.schedule();
				}
			}
		}
		return true; // visit the children
	}

	
	private void closeOpenEditorsOfRemovedFiles(IResource resource, boolean save) throws PartInitException {
		//get references to windows that should be closed:
		IWorkbenchWindow window = CubicTestPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage[] pages = window.getPages();
		for (int i = 0; i < pages.length; i++) {
			IEditorReference[] refs = pages[i].getEditorReferences();
			List<IEditorReference> toClose = new ArrayList<IEditorReference>();
			for (int j = 0; j < refs.length; j++) {
				IEditorReference reference = refs[j];
				if (reference.getEditorInput().getName().equals(resource.getName())) {
					toClose.add(reference);
				}
				
			}
			//build the array structure that Eclipse expects:
			Object[] obj = toClose.toArray();
			IEditorReference[] toCloseArray = new IEditorReference[obj.length];
			String names = "";
			for (int j = 0; j < obj.length; j++) {
				toCloseArray[j] = (IEditorReference) obj[j];
				names += toCloseArray[j].getName() + " ";
			}
			
			//close the editors:
			if (toClose.size() > 0) {
				ErrorHandler.showInfoDialog("Closing editor: " + names);
				pages[i].closeEditors(toCloseArray, save);
			}
		}
	}

}
