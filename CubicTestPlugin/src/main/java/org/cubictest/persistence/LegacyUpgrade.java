package org.cubictest.persistence;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import com.tapsterrock.jiffie.xpath.JiffieXPath;

/**
 * Used for upgrading old CubicTest XML files to the newest standard.
 * See the <code>ModelInfo</code> class for info about model versions.
 * @author chr_schwarz
 */
public class LegacyUpgrade {

	/**
	 * Upgrade model XML.
	 * Project may be null, must be handled by the upgrade code.
	 * See the <code>ModelInfo</code> class for info about model versions.
	 * @param xml
	 * @param project
	 * @return
	 */
	public static String upgradeIfNecessary(String xml, IProject project) {
		ModelVersion version = getModelVersion(xml);
		xml = upgradeModel1to2(xml, version);
		xml = upgradeModel2to3(xml, project, version);
		xml = upgradeModel3to4(xml, version);
		xml = upgradeModel4to5(xml, version);
		return xml;
	}

	private static String upgradeModel1to2(String xml, ModelVersion version) {
		if (version.getVersion() != 1) {
			return xml;
		}
		xml = StringUtils.replace(xml, "<aspect", "<common");
		xml = StringUtils.replace(xml, "</aspect", "</common");
		xml = StringUtils.replace(xml, "=\"aspect\"", "=\"common\"");
		xml = StringUtils.replace(xml, "/aspect", "/common");

		xml = StringUtils.replace(xml, "<simpleContext", "<pageSection");
		xml = StringUtils.replace(xml, "</simpleContext", "</pageSection");
		xml = StringUtils.replace(xml, "=\"simpleContext\"", "=\"pageSection\"");
		xml = StringUtils.replace(xml, "/simpleContext", "/pageSection");
		
		xml = StringUtils.replace(xml, "class=\"startPoint\"", "class=\"urlStartPoint\"");
		version.increment();
		return xml;
	}

	
	private static String upgradeModel2to3(String xml, IProject project, ModelVersion version) {
		if (version.getVersion() != 2) {
			return xml;
		}
		if (project != null && xml.indexOf("<filePath>/") == -1) {
			xml = StringUtils.replace(xml, "<filePath>", "<filePath>/" + project.getName() + "/");
		}
		version.increment();
		return xml;
	}

	private static String upgradeModel3to4(String xml, ModelVersion version) {
		if (version.getVersion() != 3) {
			return xml;
		}
		xml = StringUtils.replace(xml, "<pageSection", "<simpleContext");
		xml = StringUtils.replace(xml, "</pageSection", "</simpleContext");
		xml = StringUtils.replace(xml, "=\"pageSection\"", "=\"simpleContext\"");
		xml = StringUtils.replace(xml, "/pageSection", "/simpleContext");

		xml = StringUtils.replace(xml, "<userActions", "<userInteractionsTransition");
		xml = StringUtils.replace(xml, "</userActions", "</userInteractionsTransition");
		xml = StringUtils.replace(xml, "=\"userActions\"", "=\"userInteractionsTransition\"");
		xml = StringUtils.replace(xml, "/userActions", "/userInteractionsTransition");

		xml = StringUtils.replace(xml, "<pageElementAction", "<userInteraction");
		xml = StringUtils.replace(xml, "</pageElementAction", "</userInteraction");
		xml = StringUtils.replace(xml, "=\"pageElementAction\"", "=\"userInteraction\"");
		xml = StringUtils.replace(xml, "/pageElementAction", "/userInteraction");

		xml = StringUtils.replace(xml, "<inputs", "<userInteractions");
		xml = StringUtils.replace(xml, "</inputs", "</userInteractions");
		xml = StringUtils.replace(xml, "/inputs/", "/userInteractions/");

		version.increment();
		return xml;
	}
	
