/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.ui.gef.policies.StartPointNodeEditPolicy;
import org.cubictest.ui.gef.view.AbstractTransitionNodeFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

public class ExtensionStartPointEditPart extends SubTestEditPart {

	public ExtensionStartPointEditPart(ExtensionStartPoint startPoint) {
		super(startPoint);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new StartPointNodeEditPolicy());
	}
	
	@Override
	protected IFigure createFigure() {
		AbstractTransitionNodeFigure figure = (AbstractTransitionNodeFigure)super.createFigure();
		figure.setLabelLength(200);
		figure.setBackgroundColor(ColorConstants.buttonDarker);
		return figure;
	}
}
