/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.testutils;

import org.cubictest.export.converters.IContextConverter;
import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.export.converters.IPageElementConverter;
import org.cubictest.export.converters.ITransitionConverter;
import org.cubictest.export.converters.IUrlStartPointConverter;
import org.cubictest.export.converters.PostContextHandle;
import org.cubictest.export.converters.PreContextHandle;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.PageElement;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.context.IContext;


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

	public void handleCustomStep(AssertionList<String> AssertionList, CustomTestStep cts) {
		AssertionList.add(cts.getDisplayText());
	}

	public void handlePageElement(AssertionList<String> AssertionList, PageElement e) {
		AssertionList.add(e.getDescription());
	}

	public void handleUserInteractions(AssertionList<String> AssertionList, UserInteractionsTransition userInteractions) {
		AssertionList.add(userInteractions.getStart().getName() + " --> " + userInteractions.getEnd().getName());
		
	}

	public void handleUrlStartPoint(AssertionList<String> AssertionList, UrlStartPoint urlStartPoint) {
		AssertionList.add(urlStartPoint.getBeginAt());		
	}



}
