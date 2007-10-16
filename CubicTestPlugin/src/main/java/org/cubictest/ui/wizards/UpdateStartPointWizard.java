/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.SimpleTransition;
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
		startPointTypeSelectionPage = new StartPointTypeSelectionPage();
		addPage(startPointTypeSelectionPage);
		newUrlStartPointPage = new NewUrlStartPointPage(startPointTypeSelectionPage);
		addPage(newUrlStartPointPage);
		extentionStartPointSelectorPage = new ExtentionStartPointSelectorPage(extensionPointMap, project);
		addPage(extentionStartPointSelectorPage);
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

		url = newUrlStartPointPage.getUrl();
		ExtensionPoint extensionPoint = extentionStartPointSelectorPage.getExtensionPoint();
		IFile file = extentionStartPointSelectorPage.getExtentionPointFile();
		boolean useUrlStartPoint = startPointTypeSelectionPage.getNextPage().equals(newUrlStartPointPage);

		ConnectionPoint startPoint = null;
		if (useUrlStartPoint) {
			startPoint = WizardUtils.createUrlStartPoint(url);
			test.setStartPoint(null);
			test.setStartPoint(startPoint);
			SimpleTransition startTransition = new SimpleTransition(startPoint, firstPage);	
			test.addTransition(startTransition);
		}
		else {
			startPoint = WizardUtils.createExtensionStartPoint(file, extensionPoint, test);
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
