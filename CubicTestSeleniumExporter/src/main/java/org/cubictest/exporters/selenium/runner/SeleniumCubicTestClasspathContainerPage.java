/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *    YE Mao - version up , feature extension
 *******************************************************************************/
package org.cubictest.exporters.selenium.runner;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPageExtension;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SeleniumCubicTestClasspathContainerPage extends WizardPage
		implements IClasspathContainerPage, IClasspathContainerPageExtension {

	private IClasspathEntry containerEntryResult;

	public SeleniumCubicTestClasspathContainerPage() {
		super("CubicTest Selenium Wizard Page");
		setTitle("CubicTest Selenium");
		setDescription("Add CubicTest Selenium Classpath");
		setImageDescriptor(JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
		
		containerEntryResult = JavaCore.newContainerEntry(new Path("CUBICTEST_SELENIUM"));
		
	}

	public boolean finish() {
		return true;
	}

	public IClasspathEntry getSelection() {
		return containerEntryResult;
	}

	public void setSelection(IClasspathEntry containerEntry) {
		if(containerEntry != null)
			containerEntryResult = containerEntry;
	}

	public void createControl(Composite parent) {
		
		Composite composite= new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout(1,true));
		
		Label label= new Label(composite, SWT.NONE);
		label.setFont(composite.getFont());
		label.setText("Click \"Finish\" to add CubicTest Selenium Library");
		label.setLayoutData(new GridData());
		
		setControl(composite);
	}

	public void initialize(IJavaProject project,
			IClasspathEntry[] currentEntries) {
	}

}
