/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import org.cubictest.model.TestPartStatus;

public class CubicTestRemoteRunnerClient implements ICubicTestRunner{

	private Socket socket;
	private int port;

	public CubicTestRemoteRunnerClient(int port) 
			throws UnknownHostException, IOException {
		this.port = port;
		createSocket();
	}

	private void createSocket() throws IOException,
			UnknownHostException {
		socket = SocketFactory.getDefault().createSocket("localhost",port);
	}
	
	public String execute(String command, String... values){
		String result = null;
		try{
			if(socket == null){
				createSocket();
			}
			OutputStream os = socket.getOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			bw.write(command);
			bw.newLine();
			bw.write(values.length + "");
			bw.newLine();
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				bw.write(value);
				bw.newLine();
			}
			bw.flush();
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			result = br.readLine();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getText(String locator) {
		return execute("getText", locator);
	}

	public String getValue(String locator) {
		return execute("getValue", locator);
	}

	public boolean isTextPresent(String text) {
		return Boolean.parseBoolean(execute("isTextPresent", text));
	}

	public String getTitle() {
		return execute("getTitle");
	}

	public void waitForPageToLoad(String string) {
		execute("waitForPageToLoad", string);
	}

	public void open(String beginAt) {
		execute("open",beginAt);
	}

	public void start() {
		execute("start");
	}

	public void setTimeout(String string) {
		execute("setTimeout", string);
	}

	public void stop() {
		execute("stop");
	}

	public String execute(String commandName, String locator, String inputValue) {
		return execute(commandName, locator, inputValue);
	}

	public String execute(String commandName, String locator) {
		return execute(commandName, locator);
	}

}
