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
package org.cubictest.model.context;

import static org.cubictest.model.IdentifierType.CLASS;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.*;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.IdentifierType;

/**
 * Generic context element, default implementation of AbstractContext.
 * Can be any parent HTML element.
 * 
 * @author Christian Schwarz
 *
 */
public class Frame extends AbstractContext {
	
	@Override
	public String getType(){
		return "Frame";
	}
	
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(IdentifierType.FRAME_TYPE);
		list.add(ID);
		list.add(IdentifierType.NAME);
		list.add(IdentifierType.SRC);
		list.add(INDEX);
		list.add(CLASS);
		return list;
	}
	

	@Override
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(ActionType.MOUSE_OVER);
		actions.add(ActionType.MOUSE_OUT);
		return actions;
	}
	
	@Override
	public ActionType getDefaultAction() {
		return ActionType.MOUSE_OVER;
	}
}
