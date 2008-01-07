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
package org.cubictest.model;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a collection of PageElementActions that the user preforms to go from one application state to the next.
 * 
 * A typical UserInteractionsTransition object contains several UserInteractions that e.g. fills out a form or presses a link.
 *  
 * @author chr_schwarz
 * @author SK Skytteren  
 */
public class UserInteractionsTransition extends Transition {

	private List<UserInteraction> userInteractions = new ArrayList<UserInteraction>();
	private AbstractPage page;
	
	public UserInteractionsTransition() {}
	
	/**
	 * Construct a new UserInteractionsTransition.
	 * @param start the start node
	 * @param end the end node
	 */
	public UserInteractionsTransition(TransitionNode start, TransitionNode end) {
		super(start, end);
	}

	/**
	 * Get the page the user interactions are applied to.
	 * @return the page the user interactions are applied to.
	 */
	public AbstractPage getPage() {
		return page;
	}
	
	/**
	 * Set the page the user interactions are applied to.
	 * @param page the page the actions are applied to.
	 */
	public void setPage(AbstractPage page) {
		AbstractPage oldPage = this.page;
		this.page = page;
		firePropertyChange(PropertyAwareObject.CHILD, oldPage, page);
	}
	
	/**
	 * Get the collection of user interactions that constitutes this user interactions transition.
	 * @return the collection of actions that constitutes this user interactions transition.
	 */
	public List<UserInteraction> getUserInteractions() {
		return userInteractions;
	}
	
	/**
	 * Set the collection of user interactions that constitutes this user interactions transition.
	 * @param userInteractions the collection of actions that constitutes this user interactions transition.
	 */
	public void setUserInteractions(List<UserInteraction> userInteractions) {
		this.userInteractions = userInteractions;
		for(int i = 0 ; i < getListeners().getPropertyChangeListeners().length; i++ ) {
			for (int j = 0; j < userInteractions.size(); j++) {
				UserInteraction userInteraction = (UserInteraction)userInteractions.get(j); 
				userInteraction.addPropertyChangeListener(getListeners().getPropertyChangeListeners()[i]);
				firePropertyChange(CHILD, null, userInteraction);
			}
		}
	}
	
	/**
	 * Add user interaction to collection of user interactions.
	 * @param index the index
	 * @param userInteraction the user interaction to add.
	 */
	public void addUserInteraction(int index, UserInteraction userInteraction){
		userInteractions.add(index, userInteraction);
		for(int i = 0 ; i < getListeners().getPropertyChangeListeners().length; i++ )
			userInteraction.addPropertyChangeListener(
					getListeners().getPropertyChangeListeners()[i]);
		firePropertyChange(CHILD, null, userInteraction);
	}

	/**
	 * Add user interaction to collection of user interactions.
	 * @param userInteraction the user interaction to add.
	 */
	public void addUserInteraction(UserInteraction userInteraction){
		addUserInteraction(userInteractions.size(), userInteraction);
	}
	
	/**
	 * Remove user interaction from collection of user interactions.
	 * @param userInteraction the user interaction to remove.
	 */
	public void removeUserInteraction(UserInteraction userInteraction){
		userInteractions.remove(userInteraction);
		firePropertyChange(CHILD, null, userInteraction);
	}

	/**
	 * Utility method for checking whether there are any user interactions.
	 * @return
	 */
	public boolean hasUserInteractions() {
		for(UserInteraction action : getUserInteractions()) {
			if(action.getActionType() != ActionType.NO_ACTION) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.cubictest.model.PropertyAwareObject#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	@Override
	public void addPropertyChangeListener(PropertyChangeListener l) {
		super.addPropertyChangeListener(l);
		if (userInteractions == null) return;
		for (int i = 0; i < userInteractions.size() ; i++)
			((UserInteraction)userInteractions.get(i)).addPropertyChangeListener(l);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.cubictest.model.PropertyAwareObject#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	@Override
	public void removePropertyChangeListener(PropertyChangeListener l) {
		super.removePropertyChangeListener(l);
		if (userInteractions == null) return;
		for (int i = 0; i < userInteractions.size() ; i++)
			((UserInteraction)userInteractions.get(i)).removePropertyChangeListener(l);
	}

	public void cleanUpEmptyUserInteractions() {
		List<UserInteraction> realActions = new ArrayList<UserInteraction>(); 
		for(UserInteraction input : userInteractions) {
			if (input != null && !input.getActionType().equals(ActionType.NO_ACTION)) {
				realActions.add(input);
			}
		}
		this.userInteractions = realActions;
	}
}
