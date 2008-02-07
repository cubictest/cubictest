package org.cubictest.exporters.selenium.launch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;

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
				
				String[] results = execute(commando, params);
				OutputStreamWriter writer = 
					new OutputStreamWriter(socket.getOutputStream());
				writer.write( results.length + "\n");
				for(String result : results){
					writer.write(result + "\n");
				}
				writer.flush();
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String[] execute(String commando, String[] params) {
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
