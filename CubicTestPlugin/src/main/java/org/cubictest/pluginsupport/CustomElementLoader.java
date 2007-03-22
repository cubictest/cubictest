/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */
package org.cubictest.pluginsupport;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.cubictest.common.utils.ErrorHandler;
import org.cubictest.custom.ICustomTestStep;
import org.cubictest.pluginsupport.interfaces.IClassChangeListener;
import org.cubictest.resources.interfaces.IResourceListener;
import org.cubictest.resources.interfaces.IResourceMonitor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;

public class CustomElementLoader implements IResourceListener {
	private IFolder binFolder;
	private IFolder srcFolder;
	private IProject project;
	private ClassLoader classLoader;
	private Map<String, Long> fileTimestamps = new HashMap<String, Long>();
	private List<IClassChangeListener> listeners = new ArrayList<IClassChangeListener>();

	private class MyInvocationHandler implements InvocationHandler {

		String backendClassName;

		Object backend;

		MyInvocationHandler(String className) throws ClassNotFoundException {		   
			backendClassName = className;

			Class clz = loadClass(backendClassName);
			backend = newDynaCodeInstance(clz);
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			// check if class has been updated
			Class clz = loadClass(backendClassName);
			if (backend.getClass() != clz) {
				backend = newDynaCodeInstance(clz);
			}

			try {
				// invoke on backend
				return method.invoke(backend, args);

			} catch (InvocationTargetException e) {
				throw e.getTargetException();
			}
		}

		private Object newDynaCodeInstance(Class clz) {
			try {
				return clz.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(
						"Failed to create new instance of dynamic class "
								+ clz.getName(), e);
			}
		}
	}
	
	public CustomElementLoader(IProject project, IResourceMonitor resourceMonitor) {
		this.project = project;
		binFolder = project.getFolder("bin");
		srcFolder = project.getFolder("src");
		resourceMonitor.registerResourceListener(binFolder, this);
		recreateClassLoader();		
	}
	
	public ICustomTestStep newInstance(String name) throws ClassNotFoundException {

		try {
			MyInvocationHandler handler = new MyInvocationHandler(name);
			return (ICustomTestStep) Proxy.newProxyInstance(classLoader, new Class[] { ICustomTestStep.class }, handler);
		} catch (IllegalArgumentException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
		throw new ClassNotFoundException();
	}
	
	public Class loadClass(String name) throws ClassNotFoundException {
		IFile srcFile = binFolder.getFile(packagePathToFilePath(name) + ".class");
		String srcPath = srcFile.getFullPath().toString();
		
		if(fileTimestamps.get(srcPath) == null || fileTimestamps.get(srcPath).longValue() < srcFile.getModificationStamp()) {
			recreateClassLoader();
			fileTimestamps.put(srcPath, srcFile.getModificationStamp());
		}
		
		return Class.forName(name, true, classLoader);
	}
	
	private String packagePathToFilePath(String path) {
		return path.replace(".", "/");
	}
	
	private String binFilePathToPackagePath(String path) {
		path = path.replaceAll("^" + binFolder.getLocation().toPortableString().replaceAll("/", "\\/") + "\\/", "");
		path = path.replaceAll(".class$", "");
		path = path.replace("/", ".");
		return path;
	}

	public String cleanClassName(String name) {
		name = name.replaceAll("[^a-zA-Z0-9_.]", ""); // Replace everything that isn't alphanumeric, '.' or '_'
		name = name.replaceAll("(\\.|^)[^a-zA-Z]+", "$1"); // remove special characters following a '.' or at the start
		return name;
	}
	
	public void  createClass(String name, String template) {
		try {
			IFile srcFile = srcFolder.getFile(packagePathToFilePath(name) + ".java");
			srcFile.delete(true, true, null);
			URL templateUrl = this.getClass().getResource(template + ".class.template");
			File templateFile = FileUtils.toFile(FileLocator.toFileURL(templateUrl));
			String templateContents = FileUtils.readFileToString(templateFile, "ISO-8859-1");
			templateContents = templateContents.replaceAll("%%CLASSNAME%%", name.substring(name.lastIndexOf(".")+1));

			if (name.indexOf(".") > 0) {
				String packageName = name.substring(0, name.lastIndexOf("."));
				templateContents = templateContents.replaceAll("%%PACKAGE%%", "package " + packageName + ";");
			} else {
				templateContents = templateContents.replaceAll("%%PACKAGE%%", "");
			}

			srcFile.getLocation().toFile().getParentFile().mkdirs();
			srcFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
			
			srcFile.create(new StringBufferInputStream(templateContents), true, null);
			project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		} catch (CoreException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		} catch (IOException e) {
			ErrorHandler.logAndShowErrorDialogAndRethrow(e);
		}
	}
	
	public List<String> getClasses() {
		return getClasses(binFolder.getLocation().toFile());
	}
	
	private List<String> getClasses(File directory) {
		ArrayList<String> classes = new ArrayList<String>();
		File[] files = directory.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if(file.isDirectory()) {
					return true;
				}
				
				if(file.getAbsolutePath().endsWith(".class")) {
					try {
						for(Class interf : loadClass(binFilePathToPackagePath(file.getAbsolutePath())).getInterfaces()) {
							if(interf.equals(ICustomTestStep.class)) {
								return true;								
							}
						}
					} catch (ClassNotFoundException e) {}
				}
				return false;
			}
		});
		
		if(files != null) {
			for(File file : files) {
				if(file.isDirectory()) {
					classes.addAll(getClasses(file));
				} else {
					classes.add(binFilePathToPackagePath(file.getAbsolutePath()));
				}
			}				
		}
		return classes;
	}
	
	public IFile getClassSrcPath(String className) {
		return srcFolder.getFile(packagePathToFilePath(className) + ".java");
	}
	
	public IResource getClassBinPath(String className) {
		return binFolder.getFile(packagePathToFilePath(className) + ".class");
	}

	private void recreateClassLoader() {
		try {
			classLoader = new URLClassLoader(new URL[] { binFolder.getLocation().toFile().toURL() }, CustomElementLoader.class.getClassLoader());
		} catch (MalformedURLException e) {}
	}
	
	public void addClassChangeListener(IClassChangeListener listener) {
		this.listeners.add(listener); 
	}
	
	private void notifyClassChangeListeners() {
		for (IClassChangeListener listener : listeners) {
			listener.classChanged();
		}
	}

	public void notifyResourceChange(IResource resource) {
		recreateClassLoader();
		notifyClassChangeListeners();
	}
}