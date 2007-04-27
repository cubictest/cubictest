/*
 * This software is licensed under the terms of the GNU GENERAL PUBLIC LICENSE
 * Version 2, which can be found at http://www.gnu.org/copyleft/gpl.html
 */

var Cubic = Cubic || {};
Cubic.load = function(url) {
	var doc;
	var node;

	if(arguments[1]) {
		doc = arguments[1];
	} else {
		doc = document;
	}
	
	if(url.match(/\.css$/)) {
		node = doc.createElement('link');
		node.rel = "stylesheet";
		node.type = 'text/css';
		node.href = url;
	} else {
		node = doc.createElement('script');
		node.type = 'text/javascript';
		node.src = url;		
	}
	doc.getElementsByTagName('head')[0].appendChild(node);  
}

/* Load Yahoo UI libraries */
Cubic.load('scripts/YahooUI/yahoo/yahoo.js');
Cubic.load('scripts/YahooUI/dom/dom.js');
Cubic.load('scripts/YahooUI/event/event.js');
Cubic.load('scripts/YahooUI/container/container_core.js');
Cubic.load('scripts/YahooUI/logger/logger.js');
Cubic.load('scripts/YahooUI/menu/menu-cubic.js');

/* Load other libs */
Cubic.load('scripts/jsonrpc.js');

/* Load Cubic libraries */
Cubic.load('scripts/cubic/dom.js');
Cubic.load('scripts/cubic/recorder.js');


/* Load CSS */
Cubic.load('scripts/YahooUI/menu/assets/menu.css');
Cubic.load('scripts/YahooUI/fonts/fonts.css');
Cubic.load('scripts/YahooUI/reset/reset.css');

Cubic.load('scripts/YahooUI/logger/assets/logger.css');

