/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.htmlSkeleton.delegates;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.cubictest.export.htmlSkeleton.interfaces.IPageConverter;
import org.cubictest.export.htmlSkeleton.interfaces.IPageElementConverter;
import org.cubictest.model.Page;
import org.cubictest.model.Transition;
import org.cubictest.model.UserActions;


public class PageConverter implements IPageConverter {
	private IPageElementConverter pageElementConverter;
	private String extension;
	private Map<String, HtmlPageCreator> pages = new HashMap<String, HtmlPageCreator>();
	
	public PageConverter(String extension, IPageElementConverter pageElementConverter) {
		this.pageElementConverter = pageElementConverter;
		this.extension = extension;
	}
	
	public static boolean isNewPageTransition(Transition transition) {
		if(transition instanceof UserActions) {
			return !((UserActions) transition).hasNoActions();
		}
		return false;
	}
		
	public HtmlPageCreator convert(Page page, HtmlPageCreator pageStart, Stack<HtmlPageCreator> breadcrumbs, File outFolder, String prefix) {
		if(pageStart == null || isNewPageTransition(page.getInTransition())) {
			File pageFile = new File(outFolder, HtmlPageCreator.filenameFor(page, extension, prefix));
			pageStart = new HtmlPageCreator(pageFile, pageElementConverter, extension, prefix, page.getName());
		}
		
		pageStart.convert(page, breadcrumbs);
		return pageStart;
	}
}
