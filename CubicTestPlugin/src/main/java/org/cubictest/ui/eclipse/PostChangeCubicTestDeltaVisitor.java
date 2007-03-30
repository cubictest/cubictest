package org.cubictest.ui.eclipse;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;

public class PostChangeCubicTestDeltaVisitor implements IResourceDeltaVisitor {

	public boolean visit(IResourceDelta delta) {
		IResource resource = delta.getResource();
		if(delta.getKind() == IResourceDelta.REMOVED){
			if("aat".equals(resource.getFileExtension()) && delta.getMovedToPath() != null){
				UpdateTestsSubTests operation = new UpdateTestsSubTests(resource,delta.getMovedToPath());
				operation.setRule(resource.getProject());
				operation.schedule();
			}
		}
		return true; // visit the children
	}

}
