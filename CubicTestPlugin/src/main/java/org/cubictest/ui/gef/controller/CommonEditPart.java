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
package org.cubictest.ui.gef.controller;

import org.cubictest.model.Common;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;


/**
 * @author SK Skytteren
 *
 *	Controller for the <code>Common</code> model. 
 */
public class CommonEditPart extends AbstractPageEditPart {

	/**
	 * Constructor for the <code>CommonEditPart</code>.
	 * @param common the model which this class is a controller for.
	 */
	public CommonEditPart(Common common) {
		setModel(common);
	}

	@Override
	protected String getType() {
		return "Common";
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		CubicTestGroupFigure figure = (CubicTestGroupFigure) super.createFigure();
		figure.getHeader().setBackgroundColor(ColorConstants.lightGray);
		figure.setTooltipText("Common: $labelText\nContains elements common to several pages/states");
		return figure;
	}

}
