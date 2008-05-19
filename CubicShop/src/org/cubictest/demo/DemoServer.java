/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Stein K. Skytteren
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Stein K. Skytteren - initial API and implementation
 *******************************************************************************/
package org.cubictest.demo;

import org.apache.log4j.BasicConfigurator;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;

/**
 * Class for starting demo webapp "Bookshop" in integrated Jetty.
 * 
 * @author chr_schwarz
 *
 */
public class DemoServer {

    /**
     * Starts the server.
     * 
     * Access the CubicShop at:
     *   http://localhost:8080/cubicshop/
     * 
     * Tools.jar from the JDK lib folder must be on the classpath.
     */
	public static void main(String[] args) {
        Server server = new Server();
        SocketListener listener = new SocketListener();
        listener.setPort(8080);
        server.addListener(listener);
        try {
            server.addWebApplication("cubicshop", "webapp/");
            server.addWebApplication("bugtracker", "bugtracker/");
            System.out.println("Starting Jetty...");
            server.start();
            System.out.println("Jetty started.");
        } catch (Exception e) {
           e.printStackTrace();
      }
   }

}
