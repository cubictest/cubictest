/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.model.formElement.Option;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.eclipse.draw2d.IFigure;

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

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		TestStepLabel label = (TestStepLabel) super.createFigure();
		label.setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.OPTION_IMAGE));
		return label;
	}

}
