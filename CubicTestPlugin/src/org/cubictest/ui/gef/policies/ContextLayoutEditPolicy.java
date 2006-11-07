/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import java.util.List;

import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.MovePageElementCommand;
import org.cubictest.ui.gef.command.TransferPageElementCommand;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;



/**
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class ContextLayoutEditPolicy extends FlowLayoutEditPolicy {

	private IContext container;

	public ContextLayoutEditPolicy(IContext container){
		this.container = container;
	}
	
	public int getIndex(Request request) {
		return getFeedbackIndexFor(request);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createAddCommand(org.eclipse.gef.EditPart, org.eclipse.gef.EditPart)
	 */
	protected Command createAddCommand(EditPart child, EditPart after) {
		
		//only allow PageElements to be added:
		if (!(child instanceof PageElementEditPart)) 
			return null;
			
		TransferPageElementCommand cmd = new TransferPageElementCommand();
		
		PageElement childModel = (PageElement) child.getModel();
		cmd.setToMoveModel(childModel);
		
		IContext originalPage = (IContext) child.getParent().getModel();
		cmd.setOriginalContext(originalPage);
		
		IContext newPage = null;
		
		int newIndex = 0;
		newPage = container;

		List children = getHost().getChildren();

		if (after != null) {
			//moved behind another element on other page
			newPage = (IContext) after.getParent().getModel();	
			newIndex = after.getParent().getChildren().indexOf(after);
		}
		else if (after == null && children != null) {
			//element is moved to bottom of list
			newIndex = children.size();
		}
		cmd.setNewPage(newPage);
		
		int oldIndex = child.getParent().getChildren().indexOf(child);
		cmd.setOldIndex(oldIndex);
		
		cmd.setNewIndex(newIndex);
		
		return ViewUtil.getCompoundCommandWithResize(cmd, ViewUtil.ADD, getHost());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.OrderedLayoutEditPolicy#createMoveChildCommand(org.eclipse.gef.EditPart, org.eclipse.gef.EditPart)
	 */
	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		
		int newIndex = 0;
		List children = getHost().getChildren();
		
		if (after != null){
			//element is put after another element
			newIndex = children.indexOf(after);
		}
		else if (after == null && children != null && children.indexOf(child) == children.size() - 1) {
			//element is moved from end to end of list (no move. after is null in this case..)
			newIndex = children.size();
		} else if (after == null && children != null) {
			//element is moved to bottom of list
			newIndex = children.size();
		}
			
		MovePageElementCommand cmd = new MovePageElementCommand();
		PageElement childModel = (PageElement)child.getModel();
		cmd.setChild(childModel);
		
		
		IContext parentModel = (IContext) getHost().getModel();
		cmd.setParent(parentModel);
		
		int oldIndex = getHost().getChildren().indexOf(child);

		cmd.setOldIndex(oldIndex);
		cmd.setNewIndex(newIndex);
		
		return cmd;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	protected Command getCreateCommand(CreateRequest request) {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getDeleteDependantCommand(org.eclipse.gef.Request)
	 */
	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	
}
