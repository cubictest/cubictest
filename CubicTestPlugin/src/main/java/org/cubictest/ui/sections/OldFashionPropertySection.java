/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.ui.sections;

import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AdvancedPropertySection;

public class OldFashionPropertySection extends AdvancedPropertySection {

	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		CommandStack stack = ((GraphicalTestEditor)part).getCommandStack();
		page.setRootEntry( new UndoablePropertySheetEntry(stack));
	}
}
