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
package org.cubictest.ui.gef.command;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.CommonTransition;
import org.cubictest.model.Page;
import org.cubictest.model.Transition;


/**
 * A command that deletes a <code>Page</code>.
 * 
 * @author SK Skytteren
 */
public class DeletePageCommand extends DeleteAbstractPageCommand {

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
