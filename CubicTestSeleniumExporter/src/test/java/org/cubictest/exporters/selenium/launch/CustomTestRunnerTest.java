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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.cubictest.exporters.selenium.runner.CubicTestRemoteRunnerClient;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.runner.selenium.server.internal.CubicTestRemoteRunnerServer;
import org.eclipse.jdt.launching.SocketUtil;
import org.junit.Before;
import org.junit.Test;

public class CustomTestRunnerTest {

	private SeleniumClientProxyServer proxyServer;
	private int clientProxyPort;
	private SeleniumHolder seleniumHolder;
	private int customRunnerPort;
	private CubicTestRemoteRunnerServer customRunnerServer;
	private CubicTestRemoteRunnerClient customStepRunner;
	private TestSelenium selenium;

	@Before
	public void setUp() throws Exception {
		clientProxyPort = SocketUtil.findFreePort();
		customRunnerPort = SocketUtil.findFreePort();
		
		selenium = new TestSelenium();
		
		seleniumHolder = new SeleniumHolder(selenium, null, null);
		proxyServer = new SeleniumClientProxyServer(seleniumHolder, clientProxyPort);
		proxyServer.start();

		
		customRunnerServer = new CubicTestRemoteRunnerServer(customRunnerPort, clientProxyPort);
		new Thread(customRunnerServer).start();
		
		customStepRunner = new CubicTestRemoteRunnerClient(customRunnerPort);		
		seleniumHolder.setCustomStepRunner(customStepRunner);
	}

	@Test
	public void shouldBeAbleToSendMessageSendingMessage(){
		String result = customStepRunner.executeOnServer("cubicTestCustomStep", MockCustomTestStep.class.getName());
		assertTrue(result, result.contains("RuntimeException"));
		assertEquals("locator", selenium.getTestLocator());
		assertEquals("event", selenium.getTestEventName());
	}
	
	@Test
	public void shouldBeAbleToSendMessageSendingMessageAndArguments(){
		String result = customStepRunner.executeOnServer("cubicTestCustomStep", 
				MockCustomTestStep.class.getName(), "firstlocator", "lastevent");
		assertTrue(result, result.contains("RuntimeException"));
		assertEquals("firstlocator", selenium.getTestLocator());
		assertEquals("lastevent", selenium.getTestEventName());
	}
}
