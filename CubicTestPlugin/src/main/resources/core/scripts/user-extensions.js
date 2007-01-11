var Cubic = Cubic || {};
Cubic.load = function(url) {
	var script = document.createElement('script');
	script.type = 'text/javascript';
	script.src = url;
	document.getElementsByTagName('head')[0].appendChild(script);  
}

Cubic.load('scripts/cubic/recorder.js');
Cubic.load('scripts/cubic/jsonrpc.js');

Event.observe(window, 'load', function() {
	alert("refreshed4");
	var jsonrpc = new JSONRpcClient("/selenium-server/cubic-recorder/JSON-RPC");
	jsonrpc.recorder.assertPresent('{"tagName": "INPUT", "attributes": {"type": "text", "value": "ehalvorsen", "id": "username"}}');
	alert("refreshed3");
	cubicRecorder = new Cubic.Recorder();
}, true);