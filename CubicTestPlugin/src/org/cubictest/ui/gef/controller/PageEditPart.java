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

import org.cubictest.model.Page;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;


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
		CubicTestGroupFigure figure = (CubicTestGroupFigure) super.createFigure();
		figure.getHeader().setBackgroundColor(ColorConstants.lightBlue);
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
