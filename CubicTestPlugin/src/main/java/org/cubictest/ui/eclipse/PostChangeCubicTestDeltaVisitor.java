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
package org.cubictest.ui.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.utils.ModelUtil;
import org.cubictest.common.utils.UserInfo;
import org.eclipse.core.resources.IFile;
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

	private static final String MOVE = "move/rename";
	private static final String DELETE = "delete";


	public boolean visit(IResourceDelta delta) throws PartInitException {
		IResource resource = delta.getResource();
		if(delta.getKind() == IResourceDelta.REMOVED){
			if(ModelUtil.isTestFile(resource.getName())) {
				if (delta.getMovedToPath() == null){
					//delete
					closeOpenEditorsOfRemovedFiles(resource, false, DELETE);
				}
				else {
					//move
					closeOpenEditorsOfRemovedFiles(resource, true, MOVE);
					
					//Schedule traversal of tests to update references (if any)
					IFile newFile = resource.getWorkspace().getRoot().getFile(delta.getMovedToPath());
					UpdateTestsSubTests operation = new UpdateTestsSubTests(resource, newFile);
					operation.setRule(resource.getProject());
					operation.schedule();
				}
			}
			else if (resource.getType() == IResource.FOLDER){
				IResourceDelta[] deltas = delta.getAffectedChildren();
				for (int i = 0; i < deltas.length; i++) {
					visit(deltas[i]);
				}
			}
		}
		return true;
	}

	
	private void closeOpenEditorsOfRemovedFiles(IResource resource, boolean save, String operation) throws PartInitException {
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
				if (!operation.equals(DELETE)) {
					UserInfo.showInfoDialog("Closing editor due to " + operation + ": " + names);
				}
				pages[i].closeEditors(toCloseArray, save);
			}
		}
	}
}
