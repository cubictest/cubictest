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
 * @author 
 */
public class UserActions extends Transition {

	private List<PageElementAction> inputs = new ArrayList<PageElementAction>();
	private AbstractPage page;
	
	/**
	 * @param start
	 * @param end
	 */
	public UserActions(TransitionNode start, TransitionNode end) {
		super(start, end);		
	}

	public AbstractPage getPage() {
		return page;
	}
	
	public void setPage(AbstractPage page) {
		AbstractPage oldPage = this.page;
		this.page = page;
		firePropertyChange(PropertyAwareObject.CHILD, oldPage, page);
	}
	
	public List<PageElementAction> getInputs() {
		return inputs;
	}
	
	public void setInputs(List<PageElementAction> inputs) {
		this.inputs = inputs;
		for(int i = 0 ; i < getListeners().getPropertyChangeListeners().length; i++ )
			for (int j = 0; j < inputs.size(); j++)
				((PageElementAction)inputs.get(j)).addPropertyChangeListener(
							getListeners().getPropertyChangeListeners()[i]);
	}
	
	public void addInput(PageElementAction input){
		inputs.add(input);
		for(int i = 0 ; i < getListeners().getPropertyChangeListeners().length; i++ )
			input.addPropertyChangeListener(
					getListeners().getPropertyChangeListeners()[i]);
		firePropertyChange(CHILD, null, input);
	}

	public void removeInput(PageElementAction input){
		inputs.remove(input);
		firePropertyChange(CHILD, null, input);
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		super.addPropertyChangeListener(l);
		if (inputs == null) return;
		for (int i = 0; i < inputs.size() ; i++)
			((PageElementAction)inputs.get(i)).addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l) {
		super.removePropertyChangeListener(l);
		if (inputs == null) return;
		for (int i = 0; i < inputs.size() ; i++)
			((PageElementAction)inputs.get(i)).removePropertyChangeListener(l);
	}
	
	public boolean hasNoActions() {
		for(PageElementAction action : getInputs()) {
			if(action.getAction() != ActionType.NO_ACTION) {
				return false;
			}
		}
		return true;
	}
}
