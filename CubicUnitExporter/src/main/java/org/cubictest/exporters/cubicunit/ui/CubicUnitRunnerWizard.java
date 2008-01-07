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
package org.cubictest.exporters.cubicunit.ui;

import org.eclipse.jface.wizard.Wizard;

public class CubicUnitRunnerWizard extends Wizard {
	
	private SetValuesPage page;

	public CubicUnitRunnerWizard() {
		super();
		setWindowTitle("CubicUnitRunner");
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		page = new SetValuesPage();
		addPage(page);
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}
	
	public int getPort(){
		return page.getPort();
	}
	
	public BrowserType getBrowserType(){
		return page.getBrowserType();
	}
}
