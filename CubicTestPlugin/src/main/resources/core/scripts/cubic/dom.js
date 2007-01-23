Cubic.dom = {};

Cubic.dom.serializeDomNode = function(domNode) {
	var data = {};
	data.properties = {};
	for(var i=0; i < Cubic.dom.serializeDomNode.properties.length; i++) {
		property = Cubic.dom.serializeDomNode.properties[i];
		if(typeof domNode[property] != "undefined") {
			data.properties[property] = domNode[property];			
		}
	}
	if((domNode.tagName == "INPUT" || domNode.tagName == "SELECT" || domNode.tagName == "TEXTAREA") && domNode.id) {
		var ancestor = domNode.form || document;
		var labels = Element.getElementsBySelector(ancestor, "label");
		for(var i=0; i < labels.length; i++) {
			if(labels[i].htmlFor == domNode.id) {
				data.label = labels[i].innerHTML;
			}
		}
	}
	return toJSON(data);
}

Cubic.dom.serializeDomNode.properties = [
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