dojo.provide("cwapp.main.FilteringSelectStatic");

dojo.require("dijit.form.FilteringSelect");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.main.FilteringSelectStatic", [ dijit.form.FilteringSelect ],
{
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

        this.textbox.readOnly = true;
        dojo.addClass(this.domNode, "FilteringSelectStatic");
        dojo.connect(this.textbox, "onmouseover", this, "_textBoxMouseOver");
		dojo.connect(this.textbox, "onclick", this, "_textBoxClicked");
		dojo.connect(this.textbox, "onfocus", this, "_textBoxClicked");
	},
	
	// private functions
	_textBoxClicked : function() {
		this.toggleDropDown();
	},
	
	_textBoxMouseOver : function() {
		dojo.style(this.domNode, {cursor: "default"});
	}
});
