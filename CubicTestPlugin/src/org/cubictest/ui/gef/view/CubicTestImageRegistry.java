/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 * 
 */
package org.cubictest.ui.gef.view;

import java.net.URL;

import org.cubictest.CubicTestPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

/**
 * CubicTest image registry wrapper.
 * 
 * @author skyt
 * @author chr_schwarz
 */
public class CubicTestImageRegistry {

	private static ImageRegistry imageRegistry = new ImageRegistry();
	
	public static final String PAGE_IMAGE 		= "page";
	public static final String COMMON_IMAGE 	= "common";
	public static final String EXTENSION_POINT_IMAGE 	= "extensionPoint";
	public static final String LINK_IMAGE 		= "link";
	public static final String NOT_LINK_IMAGE 	= "notLink";
	public static final String BUTTON_IMAGE		= "button";
	public static final String TEXT_IMAGE 		= "txt";
	public static final String NOT_TEXT_IMAGE 	= "notTxt";
	public static final String TITLE_IMAGE 		= "title";
	public static final String NOT_TITLE_IMAGE  = "notTitle";
	public static final String PASSWORD_IMAGE 	= "password";
	public static final String SELECT_IMAGE 	= "select";
	public static final String OPTION_IMAGE 	= "option";
	public static final String SUBMIT_IMAGE 	= "submit";
	public static final String TEXT_AREA_IMAGE 	= "textArea";
	public static final String TEXT_FIELD_IMAGE = "textField";
	public static final String RADIO_BUTTON_CHECKED_IMAGE 	= "radioButtonChecked";
	public static final String RADIO_BUTTON_UNCHECKED_IMAGE = "radioButtonUnchecked";
	public static final String MENU_IMAGE		= "menu";
	public static final String MENU_ITEM_IMAGE 	= "menuItem";
	public static final String LISTBOX_IMAGE	= "listbox";
	public static final String LIST_ITEM_IMAGES	= "listItem";
	public static final String CHECKBOX_CHECKED_IMAGE 	= "checkbox";
	public static final String CHECKBOX_UNCHECKED_IMAGE = "checkbox-unchecked";
	public static final String IMAGE_IMAGE = "image";
	private static final String NOT_IMAGE_IMAGE = null;
	
