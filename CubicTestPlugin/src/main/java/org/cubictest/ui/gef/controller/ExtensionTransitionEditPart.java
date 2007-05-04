/*
 * Created on 09.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.gef.controller;

import java.beans.PropertyChangeEvent;

import org.cubictest.CubicTestPlugin;
import org.cubictest.common.exception.TestNotFoundException;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.command.NoOperationCommand;
import org.cubictest.ui.gef.view.CubicTestLabel;
import org.cubictest.ui.gef.wizards.ExposeExtensionPointWizard;
import org.cubictest.ui.utils.ViewUtil;
import org.cubictest.ui.wizards.UpdateExtensionStartPointWizard;
import org.cubictest.ui.wizards.UpdateStartPointWizard;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MidpointLocator;
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
		if (getModel().getStart() instanceof ExtensionStartPoint) {
			tooltip = new Label("Starting from an extension point in another test." + tip);
		}
		else if (getModel().getStart() instanceof SubTest) {
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
			SubTest subtest = (SubTest) getModel().getStart();
			ErrorHandler.logAndShowErrorDialogAndThrow("SubTest not found: " + subtest.getFilePath() + ". Unable to open test.");
		}
		
		if (getModel().getExtensionPoint() == null) {
			//ExtensionStartPoint
			
			if (getModel().getStart() instanceof ExtensionStartPoint) {
				ExtensionStartPoint start = (ExtensionStartPoint) getModel().getStart();
				ErrorHandler.showWarnDialog("The extension point \"" + start.getSourceExtensionPointName() + "\" used as start point " +
						"was not found in file \"" + start.getFileName() + "\"\n\n" +
						"Press OK to select a new extension point to use as start point in the test.");
				UpdateStartPointWizard wiz = launchNewTestWizard(start.getTest());
				if (wiz.getExTrans() == null) {
					ErrorHandler.logAndShowErrorDialogAndThrow("No extension point selected. Unable to continue.");
				}
				setModel(wiz.getExTrans());
				getViewer().getEditDomain().getCommandStack().execute(new NoOperationCommand());
			}
			else {
				//SubTest
				SubTest start = (SubTest) getModel().getStart();
				ErrorHandler.showWarnDialog("The extension point that was used in subtest \"" + start.getName() +  
						"\" was not found in that subtest. " +
						"Press OK to reselect an extension point.");
				
				//open dialog to select which exPoint to extend from:
				TestEditPart testPart = null;
				if (getTarget() != null) {
					testPart = ViewUtil.getSurroundingTestPart(getTarget());
				}
				else if (getSource() != null) {
					testPart = ViewUtil.getSurroundingTestPart(getSource());
				}
				else {
					ErrorHandler.logAndShowErrorDialogAndThrow("Could not open test.");
				}
				Test test = (Test) testPart.getModel();
				ExposeExtensionPointWizard exposeExtensionPointWizard = new ExposeExtensionPointWizard(
						(SubTest) getModel().getStart(), test);
				WizardDialog dlg = new WizardDialog(new Shell(),
						exposeExtensionPointWizard);
				if (dlg.open() == WizardDialog.CANCEL) {
					ErrorHandler.logAndShowErrorDialogAndThrow("No extension point selected. Unable to continue.");
				}
				test.removeTransition(getModel());
				ExtensionTransition transition = new ExtensionTransition(getModel().getStart(), getModel().getEnd(),
						exposeExtensionPointWizard.getSelectedExtensionPoint());
				test.addTransition(transition);
				setModel(transition);
				getViewer().getEditDomain().getCommandStack().execute(new NoOperationCommand());
			}
		}
		label = new CubicTestLabel(getModel().getExtensionPoint().getName());
		label.setTooltipText(tooltip.getText());
		
		MidpointLocator locator = new MidpointLocator(conn, 0);
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
