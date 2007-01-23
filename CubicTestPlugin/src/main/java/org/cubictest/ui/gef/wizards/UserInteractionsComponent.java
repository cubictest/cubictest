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
 * Component view for creating new user interaction input.
 * @author SK Skytteren
 * @author chr_schwarz
 */
public class UserInteractionsComponent {
	
	private static final String CHOOSE = "--Choose--";
	private TableViewer viewer;
	private Table table;
	private CellEditor[] cellEditors;
	
	private static final String ELEMENT = "element";
	private static final String ACTION = "action";
	private static final String INPUT = "input";
	
	private String[] columnNames = new String[] {ELEMENT, ACTION, INPUT};
	
	private UserInteractionsTransition transition;
	private List<IActionElement> allElements;
	private String[] elementNames;
	private Test test;
	private List<PageElement> flattenedElements = new ArrayList<PageElement>(); 
	private List<UserInteraction> elementActions;
	
	private String[] currentActions;
	private UserInteraction activeAction;

	public UserInteractionsComponent(UserInteractionsTransition transition, Test test) {
		this.test = test;
		this.transition = transition;
		
		List<PageElement> rootElements = new ArrayList<PageElement>();
		
		AbstractPage start = (AbstractPage)transition.getStart();
		
		elementActions = new ArrayList<UserInteraction>();
		List<UserInteraction> toRemove = new ArrayList<UserInteraction>();
		
		//clean up elementActions:
		List<UserInteraction> inputs = transition.getInputs();
		for (UserInteraction action : inputs) {
			IActionElement element = action.getElement();
			if (element != null) {
				elementActions.add(action);
			}
			else {
				toRemove.add(action);
			}
		}
		for (UserInteraction action : toRemove) {
			transition.removeInput(action);
		}
		
		if(start instanceof Page) { // process commonTrasitions for pages
			rootElements.addAll(start.getElements());
			List<CommonTransition> commonTransitions = ((Page)start).getCommonTransitions();
			for (CommonTransition at : commonTransitions)
				rootElements.addAll(((Common)(at).getStart()).getElements());			
		}
		
		populateFlattenedElements(rootElements);
		
		allElements = new ArrayList<IActionElement>();
		for(Object o : flattenedElements){
			if(o instanceof IActionElement && (((IActionElement) o).getActionTypes().size() > 0)){
				allElements.add((IActionElement)o);
			}
		}
		allElements.add (new WebBrowser());
	}
	
