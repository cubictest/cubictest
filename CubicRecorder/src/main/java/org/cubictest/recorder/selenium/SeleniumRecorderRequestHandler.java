/*******************************************************************************
 * Copyright (c) 2005, 2008 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *    Christian Schwarz - bug fixes and usability improvements
 *******************************************************************************/
package org.cubictest.recorder.selenium;

import java.io.IOException;

import org.cubictest.recorder.IRecorder;
import org.mortbay.http.HttpException;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.handler.AbstractHttpHandler;

public class SeleniumRecorderRequestHandler extends AbstractHttpHandler {
    private final IRecorder recorder;

	public SeleniumRecorderRequestHandler(IRecorder recorder) {
		this.recorder = recorder;
	}

	public void handle(String pathInContext, String pathParams, HttpRequest request, HttpResponse response) throws HttpException, IOException {
    	System.out.println("Recording: " + request.toString());
    	
    	request.setHandled(true);
	}
}
