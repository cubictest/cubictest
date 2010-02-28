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
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.model.formElement.TextArea;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 * @author SK Skytteren
 *
 * Controller for the <code>TextArea</code> model.
 */
public class FormTextAreaEditPart extends FormElementEditPart {

	/**
	 * Constructor for <code>FormTextAreaEditPart</code>.
	 * @param area
	 */
	public FormTextAreaEditPart(TextArea area) {
		setModel(area);
	}

	@Override
	protected Image getImage(boolean isNot) {
		return CubicTestImageRegistry.get(CubicTestImageRegistry.TEXT_AREA_IMAGE,isNot);
	}
}
