package org.cubictest.ui.sections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.cubictest.model.Identifier;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.command.ChangeIdentiferProbabilityCommand;
import org.cubictest.ui.gef.command.ChangeIdentifierI18nKeyCommand;
import org.cubictest.ui.gef.command.ChangeIdentifierParamKeyCommand;
import org.cubictest.ui.gef.command.ChangeIdentifierValueCommand;
import org.cubictest.ui.gef.command.ChangeIndentifierUseI18nCommand;
import org.cubictest.ui.gef.command.ChangeIndentifierUseParamCommand;
import org.cubictest.ui.gef.command.ChangePageElementTextCommand;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class IdentifierComposite implements PropertyChangeListener {

	private Text value;
	private CCombo probability;
	private Identifier identifier;
	private Label propLabel;
	private Label type;
	private Composite firstRow;
	private Button dirEdit;
	private Button i18n;
	private CCombo i18nCombo;
	private Button param;
	private CCombo paramCombo;
	private Composite composite;
	private Composite secondRow;
	private Label i18nLabel;
	private PageElement pageElement;
	private GraphicalTestEditor editor;
	private Label paramLabel;
	private ValueListener valueListener = new ValueListener();

	public IdentifierComposite(Composite parent, 
			TabbedPropertySheetWidgetFactory factory, int lableWidth) {
		composite = factory.createComposite(parent,SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		
		//First Row
		
		firstRow = factory.createFlatFormComposite(composite);
		//Adding primary idendifier input
		
		FormData data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.width = lableWidth;
		
		dirEdit = factory.createButton(firstRow, "DirectEdit" , SWT.RADIO);
		dirEdit.setLayoutData(data);
		dirEdit.addSelectionListener(dirEditListener);
		
		//Adding type
		data = new FormData();
		data.left = new FormAttachment(dirEdit);
		data.width = lableWidth;
		
		type = factory.createLabel(firstRow, "",SWT.BOLD);
		type.setLayoutData(data);
		//Adding probability
		data = new FormData();
		data.left = new FormAttachment(type);
		
		probability = factory.createCCombo(firstRow);
		probability.setItems(new String[]{"must","should","can","indifferent","cannot","should not","must not"});
		probability.setSize(100, ITabbedPropertyConstants.VSPACE);
		probability.addSelectionListener(probabilityListener);
		probability.setBackground(ColorConstants.white);
		probability.setLayoutData(data);
		
		//Adding propLable: "be"
		data = new FormData();
		data.left = new FormAttachment(probability);
		propLabel = factory.createLabel(firstRow, "be ");
		propLabel.setLayoutData(data);

		//Adding value
		data = new FormData();
		data.left = new FormAttachment(propLabel,ITabbedPropertyConstants.HSPACE);
		data.width = lableWidth * 3;
		
		value = factory.createText(firstRow, "");
		value.setLayoutData(data);
		value.addSelectionListener(valueListener);
		value.addFocusListener(valueListener);
		//Adding secondRow
		secondRow = factory.createFlatFormComposite(composite);
		
		//Adding I18n
		i18nLabel = factory.createLabel(secondRow,"Internationalization:");
		data = new FormData();
		data.left = new FormAttachment(10,ITabbedPropertyConstants.HSPACE);
		i18nLabel.setLayoutData(data);
		
		i18n = factory.createButton(secondRow, "", SWT.CHECK);
		i18n.addSelectionListener(i18nListener);
		data = new FormData();
		data.left = new FormAttachment(i18nLabel, ITabbedPropertyConstants.HSPACE);
		i18n.setLayoutData(data);
		
		i18nCombo = factory.createCCombo(secondRow, SWT.NONE);
		i18nCombo.addSelectionListener(i18nComboListener);
		data = new FormData();
		data.left = new FormAttachment(i18n, ITabbedPropertyConstants.HSPACE);
		i18nCombo.setLayoutData(data);
		
		//Adding parameterisation
		paramLabel = factory.createLabel(secondRow, "Paramterization:");
		data = new FormData();
		data.left = new FormAttachment(i18nCombo,20);
		paramLabel.setLayoutData(data);
		
		param = factory.createButton(secondRow, "", SWT.CHECK);
		param.addSelectionListener(paramListener);
		data = new FormData();
		data.left = new FormAttachment(paramLabel);
		param.setLayoutData(data);
		
		paramCombo = factory.createCCombo(secondRow, SWT.NONE);
		paramCombo.addSelectionListener(paramComboListener);
		data = new FormData();
		data.left = new FormAttachment(param);
		paramCombo.setLayoutData(data);

	}

	public void setIdentifier(PageElement pageElement, Identifier identifier) {
		this.pageElement = pageElement;
		this.identifier = identifier;
		type.setText(identifier.getType().displayValue() + ":");
		value.setText(identifier.getValue());
		
		probability.removeSelectionListener(probabilityListener);
		setProbability(identifier.getProbability());
		probability.addSelectionListener(probabilityListener);
		
		dirEdit.removeSelectionListener(dirEditListener);
		dirEdit.setSelection(pageElement.getDirectEditIdentifier().equals(identifier));
		dirEdit.addSelectionListener(dirEditListener);
		
		Test test = editor.getTest();
		if(test.getAllLanuages() == null || test.getAllLanuages().getLanguages().size() == 0){
			i18nLabel.setVisible(false);
			i18n.setVisible(false);
			i18nCombo.setVisible(false);
		}else{
			i18nLabel.setVisible(true);
			i18n.setVisible(true);
			i18n.removeSelectionListener(i18nListener);
			i18n.setSelection(identifier.useI18n());
			i18n.addSelectionListener(i18nListener);
			i18nCombo.setVisible(true);
			i18nCombo.removeSelectionListener(i18nComboListener);
			i18nCombo.setItems(
				test.getAllLanuages().getAllKeys().toArray(new String[]{}));
			i18nCombo.select(i18nCombo.indexOf(identifier.getI18nKey()));
			i18nCombo.addSelectionListener(i18nComboListener);
		}
		if(test.getParamList() == null || test.getParamList().size() == 0){
			paramLabel.setVisible(false);
			param.setVisible(false);
			paramCombo.setVisible(false);
		}else{
			paramLabel.setVisible(true);
			param.setVisible(true);
			param.removeSelectionListener(paramListener);
			param.setSelection(identifier.useI18n());
			param.addSelectionListener(paramListener);
			paramCombo.setVisible(true);
			paramCombo.removeSelectionListener(paramComboListener);
			paramCombo.setItems(
				test.getParamList().getHeaders().toArray());
			paramCombo.select(paramCombo.indexOf(identifier.getParamKey()));
			paramCombo.addSelectionListener(paramComboListener);
		}
		secondRow.setVisible(paramLabel.getVisible() || i18nLabel.getVisible());
		secondRow.pack(false);
		secondRow.redraw();
	}
	
	private void setProbability(int newProbability){
		value.setEnabled(true);
		propLabel.setEnabled(true);
		if(newProbability > 66)
			probability.select(0);
		else if(newProbability > 33)
			probability.select(1);
		else if(newProbability > 0)
			probability.select(2);
		else if(newProbability == 0){
			probability.select(3);
			value.setEnabled(false);
			propLabel.setEnabled(false);
		}else if(newProbability > -34)
			probability.select(4);
		else if(newProbability > -67)
			probability.select(5);
		else
			probability.select(6);
	}

	public void setVisible(boolean visible) {
		composite.setVisible(visible);
	}
	/**
	 * The identifier has changed
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if(!composite.isDisposed()){
			String eventName = event.getPropertyName();
			if(Identifier.VALUE.equals(eventName)){
				value.setText((String) event.getNewValue());
			}else if(Identifier.ACTUAL.equals(eventName)){
				//TODO
			}else if(Identifier.TYPE.equals(eventName)){
				
			}else if(Identifier.PROBABILITY.equals(eventName)){
				probability.removeSelectionListener(probabilityListener);
				setProbability((Integer) event.getNewValue());
				probability.addSelectionListener(probabilityListener);
			}else if(PageElement.DIRECT_EDIT_IDENTIFIER.equals(eventName)){
				dirEdit.removeSelectionListener(dirEditListener);
				dirEdit.setSelection(pageElement.getDirectEditIdentifier().equals(identifier));
				dirEdit.addSelectionListener(dirEditListener);
			}
		}
	}

	public void aboutToBeHidden() {
		if(identifier != null)
			identifier.removePropertyChangeListener(this);
		if(pageElement != null)
			pageElement.removePropertyChangeListener(this);
	}

	public void aboutToBeShown() {
		if(identifier != null)
			identifier.addPropertyChangeListener(this);
		if(pageElement != null)
			pageElement.addPropertyChangeListener(this);
	}
	
	public void setPart(GraphicalTestEditor editor) {
		this.editor = editor;
	}
	
	private SelectionListener probabilityListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			int index = probability.getSelectionIndex();
			int prob;
			value.setEnabled(true);
			propLabel.setEnabled(true);
			switch (index) {
				case 0:
					prob = 100;
					break;
				case 1: 
					prob = 66;
					break;
				case 2:
					prob = 33;
					break;
				case 3:
					prob = 0;
					value.setEnabled(false);
					propLabel.setEnabled(false);
					break;
				case 4:
					prob = -33;
					break;
				case 5:
					prob = -66;
					break;
				case 6:
					prob = -100;
					break;	
				default:
					prob = 0;
					break;
			}
			ChangeIdentiferProbabilityCommand command = 
				new ChangeIdentiferProbabilityCommand();
			command.setIdentifier(identifier);
			command.setNewProbability(prob);
			command.setOldProbability(identifier.getProbability());
			executeCommand(command);
			
		}
		public void widgetDefaultSelected(SelectionEvent e) {}
	};
	
	private SelectionListener dirEditListener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent e) {}
		public void widgetSelected(SelectionEvent e) {
			if(dirEdit.getSelection()){
				pageElement.setDirectEditIdentifier(identifier);
			}
		}
	};
	
	private class ValueListener implements SelectionListener, FocusListener{
		public void widgetDefaultSelected(SelectionEvent e) {
			updateValue();
		}
		public void widgetSelected(SelectionEvent e) {}
		public void focusGained(FocusEvent e) {}
		public void focusLost(FocusEvent e) {
			updateValue();
		}
		private void updateValue(){
			if(!value.getText().equals( identifier.getValue())){
				if(pageElement.getDirectEditIdentifier().equals(identifier)){
					ChangePageElementTextCommand command = new ChangePageElementTextCommand();
					command.setNewText(value.getText());
					command.setOldText(identifier.getValue());
					command.setPageElement(pageElement);
					executeCommand(command);
				}else{
					ChangeIdentifierValueCommand command = new ChangeIdentifierValueCommand();
					command.setIdentifer(identifier);
					command.setNewValue(value.getText());
					command.setOldValue(identifier.getValue());
					executeCommand(command);
				}
			}
		}
	};

	private SelectionListener i18nListener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent e) {}
		public void widgetSelected(SelectionEvent e) {
			if(i18n.getSelection() != identifier.useI18n()){
				ChangeIndentifierUseI18nCommand command = 
					new ChangeIndentifierUseI18nCommand();
				command.setNewUseI18n(i18n.getSelection());
				command.setOldUseI18n(identifier.useI18n());
				command.setIdentifier(identifier);
				executeCommand(command);
			}
		}
	};
	
	private SelectionListener i18nComboListener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent e) {}
		public void widgetSelected(SelectionEvent e) {
			String i18nKey = i18nCombo.getItem(i18nCombo.getSelectionIndex());
			ChangeIdentifierI18nKeyCommand command = new ChangeIdentifierI18nKeyCommand();
			command.setIndentifier(identifier);
			command.setNewI18nKey(i18nKey);
			command.setOldI18nKey(identifier.getI18nKey());
			executeCommand(command);
		}
	};
	
	private SelectionListener paramListener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent e) {}
		public void widgetSelected(SelectionEvent e) {
			if(param.getSelection() != identifier.useParam()){
				ChangeIndentifierUseParamCommand command = 
					new ChangeIndentifierUseParamCommand();
				command.setNewUseParam(param.getSelection());
				command.setOldUseParam(identifier.useParam());
				command.setIdentifier(identifier);
				executeCommand(command);
			}
		}
	};
	private SelectionListener paramComboListener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent e) {}
		public void widgetSelected(SelectionEvent e) {
			String paramKey = paramCombo.getItem(paramCombo.getSelectionIndex());
			ChangeIdentifierParamKeyCommand command = new ChangeIdentifierParamKeyCommand();
			command.setIndentifier(identifier);
			command.setNewParamKey(paramKey);
			command.setOldParamKey(identifier.getParamKey());
			executeCommand(command);
		}
	};
	
	private void executeCommand(Command command){
		pageElement.removePropertyChangeListener(this);
		identifier.removePropertyChangeListener(this);
		editor.getCommandStack().execute(command);
		identifier.addPropertyChangeListener(this);
		pageElement.addPropertyChangeListener(this);
	}
}
