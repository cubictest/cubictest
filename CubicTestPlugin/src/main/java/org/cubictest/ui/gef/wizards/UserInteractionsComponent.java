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
package org.cubictest.ui.gef.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.ModelUtil;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.ActionType;
import org.cubictest.model.Common;
import org.cubictest.model.CommonTransition;
import org.cubictest.model.IActionElement;
import org.cubictest.model.OnOffAutoTriState;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.Text;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.ui.gef.command.AddUserInteractionCommand;
import org.cubictest.ui.gef.command.DeleteUserInteractionCommand;
import org.cubictest.ui.gef.command.EditUserInteractionCommand;
import org.cubictest.ui.gef.command.MoveUserInteractionCommand;
import org.cubictest.ui.gef.command.NoOperationCommand;
import org.cubictest.ui.gef.command.MoveUserInteractionCommand.Direction;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.utils.UserInteractionDialogUtil;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Component view (GUI) for creating new user interaction.
 * Both used in dialog box for new user interaction and for properties view.
 * 
 * @author chr_schwarz
 * @author SK Skytteren
 */
public class UserInteractionsComponent {
	
	private static final int NUM_COLUMNS = 11;
	private static final String CHOOSE = "--Choose--";
	private static final String DELETE_ROW = "--Delete user interaction--";
	private static final String MOVE_UP = "--Move up--";
	private static final String MOVE_DOWN = "--Move down--";
	private TableViewer tableViewer;
	private Table table;
	
	private static final String ACTION_ELEMENT = "actionElement";
	private static final String ACTION_TYPE = "actionType";
	private static final String TEXT_INPUT = "textInput";

	private static final int ACTION_ELEMENT_COLINDEX = 0;
	private static final int ACTION_TYPE_COLINDEX = 1;
	private static final int TEXT_INPUT_COLINDEX = 2;

	private CellEditor[] cellEditors;
	private String[] columnNames = new String[] {ACTION_ELEMENT, ACTION_TYPE, TEXT_INPUT};
	private String[] actionElements;
	
	private UserInteractionsTransition transition;
	private List<IActionElement> allActionElements = new ArrayList<IActionElement>();
	private Test test;
	private TestEditPart testPart;
	private boolean isPropertiesViewMode;
	
	private String[] currentActions;
	private UserInteraction activeUserinteraction;
	private Label secsToWaitLabel;
	private org.eclipse.swt.widgets.Text secsToWaitText;
	private final PageElement preSelectedPageElement;
	private Button autoButton;
	private Button onButton;
	private Button offButton;

	public UserInteractionsComponent(UserInteractionsTransition transition, Test test, TestEditPart testPart, 
			boolean isPropertiesViewMode, PageElement preSelectedPageElement) {
		
		if (isPropertiesViewMode && testPart == null) {
			ErrorHandler.logAndThrow("Must supply a TestEditPart if command should be used for action changes");
		}
		
		this.test = test;
		this.testPart = testPart;
		this.transition = transition;
		this.isPropertiesViewMode = isPropertiesViewMode;
		this.preSelectedPageElement = preSelectedPageElement;
		
		initializeModel(transition);
	}


