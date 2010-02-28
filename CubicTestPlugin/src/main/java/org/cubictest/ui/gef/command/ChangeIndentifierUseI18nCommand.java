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

public class ChangeIndentifierUseI18nCommand extends Command {

	private boolean newUseI18n;
	private boolean oldUseI18n;
	private Identifier identifier;
	private Test test;
	public void setNewUseI18n(boolean newUseI18n) {
		this.newUseI18n = newUseI18n;
	}
	public void setOldUseI18n(boolean oldUseI18n) {
		this.oldUseI18n = oldUseI18n;
	}
	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}
	public void setTest(Test test) {
		this.test = test;
	}
	@Override
	public void execute() {
		super.execute();
		identifier.setUseI18n(newUseI18n);
		fixListener(newUseI18n);
	}
	@Override
	public void undo() {
		super.undo();
		identifier.setUseI18n(oldUseI18n);
		fixListener(oldUseI18n);
	}
	
	private void fixListener(boolean addListener){
		if(addListener)
			test.getAllLanguages().addObserver(identifier);
		else{
			if(test.getAllLanguages() != null)
				test.getAllLanguages().removeObserver(identifier);
		}
		test.updateObservers();
	}
	
}
