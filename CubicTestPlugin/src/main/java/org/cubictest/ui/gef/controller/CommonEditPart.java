/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Common;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;


/**
 * @author SK Skytteren
 *
 *	Controller for the <code>Common</code> model. 
 */
public class CommonEditPart extends AbstractPageEditPart {

	/**
	 * Constructor for the <code>CommonEditPart</code>.
	 * @param common the model which this class is a controller for.
	 */
	public CommonEditPart(Common common) {
		setModel(common);
	}

	@Override
	protected String getType() {
		return "Common";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		CubicTestGroupFigure figure = (CubicTestGroupFigure) super.createFigure();
		figure.getHeader().setBackgroundColor(ColorConstants.lightGray);
		String name = ((AbstractPage)getModel()).getName();
		figure.setTooltipText("Common: " + name + "\nContains elements common to several pages/states");
		return figure;
	}

}
