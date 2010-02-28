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

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.cubictest.model.IdentifierType.LABEL;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.Logger;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Moderator;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.command.ChangeDirectEditIdentifierCommand;
import org.cubictest.ui.gef.command.ChangeFrameTypeCommand;
import org.cubictest.ui.gef.command.ChangeIdentiferModeratorCommand;
import org.cubictest.ui.gef.command.ChangeIdentiferProbabilityCommand;
import org.cubictest.ui.gef.command.ChangeIdentiferValueToActualCommand;
import org.cubictest.ui.gef.command.ChangeIdentifierI18nKeyCommand;
import org.cubictest.ui.gef.command.ChangeIdentifierParamKeyCommand;
import org.cubictest.ui.gef.command.ChangeIdentifierValueCommand;
import org.cubictest.ui.gef.command.ChangeIndentifierUseI18nCommand;
import org.cubictest.ui.gef.command.ChangeIndentifierUseParamCommand;
import org.cubictest.ui.gef.command.ChangePageElementTextCommand;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

/**
 * Composite for editing identifier of a Page Element.
 * 
 * @author SK Skytteren 
 */
public class IdentifierComposite extends Composite implements PropertyChangeListener {

	private static final String MUST_NOT = "must not";
	private static final String SHOULD_NOT = "should not";
	private static final String CANNOT = "cannot";
	private static final String INDIFFERENT = "doesn't matter";
	private static final String CAN = "can";
	private static final String SHOULD = "should";
	private static final String MUST = "must";
	
	private static final String EQUAL = "be equal to";
	private static final String BEGIN = "begin with";
	private static final String END = "end with";
	private static final String CONTAIN = "contain";
	
	private static final String FRAME = "frame";
	private static final String IFRAME = "iframe";
	
	private Text value;
	private Label booleanLabel;
	private CCombo probability;
	private Identifier identifier;
	private CCombo moderator;
	private CCombo frameType;
	private Label type;
	private Composite firstRow;
	private Button dirEdit;
	private Button i18n;
	private CCombo i18nCombo;
	private Button param;
	private CCombo paramCombo;
	private Composite thirdRow;
	private Label i18nLabel;
	private PageElement pageElement;
	private GraphicalTestEditor editor;
	private Label paramLabel;
	private ValueListener valueListener = new ValueListener();
	private int listeners = 0;
	private Test test;
	private Label actualLabel;
	private Text actualValue;
	private Button updateButton;
	private Composite secondRow;
	private Composite line;

