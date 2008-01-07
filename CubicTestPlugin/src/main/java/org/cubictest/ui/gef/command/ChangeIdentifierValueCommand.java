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

public class ChangeIdentifierValueCommand extends Command {

	private String newValue;
	private String oldValue;
	private Identifier identifier;
	private boolean oldUseParam;
	private boolean oldUseI18n;

	public void setIdentifer(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;	
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	@Override
	public void execute() {
		super.execute();
		identifier.setValue(newValue);
		oldUseI18n = identifier.useI18n();
		identifier.setUseI18n(false);
		oldUseParam = identifier.useParam();
		identifier.setUseParam(false);
	}
	@Override
	public void undo() {
		super.undo();
		identifier.setValue(oldValue);
		identifier.setUseI18n(oldUseI18n);
		identifier.setUseParam(oldUseParam);
	}
}
