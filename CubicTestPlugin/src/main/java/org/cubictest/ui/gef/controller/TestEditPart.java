/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.policies.TestContainerEditPolicy;
import org.cubictest.ui.gef.policies.TestXYLayoutEditPolicy;
import org.cubictest.ui.gef.view.TestFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.tools.MarqueeDragTracker;
import org.eclipse.swt.graphics.Font;


/**
 * Root edit part and contoller for the <code>Test</code> model.
 * 
 * @author SK Skytteren
 */
public class TestEditPart extends PropertyChangePart{

	/**
	 * Constructor for <code>TestEditPart</code>.
	 * @param test the model
	 */
	public TestEditPart(Test test) {
		setModel(test);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		
		TestFigure fig = new TestFigure();
		String descriptionValue = ((Test)getModel()).getName();
		if(((Test)getModel()).getDescription() != "") {
			descriptionValue += ": \n    " + ((Test)getModel()).getDescription();
		}
		Label description = new Label(descriptionValue);
		description.setLocation(new Point(200,0));
		description.setTextAlignment(Label.TOP);
		description.setLabelAlignment(Label.LEFT);
		description.setSize(300, 50);
		description.setFont(new Font(null,"tahoma", 15, 1));
		description.setForegroundColor(ColorConstants.lightGray);
		fig.add(description);
		return fig;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		super.refreshVisuals();
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#getEditPolicy(java.lang.Object)
	 */
	public EditPolicy getEditPolicy(Object key) {
		
		return super.getEditPolicy(key);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new TestContainerEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new TestXYLayoutEditPolicy());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPart#getCommand(org.eclipse.gef.Request)
	 */
	
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}

	protected List getModelChildren(){
		List<Object> children = new ArrayList<Object>();
		ConnectionPoint cp = ((Test)getModel()).getStartPoint();
		if (cp != null)
			children.add(cp);
		children.addAll(((Test)getModel()).getPages());
		children.addAll(((Test)getModel()).getExtensionPoints());
		children.addAll(((Test)getModel()).getSubTests());
		children.addAll(((Test)getModel()).getCustomTestSteps());
		return children;
	}
	
	@Override
	protected void addChildVisual(EditPart childEditPart, int index) {
		super.addChildVisual(childEditPart, index+1);
	}

	public void updateParams() {
		for (Object obj : getChildren()){
			if(obj instanceof UrlStartPointEditPart)
				((UrlStartPointEditPart)obj).updateParams();
		}
	}
	
	@Override
	public DragTracker getDragTracker(Request request) {
		return new MarqueeDragTracker();
	}
	
	@Override
	public boolean isCopyable() {
		return false;
	}
	
	@Override
	public boolean isCuttable() {
		return false;
	}
}
