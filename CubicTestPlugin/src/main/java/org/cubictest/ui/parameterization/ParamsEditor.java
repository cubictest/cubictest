/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */

package org.cubictest.ui.parameterization;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.parameterization.ParamMapper;
import org.cubictest.model.parameterization.Parameter;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.persistence.ParameterPersistance;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;


/**
 * @author skytteren
 *
 */
public class ParamsEditor extends EditorPart {
	
	private boolean isDirty = false;
	
	private Table table;
	private TableViewer tableViewer;

	private ParameterList paramList;

	private Button addKey = null;

	private Composite parent;

	private GridData gridData;

	private Button addInput = null;
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		
		ParameterPersistance.saveToFile(paramList, ((IFileEditorInput)getEditorInput()).getFile());
		
		try {
			((IFileEditorInput)getEditorInput()).getFile().refreshLocal(1, monitor);
		} catch (CoreException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow("Error saving file", e);
		}
		
		this.isDirty = false;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		//not applicable
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if(!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Input must be a valid file.");
		}
		IFileEditorInput input = (IFileEditorInput)editorInput;
		
		paramList = ParameterPersistance.loadFromFile(input.getFile());
		
		setSite(site);
		setInput(editorInput);
		
		setPartName(editorInput.getName());
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_FILL //| 
				//GridData.VERTICAL_ALIGN_FILL | 
				//GridData.GRAB_VERTICAL //| 
				//GridData.FILL_BOTH
				);
		parent.setLayoutData (gridData);
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 1;
		parent.setLayout (layout);
		
		
		createTable(parent);
		createTableViewer();
		
		tableViewer.setInput(paramList);
		
		createButtons(parent);
	}

	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
			SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.VIRTUAL;
		
		table = new Table(parent, style);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = paramList.size();
		table.setLayoutData(gridData);
		
		int i = 0;
		for (Parameter parameter : paramList.getParameters()){
			TableColumn tc = new TableColumn(table, SWT.LEFT, i++);
			tc.setWidth(100);
			tc.setText(parameter.getHeader());
		}
	}
	private void createTableViewer() {
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(paramList.getHeaders().toArray());
		
		CellEditor[] cellEditors = new CellEditor[paramList.size()];
		for (int i = 0; i<cellEditors.length; i++)
			cellEditors[i] = new TextCellEditor(table);
		
		tableViewer.setCellEditors(cellEditors);
		tableViewer.setCellModifier(new ParamsTableCellModifier(this));
		tableViewer.setContentProvider(new ParamsContentProvider());
		tableViewer.setLabelProvider(new ParamsLabelProvider());
	}
	private void createButtons(Composite superparent) {
		this.parent = new Composite(superparent,superparent.getStyle());
		
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_FILL);
		parent.setLayoutData (gridData);
		
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		parent.setLayout (layout);
		createAddKey();
		createAddInput();
		
		//	Create and configure the "Delete" button
		// Commented out for now, deletion will potentially fuck up the system..
		/*
		Button deleteMessage = new Button(parent, SWT.PUSH | SWT.CENTER);
		deleteMessage.setText("Delete Message");
		Button deleteLanguage = new Button(parent, SWT.PUSH | SWT.CENTER);
		deleteLanguage.setText("Delete Message");
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80; 
		
		deleteMessage.setLayoutData(gridData); 
		deleteLanguage.setLayoutData(gridData); 
		
		delete.addSelectionListener(new SelectionAdapter() {
			//	Remove the selection and refresh the view
			public void widgetSelected(SelectionEvent e) {
				URLMapper mapper = (URLMapper) ((IStructuredSelection) 
						tableViewer.getSelection()).getFirstElement();
				if (mapper != null) {
					urlList.removeURL(mapper);
					tableViewer.remove(mapper);
					setDirty();
				} 				
			}
		});
		*/
	}

	private void createAddInput(){
		
		addInput = new Button(parent, SWT.PUSH | SWT.CENTER);
		addInput.setText("Add Input Row");

		addInput.setLayoutData(gridData);
		
		addInput.addSelectionListener(new SelectionAdapter() {
       		// Add a task to the ExampleTaskList and refresh the view
			@Override
			public void widgetSelected(SelectionEvent e) {
				ParamMapper input = paramList.createInputRow();
				tableViewer.add(input);
				tableViewer.refresh();
				setDirty();
			}
		});
		parent.update();
		parent.redraw();
	}
	
	private void createAddKey(){
		addKey  = new Button(parent, SWT.PUSH | SWT.CENTER);
		addKey.setText("Add Key");
		
		addKey.setLayoutData(gridData);
		
		addKey.addSelectionListener(new SelectionAdapter() {
			// Add a task to the ExampleTaskList and refresh the view
			@Override
			public void widgetSelected(SelectionEvent e) {			
				InputDialog dialog = new InputDialog(new Shell(),
						"Key Name", "Please enter key name:","",new IInputValidator(){
							public String isValid(String newText) {
								if (newText.length() < 3)
									return "Minimum 3 letters";
								for(String header : paramList.getHeaders().toArray()){
									if(header.equals(newText))
										return "Name already exist";
								}
								return null;
							}
						});
				if(dialog.open()==Dialog.CANCEL)
					return;
				
				
				Parameter param = paramList.createParameter();
				paramList.addParameter(param);
				param.setHeader(dialog.getValue());
				
				Table table = tableViewer.getTable();
				tableViewer.refresh();
				
				TableColumn tc = new TableColumn(table, SWT.LEFT, paramList.size()-1);
				tc.setWidth(100);
				tc.setText(param.getHeader());
				tc.pack();
	
				CellEditor[] cellEditors = new CellEditor[paramList.size()];
				for (int i = 0; i<cellEditors.length; i++){
					cellEditors[i] = new TextCellEditor(table);
				}
				
				tableViewer.setColumnProperties(paramList.getHeaders().toArray());
				
				//tableViewer.cancelEditing();
				tableViewer.setCellEditors(cellEditors);
				tableViewer.refresh();
				table.setHeaderVisible(true);
				
				table.update();
				table.redraw();
				
				setDirty();
				
			}
		});
	}
	
	@Override
	public boolean isDirty() {
		return isDirty;
	}
	public void setDirty() {
		if(this.isDirty == true)
			return;
		this.isDirty = true;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}
	
	public ParamMapper getHeaders() {
		return paramList.getHeaders();
	}
	
	/**
	 * Class to give content to the table.
	 * @author ovstetun
	 *
	 */
	class ParamsContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return paramList.getInputLines().toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			viewer.refresh();
		}
	}
	
}
