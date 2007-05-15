/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.layout;

import java.util.Iterator;

import org.cubictest.common.utils.Logger;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.ui.gef.command.MovePageCommand;
import org.cubictest.ui.gef.command.PageResizeCommand;
import org.cubictest.ui.gef.controller.AbstractPageEditPart;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class AutoLayout {
	private static final int TRANSITION_SPACE = 35;
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
		
		int num = node.getInTransition().getStart().getOutTransitions().size() - 1;
		if (num < 0) num = 0;
		
		try {

			if((node.getInTransition().getStart() instanceof UrlStartPoint) || (node.getInTransition().getStart() instanceof ExtensionStartPoint)) {
				position.x = ITestEditor.INITIAL_PAGE_POS_X + (290 * num);
			} else {				
				position.x = node.getInTransition().getStart().getPosition().x + node.getInTransition().getStart().getDimension().width / 2;
				int numOfActions = 0;
				if (node.getInTransition() instanceof UserInteractionsTransition) {
					numOfActions = ((UserInteractionsTransition) node.getInTransition()).getUserInteractions().size();
				}
				int inputSpace = TRANSITION_SPACE + (numOfActions * USER_INPUT_HEIGHT);
				position.y = node.getInTransition().getStart().getPosition().y + node.getInTransition().getStart().getDimension().height;
				position.y = position.y + inputSpace;
			}
		}
		catch(NullPointerException e) {
			Logger.warn("NullPointerException. Using default pos.");
			position.x = ITestEditor.INITIAL_PAGE_POS_X + (290 * num);
		}
		layout(node, position);
	}
	
	public void layout(TransitionNode node, Point topCenter) {
		if(node instanceof Page) {
			Page page = (Page) node;
			PageEditPart pageEditPart = (PageEditPart) this.findTransitionNodeEditPart(page);

			if(pageEditPart == null) {
				return;
			}
			
			/**
			 * Calculate dimensions
			 */
			int width = pageEditPart.getFigure().getMinimumSize().width;
			if(width < 100) {
				width = 100;
			}
			int height = 25;
			
			for(Object child : pageEditPart.getChildren()) {
				if(!(child instanceof AbstractGraphicalEditPart)) {
					continue;
				}
	
				AbstractGraphicalEditPart editPart = (AbstractGraphicalEditPart) child;
				height += 5 + editPart.getFigure().getBounds().height;
				width = Math.max(width, editPart.getFigure().getBounds().width);
			}
			width = width + 15; //some extra space
	
			PageResizeCommand resizeCmd = new PageResizeCommand();
			resizeCmd.setPage(page);
			resizeCmd.setOldDimension(page.getDimension());
			resizeCmd.setNewDimension(new Dimension(width, height));
			testEditor.getCommandStack().execute(resizeCmd);

			MovePageCommand moveCmd = new MovePageCommand();
			moveCmd.setPage(page);
			moveCmd.setOldPosition(page.getPosition());
			
			Point position = page.getPosition().getCopy();
			position.y = topCenter.y;
			position.x = topCenter.x - width / 2;
			moveCmd.setNewPosition(position);
			
			testEditor.getCommandStack().execute(moveCmd);
		}
		
		int bottom = node.getPosition().y + node.getDimension().height;

		for(Transition t : node.getOutTransitions()) {
			int numOfActions = 0;
			if (t instanceof UserInteractionsTransition) {
				numOfActions = ((UserInteractionsTransition) t).getUserInteractions().size();
			}
			layout(t.getEnd(), new Point(topCenter.x, bottom + TRANSITION_SPACE + (numOfActions * USER_INPUT_HEIGHT)));
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
		Iterator iter = this.getTestEditPart().getChildren().iterator();
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
