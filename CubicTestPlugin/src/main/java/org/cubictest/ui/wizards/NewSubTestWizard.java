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
package org.cubictest.ui.wizards;

import java.util.List;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.ModelUtil;
import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Page;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.ui.gef.layout.AutoLayout;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.INewWizard;


/**
 * Wizard for creating new sub tests.  The wizard creates one file with the extension "aat".
 * If the container resource (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target container.
 * 
 * @author Christian Schwarz 
 */

public class NewSubTestWizard extends AbstractNewSimpleStartPointTestWizard implements INewWizard {
	
	protected List<TransitionNode> refactorInitOriginalNodes;
	protected CommandStack commandStack;
	
	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public void addPages() {
		testDetailsPage = new TestDetailsPage(selection, "sub test");
		addPage(testDetailsPage);
	}
	
	public Test createEmptyTest(String name, String description) {
		Test test = WizardUtils.createEmptyTestWithSubTestStartPoint("test" + System.currentTimeMillis(), name, description);
		if (refactorInitOriginalNodes == null) {
			Page page = WizardUtils.addEmptyPage(test);
			SimpleTransition startTransition = new SimpleTransition(test.getStartPoint(), page);	
			test.addTransition(startTransition);
		}
		else {
			//we shall prepopulate sub test with nodes
			try {
				List<TransitionNode> newNodes = ViewUtil.cloneAndAddNodesToTest(test, refactorInitOriginalNodes, commandStack, false);
				
				//create transition from start point:
				if (ModelUtil.getFirstNode(newNodes) instanceof TransitionNode) {
					SimpleTransition startTransition = new SimpleTransition(test.getStartPoint(), 
							ModelUtil.getFirstNode(newNodes));	
					test.addTransition(startTransition);
				}
				
				//check if we need to create a temp state as target for the last user interaction 
				TransitionNode lastOfOriginalNodes = ModelUtil.getLastNodeInList(refactorInitOriginalNodes);
				if (lastOfOriginalNodes.hasOutTransition()) {
					Transition transToOutside = lastOfOriginalNodes.getOutTransitions().get(0);
					if ((transToOutside instanceof UserInteractionsTransition) || transToOutside instanceof ExtensionTransition) {
						Page page = new Page();
						page.setName("Next state");
						test.addPage(page);
						
						Transition trans = (Transition) transToOutside.clone();
						trans.setStart(ModelUtil.getLastNodeInList(newNodes));
						trans.setEnd(page);
						test.addTransition(trans);

						page.setPosition(new Point(ModelUtil.getLastNodeInList(newNodes).getPosition().x, 
								AutoLayout.getYPositionForNode(page)));
					}
				}
			} catch (CloneNotSupportedException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow("Unable to create sub test.", e);
			}
		}
		return test;
	}

	protected void getWizardTitle() {
		setWindowTitle("New CubicTest sub test");
	}

	public void setRefactorInitOriginalNodes(
			List<TransitionNode> refactorInitOriginalNodes) {
		this.refactorInitOriginalNodes = refactorInitOriginalNodes;
	}

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}
	
	@Override
	public boolean shouldPromptToSaveAllEditors() {
		return false;
	}

}