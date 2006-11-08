package org.cubictest.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.i18n.Language;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;


public class MessagePersistance {

	
	public static void saveToFiles(AllLanguages languages, IFile file) {
		IContainer parent = file.getParent();
		if (parent instanceof IFolder){
			IFolder folder = (IFolder)parent;
			try {
				IResource[] resources = folder.members();
				for (Language language : languages.getLanguages()){
					File langFile = null;
					for(IResource resource : resources){
						if(resource.getType() == IResource.FILE && 
								resource.getName().endsWith(language.getName() + ".properties")){
							langFile = resource.getRawLocation().toFile();
							break;
						}
					}
					
					if(langFile == null){
						IPath path = folder.getRawLocation().append("/messages_" + language.getName() 
								+ ".properties");
						langFile = path.toFile();
					}
					try {
						langFile.createNewFile();
						for(String key :language.keySet()){
							BufferedWriter bw = new BufferedWriter(new FileWriter(langFile));
							bw.write(key + "=" + language.get(key));
							bw.newLine();	
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static AllLanguages loadFromFiles(IFile startFile) {
		AllLanguages languages = new AllLanguages();
		IContainer parent = startFile.getParent();
		if (parent instanceof IFolder){
			IFolder folder = (IFolder)parent;
			try {
				IResource[] resources = folder.members();
				for (IResource resource : resources){
					if(resource.getType() == IResource.FILE && 
							"properties".equals(resource.getFileExtension())){
						String name = resource.getName();
						name = name.substring(name.lastIndexOf('_'),name.lastIndexOf('.'));
						Language language = new Language();
						language.setName(name);
						languages.addLanguage(language);
						IFile langFile = (IFile) resource;
						BufferedReader br = new BufferedReader(new InputStreamReader(langFile.getContents()));
						String line;
						try {
							line = br.readLine();
							while (line != null){
								line = line.trim();
								if(line.contains("=") && !line.startsWith("#")){
									int index = line.indexOf('=');
									String key = line.substring(0,index);
									String message = line.substring(index,line.indexOf('#'));
									language.put(key,message);
								}
								line = br.readLine();
							}
						} catch (IOException e) {
							e.printStackTrace();
							break;
						}			
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return languages;
	}

}
