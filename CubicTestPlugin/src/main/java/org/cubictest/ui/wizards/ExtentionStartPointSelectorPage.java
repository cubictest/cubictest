/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.wizards;

import java.util.Map;

import org.cubictest.model.ExtensionPoint;
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
		for(ExtensionPoint ep : extensionPoints.keySet()) {
			extentionPointNames[i++] = ep.getName() + " - " + extensionPoints.get(ep).getFullPath().toString();
		}
		list.setItems(extentionPointNames);
		list.addListener(SWT.Selection, this);
		
		int size = list.getItemCount();
		if (size > 0){
			int defaultStartpoint = 0;
			list.setSelection(defaultStartpoint);
			setSelectedStartpoint(defaultStartpoint);
		}
		
		setControl(container);
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
			if(point.equals(ep.getName() + " - " + extensionPoints.get(ep).getFullPath().toString())){
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
}
