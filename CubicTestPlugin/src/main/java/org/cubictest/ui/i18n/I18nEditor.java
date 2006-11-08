/*
 * Created on 28.may.2005
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */

package org.cubictest.ui.i18n;

import java.util.ArrayList;

import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.i18n.Language;
import org.cubictest.persistence.MessagePersistance;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * 
 * @author skytteren
 *
 */
public class I18nEditor extends EditorPart {
	
	private boolean isDirty = false;
	
	private Table table;
	private TableViewer tableViewer;

	private AllLanguages languages;
	
	private final String MESSAGE_COLUMN = "Message";

	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void doSave(IProgressMonitor monitor) {
		
		MessagePersistance.saveToFiles(languages, ((IFileEditorInput)getEditorInput()).getFile());
		
		try {
			((IFileEditorInput)getEditorInput()).getFile().refreshLocal(1, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		//ToFromURL.toFile((URLList));
		
		this.isDirty = false;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() {
		//not applicable
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.IEditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if(!(editorInput instanceof IFileEditorInput)) {
			throw new PartInitException("Input must be a valid file.");
		}
		IFileEditorInput input = (IFileEditorInput)editorInput;
		
		languages = MessagePersistance.loadFromFiles(input.getFile());
		
		setSite(site);
		setInput(editorInput);
		
		setPartName("I18n Messages");
	}
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_FILL | GridData.FILL_BOTH);
		parent.setLayoutData (gridData);
		
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 1;
		parent.setLayout (layout);
		
		createTable(parent);
		createTableViewer();
		
		languages = MessagePersistance.loadFromFiles(((IFileEditorInput)getEditorInput()).getFile());
		
		//urlList = createSampleURLList();
		tableViewer.setInput(languages);
		
		createButtons(parent);
	}

	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
			SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		
		table = new Table(parent, style);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);
		
		TableColumn tc2 = new TableColumn(table, SWT.LEFT, 0);
		tc2.setWidth(200);
		tc2.setText("Name");
		
		TableColumn tc3 = new TableColumn(table, SWT.LEFT, 1);
		tc3.setText("URL");
		tc3.setWidth(200);
	}
	private void createTableViewer() {
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		
		tableViewer.setColumnProperties(getColumnNames());
		
		CellEditor[] cellEditors = new CellEditor[languages.getLanguages().size() + 1];
		for (int i = 0; i<cellEditors.length; i++)
			cellEditors[i] = new TextCellEditor(table);
		
		tableViewer.setCellEditors(cellEditors);
		tableViewer.setCellModifier(new I18nTableCellModifier(this));
		tableViewer.setContentProvider(new I18nContentProvider());
		tableViewer.setLabelProvider(new I18nLabelProvider(this));
	}
	private void createButtons(Composite parent) {
		// TODO Auto-generated method stub
		// Create and configure the "Add" button
		Button addMessage = new Button(parent, SWT.PUSH | SWT.CENTER);
		addMessage.setText("Add Message");
		
		Button addLanguage = new Button(parent, SWT.PUSH | SWT.CENTER);
		addLanguage.setText("Add Language");
		
		GridData gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		addMessage.setLayoutData(gridData);
		addLanguage.setLayoutData(gridData);
		addMessage.addSelectionListener(new SelectionAdapter() {
       		// Add a task to the ExampleTaskList and refresh the view
			public void widgetSelected(SelectionEvent e) {
				I18nMessage message = languages.createMessage();
				tableViewer.add(message);
				setDirty();
			}
		});
		addLanguage.addSelectionListener(new SelectionAdapter() {
       		// Add a task to the ExampleTaskList and refresh the view
			public void widgetSelected(SelectionEvent e) {
				//TODO lag wizard som kan lese språket.
				Language language = languages.createLanguage();
				tableViewer.add(language);
				setDirty();
			}
		});

		//	Create and configure the "Delete" button
		// Commented out for now, deletion will potentially fuck up the system..
		Button deleteMessage = new Button(parent, SWT.PUSH | SWT.CENTER);
		deleteMessage.setText("Delete Message");
		Button deleteLanguage = new Button(parent, SWT.PUSH | SWT.CENTER);
		deleteLanguage.setText("Delete Message");
		gridData = new GridData (GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80; 
		
		deleteMessage.setLayoutData(gridData); 
		deleteLanguage.setLayoutData(gridData); 
		/*
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
	
	

	public boolean isDirty() {
		return isDirty;
	}
	public void setDirty() {
		if(this.isDirty == true)
			return;
		this.isDirty = true;
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}
	public String[] getColumnNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(Language lang: languages.getLanguages())
			names.add(lang.getName());
		return (String[])names.toArray();
	}
	/**
	 * Class to give content to the table.
	 * @author ovstetun
	 *
	 */
	class I18nContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return languages.getAllKeys().toArray();
		}

		public void dispose() {
			// TODO Auto-generated method stub
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
		}
	}
}
