/*
 * Created on 11.apr.2005
 *
 */
package org.cubictest.ui.gef.factory;

import org.cubictest.model.Common;
import org.cubictest.model.CommonTransition;
import org.cubictest.model.CustomTestStep;
import org.cubictest.model.ExtensionPoint;
import org.cubictest.model.ExtensionStartPoint;
import org.cubictest.model.ExtensionTransition;
import org.cubictest.model.Image;
import org.cubictest.model.Link;
import org.cubictest.model.Page;
import org.cubictest.model.SimpleTransition;
import org.cubictest.model.SubTest;
import org.cubictest.model.Test;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.UrlStartPoint;
import org.cubictest.model.UserActions;
import org.cubictest.model.context.AbstractContext;
import org.cubictest.model.context.Row;
import org.cubictest.model.formElement.Button;
import org.cubictest.model.formElement.Checkbox;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Password;
import org.cubictest.model.formElement.RadioButton;
import org.cubictest.model.formElement.Select;
import org.cubictest.model.formElement.TextArea;
import org.cubictest.model.formElement.TextField;
import org.cubictest.ui.gef.controller.CommonEditPart;
import org.cubictest.ui.gef.controller.CommonTransitionEditPart;
import org.cubictest.ui.gef.controller.ContextEditPart;
import org.cubictest.ui.gef.controller.CustomTestStepEditPart;
import org.cubictest.ui.gef.controller.ExtensionPointEditPart;
import org.cubictest.ui.gef.controller.ExtensionStartPointEditPart;
import org.cubictest.ui.gef.controller.ExtensionTransitionEditPart;
import org.cubictest.ui.gef.controller.PageEditPart;
import org.cubictest.ui.gef.controller.PageImageEditPart;
import org.cubictest.ui.gef.controller.PageLinkEditPart;
import org.cubictest.ui.gef.controller.PageTextEditPart;
import org.cubictest.ui.gef.controller.PageTitleEditPart;
import org.cubictest.ui.gef.controller.SimpleTransitionEditPart;
import org.cubictest.ui.gef.controller.SubTestEditPart;
import org.cubictest.ui.gef.controller.TableRowEditPart;
import org.cubictest.ui.gef.controller.TestEditPart;
import org.cubictest.ui.gef.controller.UrlStartPointEditPart;
import org.cubictest.ui.gef.controller.UserActionsEditPart;
import org.cubictest.ui.gef.controller.formElement.FormButtonEditPart;
import org.cubictest.ui.gef.controller.formElement.FormCheckboxEditPart;
import org.cubictest.ui.gef.controller.formElement.FormOptionEditPart;
import org.cubictest.ui.gef.controller.formElement.FormPasswordEditPart;
import org.cubictest.ui.gef.controller.formElement.FormRadioButtonEditPart;
import org.cubictest.ui.gef.controller.formElement.FormSelectEditPart;
import org.cubictest.ui.gef.controller.formElement.FormTextAreaEditPart;
import org.cubictest.ui.gef.controller.formElement.FormTextFieldEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;


/**
 * @author SK Skytteren
 *	
 * Used to create the EditParts from the model
 */
public class TestEditPartFactory implements EditPartFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	public EditPart createEditPart(EditPart context, Object model) {
		
		if (model instanceof Test)
			return new TestEditPart((Test)model);
		if (model instanceof ExtensionStartPoint)
			return new ExtensionStartPointEditPart((ExtensionStartPoint)model);
		if (model instanceof SubTest)
			return new SubTestEditPart((SubTest)model);
		if (model instanceof Title)
			return new PageTitleEditPart((Title)model);
		if (model instanceof Page)
			return new PageEditPart((Page)model);
		if (model instanceof Text)
			return new PageTextEditPart((Text)model);
		if (model instanceof Link)
			return new PageLinkEditPart((Link)model);
		if (model instanceof Image)
			return new PageImageEditPart((Image)model);
		if (model instanceof ExtensionPoint)
			return new ExtensionPointEditPart((ExtensionPoint)model);
		if (model instanceof SimpleTransition)
			return new SimpleTransitionEditPart((SimpleTransition)model);
		if (model instanceof ExtensionTransition)
			return new ExtensionTransitionEditPart((ExtensionTransition)model);
		if (model instanceof UserActions)
			return new UserActionsEditPart((UserActions)model);
		if (model instanceof CommonTransition)
			return new CommonTransitionEditPart((CommonTransition)model);
		if (model instanceof Common)
			return new CommonEditPart((Common)model);
		if (model instanceof TextField)
			return new FormTextFieldEditPart((TextField)model);
		if (model instanceof TextArea)
			return new FormTextAreaEditPart((TextArea)model);
		if (model instanceof Button)
			return new FormButtonEditPart((Button)model);
		if (model instanceof Select)
			return new FormSelectEditPart((Select)model);
		if (model instanceof Checkbox)
			return new FormCheckboxEditPart((Checkbox)model);
		if (model instanceof RadioButton)
			return new FormRadioButtonEditPart((RadioButton)model);
		if (model instanceof Password)
			return new FormPasswordEditPart((Password)model);
		if (model instanceof Option)
			return new FormOptionEditPart((Option)model);
		if (model instanceof Row)
			return new TableRowEditPart((AbstractContext) model);
		if (model instanceof AbstractContext)
			return new ContextEditPart((AbstractContext) model);
		if (model instanceof CustomTestStep)
			return new CustomTestStepEditPart((CustomTestStep) model);
		if (model instanceof UrlStartPoint)
			return new UrlStartPointEditPart((UrlStartPoint)model);
		return null;
	}

}
