/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.common.utils;

import static org.junit.Assert.*;

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