	public IdentifierComposite(Composite parent, TabbedPropertySheetWidgetFactory factory, int labelWidth) {
		super(parent, SWT.NONE);
		
		setBackground(ColorConstants.white);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 2;
		this.setLayout(layout);
		
		//First Row (Direct edit, ID type, probability, value), always visible:
		firstRow = new Composite(this,SWT.NONE);
		FormData data = null;
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = ITabbedPropertyConstants.HSPACE + 2;
		formLayout.marginHeight = 0;
        formLayout.spacing = 0;
        firstRow.setLayout(formLayout);
        firstRow.setBackground(ColorConstants.white);
		//DirectEdit radiobutton must be last in row to get correct focus behaviour
		
		//Adding type
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.width = labelWidth;
		type = factory.createLabel(firstRow, "",SWT.BOLD);
		type.setLayoutData(data);
		
		//Adding probability
		data = new FormData();
		data.left = new FormAttachment(type, 0);
		probability = factory.createCCombo(firstRow, SWT.BORDER);
		//probability.setItems(new String[]{MUST, SHOULD, CAN, INDIFFERENT, CANNOT, SHOULD_NOT, MUST_NOT});
		probability.setItems(new String[]{MUST, INDIFFERENT, MUST_NOT});
		probability.setSize(140, ITabbedPropertyConstants.VSPACE);
		probability.addSelectionListener(probabilityListener);
		probability.setBackground(ColorConstants.white);
		probability.setLayoutData(data);
		
		//Adding label: "moderator"
		data = new FormData();
		data.left = new FormAttachment(probability, 7);
		moderator = factory.createCCombo(firstRow, SWT.BORDER);
		moderator.setItems(new String[]{EQUAL, BEGIN, END, CONTAIN});
		moderator.setSize(140, ITabbedPropertyConstants.VSPACE);
		moderator.addSelectionListener(moderatorListener);
		moderator.setBackground(ColorConstants.white);
		moderator.setLayoutData(data);
		
		//Adding label: "frameType" used for frames
		data = new FormData();
		data.left = new FormAttachment(moderator, 7);
		frameType = factory.createCCombo(firstRow, SWT.BORDER);
		frameType.setItems(new String[]{FRAME, IFRAME});
		frameType.setSize(140, ITabbedPropertyConstants.VSPACE);
		frameType.addSelectionListener(frameListener);
		frameType.setBackground(ColorConstants.white);
		frameType.setLayoutData(data);

		//Adding label for value ("true")
		data = new FormData();
		data.left = new FormAttachment(moderator,ITabbedPropertyConstants.HSPACE);
		data.width = labelWidth * 3;
		booleanLabel = factory.createLabel(firstRow, "true");
		booleanLabel.setLayoutData(data);
		booleanLabel.setVisible(false);

		//Id has value (is not boolean). Adding value input:
		data = new FormData();
		data.left = new FormAttachment(moderator,ITabbedPropertyConstants.HSPACE);
		data.width = labelWidth * 3;
		value = factory.createText(firstRow, "", SWT.BORDER);
		value.setLayoutData(data);
		value.addModifyListener(valueListener);
		
		//Adding DirectEdit idendifier input (must be last in row to get correct focus behaviour)
		data = new FormData();
		data.left = new FormAttachment(value, ITabbedPropertyConstants.HSPACE);
		dirEdit = factory.createButton(firstRow, "Show in editor" , SWT.RADIO);
		dirEdit.setLayoutData(data);
		dirEdit.addSelectionListener(dirEditListener);
		dirEdit.setToolTipText("Select for direct edit in the graphical test editor");
		
		//Actual
		secondRow = new Composite(this,SWT.NONE){
			@Override
			public Point computeSize(int wHint, int hHint, boolean b) {
				Point point = super.computeSize(wHint, hHint, b);
				if(!secondRow.isVisible()){
					point.y = 0;
				}
				return point;
			}
		};
		secondRow.setBackground(ColorConstants.white);
        secondRow.setLayout(formLayout);
		
		data = new FormData();
		data.left = new FormAttachment(9,0);
		actualLabel = factory.createLabel(secondRow, "actually found a different value ");
		actualLabel.setLayoutData(data);
		actualLabel.setVisible(false);
		data = new FormData();
		data.left = new FormAttachment(actualLabel,ITabbedPropertyConstants.HSPACE);
		data.width = labelWidth * 3;
		actualValue = factory.createText(secondRow, "", SWT.READ_ONLY);
		actualValue.setLayoutData(data);
		actualValue.setVisible(false);
		
		data = new FormData();
		data.left = new FormAttachment(actualValue,ITabbedPropertyConstants.HSPACE);
		updateButton = factory.createButton(secondRow, "Update", SWT.PUSH);
		updateButton.setLayoutData(data);
		updateButton.setVisible(false);
		updateButton.addSelectionListener(updateButtonListener);
		
		//Third Row (i18n and params):
		thirdRow = new Composite(this,SWT.NONE){
			@Override
			public Point computeSize(int wHint, int hHint, boolean b) {
				Point point = super.computeSize(wHint, hHint, b);
				if(!thirdRow.isVisible()){
					point.y = 0;
				}
				return point;
			}
		};
		
		thirdRow.setLayout(formLayout);
		thirdRow.setBackground(ColorConstants.white);
		
		//Adding I18n
		i18nLabel = factory.createLabel(thirdRow,"Internationalisation:");
		data = new FormData();
		data.left = new FormAttachment(thirdRow, labelWidth);
		i18nLabel.setLayoutData(data);
		i18n = factory.createButton(thirdRow, "", SWT.CHECK);
		i18n.addSelectionListener(i18nListener);
		data = new FormData();
		data.left = new FormAttachment(i18nLabel, ITabbedPropertyConstants.HSPACE);
		i18n.setLayoutData(data);
		i18nCombo = factory.createCCombo(thirdRow, SWT.BORDER);
		i18nCombo.addSelectionListener(i18nComboListener);
		data = new FormData();
		data.left = new FormAttachment(i18n, ITabbedPropertyConstants.HSPACE);
		i18nCombo.setLayoutData(data);
		
		//Adding parameterisation
		paramLabel = factory.createLabel(thirdRow, "Parameterisation: ");
		data = new FormData();
		data.left = new FormAttachment(i18nCombo,ITabbedPropertyConstants.HSPACE + 20);
		paramLabel.setLayoutData(data);
		param = factory.createButton(thirdRow, "", SWT.CHECK);
		param.addSelectionListener(paramListener);
		data = new FormData();
		data.left = new FormAttachment(paramLabel, ITabbedPropertyConstants.HSPACE);
		param.setLayoutData(data);
		paramCombo = factory.createCCombo(thirdRow, SWT.BORDER);
		paramCombo.addSelectionListener(paramComboListener);
		data = new FormData();
		data.left = new FormAttachment(param, ITabbedPropertyConstants.HSPACE);
		paramCombo.setLayoutData(data);
		
		line = new Composite(this,SWT.BORDER){
			@Override
			public Point computeSize(int wHint, int hHint, boolean changed) {
				if(!thirdRow.isVisible() && !secondRow.isVisible()){
					return new Point(getParent().getSize().x,0);
				}
				return new Point(getParent().getSize().x,1);
			}
			@Override
			public int getBorderWidth() {
				return 1;
			}
		};
		line.setLayout(formLayout);
		line.setBackground(ColorConstants.lightGray);
		
		
	}
		
