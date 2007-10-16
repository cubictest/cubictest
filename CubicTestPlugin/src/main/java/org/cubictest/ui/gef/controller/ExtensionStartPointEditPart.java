/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.SubTest;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.actions.UpdateTestStartPointAction;
import org.cubictest.ui.gef.policies.StartPointNodeEditPolicy;
import org.cubictest.ui.gef.view.ExtensionStartPointFigure;
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
		String name = getModel().getName();
		ExtensionStartPointFigure figure = new ExtensionStartPointFigure(name);
		figure.setLocation(((TransitionNode)getModel()).getPosition());
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
	protected String getFileNotFoundMessage() {
		return "Error opening test:\nFile \"" + ((SubTest)getModel()).getFilePath() + "\" not found.\n" +
				"To fix, right click on start point and choose \"" + UpdateTestStartPointAction.ACTION_NAME + "\"";
	}
}
