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
import org.cubictest.model.PageElement;
import org.eclipse.gef.commands.Command;


public class ChangePageElementTextCommand extends Command {

	private PageElement element;
	private String oldText, newText;
	private Identifier identifier;
	private boolean oldUseI18n;
	private boolean oldUseParam;
	
	public void setPageElement(PageElement element) {
		this.element = element;
	}
	public void setOldText(String oldText) {
		this.oldText = oldText;
	}
	public void setNewText(String newText) {
		this.newText = newText;
	}
	
	@Override
	public void execute() {
		super.execute();
		element.setText(newText);
		identifier = element.getDirectEditIdentifier();
		oldUseI18n = identifier.useI18n();
		oldUseParam = identifier.useParam();
		identifier.setUseI18n(false);
		identifier.setUseParam(false);
	}
	
	@Override
	public void undo() {
		super.undo();
		element.setText(oldText);
		identifier.setUseI18n(oldUseI18n);
		identifier.setUseParam(oldUseParam);
	}
}