	public void setIdentifier(PageElement pageElement, Identifier identifier) {
		if(pageElement != null && pageElement.equals(this.pageElement) && 
				identifier != null && pageElement.equals(this.identifier)){
			return;
		}
		
		if(this.pageElement != null && this.identifier != null){
			removeListeners();
		}
		this.pageElement = pageElement;
		this.identifier = identifier;
		
		value.setVisible(true);
		frameType.setVisible(false);
		booleanLabel.setVisible(identifier.getType().isBoolean());
		value.setVisible(!identifier.getType().isBoolean());
		moderator.setEnabled(identifier.getType().acceptsModerator());

		if(IdentifierType.FRAME_TYPE.equals(identifier.getType())){
			if (StringUtils.isBlank(identifier.getValueWithPossibleSingleTrailingSpace())) {
				identifier.setValue(FRAME);
			}
			value.setVisible(false);
			frameType.setVisible(true);
		}
		
		addListeners();
		refresh();
	}
	
	public void refresh(){
		type.setText(identifier.getType() + ":");
		type.setToolTipText(identifier.getType().getDescription());
		value.setText(identifier.getValueWithPossibleSingleTrailingSpace());
		
		frameType.setText(identifier.getValueWithPossibleSingleTrailingSpace());
		
		probability.removeSelectionListener(probabilityListener);
		setProbability(identifier.getProbability());
		probability.addSelectionListener(probabilityListener);
		
		moderator.removeSelectionListener(moderatorListener);
		setModerator(identifier.getModerator());
		moderator.addSelectionListener(moderatorListener);
		
		dirEdit.removeSelectionListener(dirEditListener);
		dirEdit.setSelection(pageElement.getDirectEditIdentifier().equals(identifier));
		dirEdit.addSelectionListener(dirEditListener);
		
		if(identifier.getActual() != null){
			actualLabel.setVisible(true);
			actualValue.setVisible(true);
			updateButton.setVisible(true);
			secondRow.setVisible(true);
			
			actualValue.setText(identifier.getActual());
		}else{
			actualLabel.setVisible(false);
			actualValue.setVisible(false);
			actualValue.setVisible(false);
			secondRow.setVisible(false);
		}
		
		Test test = editor.getTest();
		if(test.getAllLanguages() == null || test.getAllLanguages().getLanguages().size() == 0){
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
			i18nCombo.setEnabled(identifier.useI18n());
			i18nCombo.removeSelectionListener(i18nComboListener);
			i18nCombo.setItems(
				test.getAllLanguages().getAllKeys().toArray(new String[]{}));
			if(identifier.getI18nKey() == null || "".equals(identifier.getI18nKey()))
				i18nCombo.select(0);
			else
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
			param.setSelection(identifier.useParam());
			param.addSelectionListener(paramListener);
			paramCombo.setVisible(true);
			paramCombo.setEnabled(identifier.useParam());
			paramCombo.removeSelectionListener(paramComboListener);
			paramCombo.setItems(
				test.getParamList().getHeaders().toArray());
			if(identifier.getParamKey() == null || "".equals(identifier.getParamKey()))
				paramCombo.select(0);
			else
				paramCombo.select(paramCombo.indexOf(identifier.getParamKey()));
			paramCombo.addSelectionListener(paramComboListener);
		}
		thirdRow.setVisible(paramLabel.getVisible() || i18nLabel.getVisible());		
	}
	
