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
package org.cubictest.model.formElement;

import static org.cubictest.model.ActionType.CLEAR_ALL_TEXT;
import static org.cubictest.model.ActionType.ENTER_PARAMETER_TEXT;
import static org.cubictest.model.ActionType.ENTER_TEXT;
import static org.cubictest.model.ActionType.KEY_PRESSED;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.LABEL;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.FormElement;
import org.cubictest.model.IdentifierType;

/**
 * Base class for text input.
 * 
 * @author skyt
 * @author chr_schwarz
 */
public abstract class AbstractTextInput extends FormElement {

	@Override
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ENTER_TEXT);
		actions.add(ENTER_PARAMETER_TEXT);
		actions.add(KEY_PRESSED);
		actions.add(CLEAR_ALL_TEXT);
		actions.addAll(super.getActionTypes());
		return actions;
	}
	
	@Override
	public ActionType getDefaultAction() {
		return ActionType.ENTER_TEXT;
	}
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(LABEL);
		list.add(ID);
		list.add(IdentifierType.NAME);
		list.add(IdentifierType.VALUE);
		list.add(IdentifierType.TITLE);
		return list;
	}
	
}
