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
import org.eclipse.jface.wizard.Wizard;


/**
 * Wizard for creating a new <code>UserInteractionsTransition</code>.
 * 
 * @author SK Skytteren
 * @author Christian Schwarz
 */
public class NewUserInteractionsWizard extends Wizard {

	private WizardNewUserActionsCreationPage userActionsPage;
	private UserInteractionsTransition transition;
	private Test test;
	private final PageElement selectedPageElement;

	/**
	 * @param transition
	 * @param selectedPageElement 
	 */
	public NewUserInteractionsWizard(UserInteractionsTransition transition, Test test, PageElement selectedPageElement) {
		this.selectedPageElement = selectedPageElement;
		setWindowTitle("CubicTest");
		this.transition = transition;
		this.test = test;
		setNeedsProgressMonitor(true);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		super.addPages();
		userActionsPage = new WizardNewUserActionsCreationPage(transition, test, selectedPageElement);
		userActionsPage.setTitle("New User Interaction");
		userActionsPage.setDescription("Choose page element and interaction type.");
		addPage(userActionsPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		return true;
	}
	
    public boolean canFinish() {
    	return true;
    }	
}
