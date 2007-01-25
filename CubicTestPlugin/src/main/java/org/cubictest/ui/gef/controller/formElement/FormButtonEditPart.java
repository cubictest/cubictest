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
import org.cubictest.ui.gef.view.TestStepLabel;
import org.eclipse.draw2d.IFigure;


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

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		TestStepLabel label = (TestStepLabel) super.createFigure();
		label.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.BUTTON_IMAGE));
		label.setTooltipText("Check button present: $labelText");
		return label;
	}
}
