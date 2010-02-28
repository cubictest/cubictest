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
package org.cubictest.exporters.selenium.launch;

import org.cubictest.model.Test;
import org.eclipse.swt.widgets.Display;

public class RunnerParameters {

	public Test test;
	public Display display;
	public int remoteRunnerClientListenerPort;
	public int seleniumClientProxyPort;

}
