/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.interfaces;

import org.cubictest.ui.gef.interfaces.exported.IDisposeListener;

public interface IDisposeSubject {
	public void addDisposeListener(IDisposeListener listener);
	
	public void removeDisposeListener(IDisposeListener listener);
}
