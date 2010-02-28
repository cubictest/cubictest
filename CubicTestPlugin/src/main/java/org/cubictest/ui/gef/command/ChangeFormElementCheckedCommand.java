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

import org.cubictest.model.formElement.Checkable;
import org.eclipse.gef.commands.Command;

/**
 * Command that operates on a Checkable type.
 * 
 * @author chr_schwarz
 */
public class ChangeFormElementCheckedCommand extends Command {

	private Checkable element;
	private boolean oldChecked, newChecked;
	
	public void setCheckable(Checkable element) {
		this.element = element;
		
	}
	public void setOldChecked(boolean oldChecked) {
		this.oldChecked = oldChecked;
	}
	
	public void setNewChecked(boolean newChecked) {
		this.newChecked = newChecked;
	}
	
	@Override
	public void execute() {
		super.execute();
		element.setChecked(newChecked);
	}
	
	@Override
	public void undo() {
		super.undo();
		element.setChecked(oldChecked);
	}

}
