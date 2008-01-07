/*******************************************************************************
 * Copyright (c) 2005, 2008 Christian Schwarz and Erlend S. Halvorsen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz and Erlend S. Halvorsen - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.htmlPrototype.delegates;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.exporters.htmlPrototype.interfaces.IPageElementConverter;
import org.cubictest.exporters.htmlPrototype.interfaces.IPageElementConverter.UnknownPageElementException;
import org.cubictest.exporters.htmlPrototype.utils.XmlUtils;
import org.cubictest.model.ActionType;
import org.cubictest.model.Common;
import org.cubictest.model.CommonTransition;
import org.cubictest.model.FormElement;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Link;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.Title;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.formElement.Button;
import org.jdom.Document;
import org.jdom.Element;

public class HtmlPageCreator {
	private IPageElementConverter pageElementConverter;
	private File file;
	private Map<PageElement, Element> elements = new HashMap<PageElement, Element>();
	private HashMap<UserInteractionsTransition, HtmlPageCreator> userActions = new HashMap<UserInteractionsTransition, HtmlPageCreator>();
	private Element contents;
	private Element html;
	private Element title;
	private Element breadcrumbs;
	private Element transitions;
	private Document document;
	private String name;
	private Element script;
	
	public HtmlPageCreator(File file, IPageElementConverter pageElementConverter, String extension, String prefix, String name) {
		this.pageElementConverter = pageElementConverter;
		this.file = file;
		this.name = name;
		createHtmlPage();
	}
	
	public String getName() {
		return name;
	}
	
	public String getFilename() {
		return file.getName();
	}
	
	public static boolean isNewPageTransition(UserInteractionsTransition transition) {
		for(UserInteraction action : transition.getUserInteractions()) {
			if((action.getElement() instanceof Link || action.getElement() instanceof Button) && action.getActionType() == ActionType.CLICK) {
				return true;
			}
		}
		return false;
	}
	
	public static String filenameFor(TransitionNode transitionNode, String extension, String prefix) {
		if(transitionNode.getInTransition().getStart() instanceof UrlStartPoint) {
			prefix = "index_" + prefix;
		}
		return prefix + transitionNode.getId() + extension;
	}
	
	public void convert(Page page, Stack<HtmlPageCreator> breadcrumbs) {
		if(title.getText() == "") {
			title.setText(page.getName());
		}

		Element breadcrumbRow = new Element("div");
		breadcrumbRow.setAttribute("class", "breadcrumb");
		
		// genereate breadcrumbs
		for(HtmlPageCreator breadcrumb : breadcrumbs) {
			Element link = new Element("a");
			link.setText(breadcrumb.getName());
			link.setAttribute("href", breadcrumb.getFilename());
			breadcrumbRow.addContent(link);
			breadcrumbRow.addContent(" > ");
		}
		breadcrumbRow.addContent(page.getName());
		this.breadcrumbs.addContent(breadcrumbRow);

		// convert commons
		List<CommonTransition> commonTransitions = page.getCommonTransitions();
		for(CommonTransition transition : commonTransitions) {
			Common common = (Common)transition.getStart();
			addPageElements(common.getRootElements());
		}
		
		// convert page elements
		addPageElements(page.getRootElements());
		
		save();
	}

	public void save() {
		transitions.removeContent();
		for(UserInteractionsTransition actions : userActions.keySet()) {
			if(actions.hasUserInteractions()) {
				Element entry = new Element("li");
				Element transitionLink = convertTransition(actions, userActions.get(actions));
				entry.addContent(transitionLink);
				Element description = new Element("pre");
				description.setText(transitionLink.getAttributeValue("title").toString());
				entry.addContent(description);
				transitions.addContent(entry);				
			}
		}
		Element span = new Element("span").setAttribute("id", "scriptInfo");;
		transitions.addContent(span.addContent(new Element("small").setText("Activate JavaScript to enable clickable page elements")));
		
		try {
			FileWriter fw = new FileWriter(file);
			XmlUtils.getNewXmlOutputter().output(document, fw);
		} catch (IOException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow("Error saving file", e);
		}
	}
	
	private void addPageElements(List<PageElement> elements) {
		for(PageElement pe : elements) {
			addPageElement(pe, contents);
		}
	}
	
	private void addPageElement(PageElement pe, Element result) {
		try {
			Element element = pageElementConverter.convert(pe);
			elements.put(pe, element);
			
			if(pe instanceof FormElement) {
				element = pageElementConverter.labelFormElement((FormElement) pe, element);
			}
			
			result.addContent(element);
		} catch (UnknownPageElementException e) {}

		
		if(pe instanceof Title) {
			title.setText(pe.getIdentifier(IdentifierType.LABEL).getValue());
		}
		
		
	}
	
	public void convertTransitions(HashMap<Transition, HtmlPageCreator> transitions) {
		// convert transitions
		for(Transition transition : transitions.keySet()) {
			if(transition instanceof UserInteractionsTransition) {
				String title = new String();
				for(UserInteraction action : ((UserInteractionsTransition) transition).getUserInteractions()) {
					title += action.toString() + "\n";				
				}
				System.out.println("Adding " + title + ", " + transitions.get(transition).getName());
				userActions.put((UserInteractionsTransition) transition, transitions.get(transition));
			}
		}
	}

	
	private Element convertTransition(Transition transition, HtmlPageCreator target) {
		Element link = new Element("a");
		link.addContent(target.getName());
		return convertTransition(transition, link, target);
	}
	
	private Element convertTransition(Transition transition, Element link, HtmlPageCreator target) {
		link.setAttribute("href", target.getFilename());
		if(transition instanceof UserInteractionsTransition) {
			String title = new String();
			UserInteractionsTransition ua = (UserInteractionsTransition) transition;
			String uaId = target.getFilename();
			String actionJs = "";
			
			int i = 0;
			for(UserInteraction action : ua.getUserInteractions()) {
				title += action.toString() + "\n";
				if(action.getActionType() != ActionType.NO_ACTION) {
					Element element = elements.get(action.getElement());
					String actionAttribute = "";
					String attribute = "value";
					String value = "null";
					if(action.getActionType() == ActionType.CLICK) {
						actionAttribute = "onClick";
					} else if(action.getActionType() == ActionType.CHECK || action.getActionType() == ActionType.UNCHECK) {
						actionAttribute = "onClick";
						attribute = "checked";
						if(action.getActionType() == ActionType.CHECK) {
							value = "true";
						} else {
							value = "false";
						}
					} else if(action.getActionType() == ActionType.ENTER_TEXT || action.getActionType() == ActionType.ENTER_PARAMETER_TEXT) {
						actionAttribute = "onKeyUp";
						value = "'" + action.getTextualInput().replaceAll("'", "\\'") + "'";
					} else if(action.getActionType() == ActionType.MOUSE_OVER) {
						actionAttribute = "onMouseOver";
					}
					
					String attributeValue = "";
					if(element != null) {						
						if(element.getAttributeValue(actionAttribute) != null) {
							attributeValue += element.getAttributeValue(actionAttribute);
							attributeValue = attributeValue.replaceAll("return false;", "");
						}
						
						attributeValue += "UserInteractions.test('" + uaId + "', " + i + ", this);";
						if (element.getName().equalsIgnoreCase("a")) {
							//intercept links
							attributeValue += " return false;";
						}
						element.setAttribute(actionAttribute, attributeValue);
						if(element.getAttribute("class") == null || element.getAttribute("class").toString().indexOf("actionable") == -1) {
							String classes = element.getAttributeValue("class");
							if(classes == null) {
								classes = "";
							}
							element.setAttribute("class", "actionable " + classes);
						}
					}
					if(!actionJs.equals("")) {
						actionJs += ",";
					}
					actionJs += "['" + attribute + "'," + value + ", false]";
					i++;					
				}
			}
			
			script.addContent("UserInteractions.actions['" + uaId + "'] = [" + actionJs + "];\n");
			
			link.setAttribute("title", title);
		}
		return link;
		
	}

	private void createHtmlPage() {
		html = new Element("html");
		document = new Document(html);
		Element head = new Element("head");

		breadcrumbs = new Element("div");
		breadcrumbs.setAttribute("id", "breadcrumbs");
		
		Element body = new Element("body").setAttribute("onLoad", "hideScriptInfo();");
		title = new Element("title");

		body.addContent(breadcrumbs);
		head.addContent(title);
		
		html.addContent(head);
		html.addContent(body);
		
		Element transitionsContainer = new Element("div");
		transitionsContainer.setAttribute("id", "transitions");
		Element transitionsHeading = new Element("h2");
		transitionsHeading.setText("Transitions");
		transitionsContainer.addContent(transitionsHeading);
		transitions = new Element("ul");
		transitionsContainer.addContent(transitions);
		body.addContent(transitionsContainer);

		contents = new Element("div");
		contents.setAttribute("id", "contents");
		body.addContent(contents);
		
		Element jsCubic = new Element("script");
		jsCubic.setAttribute("type", "text/javascript");
		jsCubic.setAttribute("src", "cubic.js");
		jsCubic.setText(";");
		head.addContent(jsCubic);
		
		Element style = new Element("link");
		style.setAttribute("type", "text/css");
		style.setAttribute("rel", "stylesheet");
		style.setAttribute("href", "default.css");
		head.addContent(style);
		
		script = new Element("script");
		script.setAttribute("type", "text/javascript");
		head.addContent(script);
	}
}
