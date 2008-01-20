/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.export.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.common.utils.ModelUtil;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.export.ITestRunner;
import org.cubictest.export.exceptions.UserCancelledException;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Page;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.eclipse.core.resources.IResource;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.ide.IDE;

/**
 * Base action for running tests. Contains the common logic between runner actions.
 * 
 * @author Christian Schwarz
 */
public abstract class BaseRunnerAction implements IObjectActionDelegate {

	protected ITestEditor testEditor;
	protected ITestRunner testRunner;
	private Page selectedPage;

	public BaseRunnerAction() {
		super();	
	}
		

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		Shell shell = null;
		
		try {
			Test test = getTest();

			//saving all sub tests of the test (they may not be in test-referenced memory)
			List<Test> subTests = new ArrayList<Test>();
			addSubTestsRecursive(test, subTests);
			subTests.remove(test); //"this" test can be in memory
			if (test.getStartPoint() instanceof ExtensionStartPoint) {
				subTests.add(((SubTest) test.getStartPoint()).getTest(false));
			}
			IResource[] testResources = new IResource[subTests.size()];
			int i = 0; for (Test t : subTests) {
				testResources[i] = t.getFile();
				i++;
			}
			if (!IDE.saveAllEditors(testResources, true)) {
				return;
			}
			
			if( test == null ) {
				UserInfo.showErrorDialog("Could not get test. Close editor and retry");
				return;
			}
			if (!ExportUtils.testIsOkForExport(test)) {
				ExportUtils.showTestNotOkForExportMsg(test);
				return;
			}
			
			test.resetStatus();
			
			CubicTestProjectSettings settings = new CubicTestProjectSettings(testEditor.getProject());
		
			testRunner = getTestRunner(test, Display.getCurrent(), settings);
			if (testRunner == null) {
				Logger.info("Test runner was null");
				return;
			}
			if (selectedPage != null) {
				if (!ModelUtil.isOnPathToNode(test.getStartPoint(), selectedPage)) {
					ErrorHandler.logAndShowErrorDialogAndThrow("Cannot find path from start point to selected page");
				}
				testRunner.setTargetPage(selectedPage);
			}

			shell = getShell();
			
			//run the test:
			new ProgressMonitorDialog(shell).run(true, true, testRunner);
			
			//show result:
			String result = testRunner.getResultMessage();
			test.updateAndGetStatus(null);
			showCompletedMessage(shell, result);
			finalCleanUp();
		}
		catch (UserCancelledException ignore) {
			//ok (cancel handler does clean up)
		}
		catch (InvocationTargetException e) {
			if (!(e.getCause() instanceof UserCancelledException)) {
				handleException(shell, e);
			}
		}
		catch (Exception e) {
			handleException(shell, e);
		}
	}


	private void handleException(Shell shell, Exception e) {
		try {
			if (testRunner != null) {
				((ITestRunner) testRunner).getResultMessage();
			}
			if(shell != null) {
				shell.forceActive();
			}
			ErrorHandler.logAndShowErrorDialog("Error when running test", e, shell);
		}
		finally {
			finalCleanUp();
		}
	}


	private void addSubTestsRecursive(Test test, List<Test> list) {
		if (test == null) {
			return;
		}
		for (SubTest subTest : test.getSubTests()) {
			addSubTestsRecursive(subTest.getTest(false), list);
		}
		list.add(test);
	}


	protected Test getTest() {
		return testEditor.getTest();
	}




	protected void showCompletedMessage(Shell shell, String result) {
		MessageDialog.openInformation(shell, "Test Runner done", result);
	}
	

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.testEditor = (ITestEditor) targetPart;	
	}


	public void selectionChanged(IAction action, ISelection selection) {
		this.selectedPage = null;
		Object selected = ((StructuredSelection) selection).getFirstElement();
		if (selected instanceof AbstractEditPart) {
			Object model = ((AbstractEditPart) selected).getModel();
			if (model instanceof Page) {
				this.selectedPage = (Page) model;
			}
		}
	}
	
	
	protected abstract ITestRunner getTestRunner(Test test, Display display, CubicTestProjectSettings settings);

	protected abstract Shell getShell();

	protected void finalCleanUp() {
	}
}
