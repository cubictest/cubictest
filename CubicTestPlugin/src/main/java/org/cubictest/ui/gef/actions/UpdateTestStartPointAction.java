/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.gef.actions;

import java.util.Iterator;

import org.cubictest.CubicTestPlugin;
import org.cubictest.model.IStartPoint;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.controller.AbstractNodeEditPart;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.view.CubicTestImageRegistry;
import org.cubictest.ui.wizards.UpdateStartPointWizard;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Context menu action for updating start point of page.
 * 
 * @author Christian Schwarz
 */
public class UpdateTestStartPointAction extends BaseEditorAction  {

	public static final String ACTION_ID = "CubicTestPlugin.action.updateTestStartPointAction";
	
	public UpdateTestStartPointAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected boolean calculateEnabled() {
		if(getParts() != null) {
			for(Object element : getParts()) {
				if(element instanceof EditPart) {
					if (((EditPart) element).getModel() instanceof IStartPoint) {
						return true;
					}
				}
			}			
		}
		return false;
	}

	@Override
	protected void init() {
		super.init();
		setText("Change start point for test");
		setId(ACTION_ID);
	}
	
	@Override
	public void run() {
		for (Iterator iter = this.getParts().iterator(); iter.hasNext();) {
			Object element = iter.next();
			if(element instanceof AbstractNodeEditPart) {
				AbstractNodeEditPart pageEditPart = (AbstractNodeEditPart) element;
				TestEditPart testPart = (TestEditPart) pageEditPart.getParent();
				launchNewTestWizard(testPart);
			}
		}
	}
	
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return CubicTestImageRegistry.getDescriptor(CubicTestImageRegistry.CONNECTION_IMAGE);
	}
	
	
	public UpdateStartPointWizard launchNewTestWizard(TestEditPart testPart) {
		Test test = (Test) testPart.getModel();

		// Create the wizard
		UpdateStartPointWizard wiz = new UpdateStartPointWizard();
		wiz.setFirstPage(test.getFirstNodeAfterStartPoint());
		wiz.setTest(test);
		wiz.setCommandStack(getCommandStack());
		IWorkbench workbench = CubicTestPlugin.getDefault().getWorkbench();
		wiz.init(workbench, new StructuredSelection(test.getProject()));
		
		//Create the wizard dialog
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wiz);
		dialog.open();
		
		return wiz;
	}
}
