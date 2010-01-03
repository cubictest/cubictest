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

import java.util.Iterator;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.exception.CubicException;
import org.cubictest.model.IStartPoint;
import org.cubictest.model.Test;
import org.cubictest.model.TestSuiteStartPoint;
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

	public static final String ACTION_NAME = "Change Start Point for Test";
	public static final String ACTION_ID = "CubicTestPlugin.action.updateTestStartPointAction";
	
	public UpdateTestStartPointAction(IWorkbenchPart part) {
		super(part);
	}

	@Override
	protected boolean calculateEnabled() {
		if(getParts() != null) {
			for(Object element : getParts()) {
				if(element instanceof EditPart) {
					Object model = ((EditPart) element).getModel();
					if (model instanceof TestSuiteStartPoint) {
						return false;
					}
					else if (model instanceof IStartPoint) {
						return true;
					}
					else if (model instanceof Test) {
						if (((Test) model).getStartPoint() instanceof TestSuiteStartPoint) {
							return false;
						}
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
		setText(ACTION_NAME);
		setId(ACTION_ID);
	}
	
	@Override
	public void run() {
		for (Iterator<?> iter = this.getParts().iterator(); iter.hasNext();) {
			Object element = iter.next();
			if(element instanceof AbstractNodeEditPart) {
				AbstractNodeEditPart pageEditPart = (AbstractNodeEditPart) element;
				TestEditPart testPart = (TestEditPart) pageEditPart.getParent();
				launchUpdateStartPointWizard(testPart);
			}
			else if(element instanceof TestEditPart) {
				launchUpdateStartPointWizard((TestEditPart) element);
			}
			else {
				throw new CubicException("Selected item was not a start point or a test.");
			}
		}
	}
	
	
	@Override
	public ImageDescriptor getImageDescriptor() {
		return CubicTestImageRegistry.getDescriptor(CubicTestImageRegistry.CHANGE_STARTPOINT_IMAGE);
	}
	
	
	public UpdateStartPointWizard launchUpdateStartPointWizard(TestEditPart testPart) {
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
