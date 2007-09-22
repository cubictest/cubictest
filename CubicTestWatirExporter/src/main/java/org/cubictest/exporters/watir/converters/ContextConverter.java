/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.converters;

import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.PostContextHandle;
import org.cubictest.export.converters.PreContextHandle;
import org.cubictest.exporters.watir.converters.delegates.ContextAsserterPlain;
import org.cubictest.exporters.watir.converters.delegates.ContextAsserterXPath;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.IContext;

/**
 * Converter for contexts.
 * 
 * @author chr_schwarz
 */
public class ContextConverter implements IContextConverter<WatirHolder> {

	
	public static final String ROOT_CONTEXT = "ie";


	public PostContextHandle handlePostContext(WatirHolder watirHolder, IContext a) {
		watirHolder.setPrefix(ROOT_CONTEXT);
		return PostContextHandle.DONE;
	}
	

	public PreContextHandle handlePreContext(WatirHolder watirHolder, IContext ctx) {
		if (ctx instanceof AbstractPage) {
			return PreContextHandle.CONTINUE;
		}
		
		PageElement pe = (PageElement) ctx;
		watirHolder.registerPageElement(pe);
		watirHolder.addSeparator();
		IContext context = (IContext) ctx;
		watirHolder.pushContext(context);

		watirHolder.add("# asserting " + context.toString() + " present", 2);
		watirHolder.add("begin", 2);
		
		
		if (watirHolder.requiresXPath(pe)) {
			ContextAsserterXPath.handle(watirHolder, pe);
		}
		else {
			ContextAsserterPlain.handle(watirHolder, pe);
		}
		

		PageElement element = (PageElement) ctx;
		watirHolder.add("puts \"" + WatirHolder.PASS + watirHolder.getId(element) + "\"", 3);
		watirHolder.add("passedSteps += 1 ", 3);
		watirHolder.add("rescue " + WatirHolder.TEST_STEP_FAILED, 2);
		watirHolder.add("puts \"" + WatirHolder.FAIL + watirHolder.getId(element) + "\"", 3);
		watirHolder.add("failedSteps += 1 ", 3);

		watirHolder.add("puts \"Step failed: Check " + element.toString() + " present\"", 3);
		watirHolder.add("end", 2);
		
		return PreContextHandle.CONTINUE;
	}


}
