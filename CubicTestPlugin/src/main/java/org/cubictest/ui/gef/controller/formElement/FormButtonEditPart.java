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
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.model.formElement.Button;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 * Used as the controller for the Button class.
 * 
 * @author skyt
 * @author chr_schwarz
 */
public class FormButtonEditPart extends FormElementEditPart {

	/**
	 * Constructs a FormButtonEditPart for the <code>Button</code>
	 * @param button
	 */
	public FormButtonEditPart(Button button) {
		setModel(button);
	}

	@Override
	protected Image getImage(boolean isNot) {
		return CubicTestImageRegistry.get(CubicTestImageRegistry.BUTTON_IMAGE,getModel().isNot());
	}
}
