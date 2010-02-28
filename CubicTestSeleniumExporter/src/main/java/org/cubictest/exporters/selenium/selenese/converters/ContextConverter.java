/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.selenium.selenese.converters;

import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.PostContextHandle;
import org.cubictest.export.converters.PreContextHandle;
import org.cubictest.exporters.selenium.selenese.holders.SeleneseDocument;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.IContext;

/**
 * Converter for contexts.
 * 
 * @author chr_schwarz
 */
public class ContextConverter implements IContextConverter<SeleneseDocument> {

	/**
	 * Handle entry into a new context.
	 */
	public PreContextHandle handlePreContext(SeleneseDocument doc, IContext ctx) {
		
		if (ctx instanceof AbstractContext) {
			doc.pushContext(ctx);
		}
		return PreContextHandle.CONTINUE;
	}

	
	/**
	 * Handle exit from context.
	 */
	public PostContextHandle handlePostContext(SeleneseDocument doc, IContext ctx) {
		
		if (ctx instanceof AbstractContext) {
			doc.popContext();
		}
		return PostContextHandle.DONE;
	}
}
