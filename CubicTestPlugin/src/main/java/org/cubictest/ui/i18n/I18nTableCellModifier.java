/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */

package org.cubictest.ui.i18n;

import org.eclipse.jface.viewers.ICellModifier;

/**
 */
public class I18nTableCellModifier extends Object implements ICellModifier {
	private I18nEditor editor;
	
	public I18nTableCellModifier(I18nEditor editor) {
		this.editor = editor;
	}
	
	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		return null;
	}

	public void modify(Object element, String property, Object value) {
		editor.setDirty();
		editor.getTableViewer().update(element, null);
	}
}
