/*
 * Created on 15.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.factory;

import org.eclipse.gef.EditPart;
import org.eclipse.ui.views.properties.tabbed.AbstractTypeMapper;

public class TestEditorTypeMapper extends AbstractTypeMapper {

	public Class mapType(Object object) {
		if(object instanceof EditPart)
			return ((EditPart)object).getModel().getClass();
		return super.mapType(object);
	}

}
