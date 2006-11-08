/*
 * Created on 17.may.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.sections;

import org.cubictest.ui.gef.controller.CustomTestStepEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AdvancedPropertySection;

public class CustomTestStepInput extends AdvancedPropertySection {	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof CustomTestStepEditPart);

		CommandStack stack = ((GraphicalTestEditor)part).getCommandStack();
		page.setRootEntry(new UndoablePropertySheetEntry(stack));
	}
}