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

import java.beans.PropertyChangeEvent;

import org.cubictest.model.CommonTransition;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;


/**
 * @author SK Skyttere
 *
 *	Controller for the <code>CommonTransition</code> model.
 * 
 */
public class CommonTransitionEditPart extends TransitionEditPart {

	/**
	 * Constructor for <code>CommonTransitionEditPart</code>.
	 * @param transition the model.
	 */
	public CommonTransitionEditPart(CommonTransition transition) {
		super(transition);
	}
	@Override
	protected IFigure createFigure() {
		IFigure fig = super.createFigure();
		fig.setForegroundColor(ColorConstants.gray);
		fig.setToolTip(new Label("Connection that adds all elements from the Common to the target page"));
		return fig;
	}


	public void propertyChange(PropertyChangeEvent arg0) {
		refreshVisuals();
	}

}
