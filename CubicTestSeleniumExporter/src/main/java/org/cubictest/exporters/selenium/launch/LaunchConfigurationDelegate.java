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
package org.cubictest.exporters.selenium.launch;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.common.utils.EclipseLogger;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.exporters.selenium.SeleniumExporterPlugin;
import org.cubictest.exporters.selenium.runner.CubicTestRemoteRunnerClient;
import org.cubictest.exporters.selenium.runner.converters.ContextConverter;
import org.cubictest.exporters.selenium.runner.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.converters.PageElementConverter;
import org.cubictest.exporters.selenium.runner.converters.TransitionConverter;
import org.cubictest.exporters.selenium.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.runner.util.BrowserType;
import org.cubictest.exporters.selenium.runner.util.SeleniumStarter;
import org.cubictest.model.Test;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.ui.gef.editors.GraphicalTestEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class LaunchConfigurationDelegate extends
		AbstractJavaLaunchConfigurationDelegate {

	private static final String ERROR_INVALID_PROJECT_GOT_TO_BE_A_JAVA_PROJEDT = "Error invalid project, got to be a java project";
	private static final String REMOTE_CUBIC_RUNNER_IS_NOT_ON_THE_CLASSPATH = "RemoteCubicRunner is not on the classpath";
	private static final String CUBIC_RUNNER_COULD_NOT_FIND_FREE_PORT = "CubicRunner could not find free port";
	private static final String CUBIC_UNIT_PORT = "CUBIC_UNIT_PORT";
	private static final String SELENIUM_CLIENT_PROXY = "SELENIUM_CLIENT_PROXY";
	private int serverPort;
	private SeleniumStarter seleniumStarter;
	private int seleniumPort;
	private int seleniumClientProxyPort;

	@Override
	public String getMainTypeName(ILaunchConfiguration configuration)
			throws CoreException {
		// TODO Auto-generated method stub
		return "org.cubictest.runner.selenium.server.internal.CubicTestRemoteRunnerServer";
	}

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		monitor.beginTask(MessageFormat.format("{0}...", new String[]{configuration.getName()}), 5); //$NON-NLS-1$
		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}
		
		try {
			
			monitor.subTask("Verifying attributes"); 
			try {
				preLaunchCheck(configuration, launch, new SubProgressMonitor(monitor, 2));
			} catch (CoreException e) {
				if (e.getStatus().getSeverity() == IStatus.CANCEL) {
					monitor.setCanceled(true);
					return;
				}
				throw e;
			}
			// check for cancellation
			if (monitor.isCanceled()) {
				return;
			}		
			
			serverPort = evaluatePort();
			seleniumClientProxyPort = evaluatePort(); 
			
			launch.setAttribute(CUBIC_UNIT_PORT, String.valueOf(serverPort));
			launch.setAttribute(SELENIUM_CLIENT_PROXY, String.valueOf(seleniumClientProxyPort));
			
			String mainTypeName = verifyMainTypeName(configuration);
			IVMRunner runner= getVMRunner(configuration, mode);

			File workingDir = verifyWorkingDirectory(configuration);
			String workingDirName = null;
			if (workingDir != null) {
				workingDirName= workingDir.getAbsolutePath();
			}
			
			// Environment variables
			String[] envp= getEnvironment(configuration);
			
			ArrayList vmArguments= new ArrayList();
			ArrayList programArguments= new ArrayList();
			collectExecutionArguments(configuration, vmArguments, programArguments);
			
			// VM-specific attributes
			Map vmAttributesMap= getVMSpecificAttributesMap(configuration);
			
			// Classpath
			String[] classpath = getClasspath(configuration);
			
			// Create VM config
			VMRunnerConfiguration runConfig= new VMRunnerConfiguration(mainTypeName, classpath);
			runConfig.setVMArguments((String[]) vmArguments.toArray(new String[vmArguments.size()]));
			runConfig.setProgramArguments((String[]) programArguments.toArray(new String[programArguments.size()]));
			runConfig.setEnvironment(envp);
			runConfig.setWorkingDirectory(workingDirName);
			runConfig.setVMSpecificAttributesMap(vmAttributesMap);

			// Bootpath
			runConfig.setBootClassPath(getBootpath(configuration));
			
			// check for cancellation
			if (monitor.isCanceled()) {
				return;
			}		
			
			// done the verification phase
			monitor.worked(1);
			
			monitor.subTask("Create source locator description"); 
			// set the default source locator if required
			setDefaultSourceLocator(launch, configuration);
			monitor.worked(1);		
			
			// Launch the configuration - 1 unit of work
			runner.run(runConfig, launch, monitor);
			
			// check for cancellation
			if (monitor.isCanceled()) {
				return;
			}
			
			IJavaProject project = getJavaProject(configuration);
			String fileName = getTestFileName(configuration);
			IFile testFile = project.getProject().getFile(fileName);
			Test test = null;
			try{
				IWorkbench wb = PlatformUI.getWorkbench();
				IWorkbenchWindow wbw = wb.getActiveWorkbenchWindow();
				if(wbw == null)
					wbw = wb.getWorkbenchWindows()[0];
				IWorkbenchPage ap = wbw.getActivePage();
				if(ap == null)
					ap = wbw.getPages()[0];
				GraphicalTestEditor part = (GraphicalTestEditor) 
					IDE.openEditor(ap, testFile);
				test = part.getTest();
			}catch (Exception e) {
				e.printStackTrace();
				test = TestPersistance.loadFromFile(testFile);
			}
			
			String browser = getBrowser(configuration);
			
			seleniumPort = evaluatePort();
			
			TestRunner testRunner = new TestRunner(test, seleniumPort, serverPort, 
					seleniumClientProxyPort,  BrowserType.fromId(browser));
			try{
				
				testRunner.run(monitor);
			}finally{
				testRunner.cleanUp();
			}
		}catch(Exception e){
			EclipseLogger.error(e, "Error launching test");
		} finally {
			monitor.done();
		}
	}


	private String getBrowser(ILaunchConfiguration configuration) {
		try {
			return configuration.getAttribute(
					SeleniumRunnerTab.CUBIC_TEST_BROWSER, BrowserType.FIREFOX
							.getId());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return BrowserType.FIREFOX.getId();
	}

	private String getTestFileName(ILaunchConfiguration configuration) {
		try {
			return configuration.getAttribute(
					SeleniumRunnerTab.CUBIC_TEST_NAME, "");
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Performs a check on the launch configuration's attributes. If an
	 * attribute contains an invalid value, a {@link CoreException} with the
	 * error is thrown.
	 * 
	 * @param configuration
	 *            the launch configuration to verify
	 * @param launch
	 *            the launch to verify
	 * @param monitor
	 *            the progress monitor to use
	 * @throws CoreException
	 *             an exception is thrown when the verification fails
	 */
	protected void preLaunchCheck(ILaunchConfiguration configuration,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		try {
			IJavaProject javaProject = getJavaProject(configuration);
			if ((javaProject == null) || !javaProject.exists()) {
				ErrorHandler
						.logAndShowErrorDialog(ERROR_INVALID_PROJECT_GOT_TO_BE_A_JAVA_PROJEDT);
				throw new CoreException(new Status(IStatus.ERROR,
						SeleniumExporterPlugin.PLUGIN_ID,
						ERROR_INVALID_PROJECT_GOT_TO_BE_A_JAVA_PROJEDT));
			}

			if (javaProject != null
					&& javaProject.getProject().getFile(
							new Path("tests/log_in.aat")) != null) {
				return;
			}
			ErrorHandler
					.logAndShowErrorDialog(REMOTE_CUBIC_RUNNER_IS_NOT_ON_THE_CLASSPATH);
			throw new CoreException(new Status(IStatus.ERROR,
					SeleniumExporterPlugin.PLUGIN_ID,
					REMOTE_CUBIC_RUNNER_IS_NOT_ON_THE_CLASSPATH));

		} finally {
			monitor.done();
		}
	}

	private int evaluatePort() throws CoreException {
		int port = SocketUtil.findFreePort();
		if (port == -1) {
			ErrorHandler
					.logAndShowErrorDialog(CUBIC_RUNNER_COULD_NOT_FIND_FREE_PORT);
			throw new CoreException(new Status(IStatus.ERROR,
					SeleniumExporterPlugin.PLUGIN_ID,
					CUBIC_RUNNER_COULD_NOT_FIND_FREE_PORT));
		}
		return port;
	}

	/**
	 * Collects all VM and program arguments. Implementors can modify and add
	 * arguments.
	 * 
	 * @param configuration
	 *            the configuration to collect the arguments for
	 * @param vmArguments
	 *            a {@link List} of {@link String} representing the resulting VM
	 *            arguments
	 * @param programArguments
	 *            a {@link List} of {@link String} representing the resulting
	 *            program arguments
	 * @exception CoreException
	 *                if unable to collect the execution arguments
	 */
	protected void collectExecutionArguments(
			ILaunchConfiguration configuration, List<Object>/* String */vmArguments,
			List<Object>/* String */programArguments) throws CoreException {

		// add program & VM arguments provided by getProgramArguments and
		// getVMArguments
		String pgmArgs = getProgramArguments(configuration);
		String vmArgs = getVMArguments(configuration);
		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);
		vmArguments.addAll(Arrays.asList(execArgs.getVMArgumentsArray()));
		programArguments.addAll(Arrays.asList(execArgs
				.getProgramArgumentsArray()));

		programArguments.add("-port:" + String.valueOf(serverPort)); 
		programArguments.add("-seleniumClientProxyPort:" + String.valueOf(seleniumClientProxyPort));
	}
}
