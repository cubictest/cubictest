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

import org.cubictest.model.customstep.CustomTestStepValue;
import org.cubictest.ui.gef.command.ChangeCustomTestStepValueCommand;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class CustomTestStepParameterComposite extends Composite implements PropertyChangeListener{

	private Label key;
	private Text value;
	private CustomTestStepValue paramValue;
	private SelectionListener selectionListener = new SelectionAdapter(){
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			valueChanged();
		}
	};
	private FocusListener focusListener = new FocusAdapter(){
		@Override
		public void focusLost(FocusEvent e) {
			valueChanged();
		}
	};
	private GraphicalTestEditor editor;

	
	public CustomTestStepParameterComposite(Composite parent, 
			TabbedPropertySheetWidgetFactory factory, int labelWidth) {
		super(parent, SWT.NONE);
		
		/*GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		setLayoutData(gridData);
		*/
		setBackground(ColorConstants.white);
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = ITabbedPropertyConstants.HSPACE + 2;
		formLayout.marginHeight = 0;
        formLayout.spacing = 0;
        setLayout(formLayout);
        
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.width = labelWidth;
		key = factory.createLabel(this, "",SWT.BOLD);
		key.setLayoutData(data);

		data = new FormData();
		data.left = new FormAttachment(key,0);
		data.width = 281;
		value = factory.createText(this, "", SWT.BORDER);
		value.setLayoutData(data);
		value.addSelectionListener(selectionListener);
		value.addFocusListener(focusListener );
	}

	private void valueChanged(){
		if(!value.getText().equals(paramValue.getValue())){
			ChangeCustomTestStepValueCommand command = 
				new ChangeCustomTestStepValueCommand(paramValue, value.getText());
			editor.getCommandStack().execute(command);
		}
	}
	
	public void setValue(CustomTestStepValue paramValue, boolean addListener) {
		this.paramValue = paramValue;
		String description = paramValue.getParameter().getDescription();
		key.setText(paramValue.getParameter().getKey());
		key.setToolTipText(description);
		value.setToolTipText(description);
		String val = paramValue.getValue();
		if (val == null)
			val = "";
		value.setText(val);
		if (addListener) {
			paramValue.addListener(this);
		}
	}

	public void removeListeners() {
		paramValue.removeListener(this);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		value.setText(paramValue.getValue());
	}

	public void setPart(GraphicalTestEditor editor) {
		this.editor = editor;
	}
}
