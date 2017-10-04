dojo.provide("cwapp.tab_rdm.ReferenceDataManagementTab");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("cwapp.main.AddressableTabContainer");

dojo.require("cwapp.tab_rdm.TargetReferenceDataPage");
dojo.require("cwapp.tab_rdm.SourceReferenceDataPage");
dojo.require("cwapp.tab_rdm.DataMappingsPage");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.ReferenceDataManagementTab", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm","templates/ReferenceDataManagementTab.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// private members
	
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
		this.rdmTabContainer.onShow();
	},
	
	onHide: function () {
		this.rdmTabContainer.onHide();
	}
});