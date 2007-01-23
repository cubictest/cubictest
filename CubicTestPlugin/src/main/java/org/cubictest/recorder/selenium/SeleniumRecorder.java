package org.cubictest.recorder.selenium;

import java.lang.reflect.InvocationTargetException;

import org.cubictest.recorder.IRecorder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.openqa.selenium.server.SeleniumServer;

import com.metaparadigm.jsonrpc.JSONRPCServlet;
import com.thoughtworks.selenium.DefaultSelenium;

public class SeleniumRecorder implements IRunnableWithProgress {
	private DefaultSelenium selenium;
	private SeleniumServer seleniumProxy;
	private final String url;

	public SeleniumRecorder(IRecorder recorder, String url) {
		this.url = url;
		
		// start server
		try {
			seleniumProxy = new SeleniumServer(4444);
			Server server = seleniumProxy.getServer();
			HttpContext cubicRecorder = server.getContext("/selenium-server/cubic-recorder/");
			ServletHandler servletHandler = new ServletHandler();
			servletHandler.addServlet("JSON-RPC", "/JSON-RPC", JSONRPCServlet.class.getName());
			cubicRecorder.addHandler(servletHandler);
			servletHandler.getSessionManager().addEventListener(new SeleniumRecorderSessionListener(recorder));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void stop() {
		selenium.stop();
		try {
			seleniumProxy.stop();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
			seleniumProxy.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.selenium = new DefaultSelenium("localhost", 4444, "*firefox", url);
			selenium.start();
			selenium.open(url);
        } catch (Exception e) {
			e.printStackTrace();
		}
	}
}
