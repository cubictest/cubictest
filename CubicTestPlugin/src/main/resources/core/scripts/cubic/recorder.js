Cubic.Recorder = Class.create();
Cubic.Recorder.prototype = {
	frame : 'myiframe',
	initialize : function() {
		// we need to name this frame, since the frame's document can only be accessed through frames['name'].document
		$(this.frame).name = this.frame;
		Event.observe($(this.frame), 'load', this.onload.bind(this), false);
	},
	
	onload : function() {	
		var element = frames[this.frame].document;
		var contextMenu = document.createElement('div');
		this.contextMenu = contextMenu;
		
		contextMenu.innerHTML = "Context Menu";
		contextMenu.style.display = "none";
		contextMenu.style.position = "absolute";
		document.getElementsByTagName('body')[0].appendChild(contextMenu);

		Event.observe(element, 'click', function(event) {
		
			var url = "/selenium-server/cubic-recorder/click";
			var request = new Ajax.Request(
				url,
				{
					method: 'get'
				}
			);
			Event.stop(event);
		}, false);
		
		Event.observe(element, 'mouseover', function(evt) {
			evt.target.style.border = "1px solid red";
		}, false);
		
		Event.observe(element, 'mouseout', function(evt) {
			evt.target.style.border = "";
		}, false);
				
		Event.observe(element, 'contextmenu', function(evt) {
			evt.preventDefault();
			evt.stopPropagation();
			contextMenu.style.left = evt.clientX + "px";
			contextMenu.style.top = evt.clientY + "px";
			Element.show(contextMenu);
		}, false);
		
		Event.observe(element, 'mousedown', function(evt) {
			Element.hide(contextMenu);
		}, false);
	}
}