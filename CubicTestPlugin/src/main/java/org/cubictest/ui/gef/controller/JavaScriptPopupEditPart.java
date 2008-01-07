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

import org.cubictest.model.popup.JavaScriptPopup;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.cubictest.ui.gef.view.CubicTestLabel;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.IFigure;

public class JavaScriptPopupEditPart extends AbstractPageEditPart {
	
	public JavaScriptPopupEditPart(JavaScriptPopup popup) {
		setModel(popup);
	}

	@Override
	public JavaScriptPopup getModel() {
		return (JavaScriptPopup) super.getModel();
	}
	
	@Override
	protected IFigure createFigure() {
		CubicTestGroupFigure figure = (CubicTestGroupFigure) super.createFigure();
		CubicTestLabel header = figure.getHeader();
		header.setBackgroundColor(ColorConstants.black);
		header.setForegroundColor(ColorConstants.white);
		figure.setTooltipText(getType() + ": $labelText\n");
		return figure;
	}

	@Override
	protected String getType() {
		return getModel().getType();
	}
}
