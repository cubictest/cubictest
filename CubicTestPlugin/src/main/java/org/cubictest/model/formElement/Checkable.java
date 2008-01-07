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
package org.cubictest.model.formElement;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.FormElement;
import org.cubictest.model.PropertyAwareObject;

/**
 * Base class for checkable form elements.
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
	@Override
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ActionType.CHECK);
		actions.add(ActionType.UNCHECK);
		actions.addAll(super.getActionTypes());
		return actions;
	}
}