	private void populateFlattenedElements(List<PageElement> elements) {
		for (PageElement element: elements){
			if(element instanceof IContext){
				populateFlattenedElements(((IContext) element).getElements());
				flattenedElements.add(element);
			}
			else {
				flattenedElements.add(element);
			}
		}
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

		//add empty element if empty list
		if (elementActions.size() == 0)
			elementActions.add(new UserInteraction());
		
		Button button = new Button(content, SWT.PUSH);
		button.setText("Add New User Input");
		button.pack();
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				List<UserInteraction> allInputs = (List<UserInteraction>)viewer.getInput();
				UserInteraction newInput = new UserInteraction();
				
				transition.addInput(newInput);
				transition.setPage((AbstractPage)transition.getStart());
				viewer.setInput(allInputs);
			}
		});
		
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;
		gd.horizontalSpan = 2;
		gd.verticalIndent = 7;
		
		createTable(content);
		createTableViewer();
		
	
		transition.setInputs(elementActions);
		transition.setPage((AbstractPage)transition.getStart());
		viewer.setInput(elementActions);
		
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
		tc1.setText(ELEMENT);
		tc1.setWidth(120);
		tc1.setResizable(true);
		
		TableColumn tc2 = new TableColumn(table, SWT.LEFT, 1);
		tc2.setText(ACTION);
		tc2.setWidth(120);
		tc2.setResizable(true);
		
		TableColumn tc3 = new TableColumn(table, SWT.LEFT, 2);
		tc3.setText(INPUT);
		tc3.setWidth(160);
		tc3.setResizable(true);
		
	}
	private void createTableViewer() {
		viewer = new TableViewer(table);
		viewer.setUseHashlookup(true);
		viewer.setColumnProperties(columnNames);
		
		cellEditors = new CellEditor[3];
		elementNames = new String[allElements.size()+2];
		elementNames[0] = CHOOSE;
		int a = 1;
		for (IActionElement element: allElements) {
			if (element.getActionTypes().size() > 0) {
				elementNames[a++] = element.getType() + ": " + element.getDescription();
			}
		}
		elementNames[a] = "";

		cellEditors[0] = new ActionComboBoxCellEditor(table, elementNames, SWT.READ_ONLY);
		cellEditors[1] = new ComboBoxCellEditor(table, new String[]{""}, SWT.READ_ONLY);
		cellEditors[2] = new TextCellEditor(table);
		
		viewer.setCellEditors(cellEditors);
		viewer.setContentProvider(new ActionContentProvider());
		viewer.setLabelProvider(new ActionLabelProvider());
		viewer.setCellModifier(new ActionInputCellModifier());
	}
	
	public List getUserInput(){
		return (List) viewer.getInput(); 
	}
	
	
	class ActionComboBoxCellEditor extends ComboBoxCellEditor {
		
		public ActionComboBoxCellEditor(Table table, String[] elementNames, int read_only) {
				super(table,elementNames, read_only);
		}

		
		@Override
		protected Control createControl(Composite parent) {
			CCombo comboBox = (CCombo) super.createControl(parent);
	        comboBox.addSelectionListener(new SelectionAdapter() {
	            public void widgetSelected(SelectionEvent event) {
	                Integer selectedIndex = (Integer) doGetValue();
					String name = elementNames[selectedIndex.intValue()];
					for (IActionElement iae : allElements){	
						if ( (iae.getType() + ": " + iae.getDescription()).equals(name)){
							activeAction.setElement(iae);
							break;
						}
					}
					IActionElement pe = ((UserInteraction)activeAction).getElement();
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
					deactivate();
	            }
	        });
	        return comboBox;
		}
	}
	class ActionInputCellModifier implements ICellModifier{

		public boolean canModify(Object element, String property) {
			activeAction = (UserInteraction) element;
			
			if (property.equals(INPUT)){
				if (((UserInteraction)element).getElement() instanceof AbstractTextInput){
					UserInteraction input = (UserInteraction)element;
					if(SationType.PARAMETERISATION.equals(input.getSationType())){
						ParameterList list = test.getParamList();
						String[] keys = list.getHeaders().toArray();
						cellEditors[2] = new ComboBoxCellEditor(table,keys,SWT.READ_ONLY);
						return true;
					}else{
						cellEditors[2] = new TextCellEditor(table);
					}
				}
				if (((UserInteraction) element).getAction().acceptsInput()) {
					cellEditors[2] = new TextCellEditor(table);
				}
				else {
					cellEditors[2] = new TextCellEditor(table, SWT.READ_ONLY);
					((UserInteraction) element).setInput("");
					return false;
				}
			}else if(property.equals(ACTION)){
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
			return true;
		}
		
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
					ActionType action = fInput.getAction();
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
					if(ActionType.ENTER_PARAMETER_TEXT.equals(fInput.getAction())){
						String key = fInput.getKey();
						if(key == null || "".equals(key))
							return 0;
						return test.getParamList().getHeaders().indexOf(key);
					}
					return fInput.getInput();
			}
			return result;
		}

		public void modify(Object element, String property, Object value) {
			
			int columnIndex = java.util.Arrays.asList(columnNames).indexOf(property);
			if (element instanceof TableItem) {
				UserInteraction rowItem = (UserInteraction) ((TableItem) element).getData();
				switch (columnIndex) {
					case 0: 
						String name = elementNames[((Integer) value).intValue()];
						
						for (IActionElement iae : allElements){	
							if (name.equals(CHOOSE) || name.equals("")) {
								rowItem.setElement(null);
								rowItem.setAction(ActionType.NO_ACTION);
							}
						}
						break;
					case 1:
						if(rowItem.getElement() == null)
							break;
						int i = 0;
						for(ActionType action :rowItem.getElement().getActionTypes()){
							if(i == (Integer) value){
								rowItem.setAction(action);
								if(ActionType.ENTER_PARAMETER_TEXT.equals(action))
									rowItem.setSationType(SationType.PARAMETERISATION);
								else
									rowItem.setSationType(SationType.NONE);
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
						if(ActionType.ENTER_PARAMETER_TEXT.equals(rowItem.getAction())){
							rowItem.setKey(test.getParamList().getHeaders().get((Integer)value));
							test.getParamList().getParameters().get((Integer) value).addObserver(rowItem);
							test.updateObservers();
						}
						else
							rowItem.setInput((String)value);			
						break;
				}
				viewer.update(rowItem, null);
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
					return fInput.getAction().getText();
				case 2:
					if (SationType.PARAMETERISATION.equals(fInput.getSationType()))
						return fInput.getKey();
					if (fInput.getAction().acceptsInput()) {
						return fInput.getInput();
					}
				default:
					break;
			}
			return result;
		}
		
		/**
		 * Disposes any resources
		 */
		public void dispose() {		
		}
	}
}

