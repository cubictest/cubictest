package org.cubictest.export;

import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Base interface for test runners.
 * 
 * @author Christian Schwarz
 *
 */
public interface ITestRunner extends IRunnableWithProgress {

	public String getResultMessage();
	
}
