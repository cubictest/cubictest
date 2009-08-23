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
package org.cubictest.export;

import java.lang.reflect.InvocationTargetException;

import org.cubictest.model.Page;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Base interface for runnable test exporters/plugins.
 * 
 * @author Christian Schwarz
 *
 */
public interface ICubicTestRunnable extends IRunnableWithProgress {

	/**
	 * Runs the runnable updating the monitor with status.
	 */
    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException;

    
	/**
	 * Get message for test completion.
	 * @return
	 */
	public String getResultMessage();

	/**
	 * Set page where test run will be stopped.
	 * @param selectedPage
	 */
	public void setTargetPage(Page selectedPage);
	
	
	/**
	 * Clean up after running test (e.g. stop selenium server). 
	 */
	public void cleanUp();
	
}
