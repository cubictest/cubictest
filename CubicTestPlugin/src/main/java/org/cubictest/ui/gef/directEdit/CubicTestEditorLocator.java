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
package org.cubictest.ui.gef.directEdit;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * @author SK Skytteren
 *
 * Locates the <code>Label</code> where the direct edit should be performed.
 */
public class CubicTestEditorLocator implements CellEditorLocator {

	private Label label;

	/**
	 * Constructor for <code>CubicTestEditorLocator</code>.
	 * @param label
	 */
	public CubicTestEditorLocator(Label label) {
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.tools.CellEditorLocator#relocate(org.eclipse.jface.viewers.CellEditor)
	 */
	public void relocate(CellEditor celleditor) {
		Control control = (Control) celleditor.getControl();

		Point pref = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Rectangle rect = label.getTextBounds().getCopy();
		label.translateToAbsolute(rect);
		//if (text.getCharCount() > 1)
		control.setBounds(rect.x - 1, rect.y - 1, pref.x + pref.y, pref.y + 1);
		//else
		//	text.setBounds(rect.x - 1, rect.y - 1, pref.y + 1, pref.y + 1);
	}

}
