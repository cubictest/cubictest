/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.layout;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Iterator;

import org.cubictest.common.utils.Logger;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.IStartPoint;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.ui.gef.command.MoveNodeCommand;
import org.cubictest.ui.gef.command.PageResizeCommand;
import org.cubictest.ui.gef.controller.AbstractNodeEditPart;
import org.cubictest.ui.gef.controller.AbstractPageEditPart;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.cubictest.ui.gef.view.CubicTestHeaderLabel;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class AutoLayout {
	private static final int TRANSITION_SPACE = 45;
	private static final int USER_INPUT_HEIGHT = 18;
	private final TestEditPart testEditPart;
	private ITestEditor testEditor;

	/**
	 * Public constructor.
	 * @param testEditor the GraphicalTestEditor to apply layout to.
	 */
	public AutoLayout(ITestEditor testEditor) {
		GraphicalTestEditor editor = (GraphicalTestEditor) testEditor;
		this.testEditPart = (TestEditPart) editor.getGraphicalViewer().getContents();
		this.testEditor = testEditor;
	}

	public void layout(TransitionNode node) {
		Point position = node.getPosition().getCopy();
		
		int successorNodes = 0;
		for (Transition trans : node.getInTransition().getStart().getOutTransitions()) {
			if (trans.getEnd().equals(node)) {
				break;
			}
			else if (!(trans.getEnd() instanceof ExtensionPoint)) {
				successorNodes++;
			}
		}
		
		try {

			if((node.getInTransition().getStart() instanceof IStartPoint)) {
				position.x = ITestEditor.INITIAL_PAGE_POS_X + (ITestEditor.NEW_PATH_OFFSET * successorNodes);
				position.y = ITestEditor.INITIAL_PAGE_POS_Y;
			} else {				
				position.x = node.getInTransition().getStart().getPosition().x + node.getInTransition().getStart().getDimension().width / 2;
				position.x = position.x + (ITestEditor.NEW_PATH_OFFSET * successorNodes);
				position.y = getYPositionForNode(node);
			}
		}
		catch(NullPointerException e) {
			Logger.warn("NullPointerException. Using default pos.");
			position.x = ITestEditor.INITIAL_PAGE_POS_X + (ITestEditor.NEW_PATH_OFFSET * successorNodes);
		}
		layout(node, position);
	}

	public static int getYPositionForNode(TransitionNode node) {
		int numOfActions = 0;
		UserInteractionsTransition inTransition = (UserInteractionsTransition) node.getInTransition();
		
		if (inTransition instanceof UserInteractionsTransition) {
			numOfActions = inTransition.getActiveUserInteractions().size();
			if (isNotBlank(inTransition.getName())) {
				numOfActions++; //space for action name
			}
		}
		int inputSpace = TRANSITION_SPACE + (numOfActions * USER_INPUT_HEIGHT);
		int y = node.getInTransition().getStart().getPosition().y + inTransition.getStart().getDimension().height;
		return y + inputSpace;
	}
	
	public void layout(TransitionNode node, Point topCenter) {
		if(node instanceof TransitionNode) {
			AbstractNodeEditPart nodeEditPart = (AbstractNodeEditPart) this.findTransitionNodeEditPart(node);

			if(nodeEditPart == null) {
				return;
			}
			
			/**
			 * Calculate dimensions
			 */
			int width = nodeEditPart.getFigure().getMinimumSize().width;
			if (nodeEditPart instanceof AbstractPageEditPart) {
				CubicTestHeaderLabel label = ((AbstractPageEditPart) nodeEditPart).getHeaderFigure();
				int headerWidth = label.getUnmodifiedPreferredSize().width + 6;
				if (width < headerWidth) {
					width = headerWidth;
				}
			}
			int height = 25;
			
			for(Object child : nodeEditPart.getChildren()) {
				if(!(child instanceof AbstractGraphicalEditPart)) {
					continue;
				}
	
				AbstractGraphicalEditPart editPart = (AbstractGraphicalEditPart) child;
				height += 5 + editPart.getFigure().getBounds().height;
				width = Math.max(width, editPart.getFigure().getBounds().width);
			}
	
			if (node instanceof AbstractPage) {
				if(width < 100) {
					width = 100;
				}
				width = width + 15; //some extra space
				PageResizeCommand resizeCmd = new PageResizeCommand();
				resizeCmd.setNode(node);
				resizeCmd.setOldDimension(node.getDimension());
				resizeCmd.setNewDimension(new Dimension(width, height));
				testEditor.getCommandStack().execute(resizeCmd);
			}

			MoveNodeCommand moveCmd = new MoveNodeCommand();
			moveCmd.setNode(node);
			moveCmd.setOldPosition(node.getPosition());
			
			Point position = node.getPosition().getCopy();
			position.y = topCenter.y;
			position.x = topCenter.x - width / 2;
			moveCmd.setNewPosition(position);
			
			testEditor.getCommandStack().execute(moveCmd);
		}
		
		int height = node.getDimension().height;
		if (node instanceof ConnectionPoint) {
			height = 30;
		}
		int bottom = node.getPosition().y + height;

		int numOfTrans = 0;
		for(Transition t : node.getOutTransitions()) {
			int numOfActions = 0;
			numOfTrans++;
			if (t instanceof UserInteractionsTransition) {
				numOfActions = ((UserInteractionsTransition) t).getActiveUserInteractions().size();
				if (isNotBlank(t.getName())) {
					numOfActions++; //space for action name
				}
			}
			numOfTrans = numOfTrans < 1 ? 1 : numOfTrans;
			int newX = topCenter.x + (numOfTrans - 1) * ITestEditor.NEW_PATH_OFFSET;
			int newY = bottom + TRANSITION_SPACE + (numOfActions * USER_INPUT_HEIGHT);
			if (t.getEnd() instanceof ExtensionPoint) {
				newY = node.getPosition().y + node.getDefaultDimension().height / 2;
				newX = node.getPosition().x + node.getDimension().width + 150;
				numOfTrans--; //since exPoint is placed at side
			}
			layout(t.getEnd(), new Point(newX, newY));
		}
		
	}
	
	private AbstractEditPart findTransitionNodeEditPart(TransitionNode transitionNode) {
		for(Object child : this.testEditPart.getChildren()) {
			if(((AbstractEditPart)child).getModel() == transitionNode) {
				return (AbstractEditPart)child;
			}
		}
		return null;
	}
	
	
	/**
	 * Sets the specified page as "selected" in the GUI, deselected the rest.
	 * @param page
	 */
	public void setPageSelected(AbstractPage page) {
		Iterator<?> iter = this.getTestEditPart().getChildren().iterator();
		while (iter.hasNext()) {
			EditPart part = (EditPart) iter.next();
			if (part instanceof AbstractPageEditPart) {
				AbstractPage p = (AbstractPage) part.getModel();
				if (p.equals(page)) {
					part.setSelected(EditPart.SELECTED_PRIMARY);
				}
				else {
					part.setSelected(EditPart.SELECTED_NONE);
				}
			}
		}
	}

	public TestEditPart getTestEditPart() {
		return testEditPart;
	}
}
