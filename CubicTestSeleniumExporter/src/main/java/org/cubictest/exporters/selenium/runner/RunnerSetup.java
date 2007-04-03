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
import org.cubictest.exporters.selenium.runner.util.SeleniumController;
import org.cubictest.exporters.selenium.runner.util.SeleniumController.Operation;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.Test;
import org.cubictest.model.UrlStartPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * The runner that starts servers and starts traversal of tests.
 * 
 * @author Christian
 *
 */
public class RunnerSetup implements IRunnableWithProgress {

	Test test;
	SeleniumHolder seleniumHolder;
	SeleniumController controller;
	private static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();

	
	public RunnerSetup(Test test) {
		this.test = test;
	}
	
	public void run(IProgressMonitor monitor) {
		
		try {
			controller = new SeleniumController();
			String url = getStartUrl(test);
			controller.setUrl(url);
			controller.setOperation(Operation.START);
			seleniumHolder = call(controller, 20, TimeUnit.SECONDS);
			
			TreeTestWalker<SeleniumHolder> testWalker = new TreeTestWalker<SeleniumHolder>(UrlStartPointConverter.class, 
					PageElementConverter.class, ContextConverter.class, 
					TransitionConverter.class, CustomTestStepConverter.class);
			
			monitor.beginTask("Traversing the test model...", IProgressMonitor.UNKNOWN);
			
			testWalker.convertTest(test, seleniumHolder);

		}
		catch (Exception e) {
			ErrorHandler.rethrow(e);
		}
		finally {
			controller.setOperation(Operation.STOP);
			try {
				seleniumHolder = call(controller, 20, TimeUnit.SECONDS);
			} catch (Exception e) {
				ErrorHandler.rethrow(e);
			}
		}
		monitor.done();
	}

	public void showResults() {
		seleniumHolder.showResults();
	}
	
	private String getStartUrl(Test test) {
		if (test.getStartPoint() instanceof UrlStartPoint) {
			return ((UrlStartPoint) test.getStartPoint()).getBeginAt();
		}
		else {
			//ExtensionStartPoint
			return getStartUrl(((ExtensionStartPoint) test.getStartPoint()).getTest());
		}
	}


	
	private static <T> T call(Callable<T> c, long timeout, TimeUnit timeUnit)
	    throws InterruptedException, ExecutionException, TimeoutException
	{
	    FutureTask<T> t = new FutureTask<T>(c);
	    THREADPOOL.execute(t);
	    return t.get(timeout, timeUnit);
	}


}
