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

import org.cubictest.model.CommonTransition;
import org.cubictest.model.Page;
import org.cubictest.model.Transition;


/**
 * @author Stein Kåre Skytteren
 *
 *
 * A command that deletes an <code>AbstractPage</code>.
 */
public class DeletePageCommand extends DeleteAbstractPageCommand {

//	private AbstractPage transitionNode;

	private List<CommonTransition> commonTransitions;

	protected void deleteAbstractPageTransitions(){
		for (int i = 0; i < getCommonTransitions().size(); i++)	{
			Transition t = (Transition) getCommonTransitions().get(i);
			test.removeTransition(t);
		} 
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute(){
		super.execute();
		this.deleteAbstractPageTransitions();
	}
	
	public List<CommonTransition> getCommonTransitions() {
		if (commonTransitions == null) {
			commonTransitions = new ArrayList<CommonTransition>();
			for (Object o : ((Page) transitionNode).getCommonTransitions()){
				commonTransitions.add((CommonTransition)o);
			}
		}
		return commonTransitions;
	}
	
	protected void restoreAbstractPageTransitions(){
		for (int i = 0; i < getCommonTransitions().size(); i++){
			Transition t = (Transition) getCommonTransitions().get(i);
			test.addTransition(t);
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		restoreAbstractPageTransitions();
	}
	
	public void redo() {
		super.redo();
		deleteAbstractPageTransitions();
	}
}
