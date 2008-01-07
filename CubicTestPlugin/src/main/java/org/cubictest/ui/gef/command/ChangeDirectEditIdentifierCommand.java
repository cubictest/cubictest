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
import org.cubictest.model.PageElement;
import org.eclipse.gef.commands.Command;

/**
 * Changes the identifier used for direct edit of a page element.
 *  
 * @author Christian Schwarz
 */
public class ChangeDirectEditIdentifierCommand extends Command {

	private Identifier newIdentifier;
	private Identifier oldIdentifier;
	private PageElement pageElement;


	@Override
	public void execute() {
		super.execute();
		pageElement.setDirectEditIdentifier(newIdentifier);
	}
	@Override
	public void undo() {
		super.undo();
		pageElement.setDirectEditIdentifier(oldIdentifier);
	}

	
	public void setNewIdentifier(Identifier newIdentifier) {
		this.newIdentifier = newIdentifier;
	}

	public void setOldIdentifier(Identifier oldIdentifier) {
		this.oldIdentifier = oldIdentifier;
	}

	public void setPageElement(PageElement pageElement) {
		this.pageElement = pageElement;
	}
}
