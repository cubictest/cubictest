/*
 * Created on 17.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.sections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.cubictest.model.Test;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.persistence.ParameterPersistance;
import org.cubictest.ui.gef.command.ChangeParameterListCommand;
import org.cubictest.ui.gef.command.ChangeParameterListIndexCommand;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;
import org.eclipse.ui.views.navigator.ResourceSorter;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


public class ParameterisationSection extends AbstractPropertySection implements PropertyChangeListener {

	private Test test;
	private Label fileLabel;
	private Text fileName;
	private Button chooseFileButton;
	private Label paramIndexLabel;
	private Spinner paramIndexSpinner;
	private Button refreshParamButton;
	
	ResourcePatternFilter filter = new ResourcePatternFilter(){
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IFolder)
				return true;
			if (element instanceof IProject)
				return true;
			return !super.select(viewer, parentElement, element);
		}
	};
		
	SelectionListener selectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			ElementTreeSelectionDialog dialog =
				new ElementTreeSelectionDialog(new Shell(), 
							new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			dialog.setTitle("Select a Parameterisation file");
			filter.setPatterns(new String[]{"*.params"});
			
			dialog.addFilter(filter);
			dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
			dialog.setSorter(new ResourceSorter(ResourceSorter.NAME));
			if(test.getParamList() != null){
				dialog.setInitialSelection(test.getParamList().getFileName());
			}
			if (dialog.open() == Window.OK) {
				IResource element= (IResource) dialog.getFirstResult();
				fileName.setText(element.getFullPath().toString());
			}
		}
	};
	
	class FileNameListener implements FocusListener, SelectionListener{
		public void widgetDefaultSelected(SelectionEvent e) {}
		public void widgetSelected(SelectionEvent e) {
			fileNameUpdated();
		}
		public void focusGained(FocusEvent e) {}
		public void focusLost(FocusEvent e) {
			fileNameUpdated();
		}
		private void fileNameUpdated(){
			String text = fileName.getText();
			ParameterList list = test.getParamList();
			if((list == null && !"".equals(text)) || 
					(list != null && !list.getFileName().equals(text)))
				filePathChanged();
		}
	}

	@Override
	public void createControls(Composite parent, 
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		//parent.setLayout( new GridLayout());
		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;
		fileLabel = getWidgetFactory().createLabel(composite, "Parameter file:");
		GridData data = new GridData();
		data.widthHint = STANDARD_LABEL_WIDTH + 50;
		fileLabel.setLayoutData(data);
		data = new GridData();
		//data.horizontalSpan = 2;
		data.widthHint = 300;
		fileName = getWidgetFactory().createText(composite, "");
		fileName.setLayoutData(data);
		FileNameListener fileNameListener = new FileNameListener();
		fileName.addSelectionListener(fileNameListener);
		fileName.addFocusListener(fileNameListener);
		
		data = new GridData();
		data.widthHint = 150;
		
		chooseFileButton = new Button(composite, SWT.NONE);
		chooseFileButton.setText("Browse...");
		chooseFileButton.addSelectionListener(selectionListener);
		chooseFileButton.setLayoutData(data);
		
		paramIndexLabel = getWidgetFactory().createLabel(composite, "Parameter index:");
		paramIndexSpinner = new Spinner(composite, SWT.BORDER);
		paramIndexSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChangeParameterListIndexCommand command = new ChangeParameterListIndexCommand();
				command.setParameterList(test.getParamList());
				command.setNewIndex(paramIndexSpinner.getSelection());
				command.setTest(test);
				executeCommand(command);
			}
		});
		
		refreshParamButton = getWidgetFactory().createButton(composite, "Refresh parameters", SWT.PUSH);
		refreshParamButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				filePathChanged();
			}
		});
		refreshParamButton.setLayoutData(data);
		composite.setLayout(gridLayout);
	}
	
	private void updateIndexSpinner() {
		if(test != null && null != test.getParamList()){
			ParameterList list = test.getParamList();
			int length = list.inputParameterSize();
			paramIndexSpinner.setValues(list.getParameterIndex(), 0, 
					length <= 0 ? 0 : length-1,0, 1, 5);
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		if(test != null){
			test.removePropertyChangeListener(this);
			if(test.getParamList() != null)
				test.getParamList().removePropertyChangeListener(this);
		}
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof TestEditPart);
		test = (Test) ((TestEditPart) input).getModel();
		test.addPropertyChangeListener(this);
		if(test.getParamList() != null)
			test.getParamList().addPropertyChangeListener(this);
	}
			
	private void filePathChanged() {
		String text = fileName.getText();
		ParameterList list = null;
		try{
			list = ParameterPersistance.loadFromFile(ResourcesPlugin.
					getWorkspace().getRoot().getFile(new Path(text)));
		}catch (Exception e) {
			// list will be null
		}
		ChangeParameterListCommand command = new ChangeParameterListCommand();
		command.setTest(test);
		command.setNewParamList(list);
		command.setOldParamList(test.getParamList());
		executeCommand(command);
	}

	private void executeCommand(Command command) {
		IWorkbenchPart part = getPart();
		if(part instanceof GraphicalTestEditor)
			((GraphicalTestEditor)part).getCommandStack().execute(command);
	}
	
	@Override
	public void refresh() {
		super.refresh();
		boolean visibile = (test.getParamList() != null);
		paramIndexLabel.setVisible(visibile);
		paramIndexSpinner.setVisible(visibile);
		refreshParamButton.setVisible(visibile);
		if(test.getParamList() != null){
			fileName.setText(test.getParamList().getFileName());
			updateIndexSpinner();
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		refresh();
		if(event.getOldValue() instanceof ParameterList){
			((ParameterList)event.getOldValue()).removePropertyChangeListener(this);
		}
		if(event.getNewValue() instanceof ParameterList){
			((ParameterList)event.getNewValue()).removePropertyChangeListener(this);
		}
	}
	
	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		test.removePropertyChangeListener(this);
		if(test.getParamList() != null)
			test.getParamList().removePropertyChangeListener(this);
	}
	
}