	private void setProbability(int newProbability){
		value.setEnabled(true);
		frameType.setEnabled(true);
		moderator.setEnabled(identifier.getType().acceptsModerator());
		booleanLabel.setEnabled(true);
		
		if(newProbability > 66) {
			probability.select(probability.indexOf(MUST));
			probability.setBackground(new Color(null, 221, 220, 255));
		}
		else if(newProbability > 33) {
			probability.select(probability.indexOf(SHOULD));
			probability.setBackground(new Color(null, 221, 220, 255));
		} else if(newProbability > 0) {
			probability.select(probability.indexOf(CAN));
			probability.setBackground(new Color(null, 221, 220, 255));
		} else if(newProbability == 0) {
			probability.select(probability.indexOf(INDIFFERENT));
			moderator.setEnabled(false);
			frameType.setEnabled(false);
			probability.setBackground(new Color(null, 255, 255, 255));
		}
		else if(newProbability > -34) {
			probability.select(probability.indexOf(CANNOT));
			probability.setBackground(new Color(null, 248, 240, 200));
		} else if(newProbability > -67) {
			probability.select(probability.indexOf(SHOULD_NOT));
			probability.setBackground(new Color(null, 248, 240, 200));
		} else {
			probability.select(probability.indexOf(MUST_NOT));
			probability.setBackground(new Color(null, 248, 240, 200));
		}
		probability.clearSelection();
	}
	
	private void setModerator(Moderator mod) {
		if(mod == null){
			moderator.select(moderator.indexOf(EQUAL));
			return;
		}
		switch (mod) {
		case BEGIN:
			moderator.select(moderator.indexOf(BEGIN));
			break;
		case END:
			moderator.select(moderator.indexOf(END));
			break;
		case CONTAIN:
			moderator.select(moderator.indexOf(CONTAIN));
			break;
		case EQUAL:
			moderator.select(moderator.indexOf(EQUAL));
			break;
		default:
			moderator.select(moderator.indexOf(EQUAL));
			break;
		}
	}

