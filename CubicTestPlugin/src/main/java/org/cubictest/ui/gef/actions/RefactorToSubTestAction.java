/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.gef.actions;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.ModelUtil;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.IStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.ui.gef.command.AddAbstractPageCommand;
import org.cubictest.ui.gef.command.AddSubTestCommand;
import org.cubictest.ui.gef.command.CreateTransitionCommand;
import org.cubictest.ui.gef.command.DeleteTransitionCommand;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.utils.ViewUtil;
import org.cubictest.ui.wizards.NewSubTestWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Context menu action for refactoring selection to subtest.
 * 
 * @author Christian Schwarz
 *
 */
public class RefactorToSubTestAction extends BaseEditorAction {

	Test test;
	
	public static final String ACTION_ID = "CubicTestPlugin.action.refactorToSubTest";
	
	public RefactorToSubTestAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected boolean calculateEnabled() {
		List<TransitionNode> selectedNodes = new ArrayList<TransitionNode>();
		if(getParts() != null) {
			for(Object element : getParts()) {
				if(element instanceof EditPart){
					Object model = ((EditPart) element).getModel();
					if (model instanceof IStartPoint) {
						return false;
					}
					if(model instanceof TransitionNode){
						selectedNodes.add((TransitionNode) model);
						test = ((TestEditPart) ((EditPart) element).getParent()).getTest();
					}
					if (model instanceof PageElement) {
						if (!ViewUtil.getSurroundingPagePart((EditPart) element).isSelected()) {
							return false;
						}
					}
				}
			}			
		}
		if (selectedNodes.size() < 2) {
			return false;
		}
		if (!ModelUtil.nodesContainsSingleContinuousPath(selectedNodes)) {
			return false;
		}
		if (ModelUtil.getLastNodeInList(selectedNodes).getNumberOfOutTransitions() > 1) {
			return false;
		}
		return true;
	}



	@Override
	protected void init() {
		super.init();
		setText("Extract sub test of selected nodes");
		setId(ACTION_ID);
	}
	
	@Override
	public void run() {
		try {
			//store selection and command stack while correct editor is still open:
			List<TransitionNode> selectedNodes = getSelectedNodes();
			List<EditPart> selectedNodeParts = getSelectedNodeParts();
			CommandStack commandStack = ViewUtil.getCommandStackFromActivePage();
			IProject project = ViewUtil.getProjectFromActivePage();
			Display display = ViewUtil.getDisplayFromActiveWindow();

			NewSubTestWizard wiz = new NewSubTestWizard();
			wiz.setRefactorInitOriginalNodes(selectedNodes);
			wiz.setCommandStack(getCommandStack());
			IWorkbench workbench = CubicTestPlugin.getDefault().getWorkbench();
			wiz.init(workbench, new StructuredSelection(project));
			
			//Create the wizard dialog
			WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wiz);
	
			if (dialog.open() == Window.OK) {
				while (!wiz.isDone()) {
					//performFinish is working
					Thread.sleep(100);
				}
				CompoundCommand compoundCmd = new CompoundCommand();
				
				TransitionNode firstNodeInSelection = ModelUtil.getFirstNode(selectedNodes);
				
				//create subtest:
				String filePath = wiz.getFilePath().replaceFirst("/" + project.getName(), "");
				SubTest subTest = new SubTest(filePath, project);
				subTest.setPosition(firstNodeInSelection.getPosition());
				compoundCmd.add(new AddSubTestCommand(subTest, test));
				
				List<Transition> toRemove = new ArrayList<Transition>();
				List<Transition> toAdd = new ArrayList<Transition>();
				
				//create transitions to sub test:
				if (firstNodeInSelection.hasPreviousNode()) {
					SimpleTransition trans = new SimpleTransition(firstNodeInSelection.getPreviousNode(), subTest);
					toRemove.add(firstNodeInSelection.getInTransition());
					toAdd.add(trans);
				}

				//create transition from sub test:
				TransitionNode lastNodeInSelection = ModelUtil.getLastNodeInList(selectedNodes);
				if (lastNodeInSelection.hasOutTransition()) {
					Transition transToOutside = lastNodeInSelection.getOutTransitions().get(0);
					
					TransitionNode end = transToOutside.getEnd();
					toRemove.add(transToOutside);
					if (end != null && !(end instanceof ExtensionPoint)) {
						SimpleTransition trans = new SimpleTransition(subTest, end);
						toAdd.add(trans);
					}
				}

				compoundCmd.add(ViewUtil.deleteParts(selectedNodeParts, null));

				for (Transition trans : toRemove) {
					compoundCmd.add(new DeleteTransitionCommand(trans, test));
				}
				for (Transition trans : toAdd) {
					compoundCmd.add(new CreateTransitionCommand(trans, test, true));
				}
				
				final Command compoundCmdFinal = compoundCmd;
				final CommandStack commandStackFinal = commandStack;
				display.syncExec(new Runnable() {
					public void run() {
						commandStackFinal.execute(compoundCmdFinal);
					}
				});
			}
		} catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow("Unable to extract selected items into a sub test.", e);
		}
	}
	
	
	public List<EditPart> getSelectedNodeParts() {
		List<EditPart> result = new ArrayList<EditPart>();
		
		for (Object obj : getParts()) {
			
			if(!(obj instanceof EditPart)) {
				continue;
			}

			EditPart part = (EditPart) obj;			
			if (part.getModel() instanceof TransitionNode) {
				result.add(part);
			}
		}
		return result;
	}
	
	public List<TransitionNode> getSelectedNodes() {
		List<TransitionNode> result = new ArrayList<TransitionNode>();
		for (EditPart part : getSelectedNodeParts()) {
			result.add((TransitionNode) part.getModel());
			
		}
		return result;
	}
	
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return CubicTestImageRegistry.getDescriptor(CubicTestImageRegistry.EXTENSION_POINT_IMAGE);
	}
	
	
}
