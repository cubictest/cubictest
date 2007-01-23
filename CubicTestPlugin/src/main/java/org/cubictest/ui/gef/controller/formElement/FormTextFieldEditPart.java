/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.model.formElement.TextField;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.eclipse.draw2d.IFigure;


/**
 * @author SK Skytteren
 * Controller for the <code>TextField</code> model.
 *  
 */
public class FormTextFieldEditPart extends FormElementEditPart {

	/**
	 * Constructor for the <code>FormTextFieldEditPart</code>.
	 * @param field
	 */
	public FormTextFieldEditPart(TextField field) {
		setModel(field);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		TestStepLabel label = (TestStepLabel)super.createFigure();
		label.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.TEXT_FIELD_IMAGE));
		label.setTooltipText("Check text field present: " + getElementDescription());
		return label;
	}
}
