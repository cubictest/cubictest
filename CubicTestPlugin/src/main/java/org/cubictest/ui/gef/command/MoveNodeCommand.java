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

import org.cubictest.model.TransitionNode;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;


/**
 * A command that moves a <code>Node</code> to a new location.
 * 
 * @author SK Skytteren
 */
public class MoveNodeCommand extends Command {

	private Point newPosition;
	private Point oldPosition;
	private TransitionNode node;

	/**
	 * @param page
	 */
	public void setNode(TransitionNode node) {
		this.node = node;
	}

	/**
	 * @param point
	 */
	public void setOldPosition(Point point) {
		this.oldPosition = point;		
	}

	/**
	 * @param point
	 */
	public void setNewPosition(Point point) {
		this.newPosition = point;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		node.setPosition(newPosition);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		node.setPosition(oldPosition);
	}
}
