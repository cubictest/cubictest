/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.TreeTestWalker;
import org.cubictest.exporters.selenium.runner.converters.ContextConverter;
import org.cubictest.exporters.selenium.runner.converters.CustomTestStepConverter;
import org.cubictest.exporters.selenium.runner.converters.PageElementConverter;
import org.cubictest.exporters.selenium.runner.converters.TransitionConverter;
import org.cubictest.exporters.selenium.runner.converters.UrlStartPointConverter;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.exporters.selenium.runner.util.Browser;
import org.cubictest.exporters.selenium.runner.util.SeleniumController;
import org.cubictest.exporters.selenium.runner.util.UserCancelledException;
import org.cubictest.exporters.selenium.runner.util.SeleniumController.Operation;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Test;
import org.cubictest.model.UrlStartPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

/**
 * The runner that starts the Selenium servers and test system and starts traversal of the test model.
 * 
 * @author Christian Schwarz
 */
public class RunnerSetup implements IRunnableWithProgress {

	Test test;
	SeleniumHolder seleniumHolder;
	SeleniumController controller;
	private static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();
	private Display display;

	
	public RunnerSetup(Test test, Display display) {
		this.test = test;
		this.display = display;
	}
	
	public void run(IProgressMonitor monitor) {
		
		try {
			controller = new SeleniumController();
			controller.setInitialUrlStartPoint(getInitialUrlStartPoint(test));
			controller.setBrowser(Browser.FIREFOX);
			controller.setDisplay(display);
			
			//start Selenium (browser and server), guard by timeout:
			controller.setOperation(Operation.START);
			seleniumHolder = call(controller, 40, TimeUnit.SECONDS);
			
			//ser monitor used to detect user cancel request:
			seleniumHolder.setMonitor(monitor);
			
			while (!seleniumHolder.isSeleniumStarted()) {
				//wait for selenium (server & test system) to start
				Thread.sleep(100);
			}
			
			TreeTestWalker<SeleniumHolder> testWalker = new TreeTestWalker<SeleniumHolder>(UrlStartPointConverter.class, 
					PageElementConverter.class, ContextConverter.class, 
					TransitionConverter.class, CustomTestStepConverter.class);
			
			monitor.beginTask("Traversing the test model...", IProgressMonitor.UNKNOWN);
			
			testWalker.convertTest(test, seleniumHolder);

			monitor.done();

		}
		catch (UserCancelledException e) {
			//ok, user cancelled
		}
		catch (Exception e) {
			ErrorHandler.rethrow(e);
		}
	}

	/**
	 * Method for stopping Selenium. Can be invoked by a client class.
	 */
	public void stopSelenium() {
		try {
			if (controller != null) {
				controller.setOperation(Operation.STOP);
				call(controller, 20, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			ErrorHandler.rethrow(e);
		}
	}

	/**
	 * Show the results of the test in the GUI.
	 * @return
	 */
	public String showResults() {
		if (seleniumHolder != null) {
			return seleniumHolder.showResults();
		}
		return "";
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


	/**
	 * Call a callable object, guarded by timeout.
	 */
	private static <T> T call(Callable<T> c, long timeout, TimeUnit timeUnit)
	    throws InterruptedException, ExecutionException, TimeoutException
	{
	    FutureTask<T> t = new FutureTask<T>(c);
	    THREADPOOL.execute(t);
	    return t.get(timeout, timeUnit);
	}


}
