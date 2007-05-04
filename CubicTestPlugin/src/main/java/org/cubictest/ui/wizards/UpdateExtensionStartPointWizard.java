/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import java.util.Map;

import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.pluginsupport.CustomElementLoader;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.cubictest.ui.utils.WizardUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;


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
		extentionStartPointSelectorPage.setTest(startSubTest);
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
		final ExtensionPoint extensionPoint = extentionStartPointSelectorPage.getExtensionPoint();
		final IFile file = extentionStartPointSelectorPage.getExtentionPointFile();

		Transition inTrans = firstPage.getInTransition();
		if (inTrans != null) {
			firstPage.removeInTransition();
			test.removeTransition(inTrans);
		}

		ConnectionPoint startPoint = WizardUtils.createExtensionStartPoint(file, extensionPoint, test);
		test.setStartPoint(startPoint);
		exTrans = new ExtensionTransition(startPoint, firstPage, extentionStartPointSelectorPage.getExtensionPoint());
		test.addTransition(exTrans);
		return true;
	}

	@Override
	public void populateExtensionPointMap(IContainer resource, Map<ExtensionPoint, IFile> map, IResourceMonitor monitor, CustomElementLoader loader) throws CoreException {
		for(ExtensionPoint ep : startSubTest.getAllExtensionPoints()){
			map.put(ep, startSubTest.getFile());
		}
	}

	public void setStartSubTest(Test subTest) {
		this.startSubTest = subTest;
	}
}
