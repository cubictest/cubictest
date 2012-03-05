/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.runner.selenium.server.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import org.cubictest.export.exceptions.ExporterException;
import org.openqa.selenium.server.browserlaunchers.BrowserOptions;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * edited by ye mao (cnkmym@gmail.com)
 * @date 2012-02-28
 */
public class CubicSelenium implements Selenium {

	private final int port;
	private Socket socket;

	public CubicSelenium(int port) {
		this.port = port;
	}
	
	private void createSocket() throws IOException, UnknownHostException {
		socket = SocketFactory.getDefault().createSocket("localhost",port);
	}
	
	private String[] execute(String command, String... args) {
		String[] results = null;
		try{
			if(socket == null){
				createSocket();
			}
			OutputStream os = socket.getOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			bw.write(command);
			bw.newLine();
			bw.write(args.length + "");
			bw.newLine();
			for (int i = 0; i < args.length; i++) {
				String value = args[i];
				bw.write(value);
				bw.newLine();
			}
			bw.flush();
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			int numberOfResults = Integer.parseInt(br.readLine());
			results = new String[numberOfResults];
			for(int i = 0; i < numberOfResults; i++) {
				results[i] = br.readLine();
				if (results[i].startsWith("Error: ")) {
					throw new ExporterException(results[i].replaceFirst("Error: ", ""));
				}
			}
		}catch (IOException e) {
			throw new RuntimeException("Could not execute Selenium command: " + command + " " + args, e);
		}
		return results;
	}


