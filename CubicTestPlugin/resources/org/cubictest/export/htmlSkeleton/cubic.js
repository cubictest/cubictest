UserActions = {
	actions : {},
	test : function(uaId, actionId, element) {
		if(this.actions[uaId][actionId][1] == null || this.actions[uaId][actionId][1] == element[this.actions[uaId][actionId][0]]) {
			this.actions[uaId][actionId][2] = true;
		} else {
			this.actions[uaId][actionId][2] = false;
		}
		
		if(actionId != this.actions[uaId].length-1) {
			return;
		}
		
		var done = true;
		for(var i=0; i < this.actions[uaId].length; i++) {
			done = done && this.actions[uaId][i][2];
		}
		if(done) {
			UserActions.trigger(uaId);
		}
	},
	
	trigger : function(uaId) {
		window.location.href = escape(uaId);
	}
};