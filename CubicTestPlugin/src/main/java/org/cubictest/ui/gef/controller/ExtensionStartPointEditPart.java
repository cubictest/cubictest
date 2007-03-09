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

/**
 * Controller for ExtensionStartPoints.
 * 
 * @author chr_schwarz
 */
public class ExtensionStartPointEditPart extends SubTestEditPart {

	public ExtensionStartPointEditPart(ExtensionStartPoint startPoint) {
		super(startPoint);
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new StartPointNodeEditPolicy());
	}
	
	@Override
	protected IFigure createFigure() {
		AbstractTransitionNodeFigure figure = (AbstractTransitionNodeFigure)super.createFigure();
		figure.setLabelLength(200);
		figure.setBackgroundColor(ColorConstants.buttonDarker);
		figure.setToolTipText("Test to start from: $labelText\nDouble click to open file");
		return figure;
	}
	
	@Override
	public boolean isCopyable() {
		return false;
	}
	
	@Override
	public boolean isCuttable() {
		return false;
	}
	
	@Override
	public boolean canBeTargetForPaste() {
		return false;
	}

}
