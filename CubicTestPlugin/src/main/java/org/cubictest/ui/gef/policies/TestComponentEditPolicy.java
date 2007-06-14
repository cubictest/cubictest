/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.policies;

import org.cubictest.model.Common;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Page;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.popup.JavaScriptPopup;
import org.cubictest.ui.gef.command.DeleteCommonCommand;
import org.cubictest.ui.gef.command.DeleteConnectionPointCommand;
import org.cubictest.ui.gef.command.DeleteCustomTestStepCommand;
import org.cubictest.ui.gef.command.DeleteExtensionPointCommand;
import org.cubictest.ui.gef.command.DeleteJavaScriptPopupCommand;
import org.cubictest.ui.gef.command.DeletePageCommand;
import org.cubictest.ui.gef.command.DeleteSubTestCommand;
import org.cubictest.ui.gef.command.DeleteTransitionNodeCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * @author Stein Kåre Skytteren EditPolicy for the <code>AbstractPage</code>s.
 */
public class TestComponentEditPolicy extends ComponentEditPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.ComponentEditPolicy#createDeleteCommand(org.eclipse.gef.requests.GroupRequest)
	 */
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Test test = (Test)(getHost().getParent().getModel());
		
		TransitionNode transitionNode = (TransitionNode)getHost().getModel();
		DeleteTransitionNodeCommand deleteCmd = null;
		if (transitionNode instanceof Page) {
			deleteCmd = new DeletePageCommand();
		} else if (transitionNode instanceof SubTest) {
			deleteCmd = new DeleteSubTestCommand();
		}else if (transitionNode instanceof ExtensionPoint){
			deleteCmd = new DeleteExtensionPointCommand();
		} else if (transitionNode instanceof CustomTestStepHolder) {
			deleteCmd = new DeleteCustomTestStepCommand();
		} else if (transitionNode instanceof ConnectionPoint) {
			deleteCmd = new DeleteConnectionPointCommand();
		} else if (transitionNode instanceof Common){
			deleteCmd = new DeleteCommonCommand();
		} else if (transitionNode instanceof JavaScriptPopup)
			deleteCmd = new DeleteJavaScriptPopupCommand();
		if(deleteCmd != null){
			deleteCmd.setTest(test);
			deleteCmd.setTransitionNode(transitionNode);
		}
		return deleteCmd;
	}
}
