/*
 * Created on 04.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.exporters.selenium.runner;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.exporters.selenium.runner.converters.ContextConverter;
import org.cubictest.exporters.selenium.runner.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.converters.PageElementConverter;
import org.cubictest.exporters.selenium.runner.converters.TransitionConverter;
import org.cubictest.exporters.selenium.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.Test;
import org.cubictest.model.UrlStartPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class SeleniumRunner implements IRunnableWithProgress {

	private Test test;
	
	public SeleniumRunner(Test test) {
		this.test = test;
	}
	
	public void run(IProgressMonitor monitor) {
		//	Set up dependency hierarchy:		Holder holder = new Holder();
		
		SeleniumRunnerServer server = new SeleniumRunnerServer();
		server.start();
		try {
			//wait for server to start
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		String url = ((UrlStartPoint) test.getStartPoint()).getBeginAt();
		Logger.info("Connecting to Selenium Proxy... Port " + server.getPort() + ", URL: " + url);
		SeleniumHolder seleniumHolder = new SeleniumHolder(server.getPort(), "*opera", url);
		seleniumHolder.getSelenium().start();
		seleniumHolder.getSelenium().open(url);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		TreeTestWalker<SeleniumHolder> testWalker = new TreeTestWalker<SeleniumHolder>(UrlStartPointConverter.class, 
				PageElementConverter.class, ContextConverter.class, 
				TransitionConverter.class, CustomTestStepConverter.class);
		
		monitor.beginTask("Traversing the test model...", IProgressMonitor.UNKNOWN);
		
		testWalker.convertTest(test, seleniumHolder);
		
		try {
			server.stop();
		} 
		catch (InterruptedException e) {
			ErrorHandler.logAndShowErrorDialog(e, "Problems stopping the CubicSeleniumServer");
		}
		seleniumHolder.getSelenium().stop();
		
		monitor.done();
	}



}
