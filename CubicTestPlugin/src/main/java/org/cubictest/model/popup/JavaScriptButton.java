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
package org.cubictest.model.popup;

import static org.cubictest.model.ActionType.CLICK;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.formElement.Button;

public abstract class JavaScriptButton extends Button {

	@Override
	public abstract String getType();
	
	@Override
	public String getText() {
		return "";
	}
	
	@Override
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(CLICK);
		return actions;
	}

	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		return list;
	}
	
}
