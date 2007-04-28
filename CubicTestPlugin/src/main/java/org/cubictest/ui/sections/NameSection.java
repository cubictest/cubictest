package org.cubictest.ui.sections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.cubictest.model.AbstractPage;
import org.cubictest.ui.gef.command.ChangeAbstractPageNameCommand;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class NameSection extends AbstractPropertySection {

	private Composite composite;
	private Text nameText;
	private AbstractPage abstractPage;
	
	private PropertyChangeListener abstractPageListener = new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt) {
			if(!composite.isDisposed()){
				refresh();
			}
		}
	};

	private FocusListener focusListener = new FocusListener(){
		public void focusLost(FocusEvent e) {
			textChanged();			
		}
		public void focusGained(FocusEvent e) {}
	};
	
	private void textChanged(){
		if(!nameText.getText().equals( abstractPage.getName())){
			GraphicalTestEditor part = (GraphicalTestEditor) getPart();
			ChangeAbstractPageNameCommand command = new ChangeAbstractPageNameCommand();
			command.setAbstractPage(abstractPage);
			command.setName(nameText.getText());
			command.setOldName(abstractPage.getName());
			abstractPage.removePropertyChangeListener(abstractPageListener);
			part.getCommandStack().execute(command);
			abstractPage.addPropertyChangeListener(abstractPageListener);
		}
	}
	
	private SelectionListener selectionListener = new SelectionListener(){
		public void widgetDefaultSelected(SelectionEvent e) {
			textChanged();
		}
		public void widgetSelected(SelectionEvent e) {}		
	};
	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
		TabbedPropertySheetWidgetFactory factory = getWidgetFactory();
		
		composite = factory.createFlatFormComposite(parent);
		
		Label label = factory.createLabel(composite, "Name:");
		
		FormData data = new FormData();
		data.left = new FormAttachment(0,0); 
		data.width = STANDARD_LABEL_WIDTH;
		label.setLayoutData(data);
		
		nameText = factory.createText(composite,"",SWT.NONE);
		
		data = new FormData();
		data.left = new FormAttachment(label);
		data.width = 300;
		//data.height = 40;
		nameText.setLayoutData(data);
		
		nameText.addFocusListener(focusListener);
		nameText.addSelectionListener(selectionListener);
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		if(abstractPage != null){
			abstractPage.removePropertyChangeListener(abstractPageListener);
		}
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof EditPart);
		this.abstractPage = (AbstractPage) ((EditPart) input).getModel();
		this.abstractPage.addPropertyChangeListener(abstractPageListener);
	}
	
	@Override
	public void refresh() {
		super.refresh();
		nameText.setText(abstractPage.getName());
	}
}
