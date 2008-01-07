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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Page for showing a summary of the new project being created.
 * It also provides a checkbox for launching the "create new test" wizard.
 * @author chr_schwarz
 */
public class NewProjectSummaryPage extends WizardPage implements Listener {
	private Button checkbox;
	
	public NewProjectSummaryPage() {
		super("newProjectSummaryPage");
		setPageComplete(false);
	}
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		
		GridLayout layout  = new GridLayout(2, false);
		layout.verticalSpacing = 4;
		container.setLayout(layout);
				
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		
		checkbox = new Button(container, SWT.CHECK);
		checkbox.setText("Create a new test in this project when I press \"Finish\"");
		checkbox.setSelection(true);
		checkbox.addListener(SWT.Selection,this);
		

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;

		setControl(container);
	}	

	public void handleEvent(Event event) {
	}
	
	public boolean getCreateTestOnFinish() {
		return checkbox.getSelection();
	}
	
}
