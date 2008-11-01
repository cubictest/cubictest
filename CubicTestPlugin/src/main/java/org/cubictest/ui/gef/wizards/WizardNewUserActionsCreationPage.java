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

import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.UserInteractionsTransition;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;



/**
 * @author SK Skytteren
 * Wizard page for creating a new Form connection/input.
 */
public class WizardNewUserActionsCreationPage extends WizardPage {

	private UserInteractionsComponent component;
	private Text nameText;
	private final UserInteractionsTransition actions;

	protected WizardNewUserActionsCreationPage(UserInteractionsTransition actions, Test test, PageElement selectedPageElement) {
		super("userActionsInputPage");
		this.actions = actions;
		component = new UserInteractionsComponent(actions, test, null, false, selectedPageElement);
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 4;

		Composite userInteratcion = component.createControl(container);
		GridData gd = new GridData(GridData.FILL_BOTH);
		userInteratcion.setLayoutData(gd);

		Composite nameContainer = new Composite(container, SWT.NULL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		nameContainer.setLayoutData(gd);
		
		FormLayout formLayout = new FormLayout();
		formLayout.spacing = 9;
		formLayout.marginLeft = 5;
		nameContainer.setLayout(formLayout);
		
		Label nameLabel = new Label(nameContainer, SWT.NULL);
		nameLabel.setText("User interaction name:");
		
		FormData data = new FormData();
		data.left = new FormAttachment(0,0); 
		data.width = 140;
		nameLabel.setLayoutData(data);
		
		nameText = new Text(nameContainer, SWT.BORDER | SWT.SINGLE);
		
		data = new FormData();
		data.left = new FormAttachment(nameLabel);
		data.width = 200;
		nameText.setLayoutData(data);
		
		nameText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				actions.setName(nameText.getText());
			}
		});
		
		setControl(container);
	}
}
