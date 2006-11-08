/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.parameterization;

import org.cubictest.model.parameterization.ParamMapper;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


public class ParamsLabelProvider extends LabelProvider implements ITableLabelProvider  {

	
	//private ParamsEditor editor;
	
	public ParamsLabelProvider(ParamsEditor editor) {
		//this.editor = editor;
	}
	
	
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		ParamMapper mapper = (ParamMapper) element;
		return mapper.get(columnIndex);
	}

}
