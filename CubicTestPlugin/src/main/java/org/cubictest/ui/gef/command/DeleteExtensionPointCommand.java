
/*
 * Created on 07.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.ExtensionPoint;


/**
 * @author Stein Kåre Skytteren
 *
 *
 * A command that deletes an <code>AbstractPage</code>.
 */
public class DeleteExtensionPointCommand extends DeleteTransitionNodeCommand {
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		super.execute();
		test.removeExtensionPoint((ExtensionPoint) transitionNode);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo(){
		super.undo();
		test.addExtensionPoint((ExtensionPoint) transitionNode);
	}
}
