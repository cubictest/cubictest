/*******************************************************************************
 * Copyright (c) 2005, 2008 Erlend S. Halvorsen and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Erlend S. Halvorsen - initial API and implementation
 *******************************************************************************/


Cubic.dom = {};

Cubic.dom.serializeDomNode = function(domNode) {
	var data = {};
	if(!domNode.cubicId) {
		var date = new Date();
		domNode.cubicId = date.valueOf() + '';
	}
	
	data.parentCubicId = Cubic.dom.getParentCubicId(domNode.parentNode);
	
	data.properties = {};
	for(var i=0; i < Cubic.dom.serializeDomNode.properties.length; i++) {
		property = Cubic.dom.serializeDomNode.properties[i];
		if(typeof domNode[property] != "undefined") {
			data.properties[property] = domNode[property];			
		}
	}
	if((domNode.tagName == "INPUT" || domNode.tagName == "SELECT" || domNode.tagName == "TEXTAREA") && domNode.id) {
		YAHOO.log("ancestor: " + domNode.form);
		var ancestor = domNode.ownerDocument;
		//var labels = Element.getElementsBySelector(ancestor, "label");
		var labels = ancestor.getElementsByTagName("label");
		YAHOO.log("Labels #: " + labels.length)
		for(var i=0; i < labels.length; i++) {
			YAHOO.log(labels[i].htmlFor);
			if(labels[i].htmlFor == domNode.id) {
				data.label = labels[i].innerHTML;
			}
		}
	}
	
	if(domNode.tagName == "SELECT") {
		data.selected = domNode.value;
		
	}
	
	return toJSON(data);
}

Cubic.dom.serializeDomNode.properties = [
	'cubicId',
	'href',
	'id',
	'innerHTML',
	'label',
	'name',
	'src',
	'tagName',
	'type',
	'value',
	'alt'
];

Cubic.dom.getSelectedText = function(w) {
	w = w || window;
	var txt = '';
	var foundIn = '';
	if(w.getSelection)
	{
		txt = w.getSelection();
		foundIn = 'window.getSelection()';
	}
	else if(w.document.getSelection)
	{
		txt = w.document.getSelection();
		foundIn = 'document.getSelection()';
	}
	else if(w.document.selection)
	{
		txt = w.document.selection.createRange().text;
		foundIn = 'document.selection.createRange()';
	}

	return txt;	
}


/**
 * 
 * @param {Element} element
 */
Cubic.dom.getParentCubicId = function (element){
	if(typeof element.cubicId != "undefined"){
		return element.cubicId + "";
	}
	if(element.parentElement != null || typeof element.parentElement != "undefined"){
		return Cubic.dom.getParentCubicId(element.parentElement);
	}else{
		return null;
	}
}
