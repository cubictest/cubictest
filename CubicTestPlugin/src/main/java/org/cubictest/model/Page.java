/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Page/State that can contain page elements.
 * 
 * @author skyttere
 *
 */
public class Page extends AbstractPage {

	private Page parent = null;
	private List<Page> children = new ArrayList<Page>();
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
		List<CommonTransition> oldCommons = this.commons;
		this.commons = commons;
		firePropertyChange(PropertyAwareObject.INPUT,oldCommons,commons);
	}

	/**
	 * @param transition
	 */
	public void removeCommonTransition(CommonTransition transition) {
		commons.remove(transition);
		firePropertyChange(PropertyAwareObject.INPUT,transition,null);
	}
	

	@Override
	public ArrayList<Link> getLinkElements() {
		ArrayList<Link> linkElements = super.getLinkElements();
		for (CommonTransition trans : commons){
			Common common =(Common)trans.getStart();
			linkElements.addAll(common.getLinkElements());
		}		
		return linkElements;
	}

	public void setParent(Page parent) {
		Page oldParent = this.parent;
		this.parent = parent;
		firePropertyChange(PropertyAwareObject.PARENT,oldParent,parent);
	}

	public Page getParent() {
		return parent;
	}

	public void setChildren(List<Page> children) {
		List<Page> oldChildren = this.children;
		this.children = children;
		firePropertyChange(PropertyAwareObject.INPUT,oldChildren,children);
	}

	public List<Page> getChildren() {
		return children;
	}
}
