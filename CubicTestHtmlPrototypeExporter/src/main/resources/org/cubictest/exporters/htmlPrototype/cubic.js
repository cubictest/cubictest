var UserInteractions = {
	actions : {},
	/**
	 * Test if a User Interaction is completed
	 * 
	 * A User Interaction is made up of an array of actions, and each action is made up of an array with 3 elements: 
	 *  - the name of the html attribute who's value we're interested in
	 *  - the value the html attribute should have for the action to be completed (set to null if indifferent)
	 *  - the status of the action
	 * 
	 *  
	 * @param		uaId			the id of the User Interaction, and the name of the resulting html file
	 * @param		actionId 	the action's index in the User Interaction
	 * @param		element		a reference to the html element where the action was triggered
	 */
	test : function(uaId, actionId, element) {
		// if the action's value is null, or it matches the value of the element, set status to true 
		if(this.actions[uaId][actionId][1] == null || this.actions[uaId][actionId][1] == element[this.actions[uaId][actionId][0]]) {
			this.actions[uaId][actionId][2] = true;
		} else {
			this.actions[uaId][actionId][2] = false;
		}
		
		// If this isn't the last action, return
		if(actionId != this.actions[uaId].length-1) {
			return;
		}
		
		// this is the last action, check that all the actions are completed
		var done = true;
		for(var i=0; i < this.actions[uaId].length; i++) {
			done = done && this.actions[uaId][i][2];
		}
		
		// all actions completed, trigger transition
		if(done) {
			this.trigger(uaId);
		}
	},
	
	// trigger transition
	trigger : function(uaId) {
		window.location.href = escape(uaId);
	}
};

function hideScriptInfo() {
	document.getElementById("scriptInfo").innerHTML = "";	
}
