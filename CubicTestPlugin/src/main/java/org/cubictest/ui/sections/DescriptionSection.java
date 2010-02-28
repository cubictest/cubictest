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
package org.cubictest.ui.sections;

import org.cubictest.model.IDescription;
import org.cubictest.ui.gef.command.ChangeDescriptionCommand;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class DescriptionSection extends AbstractPropertySection {

	private Composite composite;
	private Text descText;
	private IDescription description;

	private FocusListener listener = new FocusListener(){
		public void focusLost(FocusEvent e) {
			if(!descText.getText().equals( description.getDescription())){
				GraphicalTestEditor part = (GraphicalTestEditor) getPart();
				ChangeDescriptionCommand command = new ChangeDescriptionCommand();
				command.setDesctription(description);
				command.setNewDescription(descText.getText());
				command.setOldDescription(description.getDescription());
				part.getCommandStack().execute(command);
			}			
		}

		public void focusGained(FocusEvent e) {}
	};
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		
		composite = factory.createFlatFormComposite(parent);
		
		Label label = factory.createLabel(composite, "Description:");
		
		FormData data = new FormData();
		data.left = new FormAttachment(0,0); 
		data.width = STANDARD_LABEL_WIDTH + 35;
		label.setLayoutData(data);
		
		descText = factory.createText(composite,"",SWT.MULTI|SWT.V_SCROLL);
		
		data = new FormData();
		data.left = new FormAttachment(label);
		data.width = 300;
		data.height = 40;
		descText.setLayoutData(data);
		
		descText.addFocusListener(listener);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof EditPart);
		this.description = (IDescription) ((EditPart) input).getModel();
	}
	
	@Override
	public void refresh() {
		super.refresh();
		descText.setText(this.description.getDescription());
	}

}
