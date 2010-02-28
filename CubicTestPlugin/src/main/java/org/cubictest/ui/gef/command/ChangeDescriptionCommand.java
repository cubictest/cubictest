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

import org.cubictest.model.IDescription;
import org.eclipse.gef.commands.Command;


public class ChangeDescriptionCommand extends Command {

	private IDescription description;
	private String oldDescription, newDescription;
	
	public void setDesctription(IDescription description) {
		this.description = description;
		
	}
	public void setOldDescription(String oldDescription) {
		this.oldDescription = oldDescription;
	}
	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}
	
	@Override
	public void execute() {
		super.execute();
		description.setDescription(newDescription);
	}
	
	@Override
	public void undo() {
		super.undo();
		description.setDescription(oldDescription);
	}

}
