/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.watir.converters;

import static org.cubictest.model.IdentifierType.ID;

import org.apache.commons.lang.StringUtils;
import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.PostContextHandle;
import org.cubictest.export.converters.PreContextHandle;
import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.StepList;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.PageElement;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.Frame;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Select;

/**
 * Converter for contexts.
 * 
 * @author chr_schwarz
 */
public class ContextConverter implements IContextConverter<StepList> {

	
	public static final String ROOT_CONTEXT = "ie";


	public PostContextHandle handlePostContext(StepList steps, IContext a) {
		steps.setPrefix(ROOT_CONTEXT);
		return PostContextHandle.DONE;
	}
	

	public PreContextHandle handlePreContext(StepList stepList, IContext ctx) {
		if (ctx instanceof AbstractPage) {
			return PreContextHandle.CONTINUE;
		}

		stepList.addSeparator();

		if (ctx instanceof Select) {
			handleSelect(stepList, ctx);
		}
		else if (ctx instanceof Frame){
			handleFrame(stepList, ctx);
			
		}
		else if (ctx instanceof AbstractContext) {
			handleAbstractContext(stepList, ctx);
		}

		PageElement element = (PageElement) ctx;
		stepList.add("passedSteps += 1 ", 3);
		stepList.add("rescue " + StepList.TEST_STEP_FAILED, 2);
		stepList.add("failedSteps += 1 ", 3);

		String id = StringUtils.replace(element.getMainIdentifier().toString(),"\"", "\\\"");
		stepList.add("puts \"Step failed: Check " + element.getType() + " present with ID = " + id + "\"", 3);
		stepList.add("end", 2);
		
		return PreContextHandle.CONTINUE;
		
	}

	/**
	 * Assert Context present and set steplist prefix.
	 */
	private void handleAbstractContext(StepList stepList, IContext ctx) {
		
		AbstractContext context = (AbstractContext) ctx;
		if (!(context.getMainIdentifierType().equals(ID)))
			throw new ExporterException("Contexts must have identifier type = ID for Watir export. Context in error: " + ctx);

		stepList.add("# asserting " + context.getType() + "present with " + context.getMainIdentifierType() + " = " + WatirUtils.getIdText(context), 2);
		
		stepList.add("begin", 2);

		String idText = "\"" + StringUtils.replace(WatirUtils.getIdText(context),"\"", "\\\"") + "\"";
		String idType = WatirUtils.getIdType(context);

		//set prefix:
		stepList.setPrefix("(ie.div(" + idType + "," + idText + "))");
		
		//assert present:
		stepList.add("if (ie.div(" + idType + "," + idText + ") == nil)", 3);
		stepList.add("raise " + StepList.TEST_STEP_FAILED, 4);
		stepList.add("end", 3);
	}


	private void handleFrame(StepList stepList, IContext ctx) {
		Frame frame = (Frame) ctx;
		
		stepList.add("# asserting " + frame.getType() + "present with " + frame.getMainIdentifierType() + " = " + WatirUtils.getIdText(frame), 2);
		
		stepList.add("begin", 2);

		String idText = "\"" + StringUtils.replace(WatirUtils.getIdText(frame),"\"", "\\\"") + "\"";
		String idType = WatirUtils.getIdType(frame);

		stepList.setPrefix("(ie.frame(" + idType + "," + idText + "))");
		stepList.add("if (ie.frame(" + idType + "," + idText + ") == nil)", 3);
		stepList.add("raise " + StepList.TEST_STEP_FAILED, 4);
		stepList.add("end", 3);
	}


	
	/**
	 * Assert Select present, set prefix and if label, save selectListID in script to be able to select options
	 */
	private void handleSelect(StepList stepList, IContext ctx) {
		
		Select select = (Select) ctx;
		stepList.add("# asserting Select box present with " + select.getMainIdentifierType() + 
				" = " + WatirUtils.getIdText(select), 2);
		
		String idText = "\"" + WatirUtils.getIdText(select) + "\"";
		String idType = WatirUtils.getIdType(select);

		if (select.getMainIdentifierType().equals(IdentifierType.LABEL)) {
			//declare ID to save to be able to select options:
			stepList.add("selectListId = nil", 2);
		}
		stepList.add("begin", 2);
		if (select.getMainIdentifierType().equals(IdentifierType.LABEL)) {
			stepList.add(WatirUtils.getLabelTargetId(select));
			idText = "selectListId";
			idType = ":id";
			stepList.add("selectListId = labelTargetId", 3);
		}
		else if (select.getMainIdentifierType().equals(IdentifierType.NAME)) {
			idType = ":name";
		}
		else if (select.getMainIdentifierType().equals(IdentifierType.ID)) {
			idType = ":id";
		}
		//set prefix (context):
		stepList.setPrefix("ie.select_list(" + idType + ", " + idText + ")");
		
		//assert select box present:
		stepList.add("if (" + stepList.getPrefix() + " == nil)", 3);
		stepList.add("raise " + StepList.TEST_STEP_FAILED, 4);
		stepList.add("end", 3);
	}

}
