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

import org.apache.commons.lang.ArrayUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.ActionType;
import org.cubictest.model.Common;
import org.cubictest.model.CommonTransition;
import org.cubictest.model.IActionElement;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.WebBrowser;
import org.cubictest.model.SationObserver.SationType;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.AbstractTextInput;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.ui.gef.command.AddUserInteractionCommand;
import org.cubictest.ui.gef.command.EditUserInteractionCommand;
import org.cubictest.ui.gef.controller.TestEditPart;
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
 * Component view (GUI) nfor creating new user interaction input.
 * Both used in dialog box for new user interaction and for properties view.
 * 
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class UserInteractionsComponent {
	
	private static final String CHOOSE = "--Choose--";
	private TableViewer tableViewer;
	private Table table;
	
	private static final String ACTION_ELEMENT = "actionElement";
	private static final String ACTION_TYPE = "actionType";
	private static final String TEXT_INPUT = "textInput";
	
	private CellEditor[] cellEditors;
	private String[] columnNames = new String[] {ACTION_ELEMENT, ACTION_TYPE, TEXT_INPUT};
	private String[] elementNames;
	
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
		
		allActionElements.addAll(getFlattenedPageElements(elementsTree));
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
				List<UserInteraction> currentInputs = transition.getUserInteractions();
				UserInteraction newInput = new UserInteraction();

				if (useCommandForActionChanges) {
					AddUserInteractionCommand addActionCmd = new AddUserInteractionCommand();
					addActionCmd.setUserInteractionsTransition(transition);
					addActionCmd.setNewUserInteraction(newInput);
					testPart.getViewer().getEditDomain().getCommandStack().execute(addActionCmd);
				}
				else {
					transition.addUserInteraction(newInput);
				}
			
				tableViewer.setInput(currentInputs);
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
		elementNames = new String[allActionElements.size() + 2];
		elementNames[0] = CHOOSE;
		int a = 1;
		for (IActionElement element: allActionElements) {
			if (element.getActionTypes().size() > 0) {
				elementNames[a++] = element.getType() + ": " + element.getDescription();
			}
		}
		elementNames[a] = "";

		cellEditors[0] = new ActionElementComboBoxCellEditor(table, elementNames, SWT.READ_ONLY);
		cellEditors[1] = new ComboBoxCellEditor(table, new String[]{""}, SWT.READ_ONLY);
		cellEditors[2] = new TextCellEditor(table);
		
		tableViewer.setCellEditors(cellEditors);
		tableViewer.setContentProvider(new ActionContentProvider());
		tableViewer.setLabelProvider(new ActionLabelProvider());
		tableViewer.setCellModifier(new ActionInputCellModifier());
	}
	
	
	/**
	 * Cell editor for the fist column (action element).
	 * Presents a list of action elements in a drop down.
	 */
	class ActionElementComboBoxCellEditor extends ComboBoxCellEditor {
		
		public ActionElementComboBoxCellEditor(Table table, String[] elementNames, int read_only) {
				super(table,elementNames, read_only);
		}

		
		@Override
		protected Control createControl(Composite parent) {
			CCombo comboBox = (CCombo) super.createControl(parent);
	        comboBox.addSelectionListener(new SelectionAdapter() {
	        	
	        	/**
	        	 * Handles selection / change of the action element.
	        	 */
	            public void widgetSelected(SelectionEvent event) {
	                Integer selectedIndex = (Integer) doGetValue();
					String elementName = elementNames[selectedIndex.intValue()];
					for (IActionElement iae : allActionElements){	
						if ((iae.getType() + ": " + iae.getDescription()).equals(elementName)){
							//edit model:
							if (useCommandForActionChanges) {
								EditUserInteractionCommand editActionCmd = new EditUserInteractionCommand();
								editActionCmd.setUserInteraction(activeUserinteraction);
								editActionCmd.setNewElement(iae);
								editActionCmd.setOldElement(activeUserinteraction.getElement());
								testPart.getViewer().getEditDomain().getCommandStack().execute(editActionCmd);
							}
							else {
								activeUserinteraction.setElement(iae);
							}
							break;
						}
					}
					
					//Get the action types of the newly selected action element:
					
					IActionElement pe = ((UserInteraction)activeUserinteraction).getElement();
					if(pe != null){
						
						List<String> actionList = new ArrayList<String>();
						for(ActionType action : pe.getActionTypes()){
							if(ActionType.ENTER_PARAMETER_TEXT.equals(action) 
									&& test.getParamList() == null) {
								continue;
							}
							actionList.add(action.getText());
						}
						String[] actions = actionList.toArray(new String[0]);
						if (!ArrayUtils.isEquals(actions, currentActions)) {
							cellEditors[1] = new ComboBoxCellEditor(table,actions, SWT.READ_ONLY);
							currentActions = actions;
						}
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
	class ActionInputCellModifier implements ICellModifier{

		/**
		 * Checks whether a cell can be edited.
		 */
		public boolean canModify(Object element, String property) {
			activeUserinteraction = (UserInteraction) element;
			
			if (property.equals(ACTION_ELEMENT)){
				return true;
			}
			else if (property.equals(ACTION_TYPE)){
				IActionElement pe = ((UserInteraction)element).getElement();
				if(pe != null){
					
					List<String> actionList = new ArrayList<String>();
					for(ActionType action : pe.getActionTypes()){
						if(ActionType.ENTER_PARAMETER_TEXT.equals(action) 
								&& test.getParamList() == null) {
							continue;
						}
						actionList.add(action.getText());
					}
					String[] actions = actionList.toArray(new String[0]);
					if (!ArrayUtils.isEquals(actions, currentActions)) {
						cellEditors[1] = new ComboBoxCellEditor(table,actions, SWT.READ_ONLY);
						currentActions = actions;
					}
				}
			}
			else if (property.equals(TEXT_INPUT)){
				if (((UserInteraction)element).getElement() instanceof AbstractTextInput){
					UserInteraction input = (UserInteraction)element;
					if(input.useParam()){
						ParameterList list = test.getParamList();
						String[] keys = list.getHeaders().toArray();
						cellEditors[2] = new ComboBoxCellEditor(table,keys,SWT.READ_ONLY);
						return true;
					}else{
						cellEditors[2] = new TextCellEditor(table);
					}
				}
				if (((UserInteraction) element).getActionType().acceptsInput()) {
					cellEditors[2] = new TextCellEditor(table);
				}
				else {
					cellEditors[2] = new TextCellEditor(table, SWT.READ_ONLY);
					((UserInteraction) element).setTextualInput("");
					return false;
				}

			}
			return true;
		}
		
		/**
		 * Retrieves values of a model element's property.
		 */
		public Object getValue(Object element, String property) {
			UserInteraction fInput = (UserInteraction) element;
			int columnIndex = Arrays.asList(columnNames).indexOf(property);
			
			Object result = null;
			
			switch(columnIndex){
				case 0: 
					IActionElement p = fInput.getElement();
					String elementName = CHOOSE;
					if (p != null)
						elementName = p.getType() + ": " + p.getDescription();
					
					for (int i = 0; i < elementNames.length; i++) {
						if (elementName.equals(elementNames[i]))
							return i;
					}
				case 1: 
					ActionType action = fInput.getActionType();
					int j = 0;
					if(fInput.getElement() == null)
						break;
					result =  action;
					for(ActionType actionType : fInput.getElement().getActionTypes()){
						if(action.equals(actionType))
							return j;
						if(ActionType.ENTER_PARAMETER_TEXT.equals(action) 
								&& test.getParamList() == null) {
							continue;
						}
						j++;
					}
					break;
				case 2:
					if(ActionType.ENTER_PARAMETER_TEXT.equals(fInput.getActionType())){
						String key = fInput.getParamKey();
						if(key == null || "".equals(key))
							return 0;
						return test.getParamList().getHeaders().indexOf(key);
					}
					return fInput.getTextualInput();
			}
			return result;
		}

		/**
		 * Stores a cell editor's value back into the model.
		 */
		public void modify(Object element, String property, Object value) {
			
			int columnIndex = java.util.Arrays.asList(columnNames).indexOf(property);
			if (element instanceof TableItem) {
				UserInteraction rowItem = (UserInteraction) ((TableItem) element).getData();
				switch (columnIndex) {
					case 0: 
						String name = elementNames[((Integer) value).intValue()];
						
						for (int i = 0; i < allActionElements.size(); i++){	
							if (name.equals(CHOOSE) || name.equals("")) {
								rowItem.setElement(null);
								rowItem.setActionType(ActionType.NO_ACTION);
							}
						}
						break;
					case 1:
						if(rowItem.getElement() == null)
							break;
						int i = 0;
						for(ActionType action :rowItem.getElement().getActionTypes()){
							if(i == (Integer) value){
								rowItem.setActionType(action);
								rowItem.setUseI18n(ActionType.ENTER_PARAMETER_TEXT.equals(action));
								break;
							}
							if(ActionType.ENTER_PARAMETER_TEXT.equals(action) 
									&& test.getParamList() == null) {
								continue;
							}
							i++;
						}			
						break;
					case 2:
						if(ActionType.ENTER_PARAMETER_TEXT.equals(rowItem.getActionType())){
							rowItem.setParamKey(test.getParamList().getHeaders().get((Integer)value));
							test.getParamList().getParameters().get((Integer) value).addObserver(rowItem);
							test.updateObservers();
						}
						else
							rowItem.setTextualInput((String)value);			
						break;
				}
				tableViewer.update(rowItem, null);
			}
		}
	}
	
	class ActionContentProvider implements IStructuredContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			List inputs = (List) inputElement;
			return inputs.toArray();
		}

	}
	
	/**
	 * Class for getting text for cells (by columns).
	 */
	class ActionLabelProvider extends LabelProvider implements ITableLabelProvider {

		public ActionLabelProvider() {
		}
		
		public Image getColumnImage(Object element, int columnIndex) {
		    return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			UserInteraction fInput = (UserInteraction) element;
			String result = "";
			
			switch (columnIndex) {
				case 0:
					IActionElement p = fInput.getElement();
					if (p!=null)
						return p.getType() + ": " + p.getDescription();
					else
						return elementNames[0];
				case 1:
					return fInput.getActionType().getText();
				case 2:
					if (fInput.useParam())
						return fInput.getParamKey();
					if (fInput.getActionType().acceptsInput()) {
						return fInput.getTextualInput();
					}
				default:
					break;
			}
			return result;
		}
		

		public void dispose() {		
		}
	}
	
	
	/**
	 * Util method for getting all page elements of a page (traverse contexts). 
	 */
	private List<PageElement> getFlattenedPageElements(List<PageElement> elements) {
		List<PageElement> flattenedElements = new ArrayList<PageElement>(); 

		for (PageElement element: elements){
			if(element.getActionTypes().size() == 0) {
				continue;
			}

			if(element instanceof IContext){
				getFlattenedPageElements(((IContext) element).getElements());
				flattenedElements.add(element);
			}
			else {
				flattenedElements.add(element);
			}
		}
		return flattenedElements;
	}
}

