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
