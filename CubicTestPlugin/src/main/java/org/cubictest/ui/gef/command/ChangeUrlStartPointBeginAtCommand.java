/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
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
