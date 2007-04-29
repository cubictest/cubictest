/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder.ui;

import java.lang.reflect.InvocationTargetException;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Test;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.recorder.CubicRecorder;
import org.cubictest.recorder.GUIAwareRecorder;
import org.cubictest.recorder.IRecorder;
import org.cubictest.recorder.selenium.SeleniumRecorder;
import org.cubictest.ui.gef.interfaces.exported.IDisposeListener;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.cubictest.ui.gef.layout.AutoLayout;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * Action for starting / stopping the CubicRecorder.
 * 
 */
public class RecordEditorAction implements IEditorActionDelegate {
	IResource currentFile;
	private boolean running;
	private SeleniumRecorder seleniumRecorder;
	private ITestEditor testEditor;

	public RecordEditorAction() {
		super();
	}

	
	
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		AutoLayout autoLayout = new AutoLayout(testEditor);

		if(!running) {
			setRunning(true);

			Test test = testEditor.getTest();

			if(test.getStartPoint() instanceof UrlStartPoint) {
				IRecorder cubicRecorder = new CubicRecorder(test, testEditor.getCommandStack(), autoLayout);
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
			else if (test.getStartPoint() instanceof ExtensionStartPoint) {
 				ErrorHandler.showErrorDialog("The recorder only works for tests that start with a URL start point.");
			}


		} else {
			try {
				setRunning(false);
				if (seleniumRecorder != null) {
					seleniumRecorder.stop();
				}
				autoLayout.setPageSelected(null);
			} catch(Exception e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
			}
		}
				
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.testEditor = (ITestEditor) targetEditor;		
	}
	
	private void setRunning(boolean run) {
		running = run;
	}
}
