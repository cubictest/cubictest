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

import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Test;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;



/**
 * @author SK Skytteren
 * Wizard page for creating a new Form connection/input.
 */
public class WizardNewUserActionsCreationPage extends WizardPage {

	private UserInteractionsComponent component;

	protected WizardNewUserActionsCreationPage(UserInteractionsTransition actions, Test test) {
		super("userActionsInputPage");
		component = new UserInteractionsComponent(actions, test, null, false);
	}

	public void createControl(Composite parent) {
		Composite composite = component.createControl(parent);
		setControl(composite);
	}

	public void setSelectedExtensionPoint(ExtensionPoint selectedExtensionPoint) {
		
	}
	
}
