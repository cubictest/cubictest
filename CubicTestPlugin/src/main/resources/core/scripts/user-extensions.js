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
var temp = document.createElement("div");
document.body.appendChild(temp);
temp.innerHTML = '<a href="http://www.google.no/" target="myiframe" style="position: absolute; display: block; top: 0">google.no</a>';

	var iframeName = 'myiframe';
	var myLogReader = new YAHOO.widget.LogReader(); 
	var jsonrpc = new JSONRpcClient("/selenium-server/cubic-recorder/JSON-RPC");
	var yuiContextMenu;
	var cubicContextMenu;
	
	$(iframeName).name = iframeName;
	/* iframe load handler */
	Event.observe($(iframeName), 'load', function() {		
		/* Hack to make the YAHOO menu work across frames */		
		YAHOO.widget.MenuManager.oDoc = frames[iframeName].document;

		/* Add menu.css to the page in the iframe */
		Cubic.load('/selenium-server/core/scripts/YahooUI/menu/assets/menu.css', frames[iframeName].document);
		
		/* Rebuild the context menu */
		if(yuiContextMenu) {
			yuiContextMenu.destroy();
		}
		
		yuiContextMenu = new YAHOO.widget.ContextMenu("cubicContextMenu", { trigger: frames[iframeName].document, width: "auto" });
		cubicContextMenu = new Cubic.recorder.ContextMenu(yuiContextMenu);
		
		var yuiMenuItem;
		var cubicMenuItem;
		
		yuiMenuItem = yuiContextMenu.addItem("Assert %s present");
		cubicMenuItem = new Cubic.recorder.RPCContextMenuItem(yuiMenuItem, jsonrpc.recorder);
		
		cubicMenuItem.respondsTo = function(element) {
			var tag = element.tagName;
			return tag == "IMG" || tag == "INPUT" || tag == "A";
		}
		
		cubicMenuItem.execute = function() {
			jsonrpc.recorder.assertPresent(Cubic.dom.serializeDomNode(this.target));
		}
		
		cubicContextMenu.addItem(cubicMenuItem);
	
		cubicContextMenu.render(frames[iframeName].document.body);		
		
		/* Set up the action listener */
		var actionRecorder = new Cubic.recorder.ActionRecorder(jsonrpc.recorder, frames[iframeName].document.body);
		actionRecorder.ignore(yuiContextMenu.element);
		
	}, false);
}, true);