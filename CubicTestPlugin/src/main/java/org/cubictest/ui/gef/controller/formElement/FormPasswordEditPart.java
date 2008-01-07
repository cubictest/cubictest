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

import org.cubictest.model.formElement.Password;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 * @author SK Skytteren
 *
 * Controller for the <code>Password</code>.
 */
public class FormPasswordEditPart extends FormElementEditPart {

	/**
	 * Constuctor for the FormPassWordEditPart whihc is a controller 
	 * for the <code>Password</code>.
	 * @param password
	 */
	public FormPasswordEditPart(Password password) {
		super();
		setModel(password);
	}
	@Override
	protected Image getImage(boolean isNot) {
		return CubicTestImageRegistry.get(
				CubicTestImageRegistry.PASSWORD_IMAGE,getModel().isNot());
	}
}
