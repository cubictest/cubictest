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
import org.cubictest.model.Moderator;
import org.eclipse.gef.commands.Command;

public class ChangeIdentiferModeratorCommand extends Command{

	private Moderator newMod;
	private Moderator oldMod;
	private Identifier identifier;

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setNewModerator(Moderator newMod) {
		this.newMod = newMod;
	}

	public void setOldModerator(Moderator oldMod) {
		this.oldMod = oldMod;
	}

	@Override
	public void execute() {
		identifier.setModerator(newMod);
	}
	
	@Override
	public void undo() {
		identifier.setModerator(oldMod);
	}
}
