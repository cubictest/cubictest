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
package org.cubictest.ui.gef.controller;

import org.cubictest.model.Link;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 * @author SK Skytteren
 * Contoller for the <code>Link</code> model.
 *
 */
public class PageLinkEditPart extends PageElementEditPart{

	/**
	 * Constructor for <code>PageLinkEditPart</code>.
	 * @param link the model
	 */
	public PageLinkEditPart(Link link) {
		setModel(link);	
	}	
	
	@Override
	protected Image getImage(boolean not) {
		String key = CubicTestImageRegistry.LINK_IMAGE;
		return  CubicTestImageRegistry.get(key,not);
	}
	
	@Override
	public Link getModel() {
		return (Link) super.getModel();
	}
}
