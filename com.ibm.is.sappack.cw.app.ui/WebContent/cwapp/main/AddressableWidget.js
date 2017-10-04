dojo.provide("cwapp.main.AddressableWidget");

/*
 * If you are creating an Addressable Container that extends this "dojo class",
 * remember to propagate the onShow and onHide to its children.
 */

dojo.declare("cwapp.main.AddressableWidget", [], {
	
	/*
	 * Array containing the parameters in the hash used by the widget that
	 * implements this "dojo class".
	 */
	addressParameters : null,
	
	/*
	 * "Abstract" function that returns the hashObject filled with the
	 * parameters used by the widget that implements this "dojo class".
	 */
	getCurrentAddress : null,
	
	/*
	 * "Abstract" function that handles the contents of the hash, e.g., open
	 * specified tab or select a node in a tree based on the contents of the
	 * Hash. It receives a hashObject as a parameter.
	 */
	handleHash : null,
	
	startup : function() {
		this._handleHash();
		this.inherited(arguments);
	},
	
	onShow : function() {
		var current = this.getCurrentAddress();
		if (current) {
			this.setHash(current);
		}
	},
	
	onHide : function() {
		this.cleanHash();
	},
	
	setHash : function(param) {
		if (!param) {
			param = this.getCurrentAddress();
		}
		var hashObject = dojo.queryToObject(dojo.hash());
		for ( var i = 0; i < this.addressParameters.length; i++) {
			hashObject[this.addressParameters[i]] = param[this.addressParameters[i]];
		}
		dojo.hash(dojo.objectToQuery(hashObject));
	},
	
	cleanHash : function() {
		var hashObject = dojo.queryToObject(dojo.hash());
		for ( var i = 0; i < this.addressParameters.length; i++) {
			delete hashObject[this.addressParameters[i]];
		}
		dojo.hash(dojo.objectToQuery(hashObject));
	},
	
	_handleHash : function() {
		var hashObject = null;
		var hash = dojo.hash();
		if (hash) {
			hashObject = dojo.queryToObject(hash);
		}
		this.handleHash(hashObject);
	}
});