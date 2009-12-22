/*******************************************************************************
 * Copyright (c) 2005, 2008 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - bug fixes and usability improvements
 *******************************************************************************/
package org.cubictest.recorder.ui;

import java.util.HashMap;
import java.util.Map;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.ModelUtil;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.common.utils.ViewUtil;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.selenium.common.BrowserType;
import org.cubictest.exporters.selenium.common.BrowserTypeUtils;
import org.cubictest.exporters.selenium.common.SeleniumSettingsWizard;
import org.cubictest.exporters.selenium.ui.RunSeleniumRunnerAction;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.IStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.recorder.CubicRecorder;
import org.cubictest.recorder.GUIAwareRecorder;
import org.cubictest.recorder.IRecorder;
import org.cubictest.recorder.RecorderPlugin;
import org.cubictest.recorder.launch.SynchronizedCommandStack;
import org.cubictest.recorder.selenium.SeleniumRecorder;
import org.cubictest.ui.gef.interfaces.exported.IDisposeListener;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.cubictest.ui.gef.layout.AutoLayout;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Action common for the two Recorder action types (record from page edit part and record from test edit part).
 * @author Christian Schwarz
 */
public class RecordEditorActionTarget implements IObjectActionDelegate {
	public static final String RECORDER_BROWSER_TYPE = "RecorderBrowserType";
	public static final String RECORDER_REMEMBER_SETTINGS = "RecorderRememberSettings";
	private static Map<Test, Boolean> testsRecording;
	private SeleniumRecorder seleniumRecorder;
	private ITestEditor testEditor;
	private Test test;
	private Page selectedPage;
	private IAction currentState;

	public RecordEditorActionTarget() {
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

		//Handle action toggle:
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
					UserInfo.showWarnDialog("Please select a Page/State to record from (test is not empty).");
					setRunning(false);
					return;
				}
			}
			
			
			//Getting the user preferred browser:
			BrowserType browserType = BrowserTypeUtils.getPreferredBrowserType(RecorderPlugin.getDefault(), RECORDER_BROWSER_TYPE);
			if (browserType == null) {
				browserType = BrowserType.FIREFOX; //default
			}
			SeleniumSettingsWizard wizard = new SeleniumSettingsWizard(browserType, 
					RECORDER_REMEMBER_SETTINGS, RECORDER_BROWSER_TYPE, RecorderPlugin.getDefault(), true);
			WizardDialog wizDialog = new WizardDialog(new Shell(), wizard);
			boolean rememberBrowser = false;
			try {
				String remember = RecorderPlugin.getDefault().getDialogSettings().get(RECORDER_REMEMBER_SETTINGS);
				if ("true".equals(remember)) {
					rememberBrowser = true;
				}
			}
			catch (Exception ignore) {
			}
			int wizReturnCode = WizardDialog.OK;
			if (!rememberBrowser) {
				wizReturnCode = wizDialog.open();
			}
			if(wizReturnCode == WizardDialog.CANCEL){
				setRunning(false);
				return;
			}
			else {
				browserType = wizard.getBrowserType();
			}

			
			//Starting the recorder:
			Display display = new Shell().getDisplay();
			SynchronizedCommandStack syncCommandStack = new SynchronizedCommandStack(display, testEditor.getCommandStack());
			IRecorder cubicRecorder = new CubicRecorder(test, syncCommandStack, autoLayout, display);
			IRecorder guiAwareRecorder = new GUIAwareRecorder(cubicRecorder, display);
			seleniumRecorder = new SeleniumRecorder(guiAwareRecorder, getInitialUrlStartPoint(test).getBeginAt(), display, browserType, null);

			testEditor.addDisposeListener(new IDisposeListener() {
				public void disposed() {
					stopSelenium(null);
				}
			});

			try {
				new ProgressMonitorDialog(new Shell()).run(false, false, seleniumRecorder);

				//check if the browser should be forwarded:
				if (selectedNodeNeedsToForwardBrowser() || test.getStartPoint() instanceof ExtensionStartPoint) {
					UserInfo.setStatusLine("Test browser is forwarded to the selected state. Please wait...");
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
	 				runner.setActivePart(action, (IEditorPart) testEditor);
	 				runner.setCustomCompletedMessage("Recording can begin (test browser is forwarded). Result: $result.");
	 				runner.setShowCompletedMessageInStatusLine(true);
	 				runner.setStopSeleniumWhenFinished(false);
	 				runner.setSelenium(seleniumRecorder.getSelenium());
	 				runner.setPreSelectedBrowserType(browserType);
	 				if (selectedPage != null) {
	 					if (!ModelUtil.isOnPathToNode(test.getStartPoint(), selectedPage)) {
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


	private boolean selectedNodeNeedsToForwardBrowser() {
		if (selectedPage == null) {
			return false;
		}
		if (selectedPage.hasElements()) {
			return true;
		}
		if (!(selectedPage.getPreviousNode() instanceof IStartPoint)) {
			//previous node is a page/state
			return true;
		}
		return false;
	}


	private void stopSelenium(AutoLayout autoLayout) {
		try {
			if (seleniumRecorder != null) {
				seleniumRecorder.stop();
			}
		}
		catch(Exception e) {
			ErrorHandler.logAndRethrow(e);
		}
		finally {
			setRunning(false);
		}

		if (autoLayout != null) {
			autoLayout.setPageSelected(null);
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
			if (model instanceof PageElement) {
				model = ViewUtil.getSurroundingPage((EditPart)selected);
			}
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
