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
package org.cubictest.ui.sections;

import org.cubictest.model.Test;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.controller.UserInteractionsTransitionEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.wizards.UserInteractionsComponent;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property view section of a user interaction.
 * Configured in plugin.xml file.
 * 
 * @author chr_schwarz
 */
public class UserActionsSection extends AbstractPropertySection {

	private UserInteractionsTransition actions;
	private UserInteractionsComponent component;
	private Composite parent = null;
	private boolean created = false;
	private Test test;

	public UserActionsSection() {
		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof UserInteractionsTransitionEditPart);
		actions = (UserInteractionsTransition) ((UserInteractionsTransitionEditPart) input).getModel();
		Assert.isTrue(part instanceof GraphicalTestEditor);
		test = ((GraphicalTestEditor)part).getTest();
		TestEditPart testPart = (TestEditPart) ((GraphicalTestEditor) part).getGraphicalViewer().getContents();

		if(actions != null && !created && parent != null){
			component = new UserInteractionsComponent(actions, test, testPart, true);
			component.createControl(parent);
			this.parent.setSize(400, 300);
			created  = true;
		}
	}
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		if (parent != null) {
			super.createControls(parent, aTabbedPropertySheetPage);
			this.parent = parent;
		}
	}

	@Override
	public void refresh() {
		super.refresh();
	}
	
	@Override
	public boolean shouldUseExtraSpace() {
		return true;
	}
}
