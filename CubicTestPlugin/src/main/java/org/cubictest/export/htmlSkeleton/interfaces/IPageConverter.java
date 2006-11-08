/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.export.htmlSkeleton.interfaces;

import java.io.File;
import java.util.Stack;

import org.cubictest.export.htmlSkeleton.delegates.HtmlPageCreator;
import org.cubictest.model.Page;

public interface IPageConverter {

	public abstract HtmlPageCreator convert(Page page, HtmlPageCreator startOfPage, Stack<HtmlPageCreator> breadcrumbs, File outFolder, String prefix);

}