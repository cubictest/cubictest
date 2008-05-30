/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.ui.sections;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.customstep.CustomTestStep;
import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.utils.ViewUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.EditPart;
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
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class CustomTestStepInputSection extends AbstractPropertySection implements PropertyChangeListener {	
	private Composite composite;
	private List<CustomTestStepParameterComposite> composites = new ArrayList<CustomTestStepParameterComposite>();
	private CustomTestStepHolder customTestStepHolder;
	private Composite parent;
	private Label noArgumentsLabel;

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof EditPart);
		this.customTestStepHolder = (CustomTestStepHolder) ((EditPart) input).getModel();
		
		//listener handling should be done in aboutToBeShown / aboutToBeHidden, not here:
		for (CustomTestStepParameterComposite composite : composites) {
			composite.removeListeners();
		}
		updateInput(part, true);
		
	}
	
	@Override
	public void aboutToBeShown() {
		super.aboutToBeShown();
		customTestStepHolder.addPropertyChangeListener(this);		
	}
	
	@Override
	public void aboutToBeHidden() {
		super.aboutToBeHidden();
		customTestStepHolder.removePropertyChangeListener(this);
		customTestStepHolder.removePropertyChangeListener(this);
		for (CustomTestStepParameterComposite composite : composites) {
			composite.removeListeners();
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource().equals(customTestStepHolder) && evt.getPropertyName().equals(PropertyAwareObject.CUSTOMSTEP)) {
			updateInput(getPart(), false);
		}
	}
	

	private void updateInput(IWorkbenchPart part, boolean addListeners) {
		Iterator<CustomTestStepParameterComposite> idComIterator = composites.iterator();
		CustomTestStep customTestStep = customTestStepHolder.getCustomTestStep(false);
		List<CustomTestStepParameterComposite> newIdComs = new ArrayList<CustomTestStepParameterComposite>();
		if (customTestStep.getParameters().isEmpty()) {
			noArgumentsLabel.setVisible(true);
		}
		else {
			noArgumentsLabel.setVisible(false);
			for(Object obj : customTestStep.getParameters().toArray()){
				CustomTestStepParameterComposite parameterComposite;
				if (idComIterator.hasNext())
					parameterComposite = idComIterator.next();
				else{
					parameterComposite = new CustomTestStepParameterComposite(
							composite, getWidgetFactory(),	75);
					newIdComs.add(parameterComposite);
				}
				if(part instanceof GraphicalTestEditor){
					parameterComposite.setPart((GraphicalTestEditor) part);
				}
				parameterComposite.setValue(customTestStepHolder.getValue((CustomTestStepParameter) obj), addListeners);
				parameterComposite.setVisible(true);
				GridData layoutData = new GridData();
				layoutData.horizontalSpan = 3;
				parameterComposite.setLayoutData(layoutData);
			}
		}
		while(idComIterator.hasNext()){
			CustomTestStepParameterComposite parameterComposite = idComIterator.next();
			parameterComposite.setVisible(false);
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
		layout.numColumns = 3;
		layout.verticalSpacing = 2;
		composite.setLayout(layout);
		
		noArgumentsLabel = getWidgetFactory().createLabel(composite, "Custom Test Step has no input argument keys defined");
		Button refreshButton = getWidgetFactory().createButton(composite, "Refresh", SWT.PUSH);
		GridData data = new GridData();
		data.horizontalAlignment = SWT.END;
		refreshButton.setLayoutData(data);
		refreshButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent event) {
				super.widgetDefaultSelected(event);
				customTestStepHolder.reloadCustomTestStep();
				updateInput(getPart(), false);
			}
		});
		Button openTestButton = getWidgetFactory().createButton(composite, "Open", SWT.PUSH);
		openTestButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent se) {
				ViewUtil.openFileForViewing(customTestStepHolder.getFile().getFullPath().toPortableString());
			}
		});

	}

	@Override
	public void refresh() {
		super.refresh();
		
		parent.pack(true);
		parent.redraw();
		parent.update();
		
	}


}