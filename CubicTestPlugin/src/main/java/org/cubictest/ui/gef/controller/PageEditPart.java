/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.controller;

import java.util.ArrayList;
import java.util.List;

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
	
	@Override
	protected IFigure createFigure() {
		CubicTestScrollableGroupFigure figure = (CubicTestScrollableGroupFigure) super.createFigure();
		figure.getHeader().setBackgroundColor(new Color(null, 172, 170, 255));
		figure.setTooltipText("Page/state: $labelText");
		return figure;
	}

	@Override
	protected List<Transition> getModelTargetConnections() {
		Transition trans = ((TransitionNode)getModel()).getInTransition();
		List<Transition> list = new ArrayList<Transition>();
		if (trans != null)
			list.add(((TransitionNode)getModel()).getInTransition());
			list.addAll(((Page)getModel()).getCommonTransitions());
		return list;
	}
}
