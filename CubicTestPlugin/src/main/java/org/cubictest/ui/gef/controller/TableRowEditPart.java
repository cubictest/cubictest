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

import org.cubictest.model.context.AbstractContext;
import org.cubictest.ui.gef.view.CubicTestGroupFigure;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;

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
		figure.setTooltipText("Check table row present: $labelText");
		figure.getHeader().setIcon(CubicTestImageRegistry.get(CubicTestImageRegistry.ROW_IMAGE));
		return figure;
	}
}
