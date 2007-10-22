/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import org.apache.commons.lang.StringUtils;
import org.cubictest.common.utils.Logger;
import org.cubictest.persistence.TestPersistance;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.eclipse.core.resources.IProject;

/**
 * Subtest node that contains another test.
 * 
 * @author ehalvorsen
 * @author chr_schwarz
 */
public class SubTest extends ConnectionPoint {
	
	private String filePath;
	private transient Test test;
	private transient IProject project;
	private transient IResourceMonitor resourceMonitor;
	private transient boolean dangling;

	public SubTest(String filePath, IProject project) {
		super();
		this.filePath = filePath;
		this.project = project;
		if (project == null) {
			Logger.warn("Invoked SubTest constructor with null project");
		}
	}
	
	public boolean isDangling() {
		getTest(false).getName();
		return dangling;
	}
	
	/**
	 * @return Test	the sub test that this object represents
	 */
	public Test getTest(boolean forceRefreshFile) {
		try {
			if(test == null || forceRefreshFile) {
				if (project == null && test != null) {
					project = test.getProject();
				}
				test = TestPersistance.loadFromFile(project, getFilePath());
				test.setResourceMonitor(resourceMonitor);
				test.resetStatus();
				dangling = false;
			}
			
		} catch (Exception e) {
			dangling = true;
			test = new Test();
		}
		return test;
	}
	
	@Override
	public String getName() {
		Test test = getTest(false);
		if (StringUtils.isBlank(test.getName())) {
			return getFileName();
			
		}
		else {
			return test.getName() + " (" + getFileName() + ")";
		}
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
		
		for(SubTest subTest : getTest(false).getSubTests()) {
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
		setStatus(TestPartStatus.UNKNOWN);
		test = null;
	}

	public void setResourceMonitor(IResourceMonitor resourceMonitor) {
		this.resourceMonitor = resourceMonitor;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + ": Name = " + getName() + ", FilePath = " + getFilePath();
	}

	
	public void updateStatus(boolean hadException, ConnectionPoint targetConnectionPoint) {
		if (hadException) {
			setStatus(TestPartStatus.EXCEPTION);
			return;
		}
		setStatus(getTest(false).updateAndGetStatus(targetConnectionPoint));
	}

}