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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.cubictest.recorder.IRecorder;
import org.cubictest.recorder.JSONElementConverter;
import org.cubictest.recorder.JSONRecorder;

import com.metaparadigm.jsonrpc.JSONRPCBridge;

public class SeleniumRecorderSessionListener implements HttpSessionListener {

	private IRecorder recorder;
	private final String url;

	public SeleniumRecorderSessionListener(IRecorder recorder, String url) {
		this.recorder = recorder;
		this.url = url;
	}

	public void sessionCreated(HttpSessionEvent sessionEvent) {
		HttpSession session = sessionEvent.getSession();
		JSONRPCBridge json_bridge = null;
		json_bridge = (JSONRPCBridge) session.getAttribute("JSONRPCBridge");
		if(json_bridge == null) {
		    json_bridge = new JSONRPCBridge();
		    session.setAttribute("JSONRPCBridge", json_bridge);
			json_bridge.setDebug(true);
		}
		json_bridge.registerObject("recorder", new JSONRecorder(recorder, new JSONElementConverter(url)));
	}

	public void sessionDestroyed(HttpSessionEvent sessionEvent) {}
}
