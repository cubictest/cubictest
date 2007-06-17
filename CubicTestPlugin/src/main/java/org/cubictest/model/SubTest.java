/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.model;

import org.cubictest.persistence.TestPersistance;
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

	public SubTest(String filePath, IProject project) {
		super();
		this.filePath = filePath;
		this.project = project;
	}

	/**
	 * @return Test	the sub test that this object represents
	 */
	public Test getTest(boolean forceRefreshFile) {
		if(test == null || forceRefreshFile) {
			test = TestPersistance.loadFromFile(project, getFilePath());
			test.setResourceMonitor(resourceMonitor);
			test.resetStatus();
		}
		return test;
	}
	
	@Override
	public String getName() {
		return getTest(false).getName() + " (" + getFileName() + ")";
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

	public void updateStatus(boolean hadException) {
		if (hadException) {
			setStatus(TestPartStatus.EXCEPTION);
			return;
		}
		int passed = 0;
		int failed = 0;
		int exception = 0;
		int warn = 0;
		int unknown = 0;
		int elementCount = 0;
		for (AbstractPage page : getTest(false).getPages()) {
			for (PageElement pe : page.getElements()) {
				elementCount++;
				if (pe.getStatus().equals(TestPartStatus.PASS))
					passed++;
				else if (pe.getStatus().equals(TestPartStatus.FAIL))
					failed++;
				else if (pe.getStatus().equals(TestPartStatus.EXCEPTION))
					exception++;
				else if (pe.getStatus().equals(TestPartStatus.WARN))
					warn++;
				else if (pe.getStatus().equals(TestPartStatus.UNKNOWN))
					unknown++;
			}
		}
		for (SubTest subTest : getTest(false).getSubTests()) {
			elementCount++;
			if (subTest.getStatus().equals(TestPartStatus.PASS))
				passed++;
			else if (subTest.getStatus().equals(TestPartStatus.FAIL))
				failed++;
			else if (subTest.getStatus().equals(TestPartStatus.EXCEPTION))
				exception++;
			else if (subTest.getStatus().equals(TestPartStatus.WARN))
				warn++;
			else if (subTest.getStatus().equals(TestPartStatus.UNKNOWN))
				unknown++;
		}
		if (passed == elementCount)
			setStatus(TestPartStatus.PASS);
		else if (failed == elementCount)
			setStatus(TestPartStatus.FAIL);
		else if (exception == elementCount)
			setStatus(TestPartStatus.EXCEPTION);
		else if (unknown == elementCount)
			setStatus(TestPartStatus.UNKNOWN);
		else
			setStatus(TestPartStatus.WARN);
	}
	
	
}