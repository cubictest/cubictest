/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.cubicunit.runner.converters;

import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.PostContextHandle;
import org.cubictest.export.converters.PreContextHandle;
import org.cubictest.exporters.cubicunit.runner.holders.Holder;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.Identifier;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.context.IContext;
import org.cubictest.model.context.SimpleContext;
import org.cubicunit.Container;
import org.cubicunit.Document;
import org.cubicunit.Element;
import org.cubicunit.ElementTypes;
import org.cubicunit.types.DivType;

public class ContextConverter implements IContextConverter<Holder> {

	private Container container;

	public PreContextHandle handlePreContext(Holder holder, IContext context) {
		if(context instanceof AbstractPage ){
			Document doc = holder.getDocument();
			holder.setContainer(doc);
			container = doc;
		}else if(context instanceof SimpleContext){
			SimpleContext sc = (SimpleContext) context;
			try{
				container = holder.getContainer();
				DivType type = ElementTypes.DIV;
				for(Identifier id : sc.getIdentifiers()){
					switch(id.getType()){
						case ID:
							type = type.id(id.getProbability(), id.getValue());
							break;
						case PATH:
							type = type.path(id.getProbability(), id.getValue());
							break;
						case VALUE:
							type.text(id.getProbability(), id.getValue());
							break;
						default:
							;	
					}
				}
				Container subContainer = container.get(type);
				if(sc.isNot()){
					if(subContainer != null)
						sc.setStatus(TestPartStatus.FAIL);
					else
						sc.setStatus(TestPartStatus.PASS);
				}else{
					holder.setContainer(subContainer);
					holder.put(sc, (Element)subContainer);
					sc.setStatus(TestPartStatus.PASS);
				}
				
			}catch (RuntimeException e){
				sc.setStatus(TestPartStatus.EXCEPTION);
				throw e;
			}
		}
		return PreContextHandle.CONTINUE;
	}

	public PostContextHandle handlePostContext(Holder holder, IContext context) {
		holder.setContainer(container);
		return PostContextHandle.DONE;
	}
}
