/*******************************************************************************
 * Copyright (c) 2005, 2008  Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.ui;

import org.cubictest.common.utils.ErrorHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class CustomStepWizard extends Wizard {

	
	private NewClassWizardPage classWizard;
	private String name;
	
	public CustomStepWizard() {
		setWindowTitle("Create new CustomTestStep");
	}
	
	@Override
	public void addPages() {
		classWizard = new NewClassWizardPage();
		classWizard.addSuperInterface("org.cubictest.selenium.custom.ICustomTestStep");
		addPage(classWizard);
	}
	
	@Override
	public boolean performFinish() {
		name = classWizard.getTypeName();
		if(name != null && name.length() > 0){
			try {
				classWizard.createType(new NullProgressMonitor());
				return true;
			} catch (CoreException e) {
				ErrorHandler.logAndShowErrorDialog(e);
			} catch (InterruptedException e) {
				ErrorHandler.logAndShowErrorDialog(e);
			}
		}
		return false;
	}
	
	public String getClassName(){
		String packageText = classWizard.getPackageText();
		String typeTypeName = classWizard.getTypeName();
		return packageText + (packageText.length() > 0 ? "." : "") + typeTypeName;
	}

	public String getPath() {
		return classWizard.getModifiedResource().getFullPath().toPortableString();
	}

}
