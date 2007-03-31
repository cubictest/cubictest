/*
 * Created on 14.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.sections;

import org.cubictest.model.Test;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


public class TestDescriptionSection extends AbstractPropertySection {

	private Text labelText;

	private ModifyListener listener = new ModifyListener() {
		public void modifyText(ModifyEvent event) {
			test.setDescription(labelText.getText());
		}
	};
	private Test test;

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;

		labelText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		data.height = 125;
		labelText.setLayoutData(data);
		labelText.addModifyListener(listener);

		CLabel labelLabel = getWidgetFactory()
				.createCLabel(composite, "Description:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(labelText,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(labelText, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof TestEditPart);
		this.test = (Test) ((TestEditPart) input).getModel();
	}

	@Override
	public void refresh() {
		labelText.removeModifyListener(listener);
		labelText.setText(test.getDescription());
		labelText.addModifyListener(listener);
	}

}