	/**
	 * The identifier has changed
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if(!this.isDisposed()){
			String eventName = event.getPropertyName();
			if(Identifier.VALUE.equals(eventName)){
				value.setText((String) event.getNewValue());
			}else if(Identifier.ACTUAL.equals(eventName)){
				//TODO
			}else if(Identifier.TYPE.equals(eventName)){
			
			}else if(Identifier.MODERATOR.equals(eventName)){
				moderator.removeSelectionListener(moderatorListener);
				setModerator((Moderator)event.getNewValue());
				moderator.addSelectionListener(moderatorListener);
			}else if(Identifier.PROBABILITY.equals(eventName)){
				probability.removeSelectionListener(probabilityListener);
				setProbability((Integer) event.getNewValue());
				probability.addSelectionListener(probabilityListener);
			}else if(PageElement.DIRECT_EDIT_IDENTIFIER.equals(eventName)){
				dirEdit.removeSelectionListener(dirEditListener);
				dirEdit.setSelection(pageElement.getDirectEditIdentifier().equals(identifier));
				dirEdit.addSelectionListener(dirEditListener);
			}
			refresh();
		}
	}

	public void removeListeners() {
		if(identifier != null)
			identifier.removePropertyChangeListener(this);
		if(pageElement != null)
			pageElement.removePropertyChangeListener(this);
		listeners--;
	}

	public void addListeners() {
		if(identifier != null)
			identifier.addPropertyChangeListener(this);
		if(pageElement != null)
			pageElement.addPropertyChangeListener(this);
		listeners++;
	}
	
	public void setPart(GraphicalTestEditor editor) {
		this.editor = editor;
	}
	
	private SelectionListener probabilityListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			int index = probability.getSelectionIndex();
			String label = probability.getItem(index);
			int prob;
			value.setEnabled(true);
			moderator.setEnabled(identifier.getType().acceptsModerator());
			booleanLabel.setEnabled(true);
			
			if (label.equals(MUST))
				prob = 100;
			else if (label.equals(SHOULD))
				prob = 66;
			else if (label.equals(CAN))
				prob = 33;
			
			else if (label.equals(INDIFFERENT)) {
				prob = 0;
				if (dirEdit.getSelection() && identifier.getType().equals(LABEL)) {
					//the id is Direct Edit and of type Label. Should be editable.
					value.setEnabled(false);
					moderator.setEnabled(false);
					booleanLabel.setEnabled(false);
				}
				else {
					value.setEnabled(false);
					moderator.setEnabled(false);
					booleanLabel.setEnabled(false);
				}
			}
			else if (label.equals(CANNOT))
				prob = -33;
			else if (label.equals(SHOULD_NOT))
				prob = -66;
			else if (label.equals(MUST_NOT))
				prob = -100;
			else {
				Logger.warn("Unknown probability");
				prob = 0;
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
	
	private SelectionListener moderatorListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			int index = moderator.getSelectionIndex();
			String label = moderator.getItem(index);

			Moderator mod;
			if (label.equals(END))
				mod = Moderator.END;
			else if (label.equals(BEGIN))
				mod = Moderator.BEGIN;
			else if (label.equals(CONTAIN))
				mod = Moderator.CONTAIN;
			else if (label.equals(EQUAL)) {
				mod = Moderator.EQUAL;
			}else {
				Logger.warn("Unknown moderator");
				mod = Moderator.EQUAL;
			}
			
			ChangeIdentiferModeratorCommand command = 
				new ChangeIdentiferModeratorCommand();
			command.setIdentifier(identifier);
			command.setNewModerator(mod);
			command.setOldModerator(identifier.getModerator());
			executeCommand(command);
			
		}
		public void widgetDefaultSelected(SelectionEvent e) {}
	};

	private SelectionListener frameListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			int index = frameType.getSelectionIndex();
			String label = frameType.getItem(index);
			String frame = FRAME;
			if (label.equals(FRAME))
				frame = FRAME;
			else if (label.equals(IFRAME))
				frame = IFRAME;
			else {
				frame = FRAME;
				Logger.warn("Unknown frame type: " + label);
			}
			
			ChangeFrameTypeCommand command = 
				new ChangeFrameTypeCommand();
			command.setIdentifier(identifier);
			command.setFrame(frame);
			command.setOldFrame(identifier.getValueWithPossibleSingleTrailingSpace());
			executeCommand(command);
			
		}
		public void widgetDefaultSelected(SelectionEvent e) {}
	};
	
	private SelectionListener dirEditListener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent e) {}
		public void widgetSelected(SelectionEvent e) {
			if(dirEdit.getSelection()){
				ChangeDirectEditIdentifierCommand cmd = new ChangeDirectEditIdentifierCommand();
				cmd.setPageElement(pageElement);
				cmd.setOldIdentifier(pageElement.getDirectEditIdentifier());
				cmd.setNewIdentifier(identifier);
				editor.getCommandStack().execute(cmd);
			}
		}
	};
	
	private SelectionListener updateButtonListener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent e) {}

		public void widgetSelected(SelectionEvent e) {
			ChangeIdentiferValueToActualCommand cmd = new ChangeIdentiferValueToActualCommand();
			cmd.setValue(identifier.getValueWithPossibleSingleTrailingSpace());
			cmd.setActual(identifier.getActual());
			cmd.setIdentifier(identifier);
			editor.getCommandStack().execute(cmd);
		}
	};
	
	private class ValueListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			updateValue();
		}

		private void updateValue(){
			int oldPos = value.getSelection().x;
			
			if(!value.getText().equals( identifier.getValueWithPossibleSingleTrailingSpace())){
				CompoundCommand compundCommand = new CompoundCommand();
				if(pageElement.getDirectEditIdentifier().equals(identifier)){
					ChangePageElementTextCommand command = new ChangePageElementTextCommand();
					command.setNewText(value.getText());
					command.setOldText(identifier.getValueWithPossibleSingleTrailingSpace());
					command.setPageElement(pageElement);
					compundCommand.add(command);
				}else{
					ChangeIdentifierValueCommand command = new ChangeIdentifierValueCommand();
					if (isNotBlank(value.getText()) && identifier.isIndifferent()) {
						command.setProbabilityToMax(true);
					}
					if (isBlank(value.getText()) && identifier.isMaxProbability()) {
						command.setProbabilityToIndifferent(true);
					}
					command.setIdentifer(identifier);
					command.setNewValue(value.getText());
					command.setOldValue(identifier.getValueWithPossibleSingleTrailingSpace());
					command.setOldProbability(identifier.getProbability());
					compundCommand.add(command);
				}		
				executeCommand(compundCommand);
			}
			
			value.setSelection(oldPos);
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
				command.setTest(test);
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
			command.setTest(test);
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
				command.setTest(test);
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
			command.setTest(test);
			executeCommand(command);
		}
	};
	
	
	private void executeCommand(Command command){
		pageElement.removePropertyChangeListener(this);
		identifier.removePropertyChangeListener(this);
		editor.getCommandStack().execute(command);
		identifier.addPropertyChangeListener(this);
		pageElement.addPropertyChangeListener(this);
		refresh();
	}

	public void setTest(Test test) {
		this.test = test;
	}
}