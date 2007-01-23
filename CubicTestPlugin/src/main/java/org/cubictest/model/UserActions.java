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
 * A typical UserActions object contains PageElementActions that fills out a form or presses a link.
 *  
 * @author chr_schwarz
 * @author SK Skytteren  
 */
public class UserActions extends Transition {

	private List<PageElementAction> inputs = new ArrayList<PageElementAction>();
	private AbstractPage page;
	
	/**
	 * Construct a new UserActions.
	 * @param start the start node
	 * @param end the end node
	 */
	public UserActions(TransitionNode start, TransitionNode end) {
		super(start, end);		
	}

	/**
	 * Get the page the actions are applied to.
	 * @return the page the actions are applied to.
	 */
	public AbstractPage getPage() {
		return page;
	}
	
	/**
	 * Set the page the actions are applied to.
	 * @param page the page the actions are applied to.
	 */
	public void setPage(AbstractPage page) {
		AbstractPage oldPage = this.page;
		this.page = page;
		firePropertyChange(PropertyAwareObject.CHILD, oldPage, page);
	}
	
	/**
	 * Get the collection of actions that constitutes this user interactions transition.
	 * @return the collection of actions that constitutes this user interactions transition.
	 */
	public List<PageElementAction> getInputs() {
		return inputs;
	}
	
	/**
	 * Set the collection of actions that constitutes this user interactions transition.
	 * @param inputs the collection of actions that constitutes this user interactions transition.
	 */
	public void setInputs(List<PageElementAction> inputs) {
		this.inputs = inputs;
		for(int i = 0 ; i < getListeners().getPropertyChangeListeners().length; i++ )
			for (int j = 0; j < inputs.size(); j++)
				((PageElementAction)inputs.get(j)).addPropertyChangeListener(
							getListeners().getPropertyChangeListeners()[i]);
	}
	
	/**
	 * Add action to collection of actions.
	 * @param input the action to add.
	 */
	public void addInput(PageElementAction input){
		inputs.add(input);
		for(int i = 0 ; i < getListeners().getPropertyChangeListeners().length; i++ )
			input.addPropertyChangeListener(
					getListeners().getPropertyChangeListeners()[i]);
		firePropertyChange(CHILD, null, input);
	}
	/**
	 * Remove action from collection of actions.
	 * @param input the action to remove.
	 */
	public void removeInput(PageElementAction input){
		inputs.remove(input);
		firePropertyChange(CHILD, null, input);
	}

	
	/**
	 * Utility method for checking whether there are any actions.
	 * @return
	 */
	public boolean hasNoActions() {
		for(PageElementAction action : getInputs()) {
			if(action.getAction() != ActionType.NO_ACTION) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.cubictest.model.PropertyAwareObject#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		super.addPropertyChangeListener(l);
		if (inputs == null) return;
		for (int i = 0; i < inputs.size() ; i++)
			((PageElementAction)inputs.get(i)).addPropertyChangeListener(l);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.cubictest.model.PropertyAwareObject#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		super.removePropertyChangeListener(l);
		if (inputs == null) return;
		for (int i = 0; i < inputs.size() ; i++)
			((PageElementAction)inputs.get(i)).removePropertyChangeListener(l);
	}

}
