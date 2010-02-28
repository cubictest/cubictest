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

import org.cubictest.model.formElement.Checkbox;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 * @author chr_schwarz
 *
 * Controller for the <code>Checkbox</code> class. 
 */
public class FormCheckboxEditPart extends FormElementEditPart {

	/**
	 * Constructor for the <code>FormCheckboxEditPart</code>which is the 
	 * controller for the <code>Checkbox</code> class. 
	 * @param checkbox
	 */
	public FormCheckboxEditPart(Checkbox checkbox) {
		setModel(checkbox);
	}
	
	@Override
	protected Image getImage(boolean isNot) {
		if (getModel() instanceof Checkbox) {
			Checkbox box = (Checkbox)getModel();
			String key = box.isChecked() ? CubicTestImageRegistry.CHECKBOX_CHECKED_IMAGE : 
				CubicTestImageRegistry.CHECKBOX_UNCHECKED_IMAGE;
			return CubicTestImageRegistry.get(key,isNot);
		}
		return null;
	}
}
