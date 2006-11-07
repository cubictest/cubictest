/*
 * Created on 14.jan.2006
 * 
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 *
 */
package org.cubictest.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Page;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.Test;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.geometry.Point;

/**
 * Util for new test wizards.
 * 
 * @author chr_schwarz
 */
public class WizardUtils {
	
	
	/**
	 * Creates an empty test, without any pages or commons, but with a startpoint.
	 * 
	 * @param id The id of the test.
	 * @param name The name of the test.
	 * @param description The description of the test.
	 * @return A xml representation of the test as a string.
	 */
	public static Test createEmptyTest(String id, String name, 
			String description, IFile file, ExtensionPoint point) {

		Test test = createTest(id,name,description);

		ExtensionStartPoint esp = new ExtensionStartPoint(file.getFullPath().toPortableString());
		esp.setProject(file.getProject());
		esp.setSourceExtensionPointName(point.getName());
		esp.setSourceExtensionPointPageId(point.getPageId());
		test.setStartPoint(esp);
		
		ExtensionTransition startTransition = 
			new ExtensionTransition(esp, test.getPages().get(0),point);
		test.addTransition(startTransition);
		
		return test;
	}
	
	public static Test createEmptyTest(String id, String name, 
			String description, String url) {
		Test test = createTest(id,name,description);
		
		UrlStartPoint startpoint = new UrlStartPoint();
		startpoint.setId(id);
		startpoint.setBeginAt(url);
		startpoint.setName("URL ");	
		test.setStartPoint(startpoint);
		
		SimpleTransition startTransition = new SimpleTransition(startpoint, test.getPages().get(0));	
		test.addTransition(startTransition);
		
		return test;
	}
	
	private static Test createTest(String id, String name, String description){
		Test test = new Test();
		test.setId(id);
		test.setName(name);
		test.setDescription(description);
		
		Page page = new Page();
		page.setPosition(new Point(100, 100));
		page.setDimension(TransitionNode.getDefaultDimension());
		page.setName("First Page");
		
		List<AbstractPage> pages = new ArrayList<AbstractPage>();
		pages.add(page);
		test.setPages(pages);
		
		return test;
	}
}
