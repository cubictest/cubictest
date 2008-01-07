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

import org.cubictest.model.formElement.Option;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.swt.graphics.Image;

public class FormOptionEditPart extends FormElementEditPart{
	/**
	 * Constuctor for the FormOptionEditPart whihc is a controller 
	 * for the <code>Option</code>.
	 * @param option
	 */
	public FormOptionEditPart(Option option) {
		super();
		setModel(option);
	}

	@Override
	protected Image getImage(boolean isNot) {
		return CubicTestImageRegistry.get(CubicTestImageRegistry.OPTION_IMAGE,getModel().isNot());
	}
	
}
