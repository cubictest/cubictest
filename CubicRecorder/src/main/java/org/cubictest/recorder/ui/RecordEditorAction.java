/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder.ui;

import java.util.HashMap;
import java.util.Map;

import org.cubictest.model.Test;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action for starting / stopping the CubicRecorder.
 * Will exist in two instances, one for page edit part, and one for test edit part.
 * Uses a common action target for the two instances, so that state is shared.
 * 
 */
public class RecordEditorAction implements IObjectActionDelegate {
	private static Map<Test, RecordEditorActionTarget> testRecorderActionTargets;
	private Test test;

	public RecordEditorAction() {
		super();
	}

	
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		getActionTarget().run(action);
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		test = ((ITestEditor) targetPart).getTest();
		getActionTarget().setActivePart(action, targetPart);
	}


	private RecordEditorActionTarget getActionTarget() {
		if (testRecorderActionTargets == null) {
			testRecorderActionTargets = new HashMap<Test, RecordEditorActionTarget>();
		}
		if (testRecorderActionTargets.get(test) == null) {
			RecordEditorActionTarget target = new RecordEditorActionTarget();
			testRecorderActionTargets.put(test, target);
		}
		return testRecorderActionTargets.get(test);
	}


	public void selectionChanged(IAction action, ISelection selection) {
		getActionTarget().selectionChanged(action, selection);
	}

}
