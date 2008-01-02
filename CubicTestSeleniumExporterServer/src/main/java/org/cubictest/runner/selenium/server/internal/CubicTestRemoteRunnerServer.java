package org.cubictest.runner.selenium.server.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.net.ServerSocketFactory;

import org.cubictest.runner.selenium.server.ICustomTestStep;
import org.cubictest.runner.selenium.server.IElementContext;
import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 * 
 * @author Christian Schwarz
 *
 */
public class CubicTestRemoteRunnerServer implements Runnable{
	
	private final int port;
	private Selenium selenium;
	private boolean finished = false;
	private IElementContext context;

	public CubicTestRemoteRunnerServer(int port) {
		this.port = port;
		try {
			SeleniumServer server = new SeleniumServer(10101);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	   * Server program for the "Hello, world!" example.
	   * @param argv The command line arguments which are ignored.
	   */
	public static void main (String[] argv) {
		try {
			int port = -1;
			for(String arg : argv) {
				System.out.println(arg);
				if(arg.startsWith("-port:")) {
					port = Integer.parseInt(arg.substring(6));
				}
			}
			if(port < 0 ){
				System.out.println("no port found");
				return;
			}
			CubicTestRemoteRunnerServer server = 
				new CubicTestRemoteRunnerServer(port);
			System.out.println ("run!");
			new Thread(server).start();
		} catch (Exception e) {
			System.out.println ("Hello Server failed: " + e);
		}
	}

	public void run() {
		try {
			System.out.println("Start run");
			ServerSocket serverSocket = 
				ServerSocketFactory.getDefault().createServerSocket(port);
			System.out.println("got socket: " + serverSocket);
			System.out.println("Waiting incomming");
			Socket socket = serverSocket.accept();
			System.out.println("Accepted socket: " + socket);
			while (!finished ){
				InputStreamReader isr = new InputStreamReader(socket.getInputStream());
				BufferedReader br = new BufferedReader(isr);
				System.out.println("Ready to read line..");
				String commando = br.readLine();
				System.out.println("Commando: " + commando);

				int numParams = Integer.parseInt(br.readLine());
				System.out.println("numParams: " + numParams);
				
				String[] params = new String[numParams];
				
				for(int i = 0; i < numParams; i++){
					String value = br.readLine();
					params[i] = value;
					System.out.println("value[" + i + "]: " + value);
				}
				
				String result = execute(commando, params);
				System.out.println("result: " + result);
				OutputStreamWriter writer = 
					new OutputStreamWriter(socket.getOutputStream());
				writer.write(result + "\n");
				writer.flush();
			}
			socket.close();
			System.out.println("closed!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String execute(String command, String... values){
		System.out.println("execute");
		if(selenium == null){
			System.out.println("Starting browser");
			selenium = new DefaultSelenium("localhost",10101,"*iexplore",values[0]);
			selenium.start();
			selenium.setContext("10101" + port, "debug");
			selenium.open(values[0]);
			return "10101" +  port;
		}
		if("cubicTestCustomStep".equals(command)){
			try {
				ICustomTestStep testStep = (ICustomTestStep) Class.forName(values[0]).newInstance();
				if(context == null){
					context = new ElementContext();
				}
				Map<String, String> arguments = new HashMap<String, String>();
				for(int i = 0 ; i < values.length ; i += 2){
					arguments.put(values[i], values[i+1]);
				}
				testStep.execute(arguments, context, selenium);
			} catch (Exception e) {
				e.printStackTrace();
				return "Error: " + e;
			}
		}
		
		Class<?>[] classArray = new Class[values.length];
		for (int i = 0; i < values.length; i++) {
			classArray[i] = String.class;
		}
		try {
			Method method = selenium.getClass().getMethod(command, classArray);
			System.out.println("Invoking: " + method);
			return method.invoke(selenium, (Object[])values) + "";
		} catch (Exception e) {
			finished = true;
			e.printStackTrace();
			return "Error: " + e;
		}		
	}
}
