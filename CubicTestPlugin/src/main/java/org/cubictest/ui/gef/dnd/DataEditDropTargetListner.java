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
package org.cubictest.ui.gef.dnd;

import org.cubictest.ui.gef.factory.DataCreationFactory;
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
	 * @param viewer
	 */
	public DataEditDropTargetListner(EditPartViewer viewer) {
		super(viewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.TemplateTransferDropTargetListener#getFactory(java.lang.Object)
	 */
	@Override
	protected CreationFactory getFactory(Object template) {
//		if(template instanceof CustomPageElement) {
//			return new CustomPageElementCreationFactory(project, ((
//				CustomPageElement)template).getName());
//		} else {
			if (template instanceof DataCreationFactory) {
				return (DataCreationFactory) template;
			}
			return new DataCreationFactory(template);
//		}
	}
}
