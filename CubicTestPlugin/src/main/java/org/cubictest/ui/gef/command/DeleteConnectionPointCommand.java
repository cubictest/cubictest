/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.ConnectionPoint;

public class DeleteConnectionPointCommand extends DeleteTransitionNodeCommand {
	
	@Override
	public void execute() {
		super.execute();
		test.setStartPoint(null);
	}

	@Override
	public void undo() {
		super.undo();
		test.setStartPoint((ConnectionPoint) transitionNode);
	}
	
}
