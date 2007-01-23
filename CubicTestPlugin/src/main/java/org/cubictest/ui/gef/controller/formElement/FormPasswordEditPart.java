/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.model.formElement.Password;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.eclipse.draw2d.IFigure;


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

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		TestStepLabel label = (TestStepLabel) super.createFigure();
		label.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.PASSWORD_IMAGE));
		label.setTooltipText("Check password present: " + getElementDescription());
		return label;
	}
}