	public void addSelection(String arg0, String arg1) {
		execute("addSelection", arg0, arg1);
	}

	
	public void altKeyDown() {
		execute("altKeyDown");
	}

	
	public void altKeyUp() {
		execute("altKeyUp");
	}

	
	public void answerOnNextPrompt(String arg0) {
		execute("answerOnNextPrompt", arg0);
	}

	
	public void check(String arg0) {
		execute("check", arg0);
	}

	
	public void chooseCancelOnNextConfirmation() {
		execute("chooseCancelOnNextConfirmation");
	}

	
	public void click(String arg0) {
		execute("click", arg0);
	}

	
	public void clickAt(String arg0, String arg1) {
		execute("clickAt", arg0, arg1);
	}

	
	public void close() {
		execute("close");
	}

	
	public void controlKeyDown() {
		execute("controlKeyDown");
	}

	
	public void controlKeyUp() {
		execute("controlKeyUp");
	}

	
	public void createCookie(String arg0, String arg1) {
		execute("createCookie", arg0, arg1);
	}

	
	public void deleteCookie(String arg0, String arg1) {
		execute("deleteCookie", arg0, arg1);
	}

	
	public void doubleClick(String arg0) {
		execute("doubleClick", arg0);
	}

	
	public void doubleClickAt(String arg0, String arg1) {
		execute("doubleClickAt", arg0, arg1);
	}

	
	public void dragAndDrop(String arg0, String arg1) {
		execute("dragAndDrop", arg0, arg1);
	}

	
	public void dragAndDropToObject(String arg0, String arg1) {
		execute("dragAndDropToObject", arg0, arg1);
	}

	
	public void dragdrop(String arg0, String arg1) {
		execute("dragdrop", arg0, arg1);
	}

	
	public void fireEvent(String arg0, String arg1) {
		execute("fireEvent", arg0, arg1);
	}

	
	public String getAlert() {
		return execute("getAlert")[0];
	}

	
	public String[] getAllButtons() {
		return execute("getAllButtons");
	}

	
	public String[] getAllFields() {
		return execute("getAllFields");
	}

	
	public String[] getAllLinks() {
		return execute("getAllLinks");
	}

	
	public String[] getAllWindowIds() {
		return execute("getAllWindowIds");
	}

	
	public String[] getAllWindowNames() {
		return execute("getAllWindowNames");
	}

	
	public String[] getAllWindowTitles() {
		return execute("getAllWindowTitles");
	}

	
	public String getAttribute(String arg0) {
		return execute("getAttribute", arg0)[0];
	}

	
	public String[] getAttributeFromAllWindows(String arg0) {
		return execute("getAttributeFromAllWindows", arg0);
	}

	
	public String getBodyText() {
		return execute("getBodyText")[0];
	}

	
	public String getConfirmation() {
		return execute("getConfirmation")[0];
	}

	
	public String getCookie() {
		return execute("getCookie")[0];
	}

	
	public Number getCursorPosition(String arg0) {
		return Double.parseDouble(execute("getCursorPosition", arg0)[0]);
	}

	
	public Number getElementHeight(String arg0) {
		return Integer.parseInt(execute("getElementHeight", arg0)[0]);
	}

	
	public Number getElementIndex(String arg0) {
		return Integer.parseInt(execute("getElementIndex", arg0)[0]);
	}

	
	public Number getElementPositionLeft(String arg0) {
		return Integer.parseInt(execute("getElementPositionLeft", arg0)[0]);
	}

	
	public Number getElementPositionTop(String arg0) {
		return Integer.parseInt(execute("getElementPositionTop", arg0)[0]);
	}

	
	public Number getElementWidth(String arg0) {
		return Integer.parseInt(execute("getElementWidth", arg0)[0]);
	}

	
	public String getEval(String arg0) {
		return execute("getEval", arg0)[0];
	}

	
	public String getExpression(String arg0) {
		return execute("getExpression", arg0)[0];
	}

	
	public String getHtmlSource() {
		return execute("getHtmlSource")[0];
	}

	
	public String getLocation() {
		return execute("getLocation")[0];
	}

	
	public String getLogMessages() {
		return execute("getLogMessages")[0];
	}

	
	public String getPrompt() {
		return execute("getPrompt")[0];
	}

	
	public String[] getSelectOptions(String arg0) {
		return execute("getSelectOptions", arg0);
	}

	
	public String getSelectedId(String arg0) {
		return execute("getSelectedId", arg0)[0];
	}

	
	public String[] getSelectedIds(String arg0) {
		return execute("getSelectedIds", arg0);
	}

	
	public String getSelectedIndex(String arg0) {
		return execute("getSelectedIndex", arg0)[0];
	}

	
	public String[] getSelectedIndexes(String arg0) {
		return execute("getSelectedIndexes", arg0);
	}

	
	public String getSelectedLabel(String arg0) {
		return execute("getSelectedLabel", arg0)[0];
	}

	
	public String[] getSelectedLabels(String arg0) {
		return execute("getSelectedLabels", arg0);
	}

	
	public String getSelectedValue(String arg0) {
		return execute("getSelectedValue", arg0)[0];
	}

	
	public String[] getSelectedValues(String arg0) {
		return execute("getSelectedValues", arg0);
	}

	
	public String getSpeed() {
		return execute("getSpeed")[0];
	}

	
	public String getTable(String arg0) {
		return execute("getTable", arg0)[0];
	}

	
	public String getText(String arg0) {
		return execute("getText", arg0)[0];
	}

	
	public String getTitle() {
		return execute("getTitle")[0];
	}

	
	public String getValue(String arg0) {
		return execute("getValue", arg0)[0];
	}

	
	public boolean getWhetherThisFrameMatchFrameExpression(String arg0,
			String arg1) {
		return Boolean.parseBoolean(execute("getWhetherThisFrameMatchFrameExpression", arg0, arg1)[0]);
	}

	
	public boolean getWhetherThisWindowMatchWindowExpression(String arg0,
			String arg1) {
		return Boolean.parseBoolean(execute("getWhetherThisWindowMatchWindowExpression", arg0, arg1)[0]);
	}

	
	public void goBack() {
		execute("goBack");
	}

	
	public boolean isAlertPresent() {
		return Boolean.parseBoolean(execute("isAlertPresent")[0]);
	}

	
	public boolean isChecked(String arg0) {
		return Boolean.parseBoolean(execute("isChecked", arg0)[0]);
	}

	
	public boolean isConfirmationPresent() {
		return Boolean.parseBoolean(execute("isConfirmationPresent")[0]);
	}

	
	public boolean isEditable(String arg0) {
		return Boolean.parseBoolean(execute("isEditable", arg0)[0]);
	}

	
	public boolean isElementPresent(String arg0) {
		return Boolean.parseBoolean(execute("isElementPresent", arg0)[0]);
	}

	
	public boolean isOrdered(String arg0, String arg1) {
		return Boolean.parseBoolean(execute("isOrdered", arg0, arg1)[0]);
	}

	
	public boolean isPromptPresent() {
		return Boolean.parseBoolean(execute("isPromptPresent")[0]);
	}

	
	public boolean isSomethingSelected(String arg0) {
		return Boolean.parseBoolean(execute("isSomethingSelected", arg0)[0]);
	}

	
	public boolean isTextPresent(String arg0) {
		return Boolean.parseBoolean(execute("isTextPresent", arg0)[0]);
	}

	
	public boolean isVisible(String arg0) {
		return Boolean.parseBoolean(execute("isVisible", arg0)[0]);
	}

	
	public void keyDown(String arg0, String arg1) {
		execute("keyDown", arg0, arg1);
	}

	
	public void keyPress(String arg0, String arg1) {
		execute("keyPress", arg0, arg1);
	}

	
	public void keyUp(String arg0, String arg1) {
		execute("keyUp", arg0, arg1);
	}

	
	public void metaKeyDown() {
		execute("metaKeyDown");
	}

	
	public void metaKeyUp() {
		execute("metaKeyUp");
	}

	
	public void mouseDown(String arg0) {
		execute("mouseDown", arg0);
	}

	
	public void mouseDownAt(String arg0, String arg1) {
		execute("mouseDownAt", arg0, arg1);
	}

	
	public void mouseMove(String arg0) {
		execute("mouseMove", arg0);
	}

	
	public void mouseMoveAt(String arg0, String arg1) {
		execute("mouseMoveAt", arg0, arg1);
	}

	
	public void mouseOut(String arg0) {
		execute("mouseOut", arg0);
	}

	
	public void mouseOver(String arg0) {
		execute("mouseOver", arg0);
	}

	
	public void mouseUp(String arg0) {
		execute("mouseUp", arg0);
	}

	
	public void mouseUpAt(String arg0, String arg1) {
		execute("mouseUpAt", arg0, arg1);
	}

	
	public void open(String arg0) {
		execute("open", arg0);
	}

	
	public void openWindow(String arg0, String arg1) {
		execute("openWindow", arg0, arg1);
	}

	
	public void refresh() {
		execute("refresh");
	}

	
	public void removeSelection(String arg0, String arg1) {
		execute("removeSelection", arg0, arg1);
	}

	
	public void select(String arg0, String arg1) {
		execute("select", arg0, arg1);
	}

	
	public void selectFrame(String arg0) {
		execute("selectFrame", arg0);
	}

	
	public void selectWindow(String arg0) {
		execute("selectWindow", arg0);
	}

	
	public void setContext(String arg0, String arg1) {
		execute("setContext", arg0, arg1);
	}

	
	public void setCursorPosition(String arg0, String arg1) {
		execute("setCursorPosition", arg0, arg1);
	}

	
	public void setSpeed(String arg0) {
		execute("setSpeed", arg0);
	}

	
	public void setTimeout(String arg0) {
		execute("setTimeout", arg0);
	}

	
	public void shiftKeyDown() {
		execute("shiftKeyDown");
	}

	
	public void shiftKeyUp() {
		execute("shiftKeyUp");
	}

	
	public void start() {
		execute("start");
	}

	
	public void stop() {
		execute("stop");
	}

	
	public void submit(String arg0) {
		execute("submit", arg0);
	}

	
	public void type(String arg0, String arg1) {
		execute("type", arg0, arg1);
	}

	
	public void uncheck(String arg0) {
		execute("uncheck", arg0);
	}

	
	public void waitForCondition(String arg0, String arg1) {
		execute("waitForCondition", arg0, arg1);
	}

	
	public void waitForPageToLoad(String arg0) {
		execute("waitForPageToLoad", arg0);
	}

	
	public void waitForPopUp(String arg0, String arg1) {
		execute("waitForPopUp", arg0, arg1);
	}

	
	public void windowFocus(String arg0) {
		execute("windowFocus", arg0);
	}

	
	public void windowMaximize(String arg0) {
		execute("windowMaximize", arg0);
	}

