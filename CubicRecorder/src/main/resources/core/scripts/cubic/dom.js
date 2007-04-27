/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */

Cubic.dom = {};

Cubic.dom.serializeDomNode = function(domNode) {
	var data = {};
	if(!domNode.cubicId) {
		var date = new Date();
		domNode.cubicId = date.valueOf() + '';
	}
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
	'value'
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