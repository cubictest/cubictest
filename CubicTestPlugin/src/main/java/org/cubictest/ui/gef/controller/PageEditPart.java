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

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Page;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.view.CubicTestScrollableGroupFigure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;


/**
 * @author SK Skytteren
 *
 * Controller for the <code>Page</code> model
 */
public class PageEditPart extends AbstractPageEditPart {

	/**
	 * Constructor for the <code>PageEditPart</code> controller.
	 * @param page the model
	 */
	public PageEditPart(Page page) {
		setModel(page);
	}
	
	@Override
	protected String getType() {
		return "Page";
	}

	protected IFigure createFigure() {
		CubicTestScrollableGroupFigure figure = (CubicTestScrollableGroupFigure) super.createFigure();
		figure.getHeader().setBackgroundColor(new Color(null, 172, 170, 255));
		String name = ((AbstractPage)getModel()).getName();
		figure.setTooltipText("Page/state: " + name);
		return figure;
	}

	protected List getModelTargetConnections() {
		Transition trans = ((TransitionNode)getModel()).getInTransition();
		List<Transition> list = new ArrayList<Transition>();
		if (trans != null)
			list.add(((TransitionNode)getModel()).getInTransition());
			list.addAll(((Page)getModel()).getCommonTransitions());
		return list;
	}
}
