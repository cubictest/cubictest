/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.utils;

import com.thoughtworks.selenium.SeleniumException;
import com.thoughtworks.selenium.Wait;

/**
 * A utility class, designed to help the user automatically wait until a
 * condition turns true.
 */
public abstract class CubicWait extends Wait {

	
    /** Returns true when it's time to stop waiting */
    public abstract boolean until() throws SeleniumException;
    
    
    /** Wait until the "until" condition returns true or time runs out.
     * 
     * @param message the failure message
     * @param timeoutInMilliseconds the amount of time to wait before giving up
     * @param intervalInMilliseconds the interval to pause between checking "until"
     * @throws WaitTimedOutException if "until" doesn't return true until the timeout
     * @see #until()
     */
    @Override
    public void wait(String message, long timeoutInMilliseconds, long intervalInMilliseconds) {
        long start = System.currentTimeMillis();
        long end = start + timeoutInMilliseconds;
        while (System.currentTimeMillis() < end) {
            try {
				if (until()) return;
			} catch (SeleniumException se) {
				System.out.println("Ignoring Selenium Exception. Retrying.. " + se.toString());
			}
            try {
                Thread.sleep(intervalInMilliseconds);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new WaitTimedOutException(message);
    }
    
}
