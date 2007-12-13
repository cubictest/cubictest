/*
 * Created on 14.may.2006
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
import org.cubictest.ui.gef.controller.SubTestEditPart;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


public class SubTestInfoSection extends AbstractPropertySection implements PropertyChangeListener {

	private Test test;
	private SubTest subtest;
	private Label testLabel;
	private Button openTestButton;
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = false;
		
		createOpenTestButton(composite);
		
		composite.setLayout(gridLayout);
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

	public void propertyChange(PropertyChangeEvent evt) {
	}

}
