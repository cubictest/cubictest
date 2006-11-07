/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.actions;

import org.cubictest.model.Test;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.ui.IEditorPart;


public class ResetTestAction extends EditorPartAction {
	public static final String ACTION_ID = "cubicTestPlugin.action.resetTest";
	public ResetTestAction(IEditorPart editor) {
		super(editor);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override
	protected void init() {
		super.init();
		setText("Reset Test");
		setId(ACTION_ID);
	}
	
	@Override
	public void run() {
		super.run();
		GraphicalTestEditor testEditor = (GraphicalTestEditor)getEditorPart();
		Test test = testEditor.getTest();
		test.resetStatus();
	}

}
