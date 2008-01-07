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
package org.cubictest.ui.gef.command;

import org.cubictest.model.Identifier;
import org.eclipse.gef.commands.Command;

public class ChangeIdentiferValueToActualCommand extends Command {

	private String value;
	private String actual;
	private Identifier identifier;

	public void setValue(String value) {
		this.value = value;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	@Override
	public void execute() {
		super.execute();
		identifier.setValue(actual);
	}
	
	@Override
	public void undo() {
		super.undo();
		identifier.setValue(value);
	}
}
