/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.model.FormElement;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.gef.directEdit.CubicTestDirectEditManager;
import org.cubictest.ui.gef.view.TestStepLabel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;


/**
 * Abstract <code>EditPart</code>that provides functionality for the the other form edit parts.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public abstract class FormElementEditPart extends PageElementEditPart{

	CubicTestDirectEditManager manager;

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure(){
		TestStepLabel label = new TestStepLabel(((FormElement)getModel()).getDescription());
		label.setIcon(getImage(getModel().isNot()));
		label.setLabelAlignment(PositionConstants.LEFT);
		return label;
	}
}

