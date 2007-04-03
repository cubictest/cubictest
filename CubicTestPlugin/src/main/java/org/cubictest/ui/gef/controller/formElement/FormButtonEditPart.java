/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
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
