/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.controller;

import org.cubictest.model.Image;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;

/**
 * Controller for Image model.
 * @author chr_schwarz
 */
public class PageImageEditPart extends PageElementEditPart {
	
	public PageImageEditPart(Image image) {
		setModel(image);
	}
	
	@Override
	protected org.eclipse.swt.graphics.Image getImage(boolean not) {
		String key = CubicTestImageRegistry.IMAGE_IMAGE;
		return  CubicTestImageRegistry.get(key,not);
	}
}
