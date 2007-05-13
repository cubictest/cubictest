/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.htmlPrototype.delegates;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.cubictest.exporters.htmlPrototype.interfaces.IPageConverter;
import org.cubictest.exporters.htmlPrototype.interfaces.ITestConverter;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Page;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;

public class TestConverter implements ITestConverter {
	private IPageConverter pageConverter;
	
	private ArrayList<TransitionNode> usedPages = new ArrayList<TransitionNode>(); 
	private Stack<HtmlPageCreator> breadcrumbs = new Stack<HtmlPageCreator>();
	
	private File outFolder;

	private TestConverter parentConverter;
	private SubTest parentSubTest;
	private Map<SubTest, TestConverter> subTestConverters = new HashMap<SubTest,TestConverter>();

	private String prefix;
	
	private int step;

	private List<Transition> parentTransitions;
	
	private class PageAllreadyProcessedException extends Exception {}

	public TestConverter(IPageConverter pageConverter) {
		this.pageConverter = pageConverter;
		this.prefix = "";
	}
	
	public TestConverter(IPageConverter pageConverter, TestConverter parentConverter, List<Transition> parentTransitions, SubTest parentSubTest, String prefix) {
		this(pageConverter);
		this.parentTransitions = parentTransitions;
		this.prefix = prefix;
		this.parentConverter = parentConverter;
		this.parentSubTest = parentSubTest;
	}

	/* (non-Javadoc)
	 * @see org.cubictest.export.htmlSkeleton.delegates.ITestConverter#convert(org.cubictest.model.Test, java.io.File)
	 */
	public HashMap<Transition, HtmlPageCreator> convert(Test test, File outFolder, HtmlPageCreator rootPage) {
		this.outFolder = outFolder;
		return convertTransitions(test.getStartPoint().getOutTransitions(), rootPage);
	}
	
	public void setBreadcrumbs(Stack<HtmlPageCreator> breadcrumbs) {
		this.breadcrumbs = breadcrumbs;
	}
	
	public TransitionNode findStartOfPage(TransitionNode node) {
		if(PageConverter.isNewPageTransition(node.getInTransition())) {
			return node;
		}
		if(node.getInTransition() instanceof ExtensionTransition) {
			return subTestConverters.get(node.getInTransition().getStart()).findStartOfPage(((ExtensionTransition)node.getInTransition()).getExtensionPoint());
		} else if(node.getInTransition().getStart() instanceof Page) {
			return findStartOfPage(node.getInTransition().getStart());
		} else if(parentSubTest != null) {
			return parentConverter.findStartOfPage(parentSubTest);
		}
		
		return node;
	}

	private HashMap<Transition,HtmlPageCreator> convertTransitions(List<Transition> transitions, HtmlPageCreator rootPage) {
		HashMap<Transition, HtmlPageCreator> pages = new HashMap<Transition, HtmlPageCreator>();
		for(Transition t : transitions) {
			try {
				HtmlPageCreator pageCreator = convertPage(t.getEnd(), rootPage);
				if(t.getEnd() instanceof Page) {
					pages.put(t, pageCreator);
				}
				
				if(rootPage == null && t.getStart() instanceof UrlStartPoint) {
					// merge pages connected to the start point
					rootPage = pageCreator;
				}
			} catch (PageAllreadyProcessedException e) {}
		}
		return pages;
	}

	private HtmlPageCreator convertPage(TransitionNode node, HtmlPageCreator rootPage) throws PageAllreadyProcessedException {
		boolean newPage = false;

		if (usedPages.contains(node)) {
			throw new PageAllreadyProcessedException();
		}
		
		this.step++;
			
		System.out.println("Converting node " + node.getName() + "(root: " + (rootPage != null ? rootPage.getName() : "null") + ")");
		
		usedPages.add(node);

		HashMap<Transition, HtmlPageCreator> transitions; 

		if(node instanceof Page) {
			HtmlPageCreator newRootPage = pageConverter.convert((Page) node, rootPage, breadcrumbs, outFolder, prefix);
			if(newRootPage != rootPage) {
				newPage = true;
			}
			rootPage = newRootPage;
		} 
		
		if(newPage) {
			breadcrumbs.push(rootPage);			
		}
		
		if (node instanceof SubTest) {
			System.out.println("Converting subtest " + node.getName());
			TestConverter tc = new TestConverter(pageConverter, this, node.getOutTransitions(), (SubTest) node, ((SubTest) node).getName() + "_" + new Integer(step).toString() + "_");
			subTestConverters.put((SubTest) node, tc);
			tc.setBreadcrumbs(breadcrumbs);

			HtmlPageCreator parentPage = rootPage;
			HashMap<Transition, HtmlPageCreator> subTestTransitions = tc.convert(((SubTest) node).getTest(true), outFolder, PageConverter.isNewPageTransition(node.getInTransition()) ? null : rootPage);
			transitions = new HashMap<Transition, HtmlPageCreator>();
			
			rootPage = (HtmlPageCreator) subTestTransitions.values().toArray()[0];
			transitions.put(node.getInTransition(), rootPage);
			parentPage.convertTransitions(transitions);
		} else if(node instanceof ExtensionPoint) {
			if(parentTransitions != null) {
				for(Transition t : parentTransitions) {
					if(t instanceof ExtensionTransition && ((ExtensionTransition) t).getExtensionPoint().getId() == node.getId()) {
						rootPage = convertPage(t.getEnd(), rootPage);
					}
				}				
			}
		} else {
			transitions = convertTransitions(node.getOutTransitions(), rootPage);
			rootPage.convertTransitions(transitions);
		}
			
		rootPage.save();
		
		if(newPage) {
			breadcrumbs.pop();
		}
		return rootPage;
	}
}
