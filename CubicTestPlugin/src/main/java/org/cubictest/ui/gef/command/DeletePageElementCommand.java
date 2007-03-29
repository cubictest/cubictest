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

import org.cubictest.common.exception.CubicException;
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
 * A command deletes a page element from a page and from its user interactions.
 * 
 * @author Christian Schwarz 
 */
public class DeletePageElementCommand extends Command {

	private IContext context;
	private PageElement element;
	private int index;
	private Page page;
	private Map<UserInteractionsTransition, List<UserInteraction>> transUndoMap = new HashMap<UserInteractionsTransition, List<UserInteraction>>();
	private List<PageElement> oldContextElements = new ArrayList<PageElement>();
	private boolean confirmDialogShowed = false;
	private boolean deleteConfirmed = true;
	private String message = "";
	
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
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		message = "Element will be removed automatically from the user interactions it participates in. OK to continue?";
		if (element instanceof IContext) {
			List<PageElement> elements = ((IContext) element).getElements();
			oldContextElements.addAll(elements);
			if (elements.size() > 0) {
				message = "The element and all child elements within it will be removed automatically from the user interactions they participate in. OK to continue?";
			}
			for (PageElement pe : elements) {
				deletePageElement(pe);
			}
			index = context.getElementIndex(element);
			deletePageElement(element);
		}
		else {
			index = context.getElementIndex(element);
			deletePageElement(element);
		}
	}

	private void deletePageElement(PageElement pe) {
		//check wether element is participant in a user interaction:
		for (Transition transition : page.getOutTransitions()) {
			if (transition instanceof UserInteractionsTransition) {
				UserInteractionsTransition actionsTrans = (UserInteractionsTransition) transition;
				List<UserInteraction> toRemove = new ArrayList<UserInteraction>();
				for (UserInteraction action : actionsTrans.getUserInteractions()) {
					if (action.getElement() != null && action.getElement().equals(pe)) {
						if (!confirmDialogShowed) {
							deleteConfirmed = MessageDialog.openConfirm(new Shell(), "Confirm delete", message);
							confirmDialogShowed = true;
						}
						if (!deleteConfirmed) {
							throw new CubicException("Deletion of page element interrupted by user");
						}
						toRemove.add(action);
					}
				}
				//delete the element to remove
				for (UserInteraction interaction : toRemove) {
					if (!transUndoMap.containsKey(actionsTrans)) {
						List<UserInteraction> oldActions = new ArrayList<UserInteraction>();
						oldActions.addAll(actionsTrans.getUserInteractions());
						transUndoMap.put(actionsTrans, oldActions);
					}
					actionsTrans.removeUserInteraction(interaction);
				}
			}
		}
		context.removeElement(pe);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		if (deleteConfirmed) {
			//restore page element(s):
			if (element instanceof IContext) {
				IContext contextElement = (IContext) element;
				for (PageElement pe : oldContextElements) {
					contextElement.addElement(pe);
				}
				
			}
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