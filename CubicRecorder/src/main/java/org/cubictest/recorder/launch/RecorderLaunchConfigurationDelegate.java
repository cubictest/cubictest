/*******************************************************************************
 * Copyright (c) 2005, 2010 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - enhanced features, bug fixes and usability improvements
 *******************************************************************************/
package org.cubictest.recorder.launch;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.export.ICubicTestRunnable;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.selenium.launch.LaunchConfigurationDelegate;
import org.cubictest.exporters.selenium.launch.LaunchTestRunner;
import org.cubictest.exporters.selenium.launch.RunnerParameters;
import org.cubictest.exporters.selenium.runner.SeleniumRunnerConfiguration;
import org.cubictest.model.Test;
import org.cubictest.recorder.CubicRecorder;
import org.cubictest.recorder.GUIAwareRecorder;
import org.cubictest.recorder.IRecorder;
import org.cubictest.recorder.selenium.SeleniumRecorder;
import org.cubictest.ui.gef.interfaces.exported.IDisposeListener;
import org.cubictest.ui.gef.interfaces.exported.ITestEditor;
import org.cubictest.ui.gef.layout.AutoLayout;

public class RecorderLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	private SeleniumRecorder seleniumRecorder;

	protected ICubicTestRunnable getCubicTestRunnable(RunnerParameters parameters, SeleniumRunnerConfiguration config) {
		try {
			ITestEditor testEditor = getTestEditor();
			AutoLayout autoLayout = new AutoLayout(testEditor);
			SynchronizedCommandStack syncCommandStack = new SynchronizedCommandStack(parameters.display, testEditor.getCommandStack());
			IRecorder cubicRecorder = new CubicRecorder(parameters.test, syncCommandStack, autoLayout, parameters.display);
			IRecorder guiAwareRecorder = new GUIAwareRecorder(cubicRecorder, parameters.display);
			LaunchTestRunner initialTestRunner = new LaunchTestRunner(parameters, config);
			
			seleniumRecorder = new SeleniumRecorder(guiAwareRecorder, parameters, config.getBrowser(), initialTestRunner);
		
			testEditor.addDisposeListener(new IDisposeListener() {
				public void disposed() {
					stopSelenium(null);
				}
			});
			
			return seleniumRecorder;
		}
		catch (final Exception e) {
			parameters.display.syncExec(new Runnable() {
				public void run() {
					UserInfo.showErrorDialog(e);
				}
			});
			throw new ExporterException("Error starting the Recorder.", e);
		}
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

		if (autoLayout != null) {
			autoLayout.setPageSelected(null);
		}
	}

	@Override
	protected void verifyPreconditions(RunnerParameters parameters, SeleniumRunnerConfiguration config) {
		final Test test = parameters.test;
		if (!ExportUtils.testIsOkForRecord(test)) {
			parameters.display.syncExec(new Runnable() {
				public void run() {
					ExportUtils.showTestNotOkForRecordMsg(test);
				}
			});
			throw new ExporterException("Test not suitable for recording. Check log for details.");
		}
	}

}
