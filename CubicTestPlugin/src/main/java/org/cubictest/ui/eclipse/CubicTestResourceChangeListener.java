package org.cubictest.ui.eclipse;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;

public class CubicTestResourceChangeListener implements
		IResourceChangeListener{

	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			try {
				event.getDelta().accept(new PostChangeCubicTestDeltaVisitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}
