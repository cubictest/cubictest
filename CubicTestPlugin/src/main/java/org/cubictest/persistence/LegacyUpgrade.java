package org.cubictest.persistence;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;

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
