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

import com.thoughtworks.selenium.Selenium;

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
			for(int i = 0; i < numberOfResults; i++)
				results[i] = br.readLine();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return results;
	}

	@Override
	public void addSelection(String arg0, String arg1) {
		execute("addSelection", arg0, arg1);
	}

	@Override
	public void altKeyDown() {
		execute("altKeyDown");
	}

	@Override
	public void altKeyUp() {
		execute("altKeyUp");
	}

	@Override
	public void answerOnNextPrompt(String arg0) {
		execute("answerOnNextPrompt", arg0);
	}

	@Override
	public void check(String arg0) {
		execute("check");
	}

	@Override
	public void chooseCancelOnNextConfirmation() {
		execute("chooseCancelOnNextConfirmation");
	}

	@Override
	public void click(String arg0) {
		execute("click", arg0);
	}

	@Override
	public void clickAt(String arg0, String arg1) {
		execute("clickAt", arg0, arg1);
	}

	@Override
	public void close() {
		execute("close");
	}

	@Override
	public void controlKeyDown() {
		execute("controlKeyDown");
	}

	@Override
	public void controlKeyUp() {
		execute("controlKeyUp");
	}

	@Override
	public void createCookie(String arg0, String arg1) {
		execute("createCookie", arg0, arg1);
	}

	@Override
	public void deleteCookie(String arg0, String arg1) {
		execute("deleteCookie", arg0, arg1);
	}

	@Override
	public void doubleClick(String arg0) {
		execute("doubleClick", arg0);
	}

	@Override
	public void doubleClickAt(String arg0, String arg1) {
		execute("doubleClickAt", arg0, arg1);
	}

	@Override
	public void dragAndDrop(String arg0, String arg1) {
		execute("dragAndDrop", arg0, arg1);
	}

	@Override
	public void dragAndDropToObject(String arg0, String arg1) {
		execute("dragAndDropToObject", arg0, arg1);
	}

	@Override
	public void dragdrop(String arg0, String arg1) {
		execute("dragdrop", arg0, arg1);
	}

	@Override
	public void fireEvent(String arg0, String arg1) {
		execute("fireEvent", arg0, arg1);
	}

	@Override
	public String getAlert() {
		return execute("getAlert")[0];
	}

	@Override
	public String[] getAllButtons() {
		return execute("getAllButtons");
	}

	@Override
	public String[] getAllFields() {
		return execute("getAllFields");
	}

	@Override
	public String[] getAllLinks() {
		return execute("getAllLinks");
	}

	@Override
	public String[] getAllWindowIds() {
		return execute("getAllWindowIds");
	}

	@Override
	public String[] getAllWindowNames() {
		return execute("getAllWindowNames");
	}

	@Override
	public String[] getAllWindowTitles() {
		return execute("getAllWindowTitles");
	}

	@Override
	public String getAttribute(String arg0) {
		return execute("getAttribute", arg0)[0];
	}

	@Override
	public String[] getAttributeFromAllWindows(String arg0) {
		return execute("getAttributeFromAllWindows", arg0);
	}

	@Override
	public String getBodyText() {
		return execute("getBodyText")[0];
	}

	@Override
	public String getConfirmation() {
		return execute("getConfirmation")[0];
	}

	@Override
	public String getCookie() {
		return execute("getCookie")[0];
	}

	@Override
	public Number getCursorPosition(String arg0) {
		return Double.parseDouble(execute("getCursorPosition", arg0)[0]);
	}

	@Override
	public Number getElementHeight(String arg0) {
		return Integer.parseInt(execute("getElementHeight", arg0)[0]);
	}

	@Override
	public Number getElementIndex(String arg0) {
		return Integer.parseInt(execute("getElementIndex", arg0)[0]);
	}

	@Override
	public Number getElementPositionLeft(String arg0) {
		return Integer.parseInt(execute("getElementPositionLeft", arg0)[0]);
	}

	@Override
	public Number getElementPositionTop(String arg0) {
		return Integer.parseInt(execute("getElementPositionTop", arg0)[0]);
	}

	@Override
	public Number getElementWidth(String arg0) {
		return Integer.parseInt(execute("getElementWidth", arg0)[0]);
	}

	@Override
	public String getEval(String arg0) {
		return execute("getEval", arg0)[0];
	}

	@Override
	public String getExpression(String arg0) {
		return execute("getExpression", arg0)[0];
	}

	@Override
	public String getHtmlSource() {
		return execute("getHtmlSource")[0];
	}

	@Override
	public String getLocation() {
		return execute("getLocation")[0];
	}

	@Override
	public String getLogMessages() {
		return execute("getLogMessages")[0];
	}

	@Override
	public String getPrompt() {
		return execute("getPrompt")[0];
	}

	@Override
	public String[] getSelectOptions(String arg0) {
		return execute("getSelectOptions", arg0);
	}

	@Override
	public String getSelectedId(String arg0) {
		return execute("getSelectedId", arg0)[0];
	}

	@Override
	public String[] getSelectedIds(String arg0) {
		return execute("getSelectedIds", arg0);
	}

	@Override
	public String getSelectedIndex(String arg0) {
		return execute("getSelectedIndex", arg0)[0];
	}

	@Override
	public String[] getSelectedIndexes(String arg0) {
		return execute("getSelectedIndexes", arg0);
	}

	@Override
	public String getSelectedLabel(String arg0) {
		return execute("getSelectedLabel", arg0)[0];
	}

	@Override
	public String[] getSelectedLabels(String arg0) {
		return execute("getSelectedLabels", arg0);
	}

	@Override
	public String getSelectedValue(String arg0) {
		return execute("getSelectedValue", arg0)[0];
	}

	@Override
	public String[] getSelectedValues(String arg0) {
		return execute("getSelectedValues", arg0);
	}

	@Override
	public void getSpeed() {
		execute("getSpeed");
	}

	@Override
	public String getTable(String arg0) {
		return execute("getTable", arg0)[0];
	}

	@Override
	public String getText(String arg0) {
		return execute("getText", arg0)[0];
	}

	@Override
	public String getTitle() {
		return execute("getTitle")[0];
	}

	@Override
	public String getValue(String arg0) {
		return execute("getValue", arg0)[0];
	}

	@Override
	public boolean getWhetherThisFrameMatchFrameExpression(String arg0,
			String arg1) {
		return Boolean.parseBoolean(execute("getWhetherThisFrameMatchFrameExpression", arg0, arg1)[0]);
	}

	@Override
	public boolean getWhetherThisWindowMatchWindowExpression(String arg0,
			String arg1) {
		return Boolean.parseBoolean(execute("getWhetherThisWindowMatchWindowExpression", arg0, arg1)[0]);
	}

	@Override
	public void goBack() {
		execute("goBack");
	}

	@Override
	public boolean isAlertPresent() {
		return Boolean.parseBoolean(execute("isAlertPresent")[0]);
	}

	@Override
	public boolean isChecked(String arg0) {
		return Boolean.parseBoolean(execute("isChecked", arg0)[0]);
	}

	@Override
	public boolean isConfirmationPresent() {
		return Boolean.parseBoolean(execute("isConfirmationPresent")[0]);
	}

	@Override
	public boolean isEditable(String arg0) {
		return Boolean.parseBoolean(execute("isEditable", arg0)[0]);
	}

	@Override
	public boolean isElementPresent(String arg0) {
		return Boolean.parseBoolean(execute("isElementPresent", arg0)[0]);
	}

	@Override
	public boolean isOrdered(String arg0, String arg1) {
		return Boolean.parseBoolean(execute("isOrdered", arg0, arg1)[0]);
	}

	@Override
	public boolean isPromptPresent() {
		return Boolean.parseBoolean(execute("isPromptPresent")[0]);
	}

	@Override
	public boolean isSomethingSelected(String arg0) {
		return Boolean.parseBoolean(execute("isSomethingSelected", arg0)[0]);
	}

	@Override
	public boolean isTextPresent(String arg0) {
		return Boolean.parseBoolean(execute("isTextPresent", arg0)[0]);
	}

	@Override
	public boolean isVisible(String arg0) {
		return Boolean.parseBoolean(execute("isVisible", arg0)[0]);
	}

	@Override
	public void keyDown(String arg0, String arg1) {
		execute("keyDown", arg0, arg1);
	}

	@Override
	public void keyPress(String arg0, String arg1) {
		execute("keyPress", arg0, arg1);
	}

	@Override
	public void keyUp(String arg0, String arg1) {
		execute("keyUp", arg0, arg1);
	}

	@Override
	public void metaKeyDown() {
		execute("metaKeyDown");
	}

	@Override
	public void metaKeyUp() {
		execute("metaKeyUp");
	}

	@Override
	public void mouseDown(String arg0) {
		execute("mouseDown", arg0);
	}

	@Override
	public void mouseDownAt(String arg0, String arg1) {
		execute("mouseDownAt", arg0, arg1);
	}

	@Override
	public void mouseMove(String arg0) {
		execute("mouseMove", arg0);
	}

	@Override
	public void mouseMoveAt(String arg0, String arg1) {
		execute("mouseMoveAt", arg0, arg1);
	}

	@Override
	public void mouseOut(String arg0) {
		execute("mouseOut", arg0);
	}

	@Override
	public void mouseOver(String arg0) {
		execute("mouseOver", arg0);
	}

	@Override
	public void mouseUp(String arg0) {
		execute("mouseUp", arg0);
	}

	@Override
	public void mouseUpAt(String arg0, String arg1) {
		execute("mouseUpAt", arg0, arg1);
	}

	@Override
	public void open(String arg0) {
		execute("open", arg0);
	}

	@Override
	public void openWindow(String arg0, String arg1) {
		execute("openWindow", arg0, arg1);
	}

	@Override
	public void refresh() {
		execute("refresh");
	}

	@Override
	public void removeSelection(String arg0, String arg1) {
		execute("removeSelection", arg0, arg1);
	}

	@Override
	public void select(String arg0, String arg1) {
		execute("select", arg0, arg1);
	}

	@Override
	public void selectFrame(String arg0) {
		execute("selectFrame", arg0);
	}

	@Override
	public void selectWindow(String arg0) {
		execute("selectWindow", arg0);
	}

	@Override
	public void setContext(String arg0, String arg1) {
		execute("setContext", arg0, arg1);
	}

	@Override
	public void setCursorPosition(String arg0, String arg1) {
		execute("setCursorPosition", arg0, arg1);
	}

	@Override
	public void setSpeed(String arg0) {
		execute("setSpeed", arg0);
	}

	@Override
	public void setTimeout(String arg0) {
		execute("setTimeout", arg0);
	}

	@Override
	public void shiftKeyDown() {
		execute("shiftKeyDown");
	}

	@Override
	public void shiftKeyUp() {
		execute("shiftKeyUp");
	}

	@Override
	public void start() {
		execute("start");
	}

	@Override
	public void stop() {
		execute("stop");
	}

	@Override
	public void submit(String arg0) {
		execute("submit", arg0);
	}

	@Override
	public void type(String arg0, String arg1) {
		execute("type", arg0, arg1);
	}

	@Override
	public void uncheck(String arg0) {
		execute("uncheck", arg0);
	}

	@Override
	public void waitForCondition(String arg0, String arg1) {
		execute("waitForCondition", arg0, arg1);
	}

	@Override
	public void waitForPageToLoad(String arg0) {
		execute("waitForPageToLoad", arg0);
	}

	@Override
	public void waitForPopUp(String arg0, String arg1) {
		execute("waitForPopUp", arg0, arg1);
	}

	@Override
	public void windowFocus(String arg0) {
		execute("windowFocus", arg0);
	}

	@Override
	public void windowMaximize(String arg0) {
		execute("windowMaximize", arg0);
	}
}
