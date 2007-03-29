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

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.Common;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Transition;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.context.IContext;
import org.eclipse.gef.commands.Command;


/**
 * A command deletes a page element from a page and from its user interactions.
 * If delete element from Common, checks whether the elements is used in user interaction 
 * in one of the Common's target pages. 
 * 
 * @author Christian Schwarz 
 */
public class DeletePageElementCommand extends Command {

	private IContext context;
	private PageElement element;
	private int index;
	private AbstractPage abstractPage;
	
	//local util:
	private Map<UserInteractionsTransition, List<UserInteraction>> transUndoMap = new HashMap<UserInteractionsTransition, List<UserInteraction>>();
	private List<PageElement> oldContextElements = new ArrayList<PageElement>();
	private boolean infoDialogShowed = false;
	private boolean informAboutDeletion = false;
	private String message = "User interaction(s) were updated and element was removed.";
	

	/**
	 * Set context to delete from.
	 * @param context
	 */
	public void setContext(IContext context) {
		this.context = context;
	}

	/**
	 * Set the page element to delete.
	 * @param element
	 */
	public void setPageElement(PageElement element) {
		this.element = element;
	}
	
	/**
	 * Set the Abstract page the element should be deleted from.
	 * @param page
	 */
	public void setPage(AbstractPage page) {
		this.abstractPage = page;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		index = context.getElementIndex(element);
		if (element instanceof IContext) {
			//delete each child element in context seperately, removing them from their user interactions if applicable
			List<PageElement> elements = ((IContext) element).getElements();
			//save a backup of the elements for undo:
			oldContextElements.addAll(elements);
			if (elements.size() > 1) {
				message = "User interaction(s) were updated and elements was removed.";
			}
			for (PageElement pe : elements) {
				deletePageElement(pe);
			}
			deletePageElement(element);
		}
		else {
			deletePageElement(element);
		}
		
		if (!infoDialogShowed && informAboutDeletion) {
			//inform user if user interactions was updated
			ErrorHandler.showWarnDialog(message);
			infoDialogShowed = true;
		}

	}

	/**
	 * Delete page element from the abstract page set in this command.
	 * If page is Common, will examine the Common's tragets.
	 * If from Page, removes element from page and updates user interactions.
	 * @param pe
	 */
	private void deletePageElement(PageElement pe) {
		//check wether element is participant in a user interaction:
		if (abstractPage instanceof Common) {
			List<Transition> outTranses = ((Common) abstractPage).getOutTransitions();
			for (Transition transition : outTranses) {
				Page pageTargetByCommon = (Page) transition.getEnd();
				//clean up user interactions from page targeted by the common:
				boolean updated = cleanUpUserInteractions(pe, pageTargetByCommon);
				if (updated) {
					message = "User interaction(s) were updated and element was removed from pages targeted by the Common.";
				}
			}
		}
		else {
			//clean up user interactions from normal Page:
			cleanUpUserInteractions(pe, (Page) abstractPage);
		}
		
		//delete the page element:
		context.removeElement(pe);
	}

	/**
	 * Cleans up user interactions from <i>one</i> page
	 * @param pe
	 * @param page
	 */
	private boolean cleanUpUserInteractions(PageElement pe, Page page) {
		boolean updated = false;
		//Cleans up user interactions from one page. Do not worry about commons and their targets here.
		
		for (Transition trans : page.getOutTransitions()) {
			if (trans instanceof UserInteractionsTransition) {
				//build a list of the actions to update:
				UserInteractionsTransition actionsTrans = (UserInteractionsTransition) trans;
				List<UserInteraction> toRemove = new ArrayList<UserInteraction>();
				for (UserInteraction action : actionsTrans.getUserInteractions()) {
					if (action.getElement() != null && action.getElement().equals(pe)) {
						informAboutDeletion = true;
						toRemove.add(action);
						updated = true;
					}
				}
				//save a backup for undo:
				if (!transUndoMap.containsKey(actionsTrans)) {
					List<UserInteraction> oldActions = new ArrayList<UserInteraction>();
					oldActions.addAll(actionsTrans.getUserInteractions());
					transUndoMap.put(actionsTrans, oldActions);
				}
				
				//clean up the user interaction:
				for (UserInteraction interaction : toRemove) {
					actionsTrans.removeUserInteraction(interaction);
				}
			}
		}
		return updated;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		if (element instanceof IContext) {
			//must add the old childs:
			IContext contextElement = (IContext) element;
			for (PageElement pe : oldContextElements) {
				contextElement.addElement(pe);
			}
			
		}
		//restore the element
		context.addElement(element, index);

		//restore transitions
		for (UserInteractionsTransition trans : transUndoMap.keySet()) {
			List<UserInteraction> oldActions = new ArrayList<UserInteraction>();
			oldActions.addAll(transUndoMap.get(trans));
			trans.setUserInteractions(oldActions);
		}
	}
	
	@Override
	public void redo() {
		transUndoMap.clear();
		super.redo();
	}


}