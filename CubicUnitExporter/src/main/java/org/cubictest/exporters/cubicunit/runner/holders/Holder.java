/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.cubicunit.runner.holders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cubictest.custom.IElementContext;
import org.cubictest.export.holders.IResultHolder;
import org.cubictest.exporters.cubicunit.ui.BrowserType;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.PageElement;
import org.cubictest.model.SubTest;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.UrlStartPoint;
import org.cubicunit.Browser;
import org.cubicunit.Container;
import org.cubicunit.Document;
import org.cubicunit.Element;
import org.cubicunit.internal.selenium.SeleniumBrowserType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

public class Holder implements IResultHolder {

	private Container container;
	
	private Map<PageElement, Element> elementMap = new HashMap<PageElement, Element>();
	private List<Browser> browsers = new ArrayList<Browser>();
	private int browserIndex;
	private IElementContext context;

	private IProgressMonitor monitor;

	private Display display;

	private int port;

	private SeleniumBrowserType browserType = SeleniumBrowserType.FIREFOX;

	private UrlStartPoint urlStartPoint;; 
	
	public Holder(Display display){
		this.display = display;
	}
	
	public Container getContainer() {
		return container;
	}
	
	public void setContainer(Container container) {
		this.container = container;
	}
	
	public void put(PageElement pe, Element element){
		elementMap.put(pe, element);
	}
	
	public Element getElement(PageElement element){
		return elementMap.get(element);
	}


	public Document getDocument() {
		return getBrowser().getDocument();
	}

	public Browser getBrowser() {
		if(browsers.size() == 0)
			throw new RuntimeException("No browser opened");
		return browsers.get(browserIndex);
	}
	public Browser getNextBrowser(){
		if(browsers.size() >= ++browserIndex){
			browserIndex = 0;
		}
		return browsers.get(browserIndex);
	}
	
	public Browser getPreviousBrowser() {
		if(0 == browserIndex){
			browserIndex = browsers.size();
		}
		return browsers.get(--browserIndex);
	}
	
	public void addBrowser(Browser browser) {
		if(browsers.indexOf(browser) == -1){
			browsers.add(browser);
		}
		browserIndex = browsers.indexOf(browser);
	}

	public IElementContext getContext() {
		if (context == null) {
			context = new ElementContext();
		}
		return context;
	}

	public String toResultString() {
		return "runner";
	}

	public void addResult(final PageElement element, TestPartStatus result) {
		handleUserCancel();
		//show result immediately in the GUI:
		final TestPartStatus finalResult = result;
		display.asyncExec(new Runnable() {
			public void run() {
				if(element != null)
					element.setStatus(finalResult);
			}
		});
	}
	
	private void handleUserCancel() {
		if (monitor != null && monitor.isCanceled()) {
			getBrowser().close();
			throw new RuntimeException("Operation cancelled");
		}
	}
	
	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public void setPort(int portNumber) {
		this.port = portNumber;
	}
	
	public int getPort() {
		return port;
	}

	public void setBrowserType(BrowserType browserType) {
		switch (browserType) {
		case FIREFOX:
			this.browserType = SeleniumBrowserType.FIREFOX;
			break;
		case INTERNET_EXPLORER:
			this.browserType = SeleniumBrowserType.I_EXPLORER;
			break;
		case SAFARI:
			this.browserType = SeleniumBrowserType.SAFARI;
			break;
		case OPERA:
			this.browserType = SeleniumBrowserType.OPERA;
			break;
		default:
			this.browserType = SeleniumBrowserType.FIREFOX;
			break;
		}
	}

	public SeleniumBrowserType getBrowserType() {
		return browserType;
	}

	public void setHandledUrlStartPoint(UrlStartPoint urlStartPoint) {
		this.urlStartPoint = urlStartPoint;
	}

	public UrlStartPoint getHandledUrlStartPoint() {
		return urlStartPoint;
	}

	public void updateStatus(SubTest subTest, boolean hadException, ConnectionPoint targetConnectionPoint) {
	}

	public boolean shouldFailOnAssertionFailure() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