	public void addLocationStrategy(String arg0, String arg1) {
		execute("addLocationStrategy", arg0, arg1);
	}

	public void allowNativeXpath(String arg0) {
		execute("allowNativeXpath", arg0);
	}

	public void assignId(String arg0, String arg1) {
		execute("assignId", arg0, arg1);
	}

	public void captureScreenshot(String arg0) {
		execute("captureScreenshot", arg0);
	}

	public void chooseOkOnNextConfirmation() {
		execute("chooseOkOnNextConfirmation");
	}

	public Number getMouseSpeed() {
		return Integer.parseInt(execute("getMouseSpeed")[0]);
	}

	public Number getXpathCount(String arg0) {
		return Integer.parseInt(execute("getXpathCount", arg0)[0]);
	}

	public void highlight(String arg0) {
		execute("highlight", arg0);
	}

	public void removeAllSelections(String arg0) {
		execute("removeAllSelections", arg0);
	}

	public void runScript(String arg0) {
		execute("runScript", arg0);
	}

	public void setBrowserLogLevel(String arg0) {
		execute("setBrowserLogLevel", arg0);
	}

	public void setContext(String arg0) {
		execute("setContext", arg0);
	}

	public void setMouseSpeed(String arg0) {
		execute("setMouseSpeed", arg0);
	}

