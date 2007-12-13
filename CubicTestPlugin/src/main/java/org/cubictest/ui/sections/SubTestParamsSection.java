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

import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.parameterization.ParameterList;
import org.cubictest.ui.gef.command.ChangeSubTestParamIndexCommand;
import org.cubictest.ui.gef.controller.SubTestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section for parameterization of sub tests.
 * 
 * @author Christian Schwarz
 */
public class SubTestParamsSection extends AbstractPropertySection implements PropertyChangeListener {

	private Test test;
	private Label paramIndexLabel;
	private Button useParamIndexFromTestCheckbox;
	private Label noParamsLabel;
	private Spinner paramIndexSpinner;
	private Label useParamIndexFromTestLabel;
	private SubTest subtest;
	private Label testLabel;
	private Button openParamsButton;
	private Button openTestButton;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;
		
		createOpenTestButton(composite);
		createNoParamsLabel(composite);
		createIndexSpinner(composite);
		createUseIndexDefinedInTestCheckbox(composite);
		createOpenParamsButton(composite);
		
		composite.setLayout(gridLayout);
	}

	private void createUseIndexDefinedInTestCheckbox(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		useParamIndexFromTestLabel = getWidgetFactory().createLabel(composite, "Use parameter index defined in test:");
		useParamIndexFromTestCheckbox = getWidgetFactory().createButton(composite, "", SWT.CHECK);
		useParamIndexFromTestCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {}
			public void widgetSelected(SelectionEvent e) {
				if (useParamIndexFromTestCheckbox.getSelection()) {
					//use param defined in test
					paramIndexSpinner.setEnabled(false);
					ChangeSubTestParamIndexCommand command = new ChangeSubTestParamIndexCommand();
					command.setNewIndex(-1);
					command.setTest(subtest);
					executeCommand(command);
				}
				else {
					//use param defined in spinner
					paramIndexSpinner.setEnabled(true);
					ChangeSubTestParamIndexCommand command = new ChangeSubTestParamIndexCommand();
					command.setNewIndex(paramIndexSpinner.getSelection());
					command.setTest(subtest);
					executeCommand(command);
				}
			}
		});
		useParamIndexFromTestLabel.setLayoutData(data);
	}

	private void createNoParamsLabel(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 3;
		noParamsLabel = getWidgetFactory().createLabel(composite, "Test in subtest has no parameterization configured");
		noParamsLabel.setLayoutData(data);
	}

	private void createIndexSpinner(Composite composite) {
		GridData data = new GridData();
		data.horizontalSpan = 2;
		paramIndexLabel = getWidgetFactory().createLabel(composite, "Parameter index:");

		paramIndexSpinner = new Spinner(composite, SWT.BORDER);
		paramIndexSpinner.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (subtest.getParameterIndex() != paramIndexSpinner.getSelection()) {
					ChangeSubTestParamIndexCommand command = new ChangeSubTestParamIndexCommand();
					command.setNewIndex(paramIndexSpinner.getSelection());
					command.setTest(subtest);
					executeCommand(command);
				}
			}
		});
		paramIndexSpinner.setLayoutData(data);
	}

	private void createOpenParamsButton(Composite composite) {
		openParamsButton = getWidgetFactory().createButton(composite, "Open test parameter file", SWT.PUSH);
		openParamsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ViewUtil.openFileForViewing(test.getParamList().getFileName());
			}
		});
	}
	
	
	private void createOpenTestButton(Composite composite) {
		testLabel = getWidgetFactory().createLabel(composite, "");

		openTestButton = getWidgetFactory().createButton(composite, "Open", SWT.PUSH);
		openTestButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ViewUtil.openFileForViewing(test.getFile().getFullPath().toPortableString());
			}
		});
	}

	
	private void updateIndexSpinner() {
		if(test != null && test.getParamList() != null){
			ParameterList list = test.getParamList();
			int length = list.inputParameterSize();
			paramIndexSpinner.setValues(subtest.getParameterIndex(), 0, (length <= 0) ? 0 : length - 1, 0, 1, 5);
		}
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof SubTestEditPart);
		
		subtest = (SubTest) ((SubTestEditPart) input).getModel();
		test = subtest.getTest(false);
		testLabel.setText("Test in subtest: " + test.getName());
	}
			
	private void executeCommand(Command command) {
		IWorkbenchPart part = getPart();
		if(part instanceof GraphicalTestEditor)
			((GraphicalTestEditor)part).getCommandStack().execute(command);
		
	}
	
	@Override
	public void refresh() {
		super.refresh();
		
		if (test.hasParamsConfigured()) {
			paramIndexSpinner.setVisible(true);
			paramIndexLabel.setVisible(true);
			openParamsButton.setVisible(true);
			noParamsLabel.setVisible(false);
			useParamIndexFromTestCheckbox.setVisible(true);
			useParamIndexFromTestLabel.setVisible(true);
		}
		else {
			paramIndexSpinner.setVisible(false);
			paramIndexLabel.setVisible(false);
			openParamsButton.setVisible(false);
			noParamsLabel.setVisible(true);
			useParamIndexFromTestCheckbox.setVisible(false);
			useParamIndexFromTestLabel.setVisible(false);
		}
		
		if (subtest != null && subtest.getParameterIndex() < 0) {
			useParamIndexFromTestCheckbox.setSelection(true);
			paramIndexSpinner.setEnabled(false);
		}

		if(test.getParamList() != null) {
			updateIndexSpinner();
		}
	}


	public void propertyChange(PropertyChangeEvent event) {
		refresh();
	}
}
