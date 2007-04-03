/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.controller.formElement;

import org.cubictest.model.formElement.Checkbox;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.eclipse.swt.graphics.Image;


/**
 * @author chr_schwarz
 *
 * Controller for the <code>Checkbox</code> class. 
 */
public class FormCheckboxEditPart extends FormElementEditPart {

	/**
	 * Constructor for the <code>FormCheckboxEditPart</code>which is the 
	 * controller for the <code>Checkbox</code> class. 
	 * @param checkbox
	 */
	public FormCheckboxEditPart(Checkbox checkbox) {
		setModel(checkbox);
	}
	
	@Override
	protected Image getImage(boolean isNot) {
		if (getModel() instanceof Checkbox) {
			Checkbox box = (Checkbox)getModel();
			String key = box.isChecked() ? CubicTestImageRegistry.CHECKBOX_CHECKED_IMAGE : 
				CubicTestImageRegistry.CHECKBOX_UNCHECKED_IMAGE;
			return CubicTestImageRegistry.get(key,isNot);
		}
		return null;
	}
}
