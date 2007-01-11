package org.cubictest.recorder;

import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.openqa.selenium.server.SeleniumServer;

import com.metaparadigm.jsonrpc.JSONRPCServlet;
import com.thoughtworks.selenium.DefaultSelenium;

public class SeleniumRecorder {
	private DefaultSelenium selenium;
	private SeleniumServer seleniumProxy;

	public SeleniumRecorder(CubicRecorder recorder) {
		// start server
		try {
			seleniumProxy = new SeleniumServer(4444);
			Server server = seleniumProxy.getServer();
			HttpContext cubicRecorder = server.getContext("/selenium-server/cubic-recorder/");
			ServletHandler servletHandler = new ServletHandler();
			servletHandler.addServlet("JSON-RPC", "/JSON-RPC", JSONRPCServlet.class.getName());
			cubicRecorder.addHandler(servletHandler);
			servletHandler.getSessionManager().addEventListener(new SeleniumRecorderSessionListener(recorder));
			
	        Thread jetty = new Thread(new Runnable() {
	            public void run() {
	                try {
	                    seleniumProxy.start();
	                }
	                catch (Exception e) {
	                    System.err.println("jetty run exception seen:");
	                    e.printStackTrace();
	                }
	            }
	        });

	        jetty.start();
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start(String url) {
		this.selenium = new DefaultSelenium("localhost", 4444, "*firefox", url);
		selenium.start();
		selenium.open(url);
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
}
