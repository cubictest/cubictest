package org.cubictest.ui.sections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cubictest.model.Identifier;
import org.cubictest.model.PageElement;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class IdentifierSection extends AbstractPropertySection {
	private PageElement pageElement;
	private Composite composite;
	private List<IdentifierComposite> composites = new ArrayList<IdentifierComposite>();
	private Composite parent;;

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof EditPart);
		this.pageElement = (PageElement) ((EditPart) input).getModel();
		
		Iterator<IdentifierComposite> idComIterator = composites.iterator();
		
		List<IdentifierComposite> newIdComs = new ArrayList<IdentifierComposite>();
		for(Identifier identifier : pageElement.getIdentifiers()){
			IdentifierComposite identifierComposite;
			if (idComIterator.hasNext())
				identifierComposite = idComIterator.next();
			else{
				identifierComposite = new IdentifierComposite(
					composite, getWidgetFactory(),	STANDARD_LABEL_WIDTH);
				newIdComs.add(identifierComposite);
			}
			if(part instanceof GraphicalTestEditor)
				identifierComposite.setPart((GraphicalTestEditor) part);
			identifierComposite.setIdentifier(pageElement, identifier);
			identifierComposite.setVisible(true);
		}
		while(idComIterator.hasNext()){
			IdentifierComposite identifierComposite = idComIterator.next();
			identifierComposite.setVisible(false);
		}
		composites.addAll(newIdComs);
		refresh();
	}


	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		this.parent = parent;
		if(composite == null)
			composite = getWidgetFactory().createFlatFormComposite(parent);
		
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 1;
		composite.setLayout(layout);
	}

	@Override
	public void refresh() {
		super.refresh();
		parent.redraw();
		parent.pack(true);
		composite.redraw();
		composite.pack(true);
		composite.redraw();
	}
	
	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		for (IdentifierComposite idCom : composites) {
			idCom.aboutToBeHidden();
		}
	}
	
	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		for (IdentifierComposite idCom : composites) {
			idCom.aboutToBeShown();
		}
	}
}
