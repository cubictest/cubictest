package org.cubictest.ui.eclipse;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

public class CubicTestResourceChangeListener implements
		IResourceChangeListener, IResourceDeltaVisitor {

	public void resourceChanged(IResourceChangeEvent event) {
		System.out.println("Resources changed");
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(this);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean visit(IResourceDelta delta) {
		System.out.println("Visit kind: " + delta.getKind());
		IResource resource = delta.getResource();
		if(delta.getKind() == IResourceDelta.REMOVED){
			System.out.println("filex changed: " +resource.getFileExtension());
			if("aat".equals(resource.getFileExtension()) && delta.getMovedToPath() != null){
				UpdateTestsSubTests operation = new UpdateTestsSubTests(resource,delta.getMovedToPath());
				operation.setRule(resource.getProject());
				operation.schedule();
			}
		}
		return true; // visit the children
	}
}
