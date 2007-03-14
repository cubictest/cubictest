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
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

/**
 * CubicTest image registry wrapper.
 * 
 * @author skyt
 * @author chr_schwarz
 */
public class CubicTestImageRegistry {

	private static ImageRegistry imageRegistry = new ImageRegistry();

	public static final String PAGE_IMAGE = "page";

	public static final String COMMON_IMAGE = "common";

	public static final String EXTENSION_POINT_IMAGE = "extensionpoint";

	public static final String LINK_IMAGE = "link";

	public static final String BUTTON_IMAGE = "button";

	public static final String TEXT_IMAGE = "text";

	public static final String TITLE_IMAGE = "title";

	public static final String PASSWORD_IMAGE = "password";

	public static final String SELECT_IMAGE = "select";

	public static final String OPTION_IMAGE = "option";

	public static final String TEXT_AREA_IMAGE = "textArea";

	public static final String TEXT_FIELD_IMAGE = "textField";

	public static final String RADIO_BUTTON_CHECKED_IMAGE = "radioButton";

	public static final String RADIO_BUTTON_UNCHECKED_IMAGE = "radioButtonUnchecked";

	public static final String CHECKBOX_CHECKED_IMAGE = "checkboxChecked";

	public static final String CHECKBOX_UNCHECKED_IMAGE = "checkbox";

	public static final String IMAGE_IMAGE = "image";

	public static final String CONNECTION_IMAGE = "connection";

	public static final String USER_INTERACTION_IMAGE = "userInteraction";

	public static final String CUSTOM_STEP_IMAGE = "customStep";

	public static final String CONTEXT_IMAGE = "context";

	public static final String ROW_IMAGE = "row";
	
	public static final String NOT_IMAGE = "not";

