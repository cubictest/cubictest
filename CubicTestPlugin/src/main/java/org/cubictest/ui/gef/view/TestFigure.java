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
package org.cubictest.ui.gef.view;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ScalableFreeformLayeredPane;

/**
 * @author SK Skytteren
 * 
 */
public class TestFigure extends ScalableFreeformLayeredPane {

	Label nameLabel;
	
	public TestFigure() {
		setOpaque(true);
		setBackgroundColor(ColorConstants.white);
		FreeformLayout layout = new FreeformLayout();
		setLayoutManager(layout);
	}
	
	public void addNameLabel(Label label) {
		this.nameLabel = label;
		add(label);
	}

	public Label getNameLabel() {
		return nameLabel;
	}
}
