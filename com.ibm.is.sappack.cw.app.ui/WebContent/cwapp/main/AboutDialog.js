dojo.provide("cwapp.main.AboutDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.Dialog");

dojo.require("idx.form.Link");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.main.AboutDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {

	// basic widget settings
	widgetsInTemplate : true,
	templateString : dojo.cache("cwapp.main", "templates/AboutDialog.html"),
	
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
		dojo.connect(this.aboutLink, "onClick", this, "_openDialog");

		this.inherited(arguments);
	},
	
	// private functions
	
	_openDialog : function() {
		// Get and display the build number
		dojo.xhrGet({
			url : Services.BUILD_NUMBER,
			handleAs : "text",
			sync : true,
			load : dojo.hitch(this, function(response) {
				this.buildNumber.innerHTML = Util.formatMessage(this.msg.AboutDialog_BuildNumberLabel, response);
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
		
		this.aboutDialog.show();
	}
});
