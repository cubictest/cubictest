/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.selenium.runner.converters;

import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.PostContextHandle;
import org.cubictest.export.converters.PreContextHandle;
import org.cubictest.exporters.selenium.runner.holders.SeleniumHolder;
import org.cubictest.model.PageElement;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Select;

import com.thoughtworks.selenium.SeleniumException;

/**
 * Converter for contexts.
 * 
 * @author chr_schwarz
 */
public class ContextConverter implements IContextConverter<SeleniumHolder> {

	/**
	 * Handle entry into a new context.
	 */
	public PreContextHandle handlePreContext(SeleniumHolder seleniumHolder, IContext ctx) {
		
		if (ctx instanceof AbstractContext || ctx instanceof Select) {

			//assert context present:
			PageElement pe = (PageElement) ctx;
			String locator = "xpath=" + seleniumHolder.getFullContextWithAllElements(pe);
			
			String text = null;
			try {
				text = seleniumHolder.getSelenium().getText(locator);
				if (text == null) {
					seleniumHolder.addResult(pe, TestPartStatus.FAIL, pe.isNot());
				}
				else {
					seleniumHolder.addResult(pe, TestPartStatus.PASS, pe.isNot());
				}
			}
			catch (SeleniumException e) {
				seleniumHolder.addResult(pe, TestPartStatus.FAIL, pe.isNot());
			}

			//save the context:
			seleniumHolder.pushContext(ctx);
		}
		return PreContextHandle.CONTINUE;
	}

	
	/**
	 * Handle exit from context.
	 */
	public PostContextHandle handlePostContext(SeleniumHolder seleniumHolder, IContext ctx) {
		
		if (ctx instanceof AbstractContext || ctx instanceof Select) {
			seleniumHolder.popContext();
		}
		return PostContextHandle.DONE;
	}
}