	private static String upgradeModel4to5(String xml, ModelVersion version) {
		if (version.getVersion() != 4) {
			return xml;
		}
		try {
			Document document = new SAXBuilder().build(new StringReader(xml));
			Element rootElement = document.getRootElement();
			//fixing enums...
			JDOMXPath xpath = new JDOMXPath("//sationType|//identifierType|//action");
			for(Object obj : xpath.selectNodes(rootElement)){
				if(obj instanceof Element){
					Element sationType = (Element) obj;
					Attribute ref = sationType.getAttribute("reference");
					if(ref != null){
						JDOMXPath refXpath = new JDOMXPath(ref.getValue());
						Element trueSationType = (Element)refXpath.selectSingleNode(sationType);
						sationType.setText(trueSationType.getText());
						sationType.removeAttribute(ref);
					}
				}
			}
			//Fixing new sationObserver in Identifiers
			xpath = new JDOMXPath("//elements");
			for(Object obj : xpath.selectNodes(rootElement)){
				if(obj instanceof Element){
					Element elements = (Element) obj;
					for(Object obj2 : elements.getChildren()){
						if(obj2 instanceof Element){
							Element pageElement = (Element) obj2;
							
							Element identifiers = new Element("identifiers");
							pageElement.addContent(identifiers);
							
							Element identifier = new Element("identifier");
							identifiers.addContent(identifier);
							
							Element text = pageElement.getChild("text");
							if(text != null){
								text.setName("value");
								pageElement.removeContent(text);
								identifier.addContent(text);
							}
							
							Element identifierType = pageElement.getChild("identifierType");
							if(identifierType != null){
								pageElement.removeContent(identifierType);
								identifierType.setName("type");
							}else{
								identifierType = new Element("type");
								identifierType.setText("LABEL");
							}
							identifier.addContent(identifierType);
							
							Element sationType = pageElement.getChild("sationType");
							if(sationType != null){
								Element useI18n = new Element("useI18n");
								Element useParam = new Element("useParam");
								identifier.addContent(useI18n);
								identifier.addContent(useParam);
								Element key = pageElement.getChild("key");
								Element newKey = new Element("paramKey");
								if("NONE".equals(sationType.getText())){
									useI18n.setText("false");
									useParam.setText("false");
								}else if(sationType.getText().contains("PARAM")){
									useI18n.setText("false");
									useParam.setText("true");
								}else if(sationType.getText().contains("INTER")){
									useI18n.setText("true");
									useParam.setText("false");
									newKey = new Element("i18nKey");
								}else{
									useI18n.setText("true");
									useParam.setText("true");
								}
								newKey.setText(key.getText());
								identifier.addContent(newKey);
								pageElement.removeContent(key);
							}
							pageElement.removeContent(sationType);
						}
					}
				}
			}
			//fixing new sationObserver in userInteraction
			xpath = new JDOMXPath("//userInteraction");
			for(Object obj : xpath.selectNodes(rootElement)){
				if(obj instanceof Element){
					Element element = (Element) obj;
					
					Element sationType = element.getChild("sationType");
					if(sationType != null){
						Element useI18n = new Element("useI18n");
						Element useParam = new Element("useParam");
						element.addContent(useI18n);
						element.addContent(useParam);
						Element key = element.getChild("key");
						Element newKey = new Element("paramKey");
						if("NONE".equals(sationType.getText())){
							useI18n.setText("false");
							useParam.setText("false");
						}else if(sationType.getText().contains("PARAM")){
							useI18n.setText("false");
							useParam.setText("true");
						}else if(sationType.getText().contains("INTER")){
							useI18n.setText("true");
							useParam.setText("false");
							newKey = new Element("i18nKey");
						}else{
							useI18n.setText("true");
							useParam.setText("true");
						}
						newKey.setText(key.getText());
						element.addContent(newKey);
						element.removeContent(key);
					}
					element.removeContent(sationType);
				}
			}
			xml = new XMLOutputter().outputString(document);
			//System.out.println(xml);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JaxenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		version.increment();
		return xml;
	}

	
	private static ModelVersion getModelVersion(String xml) {
		String start = "<modelVersion>";
		String end = "</modelVersion>";
		int pos1 = xml.indexOf(start) + start.length();
		int pos2 = xml.indexOf(end);
		BigDecimal version = new BigDecimal(xml.substring(pos1, pos2).trim());

		// only supporting integer versions:
		return new ModelVersion(version.intValue());
	}	
	
	static class ModelVersion {
		private int version;
		
		public ModelVersion(int initialVersion) {
			version = initialVersion;
		}
		
		public void increment() {
			version++;
		}
		
		public int getVersion() {
			return version;
		}
		
	}
}
