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
