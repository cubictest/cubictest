/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import org.cubictest.persistence.TestPersistance;
import org.cubictest.pluginsupport.CustomElementLoader;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.eclipse.core.resources.IProject;

/**
 * Subtest node contained within another test.
 * 
 * @author ehalvorsen
 * @author chr_schwarz
 */
public class SubTest extends ConnectionPoint {
	
	private String filePath;
	private transient Test test;
	private transient IProject project;
	private transient IResourceMonitor resourceMonitor;
	private transient CustomElementLoader customTestStepLoader;

	public SubTest(String filePath, IProject project) {
		this.filePath = filePath;
		this.project = project;
		this.setId(System.currentTimeMillis() + "");
	}

	/**
	 * @return Test	the sub test that this object represents
	 */
	public Test getTest() {
		if(test == null){
			test = TestPersistance.loadFromFile(project, getFilePath());
			test.setResourceMonitor(resourceMonitor);
			test.setCustomTestStepLoader(customTestStepLoader);
		}
		return test;
	}
	
	@Override
	public String getName() {
		return getTest().getName() + " (" + getFileName() + ")";
	}
	
	public String getFileName() {
		int pos1 = getFilePath().lastIndexOf("/");
		int pos2 = getFilePath().lastIndexOf("\\");
		return getFilePath().substring(Math.max(pos1, pos2) + 1);
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public boolean containsTest(Test test) {
		if(test.getFilePath().equals(getFilePath())) {
			return true;
		}
		
		for(SubTest subTest : getTest().getSubTests()) {
			if(subTest.getFilePath() == test.getFilePath() || subTest.containsTest(test)) {
				return true;
			}
		}
		return false;
	}
	
	
	/*
	 * We need to know the project to figure out what file to load (since all paths are project relative)
	 */
	public void setProject(IProject project) {
		this.project = project;
	}
	
	
	public IProject getProject() {
		return project;
	}

	@Override
	public void resetStatus() {
		test = null;
	}

	public void setResourceMonitor(IResourceMonitor resourceMonitor) {
		this.resourceMonitor = resourceMonitor;
	}

	public void setCustomTestStepLoader(CustomElementLoader customTestStepLoader) {
		this.customTestStepLoader = customTestStepLoader;	
	} 

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": Name = " + getName() + ", FilePath = " + getFilePath();
	}
}