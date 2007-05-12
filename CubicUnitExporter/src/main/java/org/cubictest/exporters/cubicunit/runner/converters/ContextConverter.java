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
import org.cubicunit.types.ElementContainerType;

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
				ElementContainerType type = ElementTypes.CUSTOM_ELEMENT;
				for(Identifier id : sc.getIdentifiers()){
					switch(id.getType()){
						case ID:
							type = type.id(id.getProbability(), id.getValue());
							break;
						case XPATH:
							//TODO: Fixme
							//type = type.xpath(id.getProbability(), id.getValue());
							break;
						case VALUE:
							type = type.text(id.getProbability(), id.getValue());
							break;
						case ELEMENT_NAME:
							type = type.tagName(id.getProbability(), id.getValue());
							break;
						case INDEX:
							try{
								type = type.index(id.getProbability(), 
									Integer.parseInt(id.getValue()));
							}catch (NumberFormatException e) {
							}
							break;
						default:
							break;	
					}
				}
				Container subContainer = container.get(type);
				if(sc.isNot()){
					if(subContainer != null)
						holder.addResult(sc, TestPartStatus.FAIL);
					else
						holder.addResult(sc, TestPartStatus.PASS);
				}else{
					if(subContainer == null)
						holder.addResult(sc, TestPartStatus.FAIL);
					else{
						holder.addResult(sc, TestPartStatus.PASS);
						holder.setContainer(subContainer);
						holder.put(sc, (Element)subContainer);
					}
				}
				
			}catch (RuntimeException e){
				holder.addResult(sc, TestPartStatus.EXCEPTION);
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
