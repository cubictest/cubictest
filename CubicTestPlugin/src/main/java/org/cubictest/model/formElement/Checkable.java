/*
 * Created on 26.jun.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.model.formElement;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.FormElement;
import org.cubictest.model.PropertyAwareObject;

/**
 * Class for checkable form elements.
 * @author chr_schwarz
 */
public abstract class Checkable extends FormElement {

	
	/**
	 * Default state is unchecked
	 */
	private boolean checked = false;

	/**
	 * @return Returns the checked.
	 */
	public boolean isChecked() {
		return checked;
	}
	/**
	 * @param not The checked to set.
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
		firePropertyChange(PropertyAwareObject.NAME, null, checked);
	}
	
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ActionType.CHECK);
		actions.add(ActionType.UNCHECK);
		actions.addAll(super.getActionTypes());
		return actions;
	}
}
