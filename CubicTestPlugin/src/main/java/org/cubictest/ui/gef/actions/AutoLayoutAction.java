/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */

package org.cubictest.ui.gef.actions;

import org.cubictest.layout.AutoLayout;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.ui.IEditorPart;


public class AutoLayoutAction extends EditorPartAction {

	public static final String ACTION_ID = "cubicTestPlugin.action.autoLayout";
	
	public AutoLayoutAction(IEditorPart editor) {
		super(editor);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override
	protected void init() {
		super.init();
		setText("Auto-Layout");
		setId(ACTION_ID);
	}
	

	@Override
	public void run() {
		GraphicalTestEditor testEditor = (GraphicalTestEditor)getEditorPart();
		AutoLayout manager = new AutoLayout((TestEditPart) testEditor.getGraphicalViewer().getContents());
		for(Transition t : ((Test)testEditor.getGraphicalViewer().getContents().getModel()).getStartPoint().getOutTransitions()) {
			manager.layout(t.getEnd());
		}
	}
}
