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

import static org.cubictest.model.ActionType.SELECT;
import static org.cubictest.model.IdentifierType.INDEX;
import static org.cubictest.model.IdentifierType.LABEL;
import static org.cubictest.model.IdentifierType.SELECTED;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ActionType;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;

/**
 * An option in a Select list.
 * 
 * @author Christian Schwarz
 * 
 */
public class Option extends Checkable{

	private Select parent;
	
	@Override
	public String getType() {
		return "Option";
	}
	@Override
	public List<IdentifierType> getIdentifierTypes() {	
		List<IdentifierType> list = new ArrayList<IdentifierType>();
		list.add(LABEL);
		list.add(IdentifierType.VALUE);
		list.add(SELECTED);
		list.add(INDEX);
		list.add(IdentifierType.ID); //Watir may have problems with ID
		return list;
	}
	
	
	@Override
	public List<ActionType> getActionTypes() {
		List<ActionType> actions = new ArrayList<ActionType>();
		actions.add(SELECT);
		return actions;
	}

	public Select getParent() {
		return parent;
	}

	public void setParent(Select parent) {
		this.parent = parent;
	}
	
	@Override
	protected void setDefaultIdentifierValues() {
		getIdentifiers().get(0).setProbability(Identifier.MAX_PROBABILITY);
	}

}
