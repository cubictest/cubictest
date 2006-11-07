/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

/**
 * @author skyttere
 */
public class CommonTransition extends Transition {

	/**
	 * @param start
	 * @param end
	 */
	public CommonTransition(Common start, Page end) {
		super(start, end);
	}

	public void connect() {
		((Common)getStart()).addOutTransition(this);
		((Page)getEnd()).addCommonTransition(this);
	}
	
	public void disconnect() {
		getStart().removeOutTransition(this);
		((Page)getEnd()).removeCommonTransition(this);
	}
}
