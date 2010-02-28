/*******************************************************************************
 * Copyright (c) 2005, 2010 Stein K. Skytteren and Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Stein K. Skytteren and Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.persistence;

import java.util.List;

import org.cubictest.model.AbstractPage;
import org.cubictest.model.ActionType;
import org.cubictest.model.Common;
import org.cubictest.model.CommonTransition;
import org.cubictest.model.ConnectionPoint;
import org.cubictest.model.CustomTestStepHolder;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.FormElement;
import org.cubictest.model.Identifier;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.ModelInfo;
import org.cubictest.model.Page;
import org.cubictest.model.PageElement;
import org.cubictest.model.PropertyAwareObject;
import org.cubictest.model.SationObserver;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.SubTest;
import org.cubictest.model.SubTestStartPoint;
import org.cubictest.model.Test;
import org.cubictest.model.TestPartStatus;
import org.cubictest.model.TestSuiteStartPoint;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.Transition;
import org.cubictest.model.TransitionNode;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserInteraction;
import org.cubictest.model.UserInteractionsTransition;
import org.cubictest.model.WebBrowser;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.Frame;
import org.cubictest.model.context.SimpleContext;
import org.cubictest.model.customstep.CustomTestStepParameter;
import org.cubictest.model.customstep.CustomTestStepValue;
import org.cubictest.model.formElement.AbstractTextInput;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;
import org.cubictest.model.i18n.AllLanguages;
import org.cubictest.model.i18n.Language;
import org.cubictest.model.parameterization.ParamMapper;
import org.cubictest.model.parameterization.Parameter;
import org.cubictest.model.parameterization.ParameterList;

import com.thoughtworks.xstream.XStream;


/**
 * CubicTest specific XStream mapping.
 * 
 * @author chr_schwarz
 */
public class CubicTestXStream extends XStream {
	
	public CubicTestXStream() {
		setupStandardAliases();
	}
	
	private void setupStandardAliases() {
		//model (root):
		this.alias("abstractPage", AbstractPage.class);
		this.alias("actionType", ActionType.class);
		this.alias("common", Common.class);
		this.alias("commonTransition", CommonTransition.class);
		this.alias("connectionPoint", ConnectionPoint.class);
		this.alias("extensionPoint", ExtensionPoint.class);
		this.alias("extensionTransition", ExtensionTransition.class);
		this.alias("extensionStartPoint", ExtensionStartPoint.class);
		this.alias("formElement", FormElement.class);
		this.alias("identifierType", IdentifierType.class);
		this.alias("image", Image.class);
		this.alias("link", Link.class);
		this.alias("modelInfo", ModelInfo.class);
		this.alias("page", Page.class);
		this.alias("pageElement", PageElement.class);
		this.alias("userInteraction", UserInteraction.class);
		this.alias("propertyAwareObject", PropertyAwareObject.class);
		this.alias("sationObserver", SationObserver.class);
		this.alias("simpleTransition", SimpleTransition.class);
		this.alias("urlStartPoint", UrlStartPoint.class);
		this.alias("subTestStartPoint", SubTestStartPoint.class);
		this.alias("testSuiteStartPoint", TestSuiteStartPoint.class);
		this.alias("subTest", SubTest.class);
		this.alias("test", Test.class);
		this.alias("testPartStatus", TestPartStatus.class);
		this.alias("text", Text.class);
		this.alias("title", Title.class);
		this.alias("transition", Transition.class);
		this.alias("transitionNode", TransitionNode.class);
		this.alias("userInteractionsTransition", UserInteractionsTransition.class);
		this.alias("identifier", Identifier.class);
		this.alias("identifierType",IdentifierType.class);
		
		this.omitField(Language.class, "properties");
		
		//contexts:
		this.alias("abstractContext", AbstractContext.class);
		this.alias("simpleContext", SimpleContext.class);
		this.alias("frame", Frame.class);

		this.alias("webBrowser", WebBrowser.class);
		
		//formElement:
		this.alias("abstractTextInput", AbstractTextInput.class);
		this.alias("button", Button.class);
		this.alias("checkbox", Checkbox.class);
		this.alias("password", Password.class);
		this.alias("radioButton", RadioButton.class);
		this.alias("select", Select.class);
		this.alias("option", Option.class);
		this.alias("textArea", TextArea.class);
		this.alias("textField", TextField.class);
		
		//i18n:
		this.alias("allLanguages", AllLanguages.class);
		this.alias("language", Language.class);
		
		//parameterization:
		this.alias("parameter", Parameter.class);
		this.alias("parameterList", ParameterList.class);
		this.alias("paramMapper", ParamMapper.class);
		
		//misc:
		this.alias("userInteractions", List.class);
		
		//customstep:
		this.alias("customTestStep", CustomTestStepHolder.class);
		this.alias("customTestStepParameter", CustomTestStepParameter.class);
		this.alias("CustomTestStepValue", CustomTestStepValue.class);
	}
	
}