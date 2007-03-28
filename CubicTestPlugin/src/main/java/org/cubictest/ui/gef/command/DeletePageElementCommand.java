/*
 * Created on 24.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Transition;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.context.IContext;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;


/**
 * A command deletes a page element from a page and its user interactions.
 * 
 * @author Christian Schwarz 
 */
public class DeletePageElementCommand extends Command {

	private IContext context;
	private PageElement element;
	private int index;
	private Page page;
	private Map<UserInteractionsTransition, List<UserInteraction>> transUndoMap = new HashMap<UserInteractionsTransition, List<UserInteraction>>(); 
	private boolean confirmDialogShowed = false;
	private boolean deleteConfirmed = false;
	
	/**
	 * @param context
	 */
	public void setContext(IContext context) {
		this.context = context;
	}

	/**
	 * @param element
	 */
	public void setPageElement(PageElement element) {
		this.element = element;
	}
	
	@Override
	public boolean canExecute() {
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		//check wether element is participant in a user interaction:
		for (Transition transition : page.getOutTransitions()) {
			if (transition instanceof UserInteractionsTransition) {
				UserInteractionsTransition actionsTrans = (UserInteractionsTransition) transition;
				List<UserInteraction> toRemove = new ArrayList<UserInteraction>();
				for (UserInteraction action : actionsTrans.getUserInteractions()) {
					if (action.getElement().equals(element)) {
						//TODO: Check for contexts also
						if (!confirmDialogShowed) {
							deleteConfirmed = MessageDialog.openConfirm(new Shell(), "Confirm delete", 
									"Element will be removed automatically from any user interactions it participates in. " +
									"OK to continue?");
							confirmDialogShowed = true;
						}
						if (!deleteConfirmed) {
							return;
						}
						toRemove.add(action);
					}
				}
				//delete the element to remove
				for (UserInteraction interaction : toRemove) {
					List<UserInteraction> backup = new ArrayList<UserInteraction>();
					backup.addAll(actionsTrans.getUserInteractions());
					transUndoMap.put(actionsTrans, backup);
					actionsTrans.removeUserInteraction(interaction);
				}
			}
		}

		index = context.getElementIndex(element);
		context.removeElement(element);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		if (deleteConfirmed) {
			context.addElement(element, index);
			//restore transitions
			for (UserInteractionsTransition trans : transUndoMap.keySet()) {
				trans.setUserInteractions(transUndoMap.get(trans));
			}
		}
	}
	
	@Override
	public void redo() {
		if (deleteConfirmed) {
			super.redo();
		}
	}

	public void setPage(Page page) {
		this.page = page;
	}

}