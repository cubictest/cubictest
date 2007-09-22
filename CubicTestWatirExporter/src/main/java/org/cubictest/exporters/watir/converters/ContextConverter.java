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


	public PostContextHandle handlePostContext(WatirHolder steps, IContext a) {
		return PostContextHandle.DONE;
	}
	

	public PreContextHandle handlePreContext(WatirHolder stepList, IContext ctx) {
		if (ctx instanceof AbstractPage) {
			return PreContextHandle.CONTINUE;
		}
		
		PageElement pe = (PageElement) ctx;
		stepList.registerPageElement(pe);
		stepList.addSeparator();
		IContext context = (IContext) ctx;
		stepList.pushContext(context);

		stepList.add("# asserting " + context.toString() + " present", 2);
		stepList.add("begin", 2);
		
		
		if (stepList.requiresXPath(pe)) {
			ContextAsserterXPath.handle(stepList, pe);
		}
		else {
			ContextAsserterPlain.handle(stepList, pe);
		}
		

		PageElement element = (PageElement) ctx;
		stepList.add("puts \"" + WatirHolder.PASS + stepList.getId(element) + "\"", 3);
		stepList.add("passedSteps += 1 ", 3);
		stepList.add("rescue " + WatirHolder.TEST_STEP_FAILED, 2);
		stepList.add("puts \"" + WatirHolder.FAIL + stepList.getId(element) + "\"", 3);
		stepList.add("failedSteps += 1 ", 3);

		stepList.add("puts \"Step failed: Check " + element.toString() + " present\"", 3);
		stepList.add("end", 2);
		
		return PreContextHandle.CONTINUE;
	}


}
