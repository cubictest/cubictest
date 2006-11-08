/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.runner.cubicunit.delegates;

import org.cubictest.common.converters.interfaces.IContextConverter;
import org.cubictest.common.converters.interfaces.PostContextHandle;
import org.cubictest.common.converters.interfaces.PreContextHandle;
import org.cubictest.model.AbstractPage;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.context.IContext;
import org.cubictest.model.context.PageSection;
import org.cubicunit.Container;
import org.cubicunit.Document;
import org.cubicunit.Element;

public class ContextConverter implements IContextConverter<Holder> {

	private Container container;

	public PreContextHandle handlePreContext(Holder holder, IContext context) {
		if(context instanceof AbstractPage ){
			Document doc = holder.getDocument();
			holder.setContainer(doc);
			container = doc;
		}else if(context instanceof PageSection){
			PageSection sc = (PageSection) context;
			try{
				container = holder.getContainer();
				if(sc.isNot()){
					container.assertContainerNotPresent(sc.getText());
				}else{
					Container subContainer = container.assertContainerPresent(sc.getText());
					holder.setContainer(subContainer);
					holder.put(sc, (Element)subContainer);
				}
				sc.setStatus(TestPartStatus.PASS);
			}catch (AssertionError e) {
				sc.setStatus(TestPartStatus.FAIL);
				throw e;
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
