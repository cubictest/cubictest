Cubic.recorder = {};

/**
 * ContextMenu
 * @param {YAHOO.widget.ContextMenu} contextMenu
 */
Cubic.recorder.ContextMenu = function(contextMenu) {
	this.init(contextMenu);
}

Cubic.recorder.ContextMenu.prototype = {
	init: function(contextMenu) {
		this.menuItems = [];
		this.contextMenu = contextMenu;
		this.contextMenu.beforeShowEvent.subscribe(this.onBeforeShow, this, true);
		this.contextMenu.showEvent.subscribe(this.onShow, this, true);
	},
	
	onBeforeShow: function() {
		YAHOO.log("onBeforeShow");
		/* FIX for scrolling bug in Firefox (page scrolls to the bottom the first time the menu is triggered) */
		this.scrollLeft = this.contextMenu.element.ownerDocument.defaultView.scrollX;
		this.scrollTop = this.contextMenu.element.ownerDocument.defaultView.scrollY;
		for(var i=0; i < this.menuItems.length; i++) {
			this.menuItems[i].setTarget(this.contextMenu.contextEventTarget);
		}
	},
	
	onShow: function() {
		YAHOO.log("onShow");
		/* FIX for scrolling bug in Firefox (page scrolls to the bottom the first time the menu is triggered) */
		this.contextMenu.element.ownerDocument.defaultView.scrollTo(this.scrollLeft, this.scrollTop);
		//Element.scrollTo(this.contextMenu.element);
	},
	
	addItem: function(menuItem) {
		this.menuItems.push(menuItem);
	},
	
	render: function(target) {
		this.contextMenu.render(target);
	}
}


/**
 * ContextMenuItem
 */
Cubic.recorder.ContextMenuItem = function(yuiMenuItem) {
	this.init(yuiMenuItem);
}

Cubic.recorder.ContextMenuItem.prototype = {
	init: function(yuiMenuItem) {
		this.menuItem = yuiMenuItem;
		this.text = yuiMenuItem.cfg.getProperty("text");
		this.menuItem.clickEvent.subscribe(this._execute, this, true);
	},
	
	/**
	 * Wrapper method for execute, so that execute can be overridden
	 * @private
	 */
	_execute: function() {
		this.execute();
	},
	
	execute: function() {
		alert("Implement me!");
	},
	
	respondsTo: function(element) {
		alert("Implement me!");
	},
	
	setTarget: function(element) {
		YAHOO.log("setTarget(" + element.tagName + ")")
		this.setTextFor(element);
		if(this.respondsTo(element)) {
			this.target = element;
			this.menuItem.cfg.setProperty("disabled", false);
		} else {
			this.menuItem.cfg.setProperty("disabled", true);
		}
	},
	
	setTextFor: function(element) {
		var label = this.generateLabelFor(element);

		this.menuItem.cfg.setProperty("text", this.text.replace("%s", label));
	},
	
	generateLabelFor: function(element) {
		var label;
		switch(element.tagName) {
		case "A":
			label = "Link"
			break;
		case "INPUT":
			switch(element.type.toLowerCase()) {
			case "text":
				label = "Text Field";
				break;
			case "checkbox":
				label = "Checkbox";
				break;
			case "radio":
				label = "Radio Button";
				break;
			case "password":
				label = "Password Field";
				break;
			case "button":
			case "submit":
				label = "Button";
				break;
			}
			break;
		case "TEXTAREA":
			label = "Textarea";
			break;
		case "SELECT":
			if(element.multiple) {
				label = "Select Box";
			} else {
				label = "Drop-down Box";
			}
			break;
		case "TABLE":
			label = "Table";
			break;
		case "DIV":
			label = "Context";
			break;
		case "IMG":
			label = "Image";
			break;
		default:
			label = element.tagName;
			break;
		}
		if(element.id) {
			label = label + " (#" + element.id + ")";
		}

		return label;
	}
}


