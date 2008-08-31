/*******************************************************************************
 * Copyright (c) 2005, 2008 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.testutils;

import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.export.converters.PostContextHandle;
import org.cubictest.export.converters.PreContextHandle;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.PageElement;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.context.IContext;
import org.cubictest.model.customstep.data.CustomTestStepData;


/**
 * Dummy converter that just appends elements to the passed in AssertionList.
 * Appends display names of the elements for easy assertions with what is seen in the test-GUI.
 * 
 * @author chr_schwarz
 */
public class DummyConverter implements 
		IContextConverter<AssertionList<String>>, 
		ICustomTestStepConverter<AssertionList<String>>, 
		IPageElementConverter<AssertionList<String>>, 
		ITransitionConverter<AssertionList<String>>, 
		IUrlStartPointConverter<AssertionList<String>> {

	public PostContextHandle handlePostContext(AssertionList<String> AssertionList, IContext a) {
		return null;
	}

	public PreContextHandle handlePreContext(AssertionList<String> AssertionList, IContext a) {
		return null;
	}

	public void handleCustomStep(AssertionList<String> AssertionList, CustomTestStepHolder cts, CustomTestStepData data) {
		AssertionList.add(cts.getName());
	}

	public void handlePageElement(AssertionList<String> AssertionList, PageElement e) {
		AssertionList.add(e.getDirectEditIdentifier().getValue());
	}

	public void handleUserInteractions(AssertionList<String> AssertionList, UserInteractionsTransition userInteractions) {
		AssertionList.add(userInteractions.getStart().getName() + " --> " + userInteractions.getEnd().getName());
		
	}

	public void handleUrlStartPoint(AssertionList<String> AssertionList, UrlStartPoint urlStartPoint, boolean firstUrl) {
		AssertionList.add(urlStartPoint.getBeginAt());		
	}

	public String getDataKey() {
		return "dummy";
	}

}
