/*
 * Created on 17.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.sections;

import org.cubictest.model.Test;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.ui.gef.controller.UserInteractionsTransitionEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.wizards.UserActionComponent;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


public class UserActionsSection extends AbstractPropertySection {

	private UserInteractionsTransition actions;
	private UserActionComponent component;
	private Composite parent = null;
	private boolean created = false;
	private Composite composite;
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

		if(actions != null && !created && parent != null){
			component = new UserActionComponent(actions, test);
			composite = component.createControl(parent);
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
