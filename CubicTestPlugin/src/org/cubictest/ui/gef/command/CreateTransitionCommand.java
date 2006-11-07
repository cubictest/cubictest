/*
 * Created on 06.may.2005
 *
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.command;

import org.cubictest.model.Common;
import org.cubictest.model.CommonTransition;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Page;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserActions;
import org.cubictest.ui.gef.wizards.ExposeExtensionPointWizard;
import org.cubictest.ui.gef.wizards.NewCubicTestUserActionsInputWizard;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Stein Kare Skytteren
 * 
 * A command that creates a <code>Transition</code>.
 */
public class CreateTransitionCommand extends Command {

	private TransitionNode sourceNode;

	private TransitionNode targetNode;

	private Transition transition;

	private Test test;

	/**
	 * @param sourceNode
	 */
	public void setSource(TransitionNode sourceNode) {
		this.sourceNode = sourceNode;
	}

	/**
	 * @param targetNode
	 */
	public void setTarget(TransitionNode targetNode) {
		this.targetNode = targetNode;
	}

	/*
	 * @see org.eclipse.gef.commands.Command#canExecute()
	 */
	public boolean canExecute() {
		if (sourceNode.equals(targetNode))
			return false;

		if (targetNode instanceof UrlStartPoint)
			return false;
		
		if (targetNode instanceof ExtensionStartPoint)
			return false;
		
		if (targetNode == null)
			return false;

		if (targetNode.getInTransition() != null
				&& !(sourceNode instanceof Common))
			return false;

		if (targetNode instanceof Common)
			return false;
		if (sourceNode instanceof Common && !(targetNode instanceof Page))
			return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#execute()
	 */
	public void execute() {
		super.execute();
		if (sourceNode instanceof SubTest && (targetNode instanceof Page || targetNode instanceof SubTest)) {
			ExposeExtensionPointWizard exposeExtensionPointWizard = new ExposeExtensionPointWizard(
					(SubTest) sourceNode, test);
			WizardDialog dlg = new WizardDialog(new Shell(),
					exposeExtensionPointWizard);
			if (dlg.open() == WizardDialog.CANCEL) {
				return;
			}
			transition = new ExtensionTransition(sourceNode, targetNode,
					exposeExtensionPointWizard.getSelectedExtensionPoint());
		}else if(sourceNode instanceof Page && (targetNode instanceof Page || 
				targetNode instanceof SubTest || targetNode instanceof CustomTestStep)){
			transition = new UserActions(sourceNode,targetNode);
			NewCubicTestUserActionsInputWizard userActionWizard = new NewCubicTestUserActionsInputWizard(
					(UserActions) transition, test);
			WizardDialog dlg = new WizardDialog(new Shell(), userActionWizard);

			if (dlg.open() == WizardDialog.CANCEL) {
				DeleteTransitionCommand cmd = new DeleteTransitionCommand(
						transition, test);
				cmd.execute();
				transition.resetStatus();
				return;
			}
		} else if (sourceNode instanceof Common && targetNode instanceof Page) {
			transition = new CommonTransition((Common) sourceNode,
					(Page) targetNode);
		} else if (sourceNode instanceof ConnectionPoint) {
			transition = new SimpleTransition((ConnectionPoint) sourceNode,
					targetNode);
		} else if (targetNode instanceof ExtensionPoint) {
			transition = new SimpleTransition(sourceNode, targetNode);
		}
		if(transition != null) {
			test.addTransition(transition);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.commands.Command#undo()
	 */
	public void undo() {
		super.undo();
		test.removeTransition(transition);
	}

	@Override
	public void redo() {
		if(transition != null) {
			test.addTransition(transition);
		}
	}
	
	/**
	 * @param test
	 */
	public void setTest(Test test) {
		this.test = test;
	}

	/**
	 * @param transition
	 */
	public void setTransition(Transition transition) {
		this.transition = transition;
	}
}