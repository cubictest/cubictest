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
import org.cubictest.model.Text;
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
			Select select = (Select) ctx;
			stepList.add("# asserting Select box present with " + select.getMostSignificantIdentifier().getType().displayValue() + 
					" = " + select.getText(), 2);
			
			String idText = "\"" + select.getText() + "\"";
			String idType = WatirUtils.getIdType(select);
			stepList.add("selectListId = nil", 2);
			stepList.add("begin", 2);
			if (select.getMostSignificantIdentifier().getType().equals(IdentifierType.LABEL)) {
				//Handle label:
				stepList.add(WatirUtils.getLabelTargetId(select));
				idText = "selectListId";
				idType = ":id";
			}
			stepList.add("selectListId = labelTargetId", 3);
			stepList.setPrefix("ie.select_list(" + idType + ", " + idText + ")");
		}
		//Added by Genesis Campos
		else if (ctx instanceof Frame){
			Frame frame = (Frame) ctx;
			
			stepList.add("# asserting " + frame.getType() + "present with " + frame.getMostSignificantIdentifier().getType().displayValue() + " = " + frame.getText(), 2);
			
			stepList.add("begin", 2);
	
			String idText = "\"" + StringUtils.replace(frame.getText(),"\"", "\\\"") + "\"";
			String idType = WatirUtils.getIdType(frame);
	
			stepList.setPrefix("(ie.frame(" + idType + "," + idText + "))");
			stepList.add("if (ie.frame(" + idType + "," + idText + ") == nil)", 3);
			stepList.add("raise " + StepList.TEST_STEP_FAILED, 4);
			stepList.add("end", 3);
			
		}
		//End;
		else if (ctx instanceof AbstractContext) {
			AbstractContext context = (AbstractContext) ctx;
			if (!(context.getMostSignificantIdentifier().getType().equals(ID)))
				throw new ExporterException("Contexts must have identifier type = ID for Watir export");

			stepList.add("# asserting " + context.getType() + "present with " + context.getMostSignificantIdentifier().getType().displayValue() + " = " + context.getText(), 2);
			
			stepList.add("begin", 2);
	
			String idText = "\"" + StringUtils.replace(context.getText(),"\"", "\\\"") + "\"";
			String idType = WatirUtils.getIdType(context);
	
			stepList.setPrefix("(ie.div(" + idType + "," + idText + "))");
			stepList.add("if (ie.div(" + idType + "," + idText + ") == nil)", 3);
			stepList.add("raise " + StepList.TEST_STEP_FAILED, 4);
			stepList.add("end", 3);
		}

		PageElement element = (PageElement) ctx;

		stepList.add("passedSteps += 1 ", 3);
		stepList.add("rescue " + StepList.TEST_STEP_FAILED, 2);
		stepList.add("failedSteps += 1 ", 3);

		stepList.add("puts \"Step failed: Check " + element.getType() + " present with " + element.getMostSignificantIdentifier().getType().displayValue() +
				" = '" + element.getText() + "'\"", 3);
		stepList.add("end", 2);

		
		return PreContextHandle.CONTINUE;
		
	}

}
