/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.exporters.cubicunit.runner.converters;

import java.io.File;

import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.export.converters.ICustomTestStepConverter;
import org.cubictest.exporters.cubicunit.runner.holders.Holder;
import org.cubictest.model.ICustomTestStepHolder;
import org.cubictest.model.customstep.data.CustomTestStepData;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

public class CustomTestStepConverter implements ICustomTestStepConverter<Holder> {
	
	public String getDataKey(){
		return "org.cubictest.cubicunitexporter";
	}

	public void handleCustomStep(Holder t, ICustomTestStepHolder cts,
			CustomTestStepData data) {
		try {
			String path = data.getPath();
			String filePath = path.substring(path.indexOf("/",1));
			for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
				IFile file = project.getFile(filePath); 
				if(file.exists()){
					IJavaProject myJavaProject= JavaCore.create(project);
					System.out.println(myJavaProject);
					
			        if (myJavaProject == null)
			        	
			            // the project is not configured for Java (has no Java nature)
			            return;
			        		        
			        // get a .java (compilation unit), .class (class file), or
			        // .jar (package fragment root)
			        
			        IJavaElement javaFile = JavaCore.createClassFileFrom(file);
			        javaFile.exists();
				}
			}
			
			/*
			Class<?> myClass = Class.forName(data.getPath());
			
			IJavaProject myJavaProject= JavaCore.create(myProject);
	        if (myJavaProject == null)
	            // the project is not configured for Java (has no Java nature)
	            return;
	            
	        // get a package fragment or package fragment root
	        IJavaElement myPackageFragment= JavaCore.create(myFolder);
	        
	        // get a .java (compilation unit), .class (class file), or
	        // .jar (package fragment root)
	        IJavaElement myJavaFile = JavaCore.create(myFile);

			
			ICustomTestStep ctsimpl = (ICustomTestStep)myClass.newInstance();
			ctsimpl.execute(null, null, null);
			*/
		} catch (Exception e) {
			ErrorHandler.logAndRethrow(e, "File in error: " + data.getPath());
		}
	}
}
