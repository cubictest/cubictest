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

import org.cubictest.model.Test;
import org.eclipse.gef.commands.Command;


/**
 * Command for changing test description.
 * 
 * @author Christian Schwarz
 *
 */
public class ChangeTestDescriptionCommand extends Command {

	private Test test;
	
	private String oldDescription;
	
	private String newDescription;

	
	public void setNewDescription(String newDescription) {
		this.newDescription = newDescription;
	}

	public void setOldDescription(String oldDescription) {
		this.oldDescription = oldDescription;
	}

	public void setTest(Test test) {
		this.test = test;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		super.execute();
		test.setDescription(newDescription);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		test.setDescription(oldDescription);
	}
}
