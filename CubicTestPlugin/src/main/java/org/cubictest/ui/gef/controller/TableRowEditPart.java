/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.controller;

import org.cubictest.model.context.AbstractContext;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;

/**
 * Editpart for the <code>Row</code> model object.
 * 
 * @author chr_schwarz 
 */
public class TableRowEditPart extends ContextEditPart {

	public TableRowEditPart(AbstractContext context) {
		super(context);
	}

	@Override
	protected CubicTestGroupFigure createFigure() {
		CubicTestGroupFigure figure = super.createFigure();
		figure.setTooltipText("Check table row present: " + getElementDescription());
		return figure;
	}
}
