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

import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
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
	private Page page;

	@Override
	public void execute() {
		super.execute();
		
		if (page != null) {
			Point position = page.getPosition().getCopy();
			position.x = position.x + this.page.getDimension().width + 50;
			position.y = position.y + this.page.getDimension().height / 2;
			extensionPoint.setPosition(position);
		}
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

	public void setPage(Page page) {
		this.page = page;
	}

	
	public void setExtensionPoint(ExtensionPoint extensionPoint) {
		this.extensionPoint = extensionPoint;
	}

}
