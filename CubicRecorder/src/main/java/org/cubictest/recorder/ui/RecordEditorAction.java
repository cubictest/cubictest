/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder.ui;

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
 * Action for exporting to HTML prototype.
 * 
 * @author Christian Schwarz
 *
 */
public class RecordEditorAction implements IEditorActionDelegate {
	IResource currentFile;
	private boolean running;
	private SeleniumRecorder seleniumRecorder;
	private GraphicalTestEditor testEditor;

	public RecordEditorAction() {
		super();
	}

	
	
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		if(!running) {
//			GraphicalTestEditor testEditor = (GraphicalTestEditor)getEditorPart();
			
			Test test = testEditor.getTest();
			AutoLayout autoLayout = new AutoLayout((TestEditPart) testEditor.getGraphicalViewer().getContents());

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

		} else {
			seleniumRecorder.stop();
		}
		
		setRunning(!running);
		
		try {
		} 
		catch (Exception e) {
			MessageDialog.openError(new Shell(), "CubicTest HTML prototype exporter", "An error has occurred!" + "\n" + e.toString());
			e.printStackTrace();
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.testEditor = (GraphicalTestEditor) targetEditor;		
	}
	
	private void setRunning(boolean run) {
		running = run;
//		if(!running) {
//			setText("Start Recording");
//		} else {
//			setText("Stop Recording");
//		}
	}
}
