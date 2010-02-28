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

import org.cubictest.model.Test;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class SelectLanguageInTestWizard extends Wizard implements INewWizard{

	private WizardSelectLanuageInTestPage selectLanguagePage;
	private Test test;
	private String fileName;
	private String languageName;

	public SelectLanguageInTestWizard(Test test) {
		this.test = test;
	}
	
	/**
	 * Adding the pages to the wizard.
	 */
	@Override
	public void addPages() {
		selectLanguagePage = new WizardSelectLanuageInTestPage(test);
		addPage(selectLanguagePage);
	}
	
	@Override
	public boolean performFinish() {
		fileName = selectLanguagePage.getFileName();
		languageName = selectLanguagePage.getLanguageName();
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
		if(!file.exists())
			return false;
		if(languageName.length() > 0)
			return true;
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}

	public String getFileName() {
		return fileName;
	}

	public String getLanguageName() {
		return languageName;
	}

}
