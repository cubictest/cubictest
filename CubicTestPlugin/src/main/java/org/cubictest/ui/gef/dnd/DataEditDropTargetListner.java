/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.dnd;

import org.cubictest.ui.gef.factory.DataCreationFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;



/**
 * @author SK Skytteren
 *
 * Enables drag and drop in the figure.
 */
public class DataEditDropTargetListner extends TemplateTransferDropTargetListener{

	/**
	 * The constructor for the <code>DataEditDropTargetListner</code>.
	 * Uses only the <code>TemplateTransferDropTargetListener</code>'s constructor.
	 * @param project 
	 * @param viewer
	 */
	public DataEditDropTargetListner(IProject project, EditPartViewer viewer) {
		super(viewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.TemplateTransferDropTargetListener#getFactory(java.lang.Object)
	 */
	protected CreationFactory getFactory(Object template) {
//		if(template instanceof CustomPageElement) {
//			return new CustomPageElementCreationFactory(project, ((
//				CustomPageElement)template).getName());
//		} else {
			return new DataCreationFactory(template);
//		}
	}
}
