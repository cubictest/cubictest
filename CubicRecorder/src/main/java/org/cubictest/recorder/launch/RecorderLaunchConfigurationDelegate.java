/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.recorder.launch;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.ICubicTestRunnable;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.export.utils.exported.ExportUtils;
import org.cubictest.exporters.selenium.launch.LaunchConfigurationDelegate;
import org.cubictest.exporters.selenium.launch.LaunchTestRunner;
import org.cubictest.exporters.selenium.launch.LaunchTestRunner.RunnerParameters;
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

	protected ICubicTestRunnable getCubicTestRunnable(LaunchTestRunner.RunnerParameters parameters, SeleniumRunnerConfiguration config) {
		
		ITestEditor testEditor = getTestEditor();
		AutoLayout autoLayout = new AutoLayout(testEditor);
		SynchronizedCommandStack syncCommandStack = new SynchronizedCommandStack(parameters.display, testEditor.getCommandStack());
		IRecorder cubicRecorder = new CubicRecorder(parameters.test, syncCommandStack, autoLayout, parameters.display);
		IRecorder guiAwareRecorder = new GUIAwareRecorder(cubicRecorder, parameters.display);
		LaunchTestRunner initialTestRunner = new LaunchTestRunner(parameters, config);

		seleniumRecorder = new SeleniumRecorder(guiAwareRecorder, ExportUtils.getInitialUrlStartPoint(parameters.test).getBeginAt(), parameters.display, config.getBrowser(), initialTestRunner);
		cubicRecorder.setEnabled(true);
		guiAwareRecorder.setEnabled(true);
		
		testEditor.addDisposeListener(new IDisposeListener() {
			public void disposed() {
				stopSelenium(null);
			}
		});

		return seleniumRecorder;
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
