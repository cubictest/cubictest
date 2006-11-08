/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.model.formElement.Checkbox;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.gef.view.CubicTestLabel;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.eclipse.draw2d.IFigure;


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

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		TestStepLabel figure = (TestStepLabel) super.createFigure();
		Checkbox model = (Checkbox)getModel();
		String key = model.isChecked() ? CubicTestImageRegistry.CHECKBOX_CHECKED_IMAGE : 
			CubicTestImageRegistry.CHECKBOX_UNCHECKED_IMAGE;
		figure.setIcon(CubicTestImageRegistry.get(key));
		return figure;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();
		CubicTestLabel figure = (CubicTestLabel) getFigure();
		if (getModel() instanceof Checkbox) {
			Checkbox box = (Checkbox)getModel();
			String key = box.isChecked() ? CubicTestImageRegistry.CHECKBOX_CHECKED_IMAGE : 
				CubicTestImageRegistry.CHECKBOX_UNCHECKED_IMAGE;
			figure.setIcon(CubicTestImageRegistry.get(key));
		}
		
	}
}
