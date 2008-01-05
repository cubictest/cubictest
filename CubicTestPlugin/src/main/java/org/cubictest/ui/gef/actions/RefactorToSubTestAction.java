/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.actions;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.ModelUtil;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.IStartPoint;
import org.cubictest.model.PageElement;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.utils.ViewUtil;
import org.cubictest.ui.wizards.NewSubTestWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
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
		List<TransitionNode> nodes = new ArrayList<TransitionNode>();
		if(getParts() != null) {
			for(Object element : getParts()) {
				if(element instanceof EditPart){
					Object model = ((EditPart) element).getModel();
					if (model instanceof IStartPoint) {
						return false;
					}
					if(model instanceof TransitionNode){
						nodes.add((TransitionNode) model);
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
		if (nodes.size() < 2) {
			return false;
		}
		return (nodes.size() > 0) && ModelUtil.nodesContainsSingleContinuousPath(nodes);
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
			
			NewSubTestWizard wiz = new NewSubTestWizard();
			wiz.setRefactorInitOriginalNodes(selectedNodes);
			wiz.setCommandStack(getCommandStack());
			IWorkbench workbench = CubicTestPlugin.getDefault().getWorkbench();
			wiz.init(workbench, new StructuredSelection(project));
			
			//Create the wizard dialog
			WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wiz);
	
			if (dialog.open() == Window.OK) {
				TransitionNode firstNodeInSelection = ModelUtil.getFirstNode(selectedNodes);
				TransitionNode lastNodeInSelection = ModelUtil.getLastNodeInPath(selectedNodes);
				

				SubTest subTest = new SubTest(wiz.getFileName(), project);
				subTest.setPosition(firstNodeInSelection.getPosition());
				test.addSubTest(subTest);

				//create transitions to sub test:
				if (firstNodeInSelection.hasPreviousNode()) {
					SimpleTransition trans = new SimpleTransition(firstNodeInSelection.getPreviousNode(), subTest);
					test.removeTransition(firstNodeInSelection.getInTransition());
					test.addTransition(trans);
				}
				List<Transition> toRemove = new ArrayList<Transition>();
				List<Transition> toAdd = new ArrayList<Transition>();
				for (Transition outTrans : lastNodeInSelection.getOutTransitions()) {
					TransitionNode end = outTrans.getEnd();
					toRemove.add(outTrans);
					if (end != null && !(end instanceof ExtensionPoint)) {
						SimpleTransition trans = new SimpleTransition(subTest, end);
						toAdd.add(trans);
					}
				}
				for (Transition transition : toRemove) {
					test.removeTransition(transition);
				}
				for (Transition transition : toAdd) {
					test.addTransition(transition);
				}
				ViewUtil.deleteParts(selectedNodeParts, commandStack);
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
