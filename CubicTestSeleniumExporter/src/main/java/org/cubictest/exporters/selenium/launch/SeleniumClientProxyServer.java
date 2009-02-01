package org.cubictest.exporters.selenium.launch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

import org.cubictest.common.utils.Logger;
import org.cubictest.exporters.selenium.runner.holders.CubicTestLocalRunner;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;

public class SeleniumClientProxyServer extends Thread {
	
	private final SeleniumHolder seleniumHolder;
	private final int port;
	private boolean finished = false;
	private Socket socket;

	public SeleniumClientProxyServer(SeleniumHolder seleniumHolder, int port) {
		this.seleniumHolder = seleniumHolder;
		this.port = port;
	}
	
	@Override
	public void run() {
		try{
			System.out.println("SeleniumClientProxyServer will run at port " + port);
			ServerSocket serverSocket = 
				ServerSocketFactory.getDefault().createServerSocket(port);
			socket = serverSocket.accept();
			while (!finished ){
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				String commando = br.readLine();
	
				int numParams = Integer.parseInt(br.readLine());
				
				String[] params = new String[numParams];
				
				for(int i = 0; i < numParams; i++){
					String value = br.readLine();
					params[i] = value;
				}
				
				OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream());
				
				String[] results;
				try {
					results = execute(commando, params);
				} catch (Throwable e) {
					Logger.error("Exception executing Selenium command.", e);
					results = new String[] { "Error: " + e.toString()};
				}
				
				writer.write( results.length + "\n");
				for(String result : results){
					writer.write(result + "\n");
				}
				writer.flush();
			}
			socket.close();
		} catch (IOException e) {
			Logger.error("IOException in server", e);
		}
	}
	
	private String[] execute(String commando, String[] params) throws Throwable {
		CubicTestLocalRunner selenium = seleniumHolder.getSelenium();
		return selenium.execute(commando, params);
	}

	public void shutdown() {
		finished = true;
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}
}
