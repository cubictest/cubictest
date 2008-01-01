/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.PageElement;
import org.cubictest.model.TransitionNode;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.utils.ViewUtil;
import org.cubictest.ui.wizards.NewSubTestWizard;
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
					if(model instanceof TransitionNode){
						nodes.add((TransitionNode) model);
					}
					if (model instanceof PageElement) {
						if (!ViewUtil.getSurroundingPagePart((EditPart) element).isSelected()) {
							return false;
						}
					}
				}
			}			
		}
		return nodes.size() > 0;
	}

	@Override
	protected void init() {
		super.init();
		setText("Extract selected nodes into a subtest.");
		setId(ACTION_ID);
	}
	
	@Override
	public void run() {
		try {
			//store selection and command stack while correct editor is still open:
			List<TransitionNode> selectedNodes = getSelectedNodes();
			List<EditPart> selectedNodeParts = getSelectedNodeParts();
			CommandStack commandStack = ViewUtil.getCommandStackFromActivePage();
			
			NewSubTestWizard wiz = new NewSubTestWizard();
			wiz.setRefactorInitOriginalNodes(selectedNodes);
			wiz.setCommandStack(getCommandStack());
			IWorkbench workbench = CubicTestPlugin.getDefault().getWorkbench();
			wiz.init(workbench, new StructuredSelection(ViewUtil.getProjectFromActivePage()));
			
			//Create the wizard dialog
			WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wiz);
	
			if (dialog.open() == Window.OK) {
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
