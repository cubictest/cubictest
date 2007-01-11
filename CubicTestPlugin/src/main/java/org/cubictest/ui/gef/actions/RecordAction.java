/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */

package org.cubictest.ui.gef.actions;

import org.cubictest.model.Test;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.recorder.CubicRecorder;
import org.cubictest.recorder.CubicRecorderTest;
import org.cubictest.recorder.GUIAwareRecorder;
import org.cubictest.recorder.IRecorder;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.gef.ui.actions.EditorPartAction;
import org.eclipse.ui.IEditorPart;
import org.cubictest.recorder.CubicRecorderTest;
import org.cubictest.recorder.selenium.SeleniumRecorder;


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
		setId(ACTION_ID);
	}
	

	@Override
	public void run() {
		if(!running) {
			GraphicalTestEditor testEditor = (GraphicalTestEditor)getEditorPart();
			Test test = testEditor.getTest();

			if(test.getStartPoint() instanceof UrlStartPoint) {
				IRecorder cubicRecorder = new CubicRecorder(test);
				IRecorder guiAwareRecorder = new GUIAwareRecorder(cubicRecorder);
				seleniumRecorder = new SeleniumRecorder(guiAwareRecorder);
				seleniumRecorder.start(((UrlStartPoint)test.getStartPoint()).getBeginAt());			
			}			
		} else {
			seleniumRecorder.stop();
		}
		
		setRunning(!running);
	}

	private void setRunning(boolean running) {
		this.running = running;
		if(!running) {
			setText("Start Recording");
		} else {
			setText("Stop Recording");
		}
	}
}