	public void typeKeys(String arg0, String arg1) {
		execute("typeKeys", arg0, arg1);
	}

	public void waitForFrameToLoad(String arg0, String arg1) {
		execute("waitForFrameToLoad", arg0, arg1);
	}

	public void windowFocus() {
		execute("windowFocus");
	}

	public void windowMaximize() {
		execute("windowMaximize");
	}

	public void attachFile(String arg0, String arg1) {
		execute("attachFile", arg0, arg1);
	}

	public void captureEntirePageScreenshot(String arg0, String arg1) {
		execute("captureEntirePageScreenshot", arg0, arg1);
	}

	public String captureScreenshotToString() {
		return execute("captureScreenshotToString")[0];
	}

	public void contextMenu(String arg0) {
		execute("contextMenu", arg0);
	}

	public void contextMenuAt(String arg0, String arg1) {
		execute("contextMenuAt", arg0, arg1);
	}

	public void deleteAllVisibleCookies() {
		execute("deleteAllVisibleCookies");
	}

	public void focus(String arg0) {
		execute("focus", arg0);
	}

	public String getCookieByName(String arg0) {
		return execute("getCookieByName", arg0)[0];
	}

	public void ignoreAttributesWithoutValue(String arg0) {
		execute("ignoreAttributesWithoutValue", arg0);
	}

	public boolean isCookiePresent(String arg0) {
		return Boolean.parseBoolean(execute("isCookiePresent", arg0)[0]);
	}

	public void keyDownNative(String arg0) {
		execute("keyDownNative", arg0);
	}

	public void keyPressNative(String arg0) {
		execute("keyPressNative", arg0);
	}

	public void keyUpNative(String arg0) {
		execute("keyUpNative", arg0);
	}

	public void mouseDownRight(String arg0) {
		execute("mouseDownRight", arg0);
	}

	public void mouseDownRightAt(String arg0, String arg1) {
		execute("mouseDownRightAt", arg0, arg1);
	}

	public void mouseUpRight(String arg0) {
		execute("mouseUpRight", arg0);
	}

	public void mouseUpRightAt(String arg0, String arg1) {
		execute("mouseUpRightAt", arg0, arg1);
	}

	public void setExtensionJs(String arg0) {
		execute("setExtensionJs", arg0);
	}

	public void showContextualBanner() {
		execute("showContextualBanner");
	}

	public void shutDownSeleniumServer() {
		execute("shutDownSeleniumServer");
	}

	public void rollup(String arg0, String arg1) {
		execute("rollup", arg0, arg1);
	}
	
	public void start(BrowserOptions arg0) {
		throw new RuntimeException("Not implementedin CubicTest");
	}

	public String retrieveLastRemoteControlLogs() {
		throw new RuntimeException("Not implementedin CubicTest");
	}

	public void start(String arg0) {
		execute("start", arg0);
	}

	public void start(Object arg0) {
		throw new RuntimeException("Not implemented in CubicTest");
	}

	public void addScript(String arg0, String arg1) {
		execute("addScript", arg0, arg1);
	}

	public String captureEntirePageScreenshotToString(String arg0) {
		return execute("captureEntirePageScreenshotToString", arg0)[0];
	}

	public void removeScript(String arg0) {
		execute("removeScript", arg0);
	}

	public void showContextualBanner(String arg0, String arg1) {
		execute("showContextualBanner", arg0, arg1);
	}

	public void useXpathLibrary(String arg0) {
		execute("useXpathLibrary", arg0);
	}

	public void addCustomRequestHeader(String arg0, String arg1) {
		execute("addCustomRequestHeader", arg0, arg1);
	}

	public String captureNetworkTraffic(String arg0) {
		return execute("captureNetworkTraffic", arg0)[0];
	}

	public void deselectPopUp() {
		execute("deselectPopUp");
	}

	public void selectPopUp(String arg0) {
		execute("selectPopUp", arg0);
	}

	public String getLog() {
		return execute("getLog")[0];
	}

	public void open(String arg0, String arg1) {
		execute("open", arg0, arg1);
	}

	public Number getCssCount(String css) {
		return null;
	}
}