/**
 * RPCContextMenuItem
 */
Cubic.recorder.RPCContextMenuItem = function(yuiMenuItem, rpcRecorder) {
	this.init(yuiMenuItem, rpcRecorder);
}

YAHOO.extend(Cubic.recorder.RPCContextMenuItem, Cubic.recorder.ContextMenuItem, {
	init: function(yuiMenuItem, rpcRecorder) {
		Cubic.recorder.RPCContextMenuItem.superclass.init.call(this, yuiMenuItem);
		this.rpcRecorder = rpcRecorder;
	}
});


/**
 * ActionRecorder
 */
Cubic.recorder.ActionRecorder = function(rpcRecorder, domNode) {
	this.init(rpcRecorder, domNode);
}

Cubic.recorder.ActionRecorder.prototype = {
	CLICK: "Click",
	DBLCLICK: "Double click",
	MOUSE_OVER: "Mouse over",
	MOUSE_OUT: "Mouse out",
	ENTER_TEXT: "Enter text",
	
	init: function(rpcRecorder, domNode) {
		this.rpcRecorder = rpcRecorder;
		this.listen(domNode);
		this.ignoreList = [];
	},
	
	listen: function(domNode) {
		Event.observe($(domNode), 'click', this.mouseClick.bindAsEventListener(this), true);
		Event.observe($(domNode), 'keydown', this.keyDown.bindAsEventListener(this), true);
	},
	
	ignore: function(element) {
		this.ignoreList.push(element);
	},
	
	isIgnored: function(element) {
		for(var i=0; i < this.ignoreList.length; i++) {
			if(Element.descendantOf(element, this.ignoreList[i])) {
				return true;
			}
		}
		return false;
	},
	
	mouseClick: function(e) {
		var element = Event.element(e);
		if(!this.isIgnored(element)) {
			if(element.tagName != "A" && element.tagName != "IMG") {
				var temp = Event.findElement(e, "A");
				if(temp.tagName == "A") {
					element = temp;
				}
			}

			if((element.tagName == "INPUT" && element.type != "text" && element.type != "password")
				|| element.tagName == "A" || element.tagName == "IMG"
			) {
				this.rpcRecorder.addAction(this.CLICK, Cubic.dom.serializeDomNode(element));
			}
		}
	},
	
	keyDown: function(e) {
		var element = Event.element(e);
		if(this.isIgnored(element) || !this.isTextInput(element)) {
			return;
		}
		
		YAHOO.log("keyDown: " + element.tagName);
		
		if(e.keyCode == Event.KEY_RETURN) {
			return this.inputOnBlur(e);
		}
	
		if(!element.cubic_inputOnBlur) {
			element.cubic_oldInputValue = element.value;
			Event.observe(element, 'blur', this.inputOnBlur.bindAsEventListener(this), true);
			element.cubic_inputOnBlur=true;
		}
	},
	
	inputOnBlur: function(e) {
		var input = Event.element(e);
		if(typeof input.cubic_oldInputValue != "undefined") {
			if(input.cubic_oldInputValue == input.value) { // ignore event if value is unchanged
				return;
			}
		}
		this.rpcRecorder.addAction(this.ENTER_TEXT, Cubic.dom.serializeDomNode(input), input.value);
		input.cubic_oldInputValue = input.value;
	},
	
	isTextInput : function(element) {
		return (element.tagName == "INPUT" && (element.type == "text" || element.type == "password")) || element.tagName == "TEXTAREA";
	}
}
/**
 * 
 * @param {Element} element
 */
Cubic.recorder.getParentCubicId = function (element){
	if(typeof element.cubicId != "undefined"){
		return element.cubicId + "";
	}
	if(element.parentElement != null || typeof element.parentElement != "undefined"){
		return Cubic.recorder.getParentCubicId(element.parentElement);
	}else{
		return null;
	}
}