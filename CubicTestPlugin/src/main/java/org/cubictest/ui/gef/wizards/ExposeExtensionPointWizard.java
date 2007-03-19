/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html 
 */
package org.cubictest.ui.gef.wizards;

import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Wizard for creating a new transition from an extension point.
 * 
 * @author SK Skytteren 
 */
public class ExposeExtensionPointWizard extends Wizard {

	private WizardSelectExtensionPointPage selectExtensionPointPage;
	private SubTest subTest;
	private Test test;
	private ExtensionPoint selectedExtensionPoint;

	/**
	 * @param sourceNode
	 */
	public ExposeExtensionPointWizard(SubTest subTest, Test test) {
		this.subTest = subTest;
		this.test = test;
		setWindowTitle("CubicTest");
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		selectExtensionPointPage = new WizardSelectExtensionPointPage(
				subTest, test, new Listener() {
					public void handleEvent(Event event) {
						selectedExtensionPoint = selectExtensionPointPage
								.getExtensionPoints().get(
										((Combo) event.widget)
												.getSelectionIndex());
						selectExtensionPointPage.setPageComplete(true);
					}
				});
		addPage(selectExtensionPointPage);
	}

	public ExtensionPoint getSelectedExtensionPoint() {
		return selectedExtensionPoint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		return true;
	}

	public boolean canFinish() {
		if (selectedExtensionPoint != null) {
			return true;
		}
		return false;
	}
}
