package org.cubictest.exporters.selenium.runner.util;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;


public class SeleniumStarterTest {
    private static final int SERVER_PORT = 4444;
	private Selenium browser;
	private SeleniumServer server;
    
    @Before
    public void setUp() throws Exception {
		RemoteControlConfiguration config = new RemoteControlConfiguration();
		config.setPort(SERVER_PORT);
		config.setPortDriversShouldContact(SERVER_PORT);
		server = new SeleniumServer(false, config);
		server.start();

		browser = new DefaultSelenium("localhost",
				SERVER_PORT, "*firefox", "http://www.google.com");
        browser.start();
    }
    
    @Test
    public void testGoogle() {
        browser.open("http://www.google.com/webhp?hl=en");
        browser.type("q", "hello world");
        browser.click("btnG");
        browser.waitForPageToLoad("5000");
        assertEquals("hello world - Google Search", browser.getTitle());
    }
    
    @After
    public void tearDown() {
        browser.stop();
        server.stop();
    }
}

