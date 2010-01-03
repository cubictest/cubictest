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
package org.cubictest.common.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.Page;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UserInteractionsTransition;


/**
 * Tests the TextUtil class.
 * 
 * @author chr_schwarz
 */
public class ModelUtilTest {

	@org.junit.Test
	public void testGetLastNodeInPath() throws Exception {
		Test test = new Test();
		Page page1 = new Page();
		page1.setName("Page 1");
		Page page2 = new Page();
		page2.setName("Page 2");
		Transition trans = new UserInteractionsTransition(page1, page2);
		test.addPage(page1);
		test.addPage(page2);
		test.addTransition(trans);
		List<TransitionNode> nodes = new ArrayList<TransitionNode>();
		nodes.add(page1);
		nodes.add(page2);
		assertEquals(page2, ModelUtil.getLastNodeInList(nodes));
	}


}
