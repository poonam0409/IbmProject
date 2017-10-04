dojo.provide("cwapp.tab_bdr.BDRTab");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("cwapp.main.AddressableTabContainer");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");
dojo.require("dojo.hash");

dojo.require("cwapp.tab_bdr.TabBPH");
dojo.require("cwapp.tab_bdr.TabBO");
dojo.require("cwapp.tab_bdr.TabTables");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.BDRTab", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr","templates/BDRTab.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},

	// public functions
	
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	postCreate : function() {
		this.inherited(arguments);
	},
	
	onShow: function () {
		this.bdrTabContainer.onShow();
	},
	
	onHide: function () {
		this.bdrTabContainer.onHide();
	},
	
	onHideAttempt : function(event){
	    this.bdrTabContainer.onHideAttempt(event);
	}
});
