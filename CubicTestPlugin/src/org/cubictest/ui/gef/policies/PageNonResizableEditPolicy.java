/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;

/**
 * @author SK Skytteren
 * 
 */
public class PageNonResizableEditPolicy extends NonResizableEditPolicy {
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#getCommand(org.eclipse.gef.Request)
	 */
	public Command getCommand(Request request) {
		return super.getCommand(request);
	}
}
