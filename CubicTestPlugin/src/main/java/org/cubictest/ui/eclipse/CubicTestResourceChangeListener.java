/*
 * Created on 28.mar.2007
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.eclipse;

import org.cubictest.common.utils.ErrorHandler;
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
				ErrorHandler.logAndRethrow(e);
			}
		}
	}
}
