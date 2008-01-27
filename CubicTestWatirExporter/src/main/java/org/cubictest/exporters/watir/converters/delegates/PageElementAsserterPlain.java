/*******************************************************************************
 * Copyright (c) 2005, 2008  Christian Schwarz
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian Schwarz - initial API and implementation
 *******************************************************************************/
package org.cubictest.exporters.watir.converters.delegates;

import static org.cubictest.model.Moderator.*;

import org.cubictest.export.exceptions.ExporterException;
import org.cubictest.exporters.watir.holders.WatirHolder;
import org.cubictest.exporters.watir.utils.WatirUtils;
import org.cubictest.model.IdentifierType;
import org.cubictest.model.Moderator;
import org.cubictest.model.PageElement;
import org.cubictest.model.Text;
import org.cubictest.model.Title;
import org.cubictest.model.formElement.Option;
import org.cubictest.model.formElement.Select;

/**
 * Page element converter that uses standard Watir without XPath.
 * 
 * @author Christian Schwarz
 */
public class PageElementAsserterPlain {

	public static void handle(WatirHolder watirHolder, PageElement pe) {
		
		String idType = WatirUtils.getMainIdType(pe);
		String idValue = WatirUtils.getIdValue(pe.getMainIdentifier());
		Moderator moderator = pe.getMainIdentifier().getModerator();

		if (pe instanceof Title) {
			if (moderator.equals(EQUAL)) {
				watirHolder.add("if (ie.title() != " + idValue + ")", 2);
			} else {
				watirHolder.add("if (ie.title() !~ " + idValue + ")", 2);
			}
			watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 3);
			watirHolder.add("end", 2);
		}
		else if (pe instanceof Option) {
			Option option = (Option) pe;
			Select select = (Select) option.getParent();

			//Check that the parent select list present:
			watirHolder.add("if not " + watirHolder.getVariableName(select)
					+ ".methods.member?(\"select_value\")", 3);
			watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
			watirHolder.add("end", 3);
			
			if (option.getMainIdentifierType().equals(IdentifierType.LABEL)) {
				watirHolder.add(watirHolder.getVariableName(select) + ".getAllContents().each do |optLabel|", 3);
				if (moderator.equals(EQUAL)) {
					watirHolder.add("if(optLabel == " + idValue + ")", 4);
				}
				else {
					watirHolder.add("if((optLabel =~ " + idValue + ") != nil)", 4);
				}
				watirHolder.add(watirHolder.getVariableName(option) + " = optLabel", 5);
				watirHolder.add("end", 4);
				watirHolder.add("end", 3);
			}
			else if (option.getMainIdentifierType().equals(IdentifierType.VALUE)) {
				watirHolder.add(watirHolder.getVariableName(option) + " = "
						+ watirHolder.getVariableName(select)
						+ ".option(:value, " + idValue + ")", 3);
			} else if (option.getMainIdentifierType().equals(IdentifierType.INDEX)) {
				watirHolder.add(watirHolder.getVariableName(option) + " = "
						+ watirHolder.getVariableName(select)
						+ ".getAllContents()["
						+ option.getMainIdentifierValue() + "]", 3);
			}
			else {
				throw new ExporterException(
						"Only label, value and index are supported identifierts "
								+ "for options in select lists in Watir");
			}
			watirHolder.add("if (" + watirHolder.getVariableName(option) + " == nil)", 3);
			watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 4);
			watirHolder.add("end", 3);
		}
		else {
			//all other page elements:			

			if (WatirUtils.hasExternalLabel(pe)) {
				//set identifier to "id", the one that the label points to.
				watirHolder.add(WatirUtils.getLabelTargetId(pe, watirHolder));
				watirHolder.addSeparator();
				idValue = watirHolder.getVariableName(pe);
				idType = ":id";
			}
			
			watirHolder.add("pass = 0", 3);
			if (pe instanceof Text) {
				String compare = pe.isNot() ? "!=" : "=="; 
				watirHolder.add("while ie.text.index(" + idValue + ") " + compare + " nil do", 3);
			}
			else {
				//all other page elements:			

				String not = pe.isNot() ? "" : "not ";
				
				IdentifierType mainIdType = pe.getMainIdentifierType();
				if ((mainIdType.equals(IdentifierType.SRC) || mainIdType.equals(IdentifierType.HREF)) 
						&& !pe.getMainIdentifierValue().startsWith("http")) {
					//SRC or HREF is relative and must be made absolute (add hostname and protocol):
					if (pe.getMainIdentifierValue().startsWith("/")) {
						//we want to extract protocol and hostname for IE current page, and append our value
						idValue = "ie.url[0, ie.url.split('//')[1].index('/') + ie.url.split('//')[0].length + 2] + \"" + pe.getMainIdentifierValue() + "\"";
					}
					else {
						//we want to extract the whole path except last file, and append our value
						idValue = "ie.url[0, ie.url.rindex('/')] + \"/" + pe.getMainIdentifierValue() + "\"";
					}
				}
				//save variable
				watirHolder.add(watirHolder.getVariableName(pe) + " = ie." + WatirUtils.getElementType(pe) + "(" + idType + ", " + idValue + ")", 3);
				
				//assert present
				watirHolder.add("while " + not + watirHolder.getVariableName(pe) + ".exists? do", 3);
			}
			watirHolder.add("if (pass > 20)", 4);
			watirHolder.add("raise " + WatirHolder.TEST_STEP_FAILED, 5);
			watirHolder.add("end", 4);
			watirHolder.add("sleep 0.1", 4);
			watirHolder.add("pass += 1", 4);
			watirHolder.add("end", 3);

		}
	}

}
