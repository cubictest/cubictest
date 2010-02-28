/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.wizards;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;

/**
 * The "New" wizard page allows setting the container for
 * the new file as well as the file name. The page
 * will only accept file name without the extension OR
 * with the extension that matches the expected one (aat or one set).
 */

public class TestDetailsPage extends NewFileWithNameAndDescriptionPage {
	
	public TestDetailsPage(ISelection selection, String newItemType) {
		super(selection, newItemType);
	}
	
	@Override
	public IWizardPage getNextPage() {
		return getWizard().getPage(StartPointTypeSelectionPage.NAME);
	}

}