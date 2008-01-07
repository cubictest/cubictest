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

import org.cubictest.model.UrlStartPoint;
import org.eclipse.gef.commands.Command;

public class ChangeUrlStartPointBeginAtCommand extends Command {

	private String beginAt;
	private UrlStartPoint startPoint;
	private String oldBeginAt;

	public void setNewUrl(String beginAt) {
		this.beginAt = beginAt;
	}

	public void setStartpoint(UrlStartPoint urlStartPoint) {
		this.startPoint = urlStartPoint;
	}

	@Override
	public void execute() {
		oldBeginAt = startPoint.getBeginAt();
		startPoint.setBeginAt(beginAt);
	}
	
	@Override
	public void undo() {
		startPoint.setBeginAt(oldBeginAt);
	}
}