	public void initializeModel(UserInteractionsTransition transition) {
		allActionElements = ModelUtil.getActionElements((AbstractPage) transition.getStart());
		transition.setPage((AbstractPage)transition.getStart());
	}

	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createControl(Composite parent) {
		Composite content = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout(NUM_COLUMNS, false);
		layout.verticalSpacing = 4;
		content.setLayout(layout);
		
		Label fill = new Label(content, SWT.NULL);
		fill.setText("User interaction input");

		
		Button button = new Button(content, SWT.PUSH);
		button.setText("Add New User Input");
		button.pack();
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				List<UserInteraction> currentUserInteractions = transition.getUserInteractions();
				UserInteraction newUserInteraction = new UserInteraction();

				if (isPropertiesViewMode) {
					AddUserInteractionCommand addActionCmd = new AddUserInteractionCommand();
					addActionCmd.setUserInteractionsTransition(transition);
					addActionCmd.setNewUserInteraction(newUserInteraction);
					testPart.getViewer().getEditDomain().getCommandStack().execute(addActionCmd);
				}
				else {
					transition.addUserInteraction(newUserInteraction);
				}
			
				tableViewer.setInput(currentUserInteractions);
				tableViewer.editElement(newUserInteraction, 0);

			}
		});
		
		Label label = new Label(content, SWT.NONE);
		label.setText("Transition reloads page:");

		label = new Label(content, SWT.NONE);
		label.setText(" Auto:");
		autoButton = new Button(content, SWT.RADIO);
		autoButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				secsToWaitLabel.setVisible(false);
				secsToWaitText.setVisible(false);
				transition.setReloadsPage(OnOffAutoTriState.AUTO);
				setEditorDirty();
			}
		});

		label = new Label(content, SWT.NONE);
		label.setText(" Yes:");
		onButton = new Button(content, SWT.RADIO);
		onButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				secsToWaitLabel.setVisible(false);
				secsToWaitText.setVisible(false);
				transition.setReloadsPage(OnOffAutoTriState.ON);
				setEditorDirty();
			}
		});

		label = new Label(content, SWT.NONE);
		label.setText(" No:");
		offButton = new Button(content, SWT.RADIO);
		offButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				secsToWaitLabel.setVisible(true);
				secsToWaitText.setVisible(true);
				transition.setReloadsPage(OnOffAutoTriState.OFF);
				setEditorDirty();
			}
		});
		secsToWaitLabel = new Label(content, SWT.NONE);
		secsToWaitLabel.setText(" Seconds to wait for result:");
		secsToWaitLabel.setVisible(offButton.getSelection());
		secsToWaitText = new org.eclipse.swt.widgets.Text(content, SWT.NONE);
		secsToWaitText.setText(transition.getSecondsToWaitForResult() + "");
		secsToWaitText.setVisible(offButton.getSelection());
		secsToWaitText.setLayoutData(new GridData(30, SWT.DEFAULT));
		secsToWaitText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				transition.setSecondsToWaitForResult(Integer.parseInt(secsToWaitText.getText()));
				setEditorDirty();
			}
		});
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalSpan = NUM_COLUMNS;
		gd.verticalIndent = 7;
		
		createTable(content);
		createTableViewer();
		
		populateView();
		return content;
	}


	public void populateView() {
		//populate viewer with initial user interactions:
		List<UserInteraction> userInteractions = transition.getUserInteractions();
		int numInteractable = 0;
		UserInteraction first = new UserInteraction();
		if (!isPropertiesViewMode && (userInteractions == null || userInteractions.size() == 0)) {
			//lets preselect one in wizard mode:
			int index = -1;
			for (IActionElement pageElement : allActionElements) {
				index++;
				if (!(pageElement instanceof Text)) {
					numInteractable++;
				}
			}
			if (numInteractable == 1) {
				//sinle element on page. Can preselct it:
				first.setElement(allActionElements.get(index));
			}
			else if (preSelectedPageElement != null) {
				//we have multiple elements, but one was selected by the user. Preselct it:
				first.setElement(preSelectedPageElement);
			}
			userInteractions.add(first);
		}
		
		OnOffAutoTriState transReloadsPage = transition.getReloadsPage();
		autoButton.setSelection(transReloadsPage.equals(OnOffAutoTriState.AUTO));
		onButton.setSelection(transReloadsPage.equals(OnOffAutoTriState.ON));
		offButton.setSelection(transReloadsPage.equals(OnOffAutoTriState.OFF));
		if (transReloadsPage.equals(OnOffAutoTriState.OFF)) {
			secsToWaitLabel.setVisible(true);
			secsToWaitText.setVisible(true);
		}
		else {
			secsToWaitLabel.setVisible(false);
			secsToWaitText.setVisible(false);
		}
		secsToWaitText.setText(transition.getSecondsToWaitForResult() + "");
		
		tableViewer.setInput(userInteractions);
		transition.setUserInteractions(userInteractions);
		
		if (numInteractable == 1) {
			tableViewer.editElement(first, 1);
		}
		else if (preSelectedPageElement != null) {
			tableViewer.editElement(first, 1);
		}
		else {
			tableViewer.editElement(first, 0);
		}
	}
	
	
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
		SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	
		table = new Table(parent, style);
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = NUM_COLUMNS;
		
		table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableColumn tc1 = new TableColumn(table, SWT.LEFT, 0);
		tc1.setText("Action element");
		tc1.setWidth(120);
		tc1.setResizable(true);
		
		TableColumn tc2 = new TableColumn(table, SWT.LEFT, 1);
		tc2.setText("Action type");
		tc2.setWidth(120);
		tc2.setResizable(true);
		
		TableColumn tc3 = new TableColumn(table, SWT.LEFT, 2);
		tc3.setText("Text input");
		tc3.setWidth(160);
		tc3.setResizable(true);
		
	}
	
	
	private void createTableViewer() {
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(columnNames);
		
		cellEditors = new CellEditor[3];
		if (isPropertiesViewMode) {
			//reserve space for move up and move down:
			actionElements = new String[allActionElements.size() + 4];
		}
		else {
			actionElements = new String[allActionElements.size() + 2];
		}
		
		actionElements[ACTION_ELEMENT_COLINDEX] = CHOOSE;
		int a = 1;
		for (IActionElement element: allActionElements) {
			if (element.getActionTypes().size() > 0) {
				actionElements[a++] = UserInteractionDialogUtil.getLabel(element, allActionElements);
			}
		}
		if (isPropertiesViewMode) {
			actionElements[a++] = MOVE_UP;
			actionElements[a++] = MOVE_DOWN;
		}
		actionElements[a++] = DELETE_ROW;

		cellEditors[ACTION_ELEMENT_COLINDEX] = new ActionElementComboBoxCellEditor(table, actionElements, SWT.READ_ONLY);
		cellEditors[ACTION_TYPE_COLINDEX] = new ComboBoxCellEditor(table, new String[]{""}, SWT.READ_ONLY);
		cellEditors[TEXT_INPUT_COLINDEX] = new TextCellEditor(table);
		
		tableViewer.setCellEditors(cellEditors);
		tableViewer.setContentProvider(new ActionContentProvider());
		tableViewer.setLabelProvider(new ActionLabelProvider());
		tableViewer.setCellModifier(new UserInteractionCellModifier());
	}
	
	
	/**
	 * Cell editor for the fist column (action element).
	 * Presents a list of action elements in a drop down.
	 */
	class ActionElementComboBoxCellEditor extends ComboBoxCellEditor {
		
		public ActionElementComboBoxCellEditor(Table table, String[] elementNames, int read_only) {
				super(table,elementNames, read_only);
		}

		/**
		 * Create dropdown list with action elements.
		 */
		@Override
		protected Control createControl(Composite parent) {
			CCombo comboBox = (CCombo) super.createControl(parent);
	        comboBox.addSelectionListener(new SelectionAdapter() {
	        	
	        	/**
	        	 * Handles selection / change of the action element.
	        	 */
	            public void widgetSelected(SelectionEvent event) {
	                Integer selectedIndex = (Integer) doGetValue();
					String elementName = actionElements[selectedIndex.intValue()];
					
					//get the IActionElement object:
					IActionElement selectedActionElement = null;
					boolean delete = false;
					boolean move = false;
					for (IActionElement actionElement : allActionElements){	
						if (elementName.equals(DELETE_ROW)) {
							selectedActionElement = null;
							delete = true;
							break;
						}
						else if (elementName.equals(MOVE_UP) || elementName.equals(MOVE_DOWN)) {
							move = true;
						}
						else if (UserInteractionDialogUtil.getLabel(actionElement, allActionElements).equals(elementName)){
							selectedActionElement = actionElement;
							break;
						}
					}
					if (delete) {
						//delete the user interaction-row:
						if (isPropertiesViewMode) {
							DeleteUserInteractionCommand deleteActionCmd = new DeleteUserInteractionCommand();
							deleteActionCmd.setIndex(transition.getUserInteractions().indexOf(activeUserinteraction));
							deleteActionCmd.setUserInteractionsTransition(transition);
							deleteActionCmd.setUserInteraction(activeUserinteraction);
							testPart.getViewer().getEditDomain().getCommandStack().execute(deleteActionCmd);
						}
						else {
							transition.removeUserInteraction(activeUserinteraction);
						}
						activeUserinteraction = null;
						List<UserInteraction> currentUserInteractions = transition.getUserInteractions();
						tableViewer.setInput(currentUserInteractions);
					}
					else if (move) {
						Direction dir = Direction.UP;
						if (elementName.equals(MOVE_DOWN))
							dir = Direction.DOWN;
						
						MoveUserInteractionCommand moveActionCmd = new MoveUserInteractionCommand();
						moveActionCmd.setDirection(dir);
						moveActionCmd.setUserInteractionsTransition(transition);
						moveActionCmd.setUserInteraction(activeUserinteraction);
						testPart.getViewer().getEditDomain().getCommandStack().execute(moveActionCmd);

						activeUserinteraction = null;
						List<UserInteraction> currentUserInteractions = transition.getUserInteractions();
						tableViewer.setInput(currentUserInteractions);
					}
					else {
						//edit the user interaction:
						if (isPropertiesViewMode) {
							EditUserInteractionCommand editActionCmd = new EditUserInteractionCommand();
							editActionCmd.setUserInteraction(activeUserinteraction);
							editActionCmd.setNewElement(selectedActionElement);
							editActionCmd.setOldElement(activeUserinteraction.getElement());
							testPart.getViewer().getEditDomain().getCommandStack().execute(editActionCmd);
						}
						else {
							activeUserinteraction.setElement(selectedActionElement);
						}
					}

					
					//Get and populate the action types of the newly selected action element:
					if (activeUserinteraction != null) {
						IActionElement element = ((UserInteraction) activeUserinteraction).getElement();
						if(element != null) {
							currentActions = UserInteractionDialogUtil.getActionTypeLabelsForElement(element, test);
							cellEditors[ACTION_TYPE_COLINDEX] = new ComboBoxCellEditor(table, currentActions, SWT.READ_ONLY);
						}
					}
					//make the change immediately visible in the graphical test editor:
					deactivate();
					if (activeUserinteraction == null && !transition.getUserInteractions().isEmpty()) {
						activeUserinteraction = transition.getUserInteractions().get(0);
					}
					if (activeUserinteraction != null) {
						tableViewer.editElement(activeUserinteraction, 1);
					}
	            }

				
	        });
	        return comboBox;
		}
	}
	
	
	/**
	 * Cell modifier that
	 * - Checks whether a cell can be edited.
	 * - Retrieves values of a model element's property.
	 * - Stores a cell editor's value back into the model.
	 *  
	 */
	class UserInteractionCellModifier implements ICellModifier{

		/**
		 * Checks whether a cell can be edited.
		 */
		public boolean canModify(Object obj, String property) {
			activeUserinteraction = (UserInteraction) obj;
			
			if (property.equals(ACTION_ELEMENT)){
				return true;
			}
			else if (property.equals(ACTION_TYPE)){
				//populate selected dropdown:
				currentActions = UserInteractionDialogUtil.getActionTypeLabelsForElement(activeUserinteraction.getElement(), test);
				cellEditors[ACTION_TYPE_COLINDEX] = new ComboBoxCellEditor(table, currentActions, SWT.READ_ONLY);
			}
			
			else if (property.equals(TEXT_INPUT)){
				if (activeUserinteraction.useParam()){
					//get parameterization keys:
					ParameterList list = test.getParamList();
					String[] keys = list.getHeaders().toArray();
					cellEditors[TEXT_INPUT_COLINDEX] = new ComboBoxCellEditor(table,keys,SWT.READ_ONLY);
				}
				else if (activeUserinteraction.getActionType().acceptsInput()) {
					cellEditors[TEXT_INPUT_COLINDEX] = new TextCellEditor(table);
				}
				else {
					cellEditors[TEXT_INPUT_COLINDEX] = new TextCellEditor(table, SWT.READ_ONLY);
					activeUserinteraction.setTextualInput("");
					return false;
				}

			}
			return true;
		}
		
		
		/**
		 * Retrieves the value of a model element's property.
		 */
		public Object getValue(Object obj, String property) {
			UserInteraction userInteraction = (UserInteraction) obj;
			int columnIndex = Arrays.asList(columnNames).indexOf(property);
			
			Object result = null;
			
			switch(columnIndex){
			
				case ACTION_ELEMENT_COLINDEX: 
					IActionElement element = userInteraction.getElement();
					String elementName = CHOOSE;
					if (element != null)
						elementName = UserInteractionDialogUtil.getLabel(element, allActionElements);
					
					for (int i = 0; i < actionElements.length; i++) {
						if (elementName.equals(actionElements[i]))
							return i;
					}
					
				case ACTION_TYPE_COLINDEX: 
					ActionType action = userInteraction.getActionType();
					int j = 0;
					if(userInteraction.getElement() == null)
						break;
					result =  action;
					for (ActionType actionType : userInteraction.getElement().getActionTypes()){
						if (action.equals(actionType))
							return j;
						if (ActionType.ENTER_PARAMETER_TEXT.equals(action) && test.getParamList() == null) {
							continue;
						}
						j++;
					}
					break;
					
				case TEXT_INPUT_COLINDEX:
					if(ActionType.ENTER_PARAMETER_TEXT.equals(userInteraction.getActionType())){
						String key = userInteraction.getParamKey();
						if(key == null || "".equals(key))
							return 0;
						return test.getParamList().getHeaders().indexOf(key);
					}
					return userInteraction.getTextualInput();
			}
			return result;
		}

		/**
		 * Stores a cell editor's value back into the model.
		 */
		public void modify(Object tableItem, String columnName, Object value) {
			
			int columnIndex = java.util.Arrays.asList(columnNames).indexOf(columnName);
			
			if (tableItem instanceof TableItem) {
				
				UserInteraction userInteraction = (UserInteraction) ((TableItem) tableItem).getData();
				
				switch (columnIndex) {
				
					case ACTION_ELEMENT_COLINDEX: 
						//Update of element on model is done by the SelectionAdapter of the dropdown.
						break;
						
					case ACTION_TYPE_COLINDEX:
						if(userInteraction.getElement() == null)
							break;
						
						//Update action type:
						int index = (Integer) value;
						ActionType action = UserInteractionDialogUtil.getActionTypesForElement(userInteraction.getElement(), test).get(index);
						
						//edit model:
						if (isPropertiesViewMode) {
							EditUserInteractionCommand editActionCmd = new EditUserInteractionCommand();
							editActionCmd.setUserInteraction(userInteraction);
							editActionCmd.setNewActionType(action);
							editActionCmd.setOldActionType(userInteraction.getActionType());
							testPart.getViewer().getEditDomain().getCommandStack().execute(editActionCmd);
						}
						else {
							userInteraction.setActionType(action);
						}

						userInteraction.setUseParam(ActionType.ENTER_PARAMETER_TEXT.equals(action));
						break;
						
					case TEXT_INPUT_COLINDEX:
						if(ActionType.ENTER_PARAMETER_TEXT.equals(userInteraction.getActionType())){
							if (test.getParamList() != null) {
								userInteraction.setParamKey(test.getParamList().getHeaders().get((Integer)value));
								test.getParamList().removeObserverFromAllParams(userInteraction);
								test.getParamList().getParameters().get((Integer) value).addObserver(userInteraction);
								test.updateObservers();
							}
						}
						else {
							//edit model:
							if (test.getParamList() != null) {
								test.getParamList().removeObserverFromAllParams(userInteraction);
							}
							
							if (isPropertiesViewMode) {
								EditUserInteractionCommand editActionCmd = new EditUserInteractionCommand();
								editActionCmd.setUserInteraction(userInteraction);
								editActionCmd.setNewTextInput((String)value);
								editActionCmd.setOldTextInput(userInteraction.getTextualInput());
								testPart.getViewer().getEditDomain().getCommandStack().execute(editActionCmd);
							}
							else {
								userInteraction.setTextualInput((String)value);
							}
						}
						break;
				}
				tableViewer.update(userInteraction, null);
			}
		}
	}
	
	
	/**
	 * Gets content for the dropdowns. Just loops back input.
	 */
	class ActionContentProvider implements IStructuredContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		/**
		 * Get contents to display in dropdown.
		 */
		public Object[] getElements(Object inputElement) {
			List actionTypes = (List) inputElement;
			return actionTypes.toArray();
		}

	}
	
	/**
	 * Class for getting text content for display in a cell.
	 */
	class ActionLabelProvider extends LabelProvider implements ITableLabelProvider {

		public ActionLabelProvider() {
		}
		
		public Image getColumnImage(Object element, int columnIndex) {
		    return null;
		}

		/**
		 * Get text contents to display in cell.
		 */
		public String getColumnText(Object obj, int columnIndex) {
			UserInteraction userInteraction = (UserInteraction) obj;
			String result = "";
			
			switch (columnIndex) {
				case ACTION_ELEMENT_COLINDEX:
					IActionElement element = userInteraction.getElement();
					if (element != null) {
						return UserInteractionDialogUtil.getLabel(element, allActionElements);
					}
					else
						return CHOOSE;
					
				case ACTION_TYPE_COLINDEX:
					return userInteraction.getActionType().getText();
					
				case TEXT_INPUT_COLINDEX:
					if (userInteraction.useParam()) {
						return userInteraction.getParamKey();
					}
					if (userInteraction.getActionType().acceptsInput()) {
						return userInteraction.getTextualInput();
					}
					
				default:
					break;
			}
			return result;
		}
		

		public void dispose() {		
		}
		
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}
	
	private void setEditorDirty() {
		if (isPropertiesViewMode) {
			NoOperationCommand cmd = new NoOperationCommand();
			testPart.getViewer().getEditDomain().getCommandStack().execute(cmd);
		}
	}


	public void setTransition(UserInteractionsTransition transition) {
		this.transition = transition;
	}


}