	/**
	 * Note: An image registry owns all of the image objects registered with it,
	 * and automatically disposes of them the SWT Display is disposed.
	 */
	static {
		String iconPath = "icons/";

		Bundle pluginBundle = CubicTestPlugin.getDefault().getBundle();

		URL pageUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ PAGE_IMAGE + ".gif"), null);
		imageRegistry.put(PAGE_IMAGE, ImageDescriptor.createFromURL(pageUrl));
		
		URL notUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ NOT_IMAGE + ".gif"), null);
		imageRegistry.put(NOT_IMAGE, ImageDescriptor.createFromURL(notUrl));

		URL commonUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ COMMON_IMAGE + ".gif"), null);
		imageRegistry.put(COMMON_IMAGE, ImageDescriptor
				.createFromURL(commonUrl));

		URL extensionPointUrl = FileLocator.find(pluginBundle, new Path(
				iconPath + EXTENSION_POINT_IMAGE + ".gif"), null);
		imageRegistry.put(EXTENSION_POINT_IMAGE, ImageDescriptor
				.createFromURL(extensionPointUrl));

		URL linkUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ LINK_IMAGE + ".gif"), null);
		imageRegistry.put(LINK_IMAGE, ImageDescriptor.createFromURL(linkUrl));
		/*
		URL notLinkUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ NOT_LINK_IMAGE + ".gif"), null);
		imageRegistry.put(NOT_LINK_IMAGE, ImageDescriptor
				.createFromURL(notLinkUrl));
		*/
		
		URL buttonUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ BUTTON_IMAGE + ".gif"), null);
		imageRegistry.put(BUTTON_IMAGE, ImageDescriptor
				.createFromURL(buttonUrl));

		URL textUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ TEXT_IMAGE + ".gif"), null);
		imageRegistry.put(TEXT_IMAGE, ImageDescriptor.createFromURL(textUrl));
		
		/*
		URL notTextUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ NOT_TEXT_IMAGE + ".gif"), null);
		imageRegistry.put(NOT_TEXT_IMAGE, ImageDescriptor
				.createFromURL(notTextUrl));
		*/

		URL titleUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ TITLE_IMAGE + ".gif"), null);
		imageRegistry.put(TITLE_IMAGE, ImageDescriptor.createFromURL(titleUrl));

		URL passwordUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ PASSWORD_IMAGE + ".gif"), null);
		imageRegistry.put(PASSWORD_IMAGE, ImageDescriptor
				.createFromURL(passwordUrl));

		URL selectUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ SELECT_IMAGE + ".gif"), null);
		imageRegistry.put(SELECT_IMAGE, ImageDescriptor
				.createFromURL(selectUrl));

		URL optionUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ OPTION_IMAGE + ".gif"), null);
		imageRegistry.put(OPTION_IMAGE, ImageDescriptor
				.createFromURL(optionUrl));

		URL textAreaUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ TEXT_AREA_IMAGE + ".gif"), null);
		imageRegistry.put(TEXT_AREA_IMAGE, ImageDescriptor
				.createFromURL(textAreaUrl));

		URL textFieldUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ TEXT_FIELD_IMAGE + ".gif"), null);
		imageRegistry.put(TEXT_FIELD_IMAGE, ImageDescriptor
				.createFromURL(textFieldUrl));

		URL radioButtonCheckedUrl = FileLocator.find(pluginBundle, new Path(
				iconPath + RADIO_BUTTON_CHECKED_IMAGE + ".gif"), null);
		imageRegistry.put(RADIO_BUTTON_CHECKED_IMAGE, ImageDescriptor
				.createFromURL(radioButtonCheckedUrl));

		URL radioButtonUncheckedUrl = FileLocator.find(pluginBundle, new Path(
				iconPath + RADIO_BUTTON_UNCHECKED_IMAGE + ".gif"), null);
		imageRegistry.put(RADIO_BUTTON_UNCHECKED_IMAGE, ImageDescriptor
				.createFromURL(radioButtonUncheckedUrl));

		URL checkboxCheckedUrl = FileLocator.find(pluginBundle, new Path(
				iconPath + CHECKBOX_CHECKED_IMAGE + ".gif"), null);
		imageRegistry.put(CHECKBOX_CHECKED_IMAGE, ImageDescriptor
				.createFromURL(checkboxCheckedUrl));

		URL checkboxUncheckedUrl = FileLocator.find(pluginBundle, new Path(
				iconPath + CHECKBOX_UNCHECKED_IMAGE + ".gif"), null);
		imageRegistry.put(CHECKBOX_UNCHECKED_IMAGE, ImageDescriptor
				.createFromURL(checkboxUncheckedUrl));

		URL imageUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ IMAGE_IMAGE + ".gif"), null);
		imageRegistry.put(IMAGE_IMAGE, ImageDescriptor.createFromURL(imageUrl));
		
		/*
		URL notImageUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ NOT_IMAGE_IMAGE + ".gif"), null);
		imageRegistry.put(NOT_IMAGE_IMAGE, ImageDescriptor
				.createFromURL(notImageUrl));
		*/

		URL connectionUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ CONNECTION_IMAGE + ".gif"), null);
		imageRegistry.put(CONNECTION_IMAGE, ImageDescriptor
				.createFromURL(connectionUrl));

		URL userInteractionUrl = FileLocator.find(pluginBundle, new Path(
				iconPath + USER_INTERACTION_IMAGE + ".gif"), null);
		imageRegistry.put(USER_INTERACTION_IMAGE, ImageDescriptor
				.createFromURL(userInteractionUrl));

		URL customStepUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ CUSTOM_STEP_IMAGE + ".gif"), null);
		imageRegistry.put(CUSTOM_STEP_IMAGE, ImageDescriptor
				.createFromURL(customStepUrl));

		URL contextUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ CONTEXT_IMAGE + ".gif"), null);
		imageRegistry.put(CONTEXT_IMAGE, ImageDescriptor
				.createFromURL(contextUrl));

		URL rowUrl = FileLocator.find(pluginBundle, new Path(iconPath
				+ ROW_IMAGE + ".gif"), null);
		imageRegistry.put(ROW_IMAGE, ImageDescriptor.createFromURL(rowUrl));
	}

	public static Image get(String key) {
		return imageRegistry.get(key);
	}

	public static ImageDescriptor getDescriptor(String key) {
		return imageRegistry.getDescriptor(key);
	}

	public static ImageDescriptor getNotDescriptor(String key) {
		return ImageDescriptor.createFromImage(getNot(key));
	}
	
	public static ImageDescriptor getDescriptor(String key, boolean not) {
		if(not)
			return ImageDescriptor.createFromImage(getNot(key));
		return getDescriptor(key);
	}
	
	private static Image getNot(String key) {
		Image notImage = new Image(Display.getCurrent(),new Rectangle(0,0,16,16));
		GC gc = new GC(notImage);
		Image baseImage = imageRegistry.get(key);
		if(baseImage != null){
			gc.drawImage(baseImage, 0, 0);
		}else{
			gc.setBackground(ColorConstants.white);
		}
		//gc.drawText("This is a copy",0,0);
		gc.setForeground(ColorConstants.red);
		gc.setAlpha(95);
		//gc.setLineWidth(2);
		gc.drawOval(0, 0, 15, 15);
		gc.drawLine(3,12,12,3);
		
		gc.setForeground(new Color(Display.getCurrent(),220,0,0));
		gc.drawOval(1, 1, 13, 13);
		gc.drawLine(3,13,13,3);
		gc.dispose();
		return notImage;
	}

	public static Image get(String key, boolean not) {
		if(not){
			return getNot(key);
		}
		return get(key);
	}
}
