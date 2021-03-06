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
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.exception.TestNotFoundException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.common.utils.ViewUtil;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.command.NoOperationCommand;
import org.cubictest.ui.gef.view.CubicTestLabel;
import org.cubictest.ui.gef.wizards.ExposeExtensionPointWizard;
import org.cubictest.ui.wizards.UpdateExtensionStartPointWizard;
import org.cubictest.ui.wizards.UpdateStartPointWizard;
import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;

/**
 * Controller for extension transitions.
 * 
 * @author Christian Schwarz
 */
public class ExtensionTransitionEditPart extends TransitionEditPart {

	private CubicTestLabel label;

	public ExtensionTransitionEditPart(ExtensionTransition transition) {
		super(transition);
	}
	
	@Override
	public IFigure getFigure() {
		PolylineConnection conn = (PolylineConnection) super.getFigure();
		
		Label tooltip = null;
		String tip = "\nThe source page of the extension point and the target page will be the same page/state.";
		TransitionNode startNode = getModel().getStart();
		if (startNode instanceof ExtensionStartPoint) {
			tooltip = new Label("Starting from an extension point in another test." + tip);
		}
		else if (startNode instanceof SubTest) {
			tooltip = new Label("Connection from an extension point in a subtest." + tip);
		}
		else {
			tooltip = new Label("Connection from an extension point in another test." + tip);
		}
		conn.setToolTip(tooltip);
		
		try {
			getModel().getExtensionPoint();
		}
		catch (TestNotFoundException e) {
			SubTest subtest = (SubTest) startNode;
			ErrorHandler.logAndShowErrorDialogAndThrow("SubTest not found: " + subtest.getFilePath() + ". Unable to open test.");
		}
		
		if (getModel().getExtensionPoint() == null) {
			//Model is inconsistent. We must fix it.
			
			if (startNode instanceof ExtensionStartPoint) {
				ExtensionStartPoint exStartPoint = (ExtensionStartPoint) startNode;
				UserInfo.showWarnDialog("The extension point \"" + exStartPoint.getSourceExtensionPointName() + "\" used as start point " +
						"was not found in file \"" + exStartPoint.getFileName() + "\"\n\n" +
						"Press OK to select a new extension point to use as start point in the test.");
				UpdateStartPointWizard wiz = launchNewTestWizard(exStartPoint.getTest(true));
				if (wiz.getExTrans() == null) {
					ErrorHandler.logAndThrow("No extension point selected. Unable to continue.");
				}
				setModel(wiz.getExTrans());
				getSource().setModel(wiz.getExTrans().getStart());
			}
			else if (startNode instanceof SubTest) {
				SubTest subTest = (SubTest) startNode;
				
				//open dialog to select which exPoint to extend from:
				TestEditPart testPart = null;
				Test test = null;
				if (getTarget() != null) {
					testPart = ViewUtil.getSurroundingTestPart(getTarget());
					test = (Test) testPart.getModel();
				}
				else if (getSource() != null) {
					testPart = ViewUtil.getSurroundingTestPart(getSource());
					test = (Test) testPart.getModel();
				}
				else {
					test = new Test();
				}
				
				ExtensionPoint target = null;
				if (subTest.isDangling()) {
					//do nothing. Must be fixed in editor.
				}
				else if (subTest.getTest(true).getAllExtensionPoints().size() == 0) {
					ErrorHandler.logAndThrow("The extension point that was used in subtest \"" + subTest.getName() +  
							"\" was not found.\n" +
							"Create an extension point in " + subTest.getName() + " and retry.");
				}
				else {
					UserInfo.showWarnDialog("The extension point that was used in subtest \"" + subTest.getName() +  
							"\" has changed or has been removed.\n" +
							"Press OK to select a new extension point to continue from.");

					ExposeExtensionPointWizard exposeExtensionPointWizard = new ExposeExtensionPointWizard(subTest, test);
					WizardDialog dlg = new WizardDialog(new Shell(),
							exposeExtensionPointWizard);
					if (dlg.open() == WizardDialog.CANCEL) {
						ErrorHandler.logAndThrow("No extension point selected. Unable to continue.");
					}
					target = exposeExtensionPointWizard.getSelectedExtensionPoint();
					test.removeTransition(getModel());
					ExtensionTransition transition = new ExtensionTransition(subTest, getModel().getEnd(), target);
					test.addTransition(transition);
					setModel(transition);
				}
			}
			//mark editor as dirty:
			getViewer().getEditDomain().getCommandStack().execute(new NoOperationCommand());
		}
		
		if (getModel().getExtensionPoint() != null) {
			label = new CubicTestLabel(getModel().getExtensionPoint().getName());
		}
		else {
			label = new CubicTestLabel("");
		}
		label.setTooltipText(tooltip.getText());
		
		ConnectionEndpointLocator locator = new ConnectionEndpointLocator(conn, true);
		conn.add(label, locator);
		
		return conn;
	}
	
	@Override
	public ExtensionTransition getModel() {
		return (ExtensionTransition) super.getModel();
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		label.setText(getModel().getExtensionPoint().getName());
	}
	
	public UpdateStartPointWizard launchNewTestWizard(Test startSubTest) {
		// Create the wizard
		UpdateExtensionStartPointWizard wiz = new UpdateExtensionStartPointWizard();
		wiz.setFirstPage((TransitionNode) getModel().getEnd());
		TestEditPart testPart = ViewUtil.getSurroundingTestPart(getSource());
		Test test = (Test) testPart.getModel();
		wiz.setTest(test);
		wiz.setStartSubTest(startSubTest);
		IWorkbench workbench = CubicTestPlugin.getDefault().getWorkbench();
		wiz.init(workbench, new StructuredSelection(test.getProject()));
		
		//Create the wizard dialog
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wiz);
		dialog.open();
		
		return wiz;
	}
}
