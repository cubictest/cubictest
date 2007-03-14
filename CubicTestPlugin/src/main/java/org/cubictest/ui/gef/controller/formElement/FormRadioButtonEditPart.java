/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.model.formElement.RadioButton;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Image;


public class FormRadioButtonEditPart extends FormElementEditPart{

	public FormRadioButtonEditPart(RadioButton button) {
		super();
		setModel(button);
	}

	@Override
	protected IFigure createFigure() {
		TestStepLabel figure = (TestStepLabel) super.createFigure();
		String key = CubicTestImageRegistry.RADIO_BUTTON_CHECKED_IMAGE;
		figure.setIcon(CubicTestImageRegistry.get(key));
		return figure;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();
		RadioButton button = (RadioButton)getModel();
		((TestStepLabel)getFigure()).setTooltipText("Check radiobutton present: $labelText"
				+ (button.isChecked() ? " (checked)" : " (unchecked)"));
	}
	
	@Override
	protected Image getImage(boolean isNot) {
		RadioButton button = (RadioButton)getModel();
		String key = button.isChecked() ? CubicTestImageRegistry.RADIO_BUTTON_CHECKED_IMAGE : 
			CubicTestImageRegistry.RADIO_BUTTON_UNCHECKED_IMAGE;
		return CubicTestImageRegistry.get(key,isNot);
	}
}
