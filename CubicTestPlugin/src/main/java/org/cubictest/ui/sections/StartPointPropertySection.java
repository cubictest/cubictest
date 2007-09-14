/*
 * Created on 16.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.sections;

import org.cubictest.model.UrlStartPoint;
import org.cubictest.ui.gef.command.ChangeUrlStartPointBeginAtCommand;
import org.cubictest.ui.gef.controller.UrlStartPointEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
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


public class StartPointPropertySection extends AbstractPropertySection {

	private Text urlText;

	private ModifyListener listener = new ModifyListener() {
		public void modifyText(ModifyEvent event) {
			GraphicalTestEditor part = (GraphicalTestEditor) getPart();
			ChangeUrlStartPointBeginAtCommand command = new ChangeUrlStartPointBeginAtCommand();
			command.setNewUrl(urlText.getText());
			command.setStartpoint(urlStartPoint);
			part.getCommandStack().execute(command);
		}
	};

	private UrlStartPoint urlStartPoint;

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory()
				.createFlatFormComposite(parent);
		FormData data;

		urlText = getWidgetFactory().createText(composite, ""); 
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		urlText.setLayoutData(data);
		urlText.addModifyListener(listener);

		CLabel labelLabel = getWidgetFactory()
				.createCLabel(composite, "Begin at URL:");
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(urlText,
				-ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(urlText, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}
	
	@Override
	public void refresh() {
		urlText.removeModifyListener(listener);
		urlText.setText(urlStartPoint.getBeginAt());
		urlText.addModifyListener(listener);
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof StructuredSelection);
		Object obj = ((StructuredSelection) selection).getFirstElement();
		Assert.isTrue(obj instanceof UrlStartPointEditPart);
		urlStartPoint = (UrlStartPoint)((UrlStartPointEditPart)obj).getModel();
	}
}
