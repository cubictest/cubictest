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

import org.cubictest.model.AbstractPage;
import org.cubictest.model.NamePropertyObject;
import org.cubictest.ui.gef.command.ChangeNameCommand;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class NameSection extends AbstractPropertySection {

	private Composite composite;
	private Text nameText;
	private NamePropertyObject namePropertyObject;
	
	private PropertyChangeListener namePropertyObjectListener = new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt) {
			if(!composite.isDisposed()){
				refresh();
			}
		}
	};

	private FocusListener focusListener = new FocusListener(){
		public void focusLost(FocusEvent e) {
			textChanged();			
		}
		public void focusGained(FocusEvent e) {}
	};
	
	private void textChanged(){
		if(!nameText.getText().equals( namePropertyObject.getName())){
			GraphicalTestEditor part = (GraphicalTestEditor) getPart();
			ChangeNameCommand command = new ChangeNameCommand();
			command.setNamePropertyObject(namePropertyObject);
			command.setNewName(nameText.getText());
			command.setOldName(namePropertyObject.getName());
			namePropertyObject.removePropertyChangeListener(namePropertyObjectListener);
			part.getCommandStack().execute(command);
			namePropertyObject.addPropertyChangeListener(namePropertyObjectListener);
		}
	}
	
	private SelectionListener selectionListener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent e) {
			textChanged();
		}
		public void widgetSelected(SelectionEvent e) {}		
	};
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		
		composite = factory.createFlatFormComposite(parent);
		
		Label label = factory.createLabel(composite, "Name:");
		
		FormData data = new FormData();
		data.left = new FormAttachment(0,0); 
		data.width = STANDARD_LABEL_WIDTH + 35;
		label.setLayoutData(data);
		
		nameText = factory.createText(composite,"",SWT.NONE);
		
		data = new FormData();
		data.left = new FormAttachment(label);
		data.width = 300;
		//data.height = 40;
		nameText.setLayoutData(data);
		
		nameText.addFocusListener(focusListener);
		nameText.addSelectionListener(selectionListener);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		if(namePropertyObject != null){
			namePropertyObject.removePropertyChangeListener(namePropertyObjectListener);
		}
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof EditPart);
		this.namePropertyObject = (NamePropertyObject) ((EditPart) input).getModel();
		this.namePropertyObject.addPropertyChangeListener(namePropertyObjectListener);
	}
	
	@Override
	public void refresh() {
		super.refresh();
		nameText.setText(namePropertyObject.getName());
	}
}
