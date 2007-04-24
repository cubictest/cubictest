/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import java.util.List;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.Common;
import org.cubictest.model.CommonTransition;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.command.MovePageElementCommand;
import org.cubictest.ui.gef.command.TransferPageElementCommand;
import org.cubictest.ui.gef.controller.PageElementEditPart;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
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
			
		TransferPageElementCommand transferCmd = new TransferPageElementCommand();
		
		PageElement childModel = (PageElement) child.getModel();
		transferCmd.setElement(childModel);
		transferCmd.setSourcePage(ViewUtil.getSurroundingPage(child));
		transferCmd.setTargetPage(ViewUtil.getSurroundingPage(getHost()));
		
		IContext originalPage = (IContext) child.getParent().getModel();
		transferCmd.setOriginalContext(originalPage);
		
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
		transferCmd.setNewContext(newPage);
		
		transferCmd.setNewIndex(newIndex);
		
		CompoundCommand compoundCmd = (CompoundCommand) ViewUtil.getCompoundCommandWithResize(transferCmd, ViewUtil.ADD, getHost());
		
		//creating common transition automatically if applicable:
		AbstractPage targetPage = ViewUtil.getSurroundingPage(getHost());
		AbstractPage sourcePage = ViewUtil.getSurroundingPage(child);
		if (targetPage instanceof Common && sourcePage instanceof Page) {
			List<CommonTransition> commons = ((Page) sourcePage).getCommonTransitions();
			boolean createTrans = true;

			for (CommonTransition transition : commons) {
				if (transition.getEnd().equals(sourcePage) && transition.getStart().equals(targetPage)) {
					createTrans = false;
				}
			}
		
			if (createTrans) {
				CreateTransitionCommand transitionCmd = new CreateTransitionCommand();
				transitionCmd.setTest(ViewUtil.getSurroundingTest(getHost()));
				transitionCmd.setSource(targetPage);
				transitionCmd.setTarget(sourcePage);
				compoundCmd = (CompoundCommand) compoundCmd.chain(transitionCmd);
			}
		}
		
		return compoundCmd;
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

	@Override
	public void showTargetFeedback(Request request) {
		if (request instanceof CreateRequest) {
			CreateRequest createReq = (CreateRequest) request;
			Class type = (Class) createReq.getNewObjectType();
			if (type != null && PageElement.class.isAssignableFrom(type)) {
				super.showTargetFeedback(request);
			}
		}
		else {
			super.showTargetFeedback(request);
		}
	}
}
