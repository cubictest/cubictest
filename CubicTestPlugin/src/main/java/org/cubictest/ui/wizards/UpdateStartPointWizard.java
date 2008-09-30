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
package org.cubictest.ui.wizards;

import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.SubTestStartPoint;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.ui.gef.command.NoOperationCommand;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.gef.commands.CommandStack;


/**
 * Wizard for updating start point. Can be changed to an extension start point or a URL start point.
 * 
 * @author Christian Schwarz
 */

public class UpdateStartPointWizard extends NewTestWizard {

	Test test;
	TransitionNode firstPage;
	ExtensionTransition exTrans;
	private CommandStack commandStack;
	private String url;
	
	public UpdateStartPointWizard() {
		super();
	}

	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public void addPages() {
		boolean testHasExtensionPoints = !extensionPointMap.isEmpty();
		startPointTypeSelectionPage = new StartPointTypeSelectionPage(testHasExtensionPoints);
		addPage(startPointTypeSelectionPage);
		newUrlStartPointPage = new NewUrlStartPointPage(startPointTypeSelectionPage);
		addPage(newUrlStartPointPage);
		extentionStartPointSelectorPage = new ExtentionStartPointSelectorPage(extensionPointMap, project);
		addPage(extentionStartPointSelectorPage);
	}
	
	@Override
	protected void getWizardTitle() {
		setWindowTitle("Change start point of test");
	}
	
	/**
	 * This method is called when 'Finish' button is pressed in the wizard.
	 */
	@Override
	public boolean performFinish() {
		Transition inTrans = firstPage.getInTransition();
		if (inTrans != null) {
			firstPage.removeInTransition();
			test.removeTransition(inTrans);
		}

		if (startPointTypeSelectionPage.isUrlStartPointSelected()) {
			url = newUrlStartPointPage.getUrl();
			ConnectionPoint startPoint = WizardUtils.createUrlStartPoint(url);
			test.setStartPoint(null);
			test.setStartPoint(startPoint);
			SimpleTransition startTransition = new SimpleTransition(startPoint, firstPage);	
			test.addTransition(startTransition);
		}
		else if (startPointTypeSelectionPage.isSubTestStartPointSelected()) {
			test.setStartPoint(null);
			SubTestStartPoint startPoint = WizardUtils.createSubTestStartPoint();
			test.setStartPoint(startPoint);
			SimpleTransition startTransition = new SimpleTransition(startPoint, firstPage);	
			test.addTransition(startTransition);
		}
		else {
			IFile file = extentionStartPointSelectorPage.getExtentionPointFile();
			ExtensionPoint extensionPoint = extentionStartPointSelectorPage.getExtensionPoint();
			ConnectionPoint startPoint = WizardUtils.createExtensionStartPoint(file, extensionPoint, test);
			test.setStartPoint(null); //mandatory for listeners
			test.setStartPoint(startPoint);
			exTrans = new ExtensionTransition(startPoint, firstPage, extensionPoint);
			test.addTransition(exTrans);
		}
		if (commandStack != null) {
			NoOperationCommand noOp = new NoOperationCommand();
			commandStack.execute(noOp); //flag as dirty.
		}
		return true;
	}

	
	public void setTest(Test test) {
		this.test = test;
	}

	public void setFirstPage(TransitionNode firstPage) {
		this.firstPage = firstPage;
	}

	public ExtensionTransition getExTrans() {
		return exTrans;
	}

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	public String getUrl() {
		return url;
	}
}
