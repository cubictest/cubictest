/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;

import org.cubictest.model.CommonTransition;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;


/**
 * @author SK Skyttere
 *
 *	Controller for the <code>CommonTransition</code> model.
 * 
 */
public class CommonTransitionEditPart extends TransitionEditPart {

	/**
	 * Constructor for <code>CommonTransitionEditPart</code>.
	 * @param transition the model.
	 */
	public CommonTransitionEditPart(CommonTransition transition) {
		super(transition);
	}
	
	protected IFigure createFigure() {
		IFigure fig = super.createFigure();
		fig.setForegroundColor(ColorConstants.gray);
		fig.setToolTip(new Label("Connection that adds all elements from the Common to the target page"));
		return fig;
	}


	public void propertyChange(PropertyChangeEvent arg0) {
		refreshVisuals();
	}

}
