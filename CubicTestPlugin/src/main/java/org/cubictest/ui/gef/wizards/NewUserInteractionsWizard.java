/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.wizards;

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

	/**
	 * @param transition
	 */
	public NewUserInteractionsWizard(UserInteractionsTransition transition, Test test) {
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
		userActionsPage = new WizardNewUserActionsCreationPage(transition,test);
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
