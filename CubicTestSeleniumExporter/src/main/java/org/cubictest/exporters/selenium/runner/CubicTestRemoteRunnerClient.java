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

import org.cubictest.common.utils.Logger;
import org.cubictest.model.TestPartStatus;

public class CubicTestRemoteRunnerClient {

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
	
	public String executeOnServer(String command, String... values){
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
			Logger.error("Error invoking selenium server", e);
		}
		return result;
	}

}