Event.observe(window, 'load', function() {
	// Hide output
	document.getElementsByTagName("TABLE")[0].style.width = "100%";
	document.getElementsByTagName("TABLE")[0].style.border = "1px solid red";
	document.getElementsByTagName("TR")[0].style.display = "none";

/*
var temp = document.createElement("div");
document.body.appendChild(temp);
temp.innerHTML = '<a href="https://www.dnbnor.no/" target="myiframe" style="position: absolute; display: block; top: 0">google.no</a>';
*/

	var iframeName = 'myiframe';
	//var myLogReader = new YAHOO.widget.LogReader(); 
	var jsonrpc = new JSONRpcClient("/selenium-server/cubic-recorder/JSON-RPC");
	var yuiContextMenu;
	var cubicContextMenu;
	
	$(iframeName).name = iframeName;
	
	/* iframe load handler */
	Event.observe($(iframeName), 'load', function() {
		var frameDoc = $(iframeName).contentDocument;
		/* Hack to make the YAHOO menu work across frames */		
		YAHOO.widget.MenuManager.oDoc = frameDoc;
		
		jsonrpc.recorder.setStateTitle(frameDoc.title);

		/* Add menu.css to the page in the iframe */
		Cubic.load('/selenium-server/core/scripts/YahooUI/menu/assets/menu.css', frameDoc);
		Cubic.load('/selenium-server/core/scripts/cubic/contextmenu.css', frameDoc);

		/* Rebuild the context menu */
		if(yuiContextMenu) {
			yuiContextMenu.destroy();
		}
		
		var menuElement = frameDoc.createElement("div");
		menuElement.id = "cubicContextMenu";
		frameDoc.body.insertBefore(menuElement, frameDoc.body.firstChild);
		yuiContextMenu = new YAHOO.widget.ContextMenu("cubicContextMenu", { trigger: frameDoc, width: "", zIndex: 1000 });
		cubicContextMenu = new Cubic.recorder.ContextMenu(yuiContextMenu);
		
		var yuiMenuItem;
		var cubicMenuItem;
		
		/* Create Menu Elements */
		/**
		 * Assert %s Present
		 */
		yuiMenuItem = yuiContextMenu.addItem("Assert %s present");
		cubicMenuItem = new Cubic.recorder.RPCContextMenuItem(yuiMenuItem, jsonrpc.recorder);
		
		cubicMenuItem.respondsTo = function(element) {
			var tag = element.tagName;
			return tag == "IMG" || tag == "INPUT" || tag == "A" || tag == "TEXTAREA" || tag == "SELECT" || tag == "BUTTON" || ((tag == "DIV" || tag == "TABLE") && element.id);
		}
		
		cubicMenuItem.execute = function() {
			jsonrpc.recorder.assertPresent(Cubic.dom.serializeDomNode(this.target),Cubic.recorder.getParentCubicId(this.target));
		}

		cubicContextMenu.addItem(cubicMenuItem);

		/**
		 * Assert Option Present
		 */
		
		yuiMenuItem = yuiContextMenu.addItem("Assert Option Present");
		cubicMenuItem = new Cubic.recorder.RPCContextMenuItem(yuiMenuItem, jsonrpc.recorder);
	
		cubicMenuItem.respondsTo = function(element) {
			var tag = element.tagName;
			return tag == "SELECT";
		}
		
		cubicMenuItem.execute = function() {
			YAHOO.log("execute  on assert Option Present");
			var elmt = this.target;
			var opt = elmt.options[elmt.selectedIndex];
			var parentCubicId = elmt.cubicId;
			if(typeof parentCubicId == 'undefined'){
				jsonrpc.recorder.assertPresent(Cubic.dom.serializeDomNode(elmt),Cubic.recorder.getParentCubicId(elmt));
				parentCubicId = elmt.cubicId;
			}
			if(opt != null && typeof opt != 'undefined'){
				jsonrpc.recorder.assertPresent(Cubic.dom.serializeDomNode(opt),parentCubicId);
			}
		}

		cubicContextMenu.addItem(cubicMenuItem);
		
		/**
		 * Assert Options Present
		 */
		
		yuiMenuItem = yuiContextMenu.addItem("Assert All Options Present");
		cubicMenuItem = new Cubic.recorder.RPCContextMenuItem(yuiMenuItem, jsonrpc.recorder);
	
		cubicMenuItem.respondsTo = function(element) {
			var tag = element.tagName;
			return tag == "SELECT";
		}
		
		cubicMenuItem.execute = function() {
			YAHOO.log("execute  on assert All Options Present");
			var elmt = this.target;
			var parentCubicId = elmt.cubicId;
			if(typeof parentCubicId == 'undefined'){
				jsonrpc.recorder.assertPresent(Cubic.dom.serializeDomNode(elmt),Cubic.recorder.getParentCubicId(elmt));
				parentCubicId = elmt.cubicId;
			}
			for(var i = 0; i < elmt.options.length; i++){
				var opt = elmt.options[i];
				if(opt != null && typeof opt != 'undefined'){
					jsonrpc.recorder.assertPresent(Cubic.dom.serializeDomNode(opt),parentCubicId);
				}
			}
			
		}

		cubicContextMenu.addItem(cubicMenuItem);
		
		/**
		 * Assert Page Title Present
		 */
		yuiMenuItem = yuiContextMenu.addItem("Assert Page Title Present");
		cubicMenuItem = new Cubic.recorder.RPCContextMenuItem(yuiMenuItem, jsonrpc.recorder);
		
		cubicMenuItem.respondsTo = function(element) {
			return true;
		}
		
		cubicMenuItem.execute = function() {
			jsonrpc.recorder.assertPresent(Cubic.dom.serializeDomNode(frameDoc.getElementsByTagName("TITLE")[0]),null);
		}
		
		cubicContextMenu.addItem(cubicMenuItem);
	
		/**
		 * Assert Text %s Present
		 */
		yuiMenuItem = yuiContextMenu.addItem("Assert Text '%s' Present");
		cubicMenuItem = new Cubic.recorder.RPCContextMenuItem(yuiMenuItem, jsonrpc.recorder);
		
		cubicMenuItem.respondsTo = function(element) {
			return this.selectedText;
		}
		
		cubicMenuItem.execute = function() {
			jsonrpc.recorder.assertTextPresent(this._selectedText, null);
		}
		
		cubicMenuItem.setTarget = function(element) {
			this._selectedText = this.selectedText;
			this.setTextFor(element);
		}
		
		cubicMenuItem.generateLabelFor = function(element) {
			var s = this._selectedText;
			if(!s) {
				return "";
			}
			if(s.length > 10) {
				s = s.substring(0, 7) + "...";
			}
			return s;
		}
		
		// Temporary hack until we figure out why the selection disappears when the contextmenu is displayed
		var temp = function(item) {
			YAHOO.log("observing..")
			Event.observe(frameDoc.body, 'mouseup', function(e) {
				item.selectedText = Cubic.dom.getSelectedText($(iframeName).contentWindow).toString();
			}, false);
		}
		temp(cubicMenuItem);
		
		cubicContextMenu.addItem(cubicMenuItem);
	
	
		/* Render Menu */
		cubicContextMenu.render(frameDoc.body);		
		
		/* Set up the action listener */
		var actionRecorder = new Cubic.recorder.ActionRecorder(jsonrpc.recorder, frameDoc.body);
		actionRecorder.ignore(yuiContextMenu.element);
		
	}, false);
}, true);