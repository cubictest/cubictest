/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.runner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.ITestRunner;
import org.cubictest.model.Test;
import org.eclipse.swt.widgets.Display;

/**
 * Base class for test runners.
 * 
 * @author Christian Schwarz
 */
public abstract class BaseTestRunner implements ITestRunner {

	protected static final ExecutorService THREADPOOL = Executors.newCachedThreadPool();
	protected boolean failOnAssertionFailure;
	protected CubicTestProjectSettings settings;
	protected Display display;
	protected Test test;

	public BaseTestRunner(Display display, CubicTestProjectSettings settings, Test test) {
		this.settings = settings;
		this.display = display;
		this.test = test;
	}

	
	/**
	 * Call a callable object, guarded by timeout.
	 */
	protected static <T> T call(Callable<T> c, long timeout, TimeUnit timeUnit)
	    throws InterruptedException, ExecutionException, TimeoutException
	{
	    FutureTask<T> t = new FutureTask<T>(c);
	    THREADPOOL.execute(t);
	    return t.get(timeout, timeUnit);
	}

	public void setFailOnAssertionFailure(boolean failOnAssertionFailure) {
		this.failOnAssertionFailure = failOnAssertionFailure;
	}
	
	public Test getTest() {
		return test;
	}
}
