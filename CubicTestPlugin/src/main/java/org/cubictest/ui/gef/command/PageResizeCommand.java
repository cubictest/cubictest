/*
 * Created on 12.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.TransitionNode;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;

/**
 * A command that resizes a <code>Page</code> to a new width and height.
 * @author chr_schwarz
 */
public class PageResizeCommand extends Command {

	private Dimension newDimension;
	private Dimension oldDimension;
	
	private int heightToAdd;
	private boolean addHeightMode;
	
	private TransitionNode node;

	/**
	 * @param page
	 */
	public void setNode(TransitionNode node) {
		this.node = node;
	}

	/**
	 * @param dimension
	 */
	public void setOldDimension(Dimension dimension) {
		this.oldDimension = dimension;		
	}

	/**
	 * @param dimension
	 */
	public void setNewDimension(Dimension dimension) {
		this.newDimension = dimension;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		if (addHeightMode) {
			newDimension = node.getDimension().getCopy();
			newDimension.height = newDimension.height + heightToAdd;
		}
		node.setDimension(newDimension);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		node.setDimension(oldDimension);
	}

	public void setHeightToAdd(int heightToAdd) {
		this.heightToAdd = heightToAdd;
	}

	public void addHeightMode(boolean addHeightMode) {
		this.addHeightMode = addHeightMode;
	}
}
