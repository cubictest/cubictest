package org.cubictest.recorder;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.metaparadigm.jsonrpc.JSONRPCBridge;

public class SeleniumRecorderSessionListener implements HttpSessionListener {

	private CubicRecorder recorder;

	public SeleniumRecorderSessionListener(CubicRecorder recorder) {
		this.recorder = recorder;
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
		json_bridge.registerObject("recorder", new JSONRecorder(recorder));
	}

	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
	}
}
