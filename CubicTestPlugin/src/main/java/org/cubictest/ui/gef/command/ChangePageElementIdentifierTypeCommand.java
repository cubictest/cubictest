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
package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.eclipse.gef.commands.Command;

/**
 * Command that operates on a PageElements identifyer type.
 * 
 * @author chr_schwarz
 */
public class ChangePageElementIdentifierTypeCommand extends Command {

	private Identifier identifier;
	private IdentifierType oldIdentifierType, newIdentifierType;
	
	public void setIdenfier(Identifier identifier) {
		this.identifier = identifier;
		
	}
	
	public void setOldIdentifierType(IdentifierType oldIdentifierType) {
		this.oldIdentifierType = oldIdentifierType;
	}
	
	public void setNewIdentifierType(IdentifierType newIdentifierType) {
		this.newIdentifierType = newIdentifierType;
	}
	
	@Override
	public void execute() {
		super.execute();
		identifier.setType(newIdentifierType);
	}
	
	@Override
	public void undo() {
		super.undo();
		identifier.setType(oldIdentifierType);
	}

}
