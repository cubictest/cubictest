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
package org.cubictest.ui.customstep;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.cubictest.common.utils.UserInfo;
import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.CustomTestStepParameterList;
import org.cubictest.ui.customstep.command.ChangeCustomTestStepParameterDescriptionCommand;
import org.cubictest.ui.customstep.command.ChangeCustomTestStepParameterKeyCommand;
import org.cubictest.ui.customstep.command.CreateCustomTestStepParameterCommand;
import org.cubictest.ui.customstep.command.DeleteCustomTestStepParameterCommand;
import org.cubictest.ui.customstep.section.CustomStepSection;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Table composite for Custom Step parameters.
 * 
 * @author SK Skytteren
 */
public class ParameterTableComposite extends Composite implements PropertyChangeListener {

	private static final String KEY = "Key";
	private static final String DESCRIPTION = "Description";
	private Table table = null;
	private TableViewer tableViewer = null;
	private Button addButton = null;
	private Button deleteButton = null;
	private SelectionListener addListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			CreateCustomTestStepParameterCommand command = 
				new CreateCustomTestStepParameterCommand();
			command.setParameters(parameters);
			commandStack.execute(command);
		}
	}; 
	private SelectionListener deleteListener = new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {	
			if(!tableViewer.getSelection().isEmpty()){
				CompoundCommand compoundCommand = new CompoundCommand();
				for( Object selected :((IStructuredSelection)tableViewer.getSelection()).toList()){
					DeleteCustomTestStepParameterCommand command = new DeleteCustomTestStepParameterCommand();
					command.setParameters(parameters);
					command.setParameter((CustomTestStepParameter)selected);
					compoundCommand.add(command);
				}
				commandStack.execute(compoundCommand);
			}
		}
	}; 
	private CustomTestStepParameterList parameters;  
	private ICellModifier modifier = new ICellModifier(){
		public boolean canModify(Object element, String property) {
			return true;
		}

		public Object getValue(Object element, String property) {
			if(DESCRIPTION.equals(property)){
				return ((CustomTestStepParameter)element).getDescription();
			}else if(KEY.equals(property)){
				return ((CustomTestStepParameter)element).getKey();
			}
			return null;
		}

		public void modify(Object element, String property, Object value) {
			if(element == null)
				return;
			CustomTestStepParameter parameter = 
				(CustomTestStepParameter)((TableItem)element).getData();
			if(DESCRIPTION.equals(property) && !parameter.getDescription().equals(value)){
				ChangeCustomTestStepParameterDescriptionCommand command = 
					new ChangeCustomTestStepParameterDescriptionCommand();
				command.setParameter(parameter);
				command.setNewDescription((String)value);
				commandStack.execute(command);
			}else if(KEY.equals(property) && 
					!parameter.getKey().equals(value)){
				if(parameters.isAvailableKey((String) value)){
					ChangeCustomTestStepParameterKeyCommand command = new ChangeCustomTestStepParameterKeyCommand();
					command.setParameter(parameter);
					command.setNewKey((String) value);
					commandStack.execute(command);
				}else
					UserInfo.showInfoDialog("Key already in use, pick another one!");
			}
		}		
	};
	
	IContentProvider contentProvider = new IStructuredContentProvider(){
		public Object[] getElements(Object inputElement) {
			return ((CustomTestStepParameterList)inputElement).toArray();
		}
		public void dispose() {}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	};
	private CommandStack commandStack;
	
	public ParameterTableComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		
		setBackground(ColorConstants.white);
		
		FormLayout layout = new FormLayout();
		
		Label paramLabel = new Label(this,SWT.NONE);
		paramLabel.setText("Parameters:");
		paramLabel.setBackground(ColorConstants.white);
		
		FormData layoutData = new FormData();
		layoutData.left = new FormAttachment(0,0);
		layoutData.width = CustomStepSection.STANDARD_LABEL_WIDTH;
		paramLabel.setLayoutData(layoutData);
		
		setLayout(layout);
		FormData formData = new FormData();
		formData.left = new FormAttachment(paramLabel);
		formData.width = CustomStepSection.STANDARD_LABEL_WIDTH * 2 - 9;
		formData.height = 200;
		
		table = new Table(this, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
				SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		table.setHeaderVisible(true);
		table.setLayoutData(formData);
		table.setLinesVisible(true);
		
		tableViewer = new TableViewer(table);
		tableViewer.setCellModifier(modifier);
		tableViewer.setContentProvider(contentProvider);
		TableColumn keyColumn = new TableColumn(table, SWT.LEFT, 0);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(new String[]{KEY,DESCRIPTION});
		tableViewer.setCellEditors(createCellEditors());
		tableViewer.setLabelProvider(new ParameterLabelProvider());
		keyColumn.setWidth(100);
		keyColumn.setText(KEY);
		keyColumn.setResizable(true);
		TableColumn descriptionColumn = new TableColumn(table, SWT.LEFT, 1);
		descriptionColumn.setWidth(300);
		descriptionColumn.setText(DESCRIPTION);
		descriptionColumn.setResizable(true);
		
		Composite buttons = new Composite(this,SWT.NONE);
		buttons.setBackground(ColorConstants.white);
		
		formData = new FormData();
		formData.left = new FormAttachment(table);
		buttons.setLayoutData(formData);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		buttons.setLayout(gridLayout);
		
		GridData gridData = new GridData();
		
		Label filler = new Label(buttons, SWT.NONE);
		filler.setLayoutData(gridData);
		
		addButton = new Button(buttons, SWT.PUSH);
		addButton.setText("Add");
		addButton.setToolTipText("Add another config parameter");
		//addButton.setSize(new Point(50, 23));
		addButton.addSelectionListener(addListener );
		
		gridData = new GridData();
		gridData.widthHint = 50;
		addButton.setLayoutData(gridData);
		
		deleteButton = new Button(buttons, SWT.PUSH);
		deleteButton.setText("Delete");
		//deleteButton.setSize(new Point(50, 23));
		deleteButton.addSelectionListener(deleteListener);
		
		gridData = new GridData();
		gridData.widthHint = 60;
		deleteButton.setLayoutData(gridData);		
	}

	private CellEditor[] createCellEditors() {
		CellEditor[] editors = new CellEditor[3];	
		editors[0] = new TextCellEditor(table);
		editors[1] = new TextCellEditor(table);
		editors[2] = new TextCellEditor(table);
		return editors;
	}

	public void setCustemTestStepParameters(CustomTestStepParameterList parameters) {
		super.setData(parameters);
		this.parameters = parameters;
		parameters.addChangeListener(this);		
		tableViewer.setInput(parameters);
	}

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
	}
	class ParameterLabelProvider extends LabelProvider implements ITableLabelProvider {
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if(columnIndex == 0)
				return ((CustomTestStepParameter)element).getKey();
			else if(columnIndex == 1)
				return ((CustomTestStepParameter)element).getDescription();
			return "";
		}
	}
	public void propertyChange(PropertyChangeEvent event) {
		if(CustomTestStepParameterList.ADD.equals(event.getPropertyName())){
			tableViewer.add(event.getNewValue());
		}else if(CustomTestStepParameterList.DELETE.equals(event.getPropertyName())){
			tableViewer.remove(event.getOldValue());
		}else if(CustomTestStepParameterList.NEW.equals(event.getPropertyName())){
			tableViewer.add(event.getNewValue());
			tableViewer.editElement(event.getNewValue(), 0 );
		}else{
			tableViewer.setInput(parameters);
		}
	}
	
}
