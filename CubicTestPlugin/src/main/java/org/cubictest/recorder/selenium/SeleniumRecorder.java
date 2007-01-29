package org.cubictest.recorder.selenium;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.recorder.IRecorder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.openqa.selenium.server.SeleniumServer;

import com.metaparadigm.jsonrpc.JSONRPCServlet;
import com.thoughtworks.selenium.DefaultSelenium;

public class SeleniumRecorder implements IRunnableWithProgress {
	private DefaultSelenium selenium;
	private SeleniumServer seleniumProxy;
	private int port = 4444;
	private final String url;

	public SeleniumRecorder(IRecorder recorder, String url) {
		this.url = url;
		
		// start server
		
		try {
			port = findAvailablePort();
			seleniumProxy = new SeleniumServer(port);

			Server server = seleniumProxy.getServer();
			HttpContext cubicRecorder = server.getContext("/selenium-server/cubic-recorder/");
			ServletHandler servletHandler = new ServletHandler();
			servletHandler.addServlet("JSON-RPC", "/JSON-RPC", JSONRPCServlet.class.getName());
			cubicRecorder.addHandler(servletHandler);
			servletHandler.getSessionManager().addEventListener(new SeleniumRecorderSessionListener(recorder));
		} catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}
	
	public void stop() {
		selenium.stop();
		try {
			seleniumProxy.stop();
		} catch (InterruptedException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        try {
			seleniumProxy.start();
			selenium = new DefaultSelenium("localhost", seleniumProxy.getPort(), "*firefox", url);
			selenium.start();
			selenium.open(url);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				ErrorHandler.logAndShowErrorDialogAndRethrow(e);
			}
        } catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}
	
	private int findAvailablePort() throws IOException {
		ServerSocket s = new ServerSocket();
		s.bind(null);
		int port = s.getLocalPort();
		s.close();
		return port;
	}
}
