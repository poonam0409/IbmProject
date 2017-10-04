dojo.provide("cwapp.main.AddressableTabContainer");

dojo.require("dijit.layout.TabContainer");
dojo.require("cwapp.main.AddressableWidget");

dojo.declare("cwapp.main.AddressableTabContainer", [ dijit.layout.TabContainer, cwapp.main.AddressableWidget ], {
	
	getCurrentAddress : function() {
		var address = {};
		address[this.addressParameters[0]] = this.selectedChildWidget.id;
		return address;
	},
	
	getDefaultAddress : function() {
		var address = {};
		address[this.addressParameters[0]] = this.getChildren()[0].id;
		return address;
	},
	
	constructor : function() {
		this.inherited(arguments);
		this.addressParameters = [ "tab" ];
	},
	
	postCreate : function() {
		this.inherited(arguments);
		dojo.connect(this, "_transition", this, "_onTransition");
		
		if(this.dojoAttachPoint == "bdrTabContainer"){
			dojo.connect(this.tablist, "onMouseDown", this, "onHideAttempt");   
		}
	},
	
	onShow : function() {
		this.inherited(arguments);
		this.selectedChildWidget.onShow();
	},
	
	onHide : function() {
		this.inherited(arguments);
		this.selectedChildWidget.onHide();
	},
	
	onHideAttempt : function(event) {
	    var targetClass = event.target.className;
	    if(targetClass == "dijitTabContent" || targetClass == "tabLabel"){
		this.selectedChildWidget.onHideAttempt(event);
	    } 
	},
	
	handleHash : function(hashObject) {
		if (hashObject && hashObject[this.addressParameters[0]]) {
			var tabId = hashObject[this.addressParameters[0]];
			this._selectTab(tabId);
		} else {
			this.setHash(this.getDefaultAddress());
		}
	},
	
	_onTransition : function(newPage) {
		var address = {};
		address[this.addressParameters[0]] = newPage.id;
		this.setHash(address);
	},
	
	_selectTab : function(tabId) {
		var children = this.getChildren();
		for ( var i = 0; i < children.length; i++) {
			if (children[i].id == tabId) {
				children[i].selected = true;
				break;
			}
		}
	}
});