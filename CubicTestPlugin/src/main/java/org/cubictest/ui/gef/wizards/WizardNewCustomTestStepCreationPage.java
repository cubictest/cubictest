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
package org.cubictest.ui.gef.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * @author SK Skytteren
 * Wizard page for creating an new Abstract Page.
 */
public class WizardNewCustomTestStepCreationPage extends WizardPage {

	private Text containerText;
	private String defaultContainerName;	
	
	/**
	 * 
	 * @param page
	 */
	public WizardNewCustomTestStepCreationPage() {
		super("New Custom Test Step");
		setPageComplete(true);
	}
	
	public void setContainerName(String name){
		this.defaultContainerName = name;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {

		Composite content = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 4;
		content.setLayout(layout);
		
		Label fill = new Label(content, SWT.NULL);
		fill.setText("Set the Custom Test Step name:");
		
		containerText = new Text(content, SWT.BORDER | SWT.SINGLE);
		if (defaultContainerName != null) {
			containerText.setText(defaultContainerName);
		}
		containerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				setPageComplete(containerText.getText() != "");
			}
		});
		
		setControl(content);
	}
	
	public String getText(){
		return containerText.getText();
	}
}
