/*
 * Created on 04.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.exporters.cubicunit.runner;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.exporters.cubicunit.runner.converters.ContextConverter;
import org.cubictest.exporters.cubicunit.runner.converters.CustomTestStepConverter;
import org.cubictest.exporters.cubicunit.runner.converters.ElementConverter;
import org.cubictest.exporters.cubicunit.runner.converters.TransitionConverter;
import org.cubictest.exporters.cubicunit.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.cubicunit.runner.holders.Holder;
import org.cubictest.model.Test;
import org.cubicunit.internal.selenium.CubicSeleniumServer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class CubicUnitRunner implements IRunnableWithProgress, ILaunchConfigurationDelegate{

	private Test test;
	
	public CubicUnitRunner(Test test) {
		this.test = test;
	}
	
	public void run(IProgressMonitor monitor) {
		//	Set up dependency hierarchy:
		Holder holder = new Holder();
		
		CubicSeleniumServer server = new CubicSeleniumServer(4444);
		
		TreeTestWalker<Holder> testWalker = new TreeTestWalker<Holder>(UrlStartPointConverter.class, 
				ElementConverter.class, ContextConverter.class, 
				TransitionConverter.class,CustomTestStepConverter.class);
		
		monitor.beginTask("Traversing the test model...", IProgressMonitor.UNKNOWN);
		
		testWalker.convertTest(test, holder);
		
		try {
			server.stop();
		} catch (InterruptedException e) {
			ErrorHandler.logAndShowErrorDialog(e, "Problems stopping the CubicSeleniumServer");
		}
		
		monitor.done();
	}

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
	}

}
