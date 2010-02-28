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
package org.cubictest.ui.utils;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;

public class DoubleClickableTextCellEditor extends TextCellEditor {
	
	public DoubleClickableTextCellEditor(Table parent, int style) {
		super(parent, style);
	}
	
	public DoubleClickableTextCellEditor(Table parent) {
		super(parent);
	}
	
	@Override
	protected int getDoubleClickTimeout() {
		return 0;
	}
}
