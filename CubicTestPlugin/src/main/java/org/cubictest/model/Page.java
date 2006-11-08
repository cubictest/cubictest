/*
 * Created on Apr 20, 2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author skyttere
 *
 */
public class Page extends AbstractPage {

	private List<CommonTransition> commons = new ArrayList<CommonTransition>();
	
	public void addCommonTransition(CommonTransition transition){
		commons.add(transition);
		firePropertyChange(PropertyAwareObject.INPUT,null,transition);
	}
	
	/**
	 * @return Returns the commons.
	 */
	public List<CommonTransition> getCommonTransitions() {
		return commons;
	}
	
	/**
	 * @param commons The commons to set.
	 */
	public void setCommonTransitions(List<CommonTransition> commons) {
		this.commons = commons;
	}

	/**
	 * @param transition
	 */
	public void removeCommonTransition(CommonTransition transition) {
		commons.remove(transition);
		firePropertyChange(PropertyAwareObject.INPUT,transition,null);
	}
	
	@Override
	public Page clone() throws CloneNotSupportedException {
		Page clone = (Page)super.clone();
		clone.setCommonTransitions(new ArrayList<CommonTransition>());
		return clone;
	}

	public ArrayList<Link> getLinkElements() {
		ArrayList<Link> linkElements = super.getLinkElements();
		for (CommonTransition trans : commons){
			Common common =(Common)trans.getStart();
			linkElements.addAll(common.getLinkElements());
		}		
		return linkElements;
	}
}
