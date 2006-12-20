package org.cubictest.recorder;

import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.DefaultSelenium;

public class SeleniumRecorder {
	private DefaultSelenium selenium;

	public SeleniumRecorder(CubicRecorder recorder) {
		// start server
		try {
			final SeleniumServer seleniumProxy = new SeleniumServer(4444);
			Server server = seleniumProxy.getServer();
			HttpContext cubicRecorder = new HttpContext();
			cubicRecorder.setContextPath("/selenium-server/cubic-recorder");
			cubicRecorder.addHandler(new SeleniumRecorderRequestHandler());
			server.addContext(cubicRecorder);
			
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
//		while(true) {
//			sel.open(url);
//		}
	}
}
