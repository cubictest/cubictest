/*
 * Created on 09.jan.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.demo;

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
     * Starts server!
     * 
     * Access the CubicShop at:
     *   http://localhost:8080/cubicshop/
     * 
     */
	public static void main(String[] args) {
        Server server = new Server();
        SocketListener listener = new SocketListener();
        listener.setPort(8080);
        server.addListener(listener);
        try {
            server.addWebApplication("cubicshop", "webapp/");
            server.start();
        } catch (Exception e) {
           e.printStackTrace();
      }
   }

}
