/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.runner;

import static org.cubictest.export.runner.RunnerWorkerThread.Operation.START;
import static org.cubictest.export.runner.RunnerWorkerThread.Operation.STOP;

import java.util.concurrent.Callable;

import org.cubictest.common.settings.CubicTestProjectSettings;
import org.cubictest.export.holders.IResultHolder;
import org.eclipse.swt.widgets.Display;

/**
 * Base class for test runner worker threads.
 * 
 * @author Christian Schwarz
 *
 */
public abstract class RunnerWorkerThread<T extends IResultHolder> implements Callable<T>{

	
	public enum Operation {START, STOP};
	
	public Operation operation = START;
	protected Display display;
	protected CubicTestProjectSettings settings;
	
	/**
	 * Method to start/stop the runner thread.
	 * Client should guard method by a timeout.
	 * The operations should resturn an IResultHolder for further use.
	 */
	public T call() throws InterruptedException {
		if (START.equals(operation)) {
			return doStart();
		}
		
		else if (STOP.equals(operation)){
			return doStop();
		}
		return null;
	}

	
	protected abstract T doStart();
	
	protected abstract T doStop();

	
	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

	public void setSettings(CubicTestProjectSettings settings) {
		this.settings = settings;
	}

}
