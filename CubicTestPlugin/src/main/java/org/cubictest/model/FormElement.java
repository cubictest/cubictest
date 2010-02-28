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
package org.cubictest.model;

import static org.cubictest.model.ActionType.BLUR;
import static org.cubictest.model.ActionType.FOCUS;
import static org.cubictest.model.IdentifierType.CLASS;
import static org.cubictest.model.IdentifierType.ID;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.TITLE;

import java.util.ArrayList;
import java.util.List;


/**
 * Base class for form elements.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public abstract class FormElement extends PageElement{

	
	@Override
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.addAll(super.getActionTypes());
		actions.add(FOCUS);
		actions.add(BLUR);
		return actions;
	}

	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(LABEL);
		list.add(IdentifierType.NAME);
		list.add(ID);
		list.add(TITLE);
		list.add(CLASS);
		return list;
	}
	
	@Override
	protected void setDefaultIdentifierValues() {
		//leave the ID's as the constructor made them
	}
}
