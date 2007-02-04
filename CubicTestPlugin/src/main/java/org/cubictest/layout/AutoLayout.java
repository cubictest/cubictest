package org.cubictest.layout;

import java.util.List;

import org.cubictest.model.Page;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class AutoLayout {
	private final TestEditPart testEditPart;

	public AutoLayout(TestEditPart testEditPart) {
		this.testEditPart = testEditPart;
	}

	public void layout(TransitionNode node) {
		Point position = node.getPosition().getCopy();
		try {
			if(!(node.getInTransition().getStart() instanceof UrlStartPoint)) {
				position.x = node.getInTransition().getStart().getPosition().x + node.getInTransition().getStart().getDimension().width / 2;
				position.y = node.getInTransition().getStart().getPosition().y + node.getInTransition().getStart().getDimension().height + 100;							
			} else {				
				position.x += node.getDimension().width / 2;
			}
		} catch(NullPointerException e) {
			position.x += node.getDimension().width / 2;
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
			page.setDimension(new Dimension(width, height));
			Point position = page.getPosition().getCopy();
			position.y = topCenter.y;
			position.x = topCenter.x - width / 2;
			page.setPosition(position);
		}
		
		int bottom = node.getPosition().y + node.getDimension().height;

		for(Transition t : node.getOutTransitions()) {
			layout(t.getEnd(), new Point(topCenter.x, bottom+75));
		}
		
	}
	
	private AbstractEditPart findTransitionNodeEditPart(TransitionNode transitionNode) {
		for(AbstractEditPart child : (List<AbstractEditPart>) this.testEditPart.getChildren()) {
			if(child.getModel() == transitionNode) {
				return child;
			}
		}
		return null;
	}
}
