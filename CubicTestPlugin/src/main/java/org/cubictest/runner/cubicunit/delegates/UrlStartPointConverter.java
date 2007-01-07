/*
 * Created on 04.aug.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.runner.cubicunit.delegates;

import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.model.UrlStartPoint;
import org.cubicunit.Browser;
import org.cubicunit.Document;
import org.cubicunit.WebTester;

public class UrlStartPointConverter implements IUrlStartPointConverter<Holder> {

	public void handleUrlStartPoint(Holder holder, UrlStartPoint point) {
		if (point instanceof UrlStartPoint) {
			Browser browser = WebTester.internetExplorer();
			browser.setVisible(true);
			holder.addBrowser(browser);
			UrlStartPoint urlStartPoint = (UrlStartPoint) point;
			
			Document document = browser.goTo(urlStartPoint.getBeginAt());
			holder.setContainer(document);
		}
	}
}
