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
import org.cubictest.ui.gef.command.ChangeParameterListIndexCommand;
import org.cubictest.ui.gef.controller.SubTestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
	private Label paramLabel;
	private Label noParamsLabel;
	private Spinner paramIndexSpinner;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = false;
		
		GridData data = new GridData();
		data.horizontalSpan = 2;
		noParamsLabel = getWidgetFactory().createLabel(composite, "Test in subtest has no parameterization configured");
		noParamsLabel.setLayoutData(data);

		paramLabel = getWidgetFactory().createLabel(composite, "Parameter index:");

		paramIndexSpinner = new Spinner(composite, SWT.BORDER);
		paramIndexSpinner.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				ChangeParameterListIndexCommand command = new ChangeParameterListIndexCommand();
				command.setParameterList(test.getParamList());
				command.setNewIndex(paramIndexSpinner.getSelection());
				command.setTest(test);
				executeCommand(command);
			}
		});
		
		composite.setLayout(gridLayout);
	}
	
	private void updateIndexSpinner() {
		if(test != null && null != test.getParamList()){
			ParameterList list = test.getParamList();
			int length = list.inputParameterSize();
			paramIndexSpinner.setValues(list.getParameterIndex(), 0, (length <= 0) ? 0 : length - 1, 0, 1, 5);
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
		Assert.isTrue(input instanceof SubTestEditPart);
		test = ((SubTest) ((SubTestEditPart) input).getModel()).getTest(false);
		test.addPropertyChangeListener(this);
		if(test.getParamList() != null)
			test.getParamList().addPropertyChangeListener(this);
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
		paramIndexSpinner.setVisible(visibile && testHasParams());
		
		if (testHasParams()) {
			paramLabel.setVisible(true);
			noParamsLabel.setVisible(false);
		}
		else {
			paramLabel.setVisible(false);
			noParamsLabel.setVisible(true);
		}
		
		if(test.getParamList() != null) {
			updateIndexSpinner();
		}
	}

	private boolean testHasParams() {
		if (test.getParamList() != null) {
			return test.getParamList().inputParameterSize() > 0;
		}
		return false;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if(event.getOldValue() instanceof ParameterList){
			((ParameterList)event.getOldValue()).removePropertyChangeListener(this);
		}
		if(event.getNewValue() instanceof ParameterList){
			((ParameterList)event.getNewValue()).removePropertyChangeListener(this);
		}
		refresh();
	}
	
	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		test.removePropertyChangeListener(this);
		if(test.getParamList() != null)
			test.getParamList().removePropertyChangeListener(this);
	}
	
}
