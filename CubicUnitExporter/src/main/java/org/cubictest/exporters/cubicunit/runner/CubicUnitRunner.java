/*******************************************************************************
 * Copyright (c) 2005, 2008  Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.cubicunit.runner;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.exporters.cubicunit.runner.converters.ContextConverter;
import org.cubictest.exporters.cubicunit.runner.converters.CustomTestStepConverter;
import org.cubictest.exporters.cubicunit.runner.converters.ElementConverter;
import org.cubictest.exporters.cubicunit.runner.converters.TransitionConverter;
import org.cubictest.exporters.cubicunit.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.cubicunit.runner.holders.Holder;
import org.cubictest.exporters.cubicunit.ui.BrowserType;
import org.cubictest.model.Test;
import org.cubicunit.internal.selenium.CubicSeleniumServer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public class CubicUnitRunner implements IRunnableWithProgress, ILaunchConfigurationDelegate{

	private Test test;
	private Display display;
	private final int portNumber;
	private BrowserType browserType;
	
	public CubicUnitRunner(Test test, Display display, int portNumber, BrowserType browserType) {
		this.test = test;
		this.display = display;
		this.portNumber = portNumber;
		this.browserType = browserType;
	}
	
	public void run(IProgressMonitor monitor) {
		//	Set up dependency hierarchy:
		Holder holder = new Holder(display);
		holder.setMonitor(monitor);
		holder.setPort(portNumber);
		holder.setBrowserType(browserType);
		
		CubicSeleniumServer server = new CubicSeleniumServer(portNumber);
		
		TreeTestWalker<Holder> testWalker = new TreeTestWalker<Holder>(UrlStartPointConverter.class, 
				ElementConverter.class, ContextConverter.class, 
				TransitionConverter.class,CustomTestStepConverter.class);
		
		monitor.beginTask("Traversing the test model...", IProgressMonitor.UNKNOWN);
		
		testWalker.convertTest(test, holder);
		
		holder.getBrowser().close();
		try {
			server.stop();
		} catch (InterruptedException e) {
			ErrorHandler.logAndShowErrorDialog("Problems stopping the CubicSeleniumServer", e);
		}
		
		monitor.done();
	}

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
	}

}
