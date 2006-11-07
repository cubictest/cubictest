/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.parameterization;

import org.cubictest.model.parameterization.ParamMapper;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;


public class ParamsTableCellModifier implements ICellModifier {

private ParamsEditor editor;
	
	public ParamsTableCellModifier(ParamsEditor editor) {
		this.editor = editor;
	}
	
	public boolean canModify(Object element, String property) {
		return true;
	}

	public Object getValue(Object element, String property) {
		int index = editor.getHeaders().indexOf(property);
		ParamMapper mapper = (ParamMapper) element;
		return mapper.get(index); 
	}

	public void modify(Object element, String property, Object value) {
		int index = editor.getHeaders().indexOf(property);
		TableItem item = (TableItem) element;
		ParamMapper mapper = (ParamMapper) item.getData();
		
		mapper.set((String) value, index);
		
		editor.setDirty();
		editor.getTableViewer().update(mapper, null);
	}

}
