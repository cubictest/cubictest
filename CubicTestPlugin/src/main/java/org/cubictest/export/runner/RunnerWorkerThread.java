/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
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
