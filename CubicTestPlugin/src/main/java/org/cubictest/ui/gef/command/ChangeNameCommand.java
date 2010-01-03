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

import org.cubictest.model.NamePropertyObject;
import org.eclipse.gef.commands.Command;


/**
 * Command for changing name property.
 * 
 * @author Christian Schwarz
 *
 */
public class ChangeNameCommand extends Command {

	private NamePropertyObject namePropertyObject;
	
	private String oldName;
	
	private String newName;

	
	public void setNewName(String newName) {
		this.newName = newName;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	public void setNamePropertyObject(NamePropertyObject namePropertyObject) {
		this.namePropertyObject = namePropertyObject;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		super.execute();
		namePropertyObject.setName(newName);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		namePropertyObject.setName(oldName);
	}
}
