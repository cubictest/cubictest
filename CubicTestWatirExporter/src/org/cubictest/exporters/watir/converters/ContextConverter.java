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
import org.cubictest.exporters.watir.holders.IStepList;
import org.cubictest.exporters.watir.holders.TestStep;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;
import org.cubictest.model.formElement.Select;

public class ContextConverter implements IContextConverter<IStepList> {

	
	public static final String ROOT_CONTEXT = "ie";

	public PostContextHandle handlePostContext(IStepList steps, IContext a) {
		steps.setPrefix(ROOT_CONTEXT);
		return PostContextHandle.DONE;
	}
	

	public PreContextHandle handlePreContext(IStepList steps, IContext ctx) {
		if (ctx instanceof Select) {
			Select element = (Select) ctx;
			
			String idText = element.getText();
			String idType = WatirUtils.getIdType(element);
			if (element.getIdentifierType().equals(IdentifierType.LABEL)) {
				//If label, inject script to get the ID from the label and modify variables with the injected value:
				StringBuffer buff = new StringBuffer();
				WatirUtils.appendGetLabelTargetId(buff, element, element.getDescription());
				idText = "\" + labelTargetId + \"";
				idType = ":id";
				steps.add(new TestStep(buff.toString()).setDescription("Getting select list with text = '" + element.getText()));
			}
			steps.setPrefix("ie.select_list(" + idType + ", \"" + idText + "\")");
		}
		else if (ctx instanceof AbstractContext) {
			AbstractContext context = (AbstractContext)ctx;
			
			if (!(context.getIdentifierType().equals(ID)))
				throw new ExporterException("Contexts must have identifier type = ID");
	
			String idText = StringUtils.replace(context.getText(),"\"", "\\\"");
			String idType = WatirUtils.getIdType(context);
	
			steps.setPrefix("(ie.div(" + idType + ",'" + idText + "'))");
		}
		
		return PreContextHandle.CONTINUE;
	}

}
