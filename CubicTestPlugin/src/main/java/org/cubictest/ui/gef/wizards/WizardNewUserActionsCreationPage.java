/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.wizards;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Test;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.eclipse.jface.viewers.StructuredSelection;
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
		UserInteraction action = (UserInteraction) component.getTableViewer().getElementAt(0); 
		component.getTableViewer().editElement(action, 0);
	}

	public void setSelectedExtensionPoint(ExtensionPoint selectedExtensionPoint) {
		
	}
	
}
