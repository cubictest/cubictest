/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.model.formElement.TextArea;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.eclipse.draw2d.IFigure;
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

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		TestStepLabel label = (TestStepLabel) super.createFigure();
		label.setTooltipText("Check text area present: $labelText");
		return label;
	}

	@Override
	protected Image getImage(boolean isNot) {
		return CubicTestImageRegistry.get(CubicTestImageRegistry.TEXT_AREA_IMAGE,isNot);
	}
}
