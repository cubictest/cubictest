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
