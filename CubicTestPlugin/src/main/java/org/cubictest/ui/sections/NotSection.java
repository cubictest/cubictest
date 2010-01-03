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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.cubictest.model.PageElement;
import org.cubictest.ui.gef.command.ChangePageElementNotCommand;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class NotSection extends AbstractPropertySection implements PropertyChangeListener {

	private Composite composite;
	private Button isNot;
	private PageElement pageElement;
	private SelectionListener listener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent e) {}
		public void widgetSelected(SelectionEvent e) {
			ChangePageElementNotCommand command = new ChangePageElementNotCommand();
			command.setPageElement(pageElement);
			command.setNewNot(isNot.getSelection());
			command.setOldNot(pageElement.isNot());
			((GraphicalTestEditor)getPart()).getCommandStack().execute(command);
		}
	};

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		composite = factory.createFlatFormComposite(parent);
		
		Label label = factory.createLabel(composite, "Should NOT exist: ");
		FormData data = new FormData();
		data.left = new FormAttachment(0,0);
		data.width = STANDARD_LABEL_WIDTH + 35;
		label.setLayoutData(data);
		
		data = new FormData();
		data.left = new FormAttachment(label);
		isNot = factory.createButton(composite, "", SWT.CHECK);
		isNot.addSelectionListener(listener);
		isNot.setLayoutData(data);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof EditPart);
		if(this.pageElement != null){
			pageElement.removePropertyChangeListener(this);
		}
		this.pageElement = (PageElement) ((EditPart) input).getModel();
		pageElement.addPropertyChangeListener(this);
	}
	
	@Override
	public void refresh() {
		super.refresh();
		isNot.removeSelectionListener(listener);
		isNot.setSelection(pageElement.isNot());
		isNot.addSelectionListener(listener);
	}
	
	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		pageElement.addPropertyChangeListener(this);		
	}
	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		pageElement.removePropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if(!composite.isDisposed()){
			if(PageElement.NOT.equals(event.getPropertyName())){
				isNot.removeSelectionListener(listener);
				isNot.setSelection(pageElement.isNot());
				isNot.addSelectionListener(listener);
			}
		}
	}
}
