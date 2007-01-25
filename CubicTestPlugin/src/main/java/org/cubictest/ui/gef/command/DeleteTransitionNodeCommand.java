/*
 * Created on 07.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.eclipse.gef.commands.Command;


/**
 * @author Stein Kåre Skytteren
 *
 *
 * A command that deletes an <code>AbstractPage</code>.
 */
public abstract class DeleteTransitionNodeCommand extends Command {

	protected Test test;
	protected TransitionNode transitionNode;

	protected Transition inTransition;
	private List<Transition> outTransitions;


	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		for (Transition t : getOutTransitions()) {
			test.removeTransition(t);
			if (t.getStart().getOutTransitions().contains(t))
				System.out.println("Error in DeleteTransitionNodeCommand (duplicate transition)");
		}

		if (getInTransition() != null){
			test.removeTransition(inTransition);
		}
	}
	
	public List<Transition> getOutTransitions() {
		if(outTransitions == null) {
			outTransitions = new ArrayList<Transition>();
			for (Object o : transitionNode.getOutTransitions()) {
				outTransitions.add((Transition)o);
			}
		}
		return outTransitions;
	}
	
	public Transition getInTransition() {
		if(inTransition == null) {
			inTransition = transitionNode.getInTransition();
		}
		return inTransition;
	}
	
	protected void restoreTransitionNodeTransitions(){
		for (int i = 0; i < getOutTransitions().size(); i++){
			Transition t = (Transition) getOutTransitions().get(i);
			test.addTransition(t);
		}
		
		if (getInTransition() != null){
			test.addTransition(getInTransition());
		}		
	}

	/**
	 * 
	 * @param transitionNode
	 */
	public void setTransitionNode(TransitionNode transitionNode){
		this.transitionNode = transitionNode;
	}
	
	/**
	 * 
	 * @param test
	 */
	public void setTest(Test test){
		this.test = test;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo(){
		restoreTransitionNodeTransitions();
	}
}
