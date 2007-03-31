/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

/**
 * @author skyttere
 *
 */
public class UrlStartPoint extends ConnectionPoint {
	private String beginAt = "";
	
	/**
	 * @return Returns the beginAt.
	 */
	public String getBeginAt() {
		return beginAt;
	}
	/**
	 * @param beginAt The beginAt to set.
	 */
	public void setBeginAt(String beginAt) {
		String oldBeginAt = this.beginAt;
		this.beginAt = beginAt;
		firePropertyChange(VALUE, oldBeginAt, beginAt);
	}
	
	@Override
	public void setInTransition(Transition inTransition) {
		inTransition.getStart().removeOutTransition(inTransition);
	}
	
	@Override
	public String toString() {
		return "URL: " + getBeginAt();
	}
}
