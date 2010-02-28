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
import org.eclipse.gef.commands.Command;

public class ChangeFrameTypeCommand extends Command{

	private Identifier identifier;
	private String frame;
	private String oldFrame;

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public void setFrame(String frame) {
		this.frame = frame;
	}

	public void setOldFrame(String oldFrame) {
		this.oldFrame = oldFrame;
	}
	
	@Override
	public void execute() {
		identifier.setValue(frame);
	}
	
	@Override
	public void undo() {
		identifier.setValue(oldFrame);
	}
}
