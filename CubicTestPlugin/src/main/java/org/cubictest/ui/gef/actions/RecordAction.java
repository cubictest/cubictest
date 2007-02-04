/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */

package org.cubictest.ui.gef.actions;

import java.lang.reflect.InvocationTargetException;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.layout.AutoLayout;
import org.cubictest.model.Test;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.recorder.CubicRecorder;
import org.cubictest.recorder.GUIAwareRecorder;
import org.cubictest.recorder.IRecorder;
import org.cubictest.recorder.selenium.SeleniumRecorder;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.cubictest.ui.gef.interfaces.IDisposeListener;
import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

public class RecordAction extends EditorPartAction {

	public static final String ACTION_ID = "cubicTestPlugin.action.recordTest";
	private boolean running;
	private SeleniumRecorder seleniumRecorder;
	
	public RecordAction(IEditorPart editor) {
		super(editor);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	
	@Override
	protected void init() {
		super.init();
		setRunning(false);
		setLazyEnablementCalculation(true);
		setId(ACTION_ID);
	}
	

	@Override
	public void run() {
		if(!running) {
			GraphicalTestEditor testEditor = (GraphicalTestEditor)getEditorPart();
			Test test = testEditor.getTest();
			AutoLayout autoLayout = new AutoLayout((TestEditPart) testEditor.getGraphicalViewer().getContents());

			if(test.getStartPoint() instanceof UrlStartPoint) {
				IRecorder cubicRecorder = new CubicRecorder(test, getCommandStack(), autoLayout);
				IRecorder guiAwareRecorder = new GUIAwareRecorder(cubicRecorder);
				seleniumRecorder = new SeleniumRecorder(guiAwareRecorder, ((UrlStartPoint)test.getStartPoint()).getBeginAt());

				try {
					new ProgressMonitorDialog(new Shell()).run(false, false, seleniumRecorder);
				} catch (InvocationTargetException e) {
					ErrorHandler.logAndShowErrorDialogAndRethrow(e);
				} catch (InterruptedException e) {
					ErrorHandler.logAndShowErrorDialogAndRethrow(e);
				}
				
				testEditor.addDisposeListener(new IDisposeListener() {
					public void disposed() {
						seleniumRecorder.stop();
					}
				});
			}

		} else {
			seleniumRecorder.stop();
		}
		
		setRunning(!running);
	}

	private void setRunning(boolean run) {
		running = run;
		if(!running) {
			setText("Start Recording");
		} else {
			setText("Stop Recording");
		}
	}
}
