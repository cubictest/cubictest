/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.htmlPrototype.interfaces;

import java.io.File;
import java.util.HashMap;

import org.cubictest.exporters.htmlPrototype.delegates.HtmlPageCreator;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;

public interface ITestConverter {

	public abstract HashMap<Transition, HtmlPageCreator> convert(Test test, File outFolder, HtmlPageCreator rootPage);

}