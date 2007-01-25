/*
 * Created on March 15, 2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
*/
package org.cubictest.model;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a collection of PageElementActions that the user preforms to go from one application state to the next.
 * 
 * A typical UserInteractionsTransition object contains PageElementActions that fills out a form or presses a link.
 *  
 * @author chr_schwarz
 * @author SK Skytteren  
 */
public class UserInteractionsTransition extends Transition {

	private List<UserInteraction> inputs = new ArrayList<UserInteraction>();
	private AbstractPage page;
	
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
		return inputs;
	}
	
	/**
	 * Set the collection of user interactions that constitutes this user interactions transition.
	 * @param userInteractions the collection of actions that constitutes this user interactions transition.
	 */
	public void setUserInteractions(List<UserInteraction> userInteractions) {
		this.inputs = userInteractions;
		for(int i = 0 ; i < getListeners().getPropertyChangeListeners().length; i++ )
			for (int j = 0; j < userInteractions.size(); j++)
				((UserInteraction)userInteractions.get(j)).addPropertyChangeListener(
							getListeners().getPropertyChangeListeners()[i]);
	}
	
	/**
	 * Add user interaction to collection of user interactions.
	 * @param userInteraction the user interaction to add.
	 */
	public void addUserInteraction(UserInteraction userInteraction){
		inputs.add(userInteraction);
		for(int i = 0 ; i < getListeners().getPropertyChangeListeners().length; i++ )
			userInteraction.addPropertyChangeListener(
					getListeners().getPropertyChangeListeners()[i]);
		firePropertyChange(CHILD, null, userInteraction);
	}
	
	/**
	 * Remove user interaction from collection of user interactions.
	 * @param input the user interaction to remove.
	 */
	public void removeUserInteraction(UserInteraction input){
		inputs.remove(input);
		firePropertyChange(CHILD, null, input);
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
	public void addPropertyChangeListener(PropertyChangeListener l) {
		super.addPropertyChangeListener(l);
		if (inputs == null) return;
		for (int i = 0; i < inputs.size() ; i++)
			((UserInteraction)inputs.get(i)).addPropertyChangeListener(l);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.cubictest.model.PropertyAwareObject#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		super.removePropertyChangeListener(l);
		if (inputs == null) return;
		for (int i = 0; i < inputs.size() ; i++)
			((UserInteraction)inputs.get(i)).removePropertyChangeListener(l);
	}

	public void cleanUpEmptyUserInteractions() {
		List<UserInteraction> realActions = new ArrayList<UserInteraction>(); 
		for(UserInteraction input : inputs) {
			if (input != null && !input.getActionType().equals(ActionType.NO_ACTION)) {
				realActions.add(input);
			}
		}
		this.inputs = realActions;
	}
}
