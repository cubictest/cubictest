/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder.ui;

import java.util.HashMap;
import java.util.Map;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.export.utils.exported.TestWalkerUtils;
import org.cubictest.exporters.selenium.ui.RunSeleniumRunnerAction;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.recorder.CubicRecorder;
import org.cubictest.recorder.GUIAwareRecorder;
import org.cubictest.recorder.IRecorder;
import org.cubictest.recorder.selenium.SeleniumRecorder;
import org.cubictest.ui.gef.interfaces.exported.IDisposeListener;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.cubictest.ui.gef.layout.AutoLayout;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action for starting / stopping the CubicRecorder.
 * 
 */
public class RecordEditorAction implements IObjectActionDelegate {
	private static Map<Test, Boolean> testsRecording;
	private SeleniumRecorder seleniumRecorder;
	private ITestEditor testEditor;
	private Test test;
	private Page selectedPage;
	private IAction currentState;

	public RecordEditorAction() {
		super();
	}

	
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		AutoLayout autoLayout = new AutoLayout(testEditor);
		
		if (!ExportUtils.testIsOkForRecord(test)) {
			return;
		}
		
		test.resetStatus();

		//action is toggle action:
		if(isRecording()) {
			//recorder running. stop it
			UserInfo.setStatusLine(null);
			stopSelenium(autoLayout);
			setRunning(false);

		} else {
			setRunning(true);
			TransitionNode firstPage = test.getStartPoint().getFirstNodeFromOutTransitions();
			if (firstPage != null) {
				if (firstPage.getFirstNodeFromOutTransitions() != null && selectedPage == null) {
					UserInfo.showWarnDialog("Test is not empty, please select a Page/State to record from.");
					setRunning(false);
					return;
				}
			}
			

			IRecorder cubicRecorder = new CubicRecorder(test, testEditor.getCommandStack(), autoLayout);
			IRecorder guiAwareRecorder = new GUIAwareRecorder(cubicRecorder);
			seleniumRecorder = new SeleniumRecorder(guiAwareRecorder, getInitialUrlStartPoint(test).getBeginAt(), new Shell());

			testEditor.addDisposeListener(new IDisposeListener() {
				public void disposed() {
					stopSelenium(null);
				}
			});

			try {
				new ProgressMonitorDialog(new Shell()).run(false, false, seleniumRecorder);

				//check if the browser should be forwarded:
				if (selectedPage != null || test.getStartPoint() instanceof ExtensionStartPoint) {
					UserInfo.setStatusLine("Test browser will be forwarded to start point for test.");
					long now = System.currentTimeMillis();
					while (!seleniumRecorder.isSeleniumStarted()) {
						if (System.currentTimeMillis() > now + (45 * 1000)) {
							throw new ExporterException("Timeout waiting for Selenium to start");
						}
						//wait for selenium (server & test system) to start
						Thread.yield();
						Thread.sleep(100);
					}
	 				RunSeleniumRunnerAction runner = new RunSeleniumRunnerAction();
	 				runner.setActiveEditor(action, (IEditorPart) testEditor);
	 				runner.setCustomCompletedMessage("Test browser forwarded. Result: $result. Recording can begin (test browser is open).");
	 				runner.setShowCompletedMessageInStatusLine(true);
	 				runner.setStopSeleniumWhenFinished(false);
	 				runner.setSelenium(seleniumRecorder.getSelenium());
	 				if (selectedPage != null) {
	 					if (!TestWalkerUtils.isOnPathToNode(test.getStartPoint(), selectedPage)) {
	 						ErrorHandler.logAndShowErrorDialogAndThrow("Cannot find path from start point to selected page");
	 					}
	 					runner.setTargetPage(selectedPage);
	 				}
	 				
	 				runner.setPreSelectedTest(test);
	 				runner.run(action);
				}
 				cubicRecorder.setEnabled(true);
 				if (selectedPage != null) {
 					cubicRecorder.setCursor(selectedPage);
 				}
 				guiAwareRecorder.setEnabled(true);
			}
			catch (Exception e) {
				ErrorHandler.logAndShowErrorDialog(e);
				stopSelenium(autoLayout);
				UserInfo.setStatusLine(null);
				setRunning(false);
				return;
			}
		}
	}


	private void stopSelenium(AutoLayout autoLayout) {
		try {
			if (seleniumRecorder != null) {
				seleniumRecorder.stop();
			}
			if (autoLayout != null) {
				autoLayout.setPageSelected(null);
			}
		}
		catch(Exception e) {
			ErrorHandler.logAndRethrow(e);
		}
		finally {
			setRunning(false);
		}
	}

	private void setRunning(boolean running) {
		testsRecording.put(test, running);
		currentState.setChecked(running);
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
			return getInitialUrlStartPoint(((ExtensionStartPoint) test.getStartPoint()).getTest(true));
		}
	}

	public boolean firstPageIsEmpty(Test test) {
		for(Transition t : test.getStartPoint().getOutTransitions()) {
			if(t.getEnd() instanceof Page && ((Page)t.getEnd()).getRootElements().size() == 0) {
				return true;
			}
		}
		return false;
	}


	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.testEditor = (ITestEditor) targetPart;	
		this.test = testEditor.getTest();
	}


	public void selectionChanged(IAction action, ISelection selection) {
		this.currentState = action;
		currentState.setChecked(isRecording());
		this.selectedPage = null;
		Object selected = ((StructuredSelection) selection).getFirstElement();
		if (selected instanceof AbstractEditPart) {
			Object model = ((AbstractEditPart) selected).getModel();
			if (model instanceof Page) {
				this.selectedPage = (Page) model;
			}
		}
	}


	private Boolean isRecording() {
		if (testsRecording == null) {
			testsRecording = new HashMap<Test, Boolean>();
		}
		Boolean recording =  testsRecording.get(test);
		return recording == null ? false : recording;
	}
}
