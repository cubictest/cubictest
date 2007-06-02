/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.core.resources.IFile;


/**
 * Wizard for updating extension start point.
 * 
 * @author Christian Schwarz
 */

public class UpdateExtensionStartPointWizard extends UpdateStartPointWizard {

	Test startSubTest;
	
	public UpdateExtensionStartPointWizard() {
		super();
	}

	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public void addPages() {
		extentionStartPointSelectorPage = new ExtentionStartPointSelectorPage(extensionPointMap, project);
		addPage(extentionStartPointSelectorPage);
	}
	
	@Override
	public boolean canFinish() {
		if(extentionStartPointSelectorPage.getExtensionPoint() != null) {
			return true;
		}
		return false;
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
		
		ExtensionPoint extensionPoint = extentionStartPointSelectorPage.getExtensionPoint();
		IFile file = extentionStartPointSelectorPage.getExtentionPointFile();
		ConnectionPoint startPoint = WizardUtils.createExtensionStartPoint(file, extensionPoint, test);
		test.setStartPoint(startPoint);
		exTrans = new ExtensionTransition(startPoint, firstPage, extensionPoint);
		test.addTransition(exTrans);

		return true;
	}

	public void setStartSubTest(Test subTest) {
		this.startSubTest = subTest;
	}
}
