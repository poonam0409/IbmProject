dojo.provide("cwapp.tab_bdr.bph.PlaceholderWidget");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.PlaceholderWidget", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/PlaceholderWidget.html"),
	widgetsInTemplate : true,

	// nls support
	msg : {},

	// public functions
	constructor : function(args) {
		if (args) {
			dojo.mixin(this, args);
		}
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},

	postCreate : function() {
		this.inherited(arguments);
	},
	
	setMessage : function(multiselection) {
		if (multiselection) {
			this.message.innerHTML = this.msg.BphPlaceholderMessageMultiselection;
		} else {
			this.message.innerHTML = this.msg.BphPlaceholderMessage;
		}
	}
});

