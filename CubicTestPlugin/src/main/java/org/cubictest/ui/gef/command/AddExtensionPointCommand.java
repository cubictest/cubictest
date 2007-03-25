/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

/**
 * Command for adding an extension point.
 *
 * @author Christian Schwarz
 */
public class AddExtensionPointCommand extends Command {

	private Test test;
	private ExtensionPoint extensionPoint;
	private PageEditPart pageEditPart;

	@Override
	public void execute() {
		super.execute();
		Page page = (Page) pageEditPart.getModel();
		
		for (Transition transition : page.getOutTransitions()) {
			if(transition.getEnd() instanceof ExtensionPoint) {
				return;
			}
		}
		

		Point position = page.getPosition().getCopy();
		position.x = position.x + this.pageEditPart.getContentPane().getClientArea().width + 30;
		position.y = position.y + this.pageEditPart.getContentPane().getClientArea().height / 2;
		
		extensionPoint.setPosition(position);
		extensionPoint.setName(page.getName() + " Extension Point");
		test.addExtensionPoint(extensionPoint);
	}

	@Override
	public void undo() {
		super.undo();
		if(extensionPoint != null) {
			test.removeExtensionPoint(extensionPoint);
		}
	}

	@Override
	public void redo() {
		super.redo();
	}

	
	public void setTest(Test test) {
		this.test = test;
	}

	public void setPageEditPart(PageEditPart pageEditPart) {
		this.pageEditPart = pageEditPart;
	}

	
	public void setExtensionPoint(ExtensionPoint extensionPoint) {
		this.extensionPoint = extensionPoint;
	}	
}
