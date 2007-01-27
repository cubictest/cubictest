/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */

package org.cubictest.ui.gef.actions;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.Test;
import org.cubictest.runner.cubicunit.CubicUnitRunner;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;


public class RunCubicUnitAction extends EditorPartAction {

	public static final String ACTION_ID = "cubicTestPlugin.action.runCubicUnitTest";
	
	public RunCubicUnitAction(IEditorPart editor) {
		super(editor);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}
	
	@Override
	protected void init() {
		super.init();
		setText("Run Test");
		setId(ACTION_ID);
	}
	

	@Override
	public void run() {
		try {
			super.run();
			GraphicalTestEditor testEditor = (GraphicalTestEditor)getEditorPart();
			Test test = testEditor.getTest();
			
			IRunnableWithProgress testRunner = new CubicUnitRunner(test);

			new ProgressMonitorDialog(new Shell()).run(false, false, testRunner);
		}catch (Exception e) {
			ErrorHandler.logAndShowErrorDialog(e);
		}
	}
}
