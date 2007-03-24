/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.ActionType;
import org.cubictest.model.Common;
import org.cubictest.model.CommonTransition;
import org.cubictest.model.ContextWindow;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.WebBrowser;
import org.cubictest.model.context.IContext;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.ui.gef.command.AddUserInteractionCommand;
import org.cubictest.ui.gef.command.EditUserInteractionCommand;
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
	
	private static final String CHOOSE = "--Choose--";
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
	private boolean useCommandForActionChanges;
	
	private String[] currentActions;
	private UserInteraction activeUserinteraction;

	public UserInteractionsComponent(UserInteractionsTransition transition, Test test, TestEditPart testPart, boolean useCommandForActionChanges) {
		if (useCommandForActionChanges && testPart == null) {
			ErrorHandler.logAndThrow("Must supply a TestEditPart if command should be used for action changes");
		}
		
		this.test = test;
		this.testPart = testPart;
		this.transition = transition;
		this.useCommandForActionChanges = useCommandForActionChanges;
		
		List<PageElement> elementsTree = new ArrayList<PageElement>();
		AbstractPage start = (AbstractPage)transition.getStart();
		if(start instanceof Page) { // process commonTrasitions for pages
			elementsTree.addAll(start.getElements());
			List<CommonTransition> commonTransitions = ((Page)start).getCommonTransitions();
			for (CommonTransition at : commonTransitions)
				elementsTree.addAll(((Common)(at).getStart()).getElements());			
		}
		
		allActionElements.addAll(UserInteractionDialogUtil.getFlattenedPageElements(elementsTree));
		allActionElements.add(new WebBrowser());
		//Added by Genesis Campos
		allActionElements.add(new ContextWindow());
		//End;
		transition.setPage((AbstractPage)transition.getStart());
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createControl(Composite parent) {
		Composite content = new Composite(parent, SWT.NULL);
		
		GridLayout layout = new GridLayout(2, false);
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

				if (useCommandForActionChanges) {
					AddUserInteractionCommand addActionCmd = new AddUserInteractionCommand();
					addActionCmd.setUserInteractionsTransition(transition);
					addActionCmd.setNewUserInteraction(newUserInteraction);
					testPart.getViewer().getEditDomain().getCommandStack().execute(addActionCmd);
				}
				else {
					transition.addUserInteraction(newUserInteraction);
				}
			
				tableViewer.setInput(currentUserInteractions);
			}
		});
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalSpan = 2;
		gd.verticalIndent = 7;
		
		createTable(content);
		createTableViewer();
		
		//populate viewer with initial user interactions:
		List<UserInteraction> initialUserInteractions = transition.getUserInteractions();
		if (initialUserInteractions == null || initialUserInteractions.size() == 0) {
			initialUserInteractions.add(new UserInteraction());
		}
		
		tableViewer.setInput(initialUserInteractions);
		transition.setUserInteractions(initialUserInteractions);
		
		return content;
	}
	
	
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
		SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	
		table = new Table(parent, style);
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = 2;
		
		table.setLayoutData(gd);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableColumn tc1 = new TableColumn(table, SWT.LEFT, 0);
		tc1.setText(ACTION_ELEMENT);
		tc1.setWidth(120);
		tc1.setResizable(true);
		
		TableColumn tc2 = new TableColumn(table, SWT.LEFT, 1);
		tc2.setText(ACTION_TYPE);
		tc2.setWidth(120);
		tc2.setResizable(true);
		
		TableColumn tc3 = new TableColumn(table, SWT.LEFT, 2);
		tc3.setText(TEXT_INPUT);
		tc3.setWidth(160);
		tc3.setResizable(true);
		
	}
	
	
	private void createTableViewer() {
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(columnNames);
		
		cellEditors = new CellEditor[3];
		actionElements = new String[allActionElements.size() + 2];
		actionElements[ACTION_ELEMENT_COLINDEX] = CHOOSE;
		int a = 1;
		for (IActionElement element: allActionElements) {
			if (element.getActionTypes().size() > 0) {
				actionElements[a++] = element.getType() + ": " + element.getDescription();
			}
		}
		actionElements[a] = "";

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
					for (IActionElement actionElement : allActionElements){	
						if (elementName.equals(CHOOSE) || elementName.equals("")) {
							selectedActionElement = null;
							break;
						}
						else if ((actionElement.getType() + ": " + actionElement.getDescription()).equals(elementName)){
							selectedActionElement = actionElement;
							break;
						}
					}
					//edit model:
					if (useCommandForActionChanges) {
						EditUserInteractionCommand editActionCmd = new EditUserInteractionCommand();
						editActionCmd.setUserInteraction(activeUserinteraction);
						editActionCmd.setNewElement(selectedActionElement);
						editActionCmd.setOldElement(activeUserinteraction.getElement());
						testPart.getViewer().getEditDomain().getCommandStack().execute(editActionCmd);
					}
					else {
						activeUserinteraction.setElement(selectedActionElement);
					}

					
					//Get and populate the action types of the newly selected action element:
					
					IActionElement element = ((UserInteraction) activeUserinteraction).getElement();
					if(element != null) {
						currentActions = UserInteractionDialogUtil.getActionTypeLabelsForElement(element, test);
						cellEditors[ACTION_TYPE_COLINDEX] = new ComboBoxCellEditor(table, currentActions, SWT.READ_ONLY);
					}
					//make the change immediately visible in the graphical test editor:
					deactivate();
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
						elementName = element.getType() + ": " + element.getDescription();
					
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
						if (useCommandForActionChanges) {
							EditUserInteractionCommand editActionCmd = new EditUserInteractionCommand();
							editActionCmd.setUserInteraction(userInteraction);
							editActionCmd.setNewActionType(action);
							editActionCmd.setOldActionType(userInteraction.getActionType());
							testPart.getViewer().getEditDomain().getCommandStack().execute(editActionCmd);
						}
						else {
							userInteraction.setActionType(action);
						}

						userInteraction.setUseI18n(ActionType.ENTER_PARAMETER_TEXT.equals(action));
						break;
						
					case TEXT_INPUT_COLINDEX:
						if(ActionType.ENTER_PARAMETER_TEXT.equals(userInteraction.getActionType())){
							userInteraction.setParamKey(test.getParamList().getHeaders().get((Integer)value));
							test.getParamList().getParameters().get((Integer) value).addObserver(userInteraction);
							test.updateObservers();
						}
						else {
							//edit model:
							if (useCommandForActionChanges) {
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
					if (element != null)
						return element.getType() + ": " + element.getDescription();
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
	
}

