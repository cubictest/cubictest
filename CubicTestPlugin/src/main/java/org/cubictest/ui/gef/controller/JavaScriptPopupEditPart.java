/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
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
