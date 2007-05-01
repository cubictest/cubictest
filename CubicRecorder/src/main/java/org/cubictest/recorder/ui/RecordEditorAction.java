/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder.ui;

import java.lang.reflect.InvocationTargetException;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.selenium.ui.RunSeleniumRunnerAction;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.SubTest;
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

			IRecorder cubicRecorder = new CubicRecorder(test, testEditor.getCommandStack(), autoLayout);
			IRecorder guiAwareRecorder = new GUIAwareRecorder(cubicRecorder);
			seleniumRecorder = new SeleniumRecorder(guiAwareRecorder, getInitialUrlStartPoint(test).getBeginAt());

			testEditor.addDisposeListener(new IDisposeListener() {
				public void disposed() {
					stopSelenium(null);
				}
			});

			try {
				new ProgressMonitorDialog(new Shell()).run(false, false, seleniumRecorder);

				if (test.getStartPoint() instanceof ExtensionStartPoint) {
					ErrorHandler.showInfoDialog("Test browser will be forwarded to start point for test." + "\n" + 
							"This may take a while.");
					//play forward to extension start point
					long now = System.currentTimeMillis();
					while (!seleniumRecorder.isSeleniumStarted()) {
						if (System.currentTimeMillis() > now + (30 * 1000)) {
							throw new ExporterException("Timeout (30 sec) waiting for Selenium to start");
						}
						//wait for selenium (server & test system) to start
						Thread.yield();
						Thread.sleep(100);
					}
	 				RunSeleniumRunnerAction runner = new RunSeleniumRunnerAction();
	 				runner.setCustomDoneMessage("Test browser forwarded. (Result: $result)\n\n" +
	 						"Press OK to start recording (test browser is open).");
	 				runner.setStopSeleniumWhenFinished(false);
	 				runner.setSelenium(seleniumRecorder.getSelenium());
	 				runner.setTest(((SubTest) test.getStartPoint()).getTest());
	 				if (test.getStartPoint().getOutTransitions().size() == 0) {
	 					ErrorHandler.logAndShowErrorDialogAndThrow("To start recording, the test must have at least one page connected to the start point.");
	 				}
					ExtensionPoint targetExPoint = ((ExtensionTransition) test.getStartPoint().getOutTransitions().get(0)).getExtensionPoint();
	 				runner.setTargetExtensionPoint(targetExPoint);
	 				runner.run(action);
	 				cubicRecorder.setEnabled(true);
	 				guiAwareRecorder.setEnabled(true);
				}
			}
			catch (Exception e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
				stopSelenium(autoLayout);
			}
			

		} else {
			stopSelenium(autoLayout);
		}
	}


	private void stopSelenium(AutoLayout autoLayout) {
		try {
			setRunning(false);
			if (seleniumRecorder != null) {
				seleniumRecorder.stop();
			}
			if (autoLayout != null) {
				autoLayout.setPageSelected(null);
			}
		} catch(Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
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
	
	
	/**
	 * Get the initial URL start point of the test (expands subtests).
	 */
	private UrlStartPoint getInitialUrlStartPoint(Test test) {
		if (test.getStartPoint() instanceof UrlStartPoint) {
			return (UrlStartPoint) test.getStartPoint();
		}
		else {
			//ExtensionStartPoint, get url start point recursively:
			return getInitialUrlStartPoint(((ExtensionStartPoint) test.getStartPoint()).getTest());
		}
	}

}
