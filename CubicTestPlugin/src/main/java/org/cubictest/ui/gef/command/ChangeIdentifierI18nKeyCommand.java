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
import org.cubictest.model.Test;
import org.eclipse.gef.commands.Command;

public class ChangeIdentifierI18nKeyCommand extends Command{

	private Identifier identifier;
	private String newI18nKey;
	private String oldKey;
	private Test test;

	public void setIndentifier(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public void setNewI18nKey(String newKey) {
		this.newI18nKey = newKey;
	}

	public void setOldI18nKey(String oldKey) {
		this.oldKey = oldKey;	
	}
	
	@Override
	public void execute() {
		super.execute();
		identifier.setI18nKey(newI18nKey);
		test.updateObservers();
	}
	
	@Override
	public void undo() {
		super.undo();
		identifier.setI18nKey(oldKey);
		test.updateObservers();
	}

	public void setTest(Test test) {
		this.test = test;
	}
}
