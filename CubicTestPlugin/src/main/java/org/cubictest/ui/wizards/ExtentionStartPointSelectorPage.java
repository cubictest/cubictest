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
package org.cubictest.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.Test;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;


public class ExtentionStartPointSelectorPage extends WizardPage implements Listener {

	public static final String NAME = "extensionStartPointSelector";
	
	private Map<ExtensionPoint, IFile> extensionPoints;
	private boolean pageShown;
	private Test test;
	
	List list;
	private ExtensionPoint extensionPoint = null;
	private String[] extentionPointNames;

	private IFile file;
	
	public ExtentionStartPointSelectorPage(Map<ExtensionPoint,IFile> extensionPoints, IProject project) {
		super(NAME);
		this.extensionPoints = extensionPoints;
		setTitle("Choose startpoint for new Test");
		setDescription("Choose startpoint for the new Test");
		
		setPageComplete(false);
	}
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		
		GridLayout layout  = new GridLayout(2, false);
		layout.verticalSpacing = 4;
		container.setLayout(layout);
		
		
		Label label = new Label(container, SWT.NULL);
		label.setText("Choose Startpoint");

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 200;
		
		list = new List(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		list.setLayoutData(gd);
		
		
		extentionPointNames = new String[extensionPoints.size()];
		int i = 0;
		java.util.List<ExtensionPoint> sortedExPoints = new ArrayList<ExtensionPoint>(extensionPoints.keySet());
		Collections.sort(sortedExPoints);
		for(ExtensionPoint ep : sortedExPoints) {
			extentionPointNames[i++] = getRowLabel(ep);
		}
		list.setItems(extentionPointNames);
		list.addListener(SWT.Selection, this);
		
		int size = list.getItemCount();
		if (size > 0){
			int defaultStartpoint = 0;
			list.setSelection(defaultStartpoint);
			setSelectedStartpoint(defaultStartpoint);
		}
		
		if (test != null) {
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			Label nameLabel = new Label(container, SWT.FILL);
			nameLabel.setText("Test: " + test.getName() + " (" + test.getFile().getName() + ")");
			gridData.horizontalSpan = 2;
			nameLabel.setLayoutData(gridData);			
		}
		setControl(container);
	}
	
	private String getRowLabel(ExtensionPoint ep) {
		String result = ep.getName();
		if (test == null) {
			result += " - " + extensionPoints.get(ep).getFullPath().toString();
		}
		return result;
	}
	
	public void handleEvent(Event event) {
		if(event.widget == list) {
			int index = list.getSelectionIndex();
			if(index == -1)
				return;
			
			setSelectedStartpoint(index);
		}
	}
	private void setSelectedStartpoint(int index) {
		String point = extentionPointNames[index];
		for(ExtensionPoint ep : extensionPoints.keySet()) {
			if(point.equals(getRowLabel(ep))){
				extensionPoint = ep;
				file = extensionPoints.get(ep);
				break;
			}
		}
		setPageComplete(true);
	}
	
	public ExtensionPoint getExtensionPoint() {
		return extensionPoint;
	}
	
	@Override
	public IWizardPage getNextPage() {
		return null;
	}
	public IFile getExtentionPointFile() {
		return file;
	}
	public boolean isPageShown() {
		return pageShown;
	}
	public void setPageShown(boolean pageShown) {
		this.pageShown = pageShown;
	}
	public void setTest(Test test) {
		this.test = test;
	}
	
}
