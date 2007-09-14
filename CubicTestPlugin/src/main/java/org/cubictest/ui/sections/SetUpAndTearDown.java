/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.sections;

import org.cubictest.model.Test;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class SetUpAndTearDown extends AbstractPropertySection {

	private Label classLabel;
	private Text className;
	private Button setClassButton;
	private Test test;

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;
		classLabel = getWidgetFactory()
				.createLabel(composite, "Class name:");
		GridData data = new GridData();
		data.widthHint = STANDARD_LABEL_WIDTH + 50;
		classLabel.setLayoutData(data);
		data = new GridData();
		// data.horizontalSpan = 2;
		data.widthHint = 300;
		className = getWidgetFactory().createText(composite, "");
		className.setLayoutData(data);

		setClassButton = new Button(composite, SWT.NONE);
		setClassButton.setText("Set class");
		setClassButton.addSelectionListener(selectionListener);

		composite.setLayout(gridLayout);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof TestEditPart);
		test = (Test) ((TestEditPart) input).getModel();
		if(test.getSetUpAndTearDownClassName() != null){
			className.setText(test.getSetUpAndTearDownClassName());
		}
	}
	
	@Override
	public void refresh() {
		super.refresh();
		if(test.getSetUpAndTearDownClassName() != null){
			className.setText(test.getSetUpAndTearDownClassName());
		}
	}
	
	SelectionListener selectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			test.setSetUpAndTearDownClassName(className.getText());
		}
	};
}
