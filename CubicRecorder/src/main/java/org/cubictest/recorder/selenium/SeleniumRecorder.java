/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.recorder.selenium;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.common.utils.Logger;
import org.cubictest.common.utils.UserInfo;
import org.cubictest.recorder.IRecorder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.mortbay.http.HttpContext;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHandler;
import org.openqa.selenium.server.SeleniumServer;

import com.metaparadigm.jsonrpc.JSONRPCServlet;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class SeleniumRecorder implements IRunnableWithProgress {
	private boolean seleniumStarted;
	private Selenium selenium;
	private SeleniumServer seleniumProxy;
	private int port = 4444;
	private final String url;
	private Thread serverThread;
	protected Shell shell;

	public SeleniumRecorder(IRecorder recorder, String url, Shell shell) {
		this.url = url;
		this.shell = shell;
		
		// start server
		
		try {
			port = findAvailablePort();
			System.out.println("Port: " + port);
			seleniumProxy = new SeleniumServer(port);
			
			Server server = seleniumProxy.getServer();
			HttpContext cubicRecorder = server.getContext("/selenium-server/cubic-recorder/");
			ServletHandler servletHandler = new ServletHandler();
			servletHandler.addServlet("JSON-RPC", "/JSON-RPC", JSONRPCServlet.class.getName());
			cubicRecorder.addHandler(servletHandler);
			servletHandler.getSessionManager().addEventListener(new SeleniumRecorderSessionListener(recorder, url));
		} catch (Exception e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}
	
	public void stop() {
		try {
			selenium.stop();
			seleniumProxy.stop();
		} catch(SeleniumException e) {
			Logger.error(e, e.toString());
		}
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		serverThread = new Thread() {

			@Override
			public void run() {
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
					seleniumStarted = true;
		        } catch (final Exception e) {
		        	String msg = "Error occured when recording test. Recording might not work.\n\n";
		        	if (e.toString().indexOf("Location.href") >= 0) {
		        		msg += "Looks like Selenium failed when following a redirect. If this occured at start of test, " +
		        				"try modifying the start point URL to the correct/redirected address.";
		        	}
		        	final String finalMsg = msg;
		    		shell.getDisplay().asyncExec(new Runnable() {
		    			public void run() {
		    				UserInfo.showErrorDialog(e, finalMsg);
		    			}
		    		});
					ErrorHandler.logAndRethrow(e, finalMsg);
				}
			}
			
			
		};
		
		serverThread.start();
	}
	
	private int findAvailablePort() throws IOException {
		ServerSocket s = new ServerSocket();
		s.bind(null);
		int port = s.getLocalPort();
		s.close();
		return port;
	}

	public Selenium getSelenium() {
		return selenium;
	}

	public boolean isSeleniumStarted() {
		return seleniumStarted;
	}

}
