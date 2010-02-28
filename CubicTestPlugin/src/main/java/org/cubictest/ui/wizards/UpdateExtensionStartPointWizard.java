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
		test.setStartPoint(null); //mandatory for listeners
		test.setStartPoint(startPoint);
		exTrans = new ExtensionTransition(startPoint, firstPage, extensionPoint);
		test.addTransition(exTrans);

		return true;
	}

	public void setStartSubTest(Test subTest) {
		this.startSubTest = subTest;
	}
}