	/**
	 * Note: An image registry owns all of the image objects registered with it,
	 * and automatically disposes of them the SWT Display is disposed.
	 */ 
	static {
		String iconPath = "icons/"; 
		
		Bundle pluginBundle = CubicTestPlugin.getDefault().getBundle();
		
		URL pageUrl = FileLocator.find(pluginBundle, new Path(iconPath + PAGE_IMAGE + ".gif"), null);
		imageRegistry.put(PAGE_IMAGE, ImageDescriptor.createFromURL(pageUrl));

		URL commonUrl = FileLocator.find(pluginBundle, new Path(iconPath + COMMON_IMAGE + ".gif"), null);
		imageRegistry.put(COMMON_IMAGE, ImageDescriptor.createFromURL(commonUrl));

		URL extensionPointUrl = FileLocator.find(pluginBundle, new Path(iconPath + EXTENSION_POINT_IMAGE + ".gif"), null);
		imageRegistry.put(EXTENSION_POINT_IMAGE, ImageDescriptor.createFromURL(extensionPointUrl));

		URL linkUrl = FileLocator.find(pluginBundle, new Path(iconPath + LINK_IMAGE + ".gif"), null);
		imageRegistry.put(LINK_IMAGE, ImageDescriptor.createFromURL(linkUrl));

		URL notLinkUrl = FileLocator.find(pluginBundle, new Path(iconPath + NOT_LINK_IMAGE + ".gif"), null);
		imageRegistry.put(NOT_LINK_IMAGE, ImageDescriptor.createFromURL(notLinkUrl));

		URL buttonUrl = FileLocator.find(pluginBundle, new Path(iconPath + BUTTON_IMAGE + ".gif"), null);
		imageRegistry.put(BUTTON_IMAGE, ImageDescriptor.createFromURL(buttonUrl));
		
		URL textUrl = FileLocator.find(pluginBundle, new Path(iconPath + TEXT_IMAGE + ".gif"), null);
		imageRegistry.put(TEXT_IMAGE, ImageDescriptor.createFromURL(textUrl));
		URL notTextUrl = FileLocator.find(pluginBundle, new Path(iconPath + NOT_TEXT_IMAGE + ".gif"), null);
		imageRegistry.put(NOT_TEXT_IMAGE, ImageDescriptor.createFromURL(notTextUrl));
		
		URL titleUrl = FileLocator.find(pluginBundle, new Path(iconPath + TITLE_IMAGE + ".gif"), null);
		imageRegistry.put(TITLE_IMAGE, ImageDescriptor.createFromURL(titleUrl));
		
		URL passwordUrl = FileLocator.find(pluginBundle, new Path(iconPath + PASSWORD_IMAGE + ".gif"), null);
		imageRegistry.put(PASSWORD_IMAGE, ImageDescriptor.createFromURL(passwordUrl));
		
		URL selectUrl = FileLocator.find(pluginBundle, new Path(iconPath + SELECT_IMAGE + ".gif"), null);
		imageRegistry.put(SELECT_IMAGE, ImageDescriptor.createFromURL(selectUrl));

		URL optionUrl = FileLocator.find(pluginBundle, new Path(iconPath + OPTION_IMAGE + ".gif"), null);
		imageRegistry.put(OPTION_IMAGE, ImageDescriptor.createFromURL(optionUrl));

		URL submitUrl = FileLocator.find(pluginBundle, new Path(iconPath + SUBMIT_IMAGE + ".gif"), null);
		imageRegistry.put(SUBMIT_IMAGE, ImageDescriptor.createFromURL(submitUrl));
		
		URL textAreaUrl = FileLocator.find(pluginBundle, new Path(iconPath + TEXT_AREA_IMAGE + ".gif"), null);
		imageRegistry.put(TEXT_AREA_IMAGE, ImageDescriptor.createFromURL(textAreaUrl));
		
		URL textFieldUrl = FileLocator.find(pluginBundle, new Path(iconPath + TEXT_FIELD_IMAGE + ".gif"), null);
		imageRegistry.put(TEXT_FIELD_IMAGE, ImageDescriptor.createFromURL(textFieldUrl));

		URL radioButtonCheckedUrl = FileLocator.find(pluginBundle, new Path(iconPath + RADIO_BUTTON_CHECKED_IMAGE + ".gif"), null);
		imageRegistry.put(RADIO_BUTTON_CHECKED_IMAGE, ImageDescriptor.createFromURL(radioButtonCheckedUrl));
		
		URL radioButtonUncheckedUrl = FileLocator.find(pluginBundle, new Path(iconPath + RADIO_BUTTON_UNCHECKED_IMAGE + ".gif"), null);
		imageRegistry.put(RADIO_BUTTON_UNCHECKED_IMAGE, ImageDescriptor.createFromURL(radioButtonUncheckedUrl));

		URL menuUrl = FileLocator.find(pluginBundle, new Path(iconPath + MENU_IMAGE + ".gif"), null);
		imageRegistry.put(MENU_IMAGE, ImageDescriptor.createFromURL(menuUrl));
		
		URL menuItemUrl = FileLocator.find(pluginBundle, new Path(iconPath + MENU_ITEM_IMAGE + ".gif"), null);
		imageRegistry.put(MENU_ITEM_IMAGE, ImageDescriptor.createFromURL(menuItemUrl));
		
		URL listboxUrl = FileLocator.find(pluginBundle, new Path(iconPath + LISTBOX_IMAGE + ".gif"), null);
		imageRegistry.put(LISTBOX_IMAGE, ImageDescriptor.createFromURL(listboxUrl));
		
		URL listItemUrl = FileLocator.find(pluginBundle, new Path(iconPath + LIST_ITEM_IMAGES + ".gif"), null);
		imageRegistry.put(LIST_ITEM_IMAGES, ImageDescriptor.createFromURL(listItemUrl));
		
		URL checkboxCheckedUrl = FileLocator.find(pluginBundle, new Path(iconPath + CHECKBOX_CHECKED_IMAGE + ".gif"), null);
		imageRegistry.put(CHECKBOX_CHECKED_IMAGE, ImageDescriptor.createFromURL(checkboxCheckedUrl));
		
		URL checkboxUncheckedUrl = FileLocator.find(pluginBundle, new Path(iconPath + CHECKBOX_UNCHECKED_IMAGE + ".gif"), null);
		imageRegistry.put(CHECKBOX_UNCHECKED_IMAGE, ImageDescriptor.createFromURL(checkboxUncheckedUrl));
		
		URL imageUrl = FileLocator.find(pluginBundle, new Path(iconPath + IMAGE_IMAGE + ".gif"), null);
		imageRegistry.put(IMAGE_IMAGE, ImageDescriptor.createFromURL(imageUrl));
		
		URL notImageUrl = FileLocator.find(pluginBundle, new Path(iconPath + NOT_IMAGE_IMAGE + ".gif"), null);
		imageRegistry.put(NOT_IMAGE_IMAGE, ImageDescriptor.createFromURL(notImageUrl));
	}
	
	public static Image get(String key){
		return imageRegistry.get(key);
	}

	public ImageDescriptor getDescriptor(String key){
		return imageRegistry.getDescriptor(key);
	}

	
}
