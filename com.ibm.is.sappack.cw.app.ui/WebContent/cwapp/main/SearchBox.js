dojo.provide("cwapp.main.SearchBox");

dojo.require("dijit.form.TextBox");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.main.SearchBox", [ dijit.form.TextBox ],
{
	intermediateChanges : true,
	clearFilterConditionLink : null,
	
	// constants
	_con : {
		EMPTY_STRING : "",
	},

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
		this._setupSearchBox();
		
		this.connect(this.clearFilterConditionLink, "onclick", "_clearFilterCondition");
		this.connect(this, "onChange", "_checkValue");
		
		this._checkValue();

		this.inherited(arguments);
	},
	
	// private functions
	_setupSearchBox : function() {
		var domNode = this.domNode;
		dojo.addClass(domNode, "SearchBox");

		// adding a link which in the form of an image to the TextBox as the first child
		// please note that by doing so the overall length of the widget is increased by the size
		// and position of the image
		// however, this increased size is not taken into account when the widget is sized
		// by parent containers in case of a relative size specification (e.g. width: 100%)
		// so in order to have the widget horizontally fill the parent widget never specify
		// a width of 100% as this actually means 'the width of the parent widget + 20px'
		// but stick to a width assignment of max. 95%
		this.clearFilterConditionLink = dojo.create("a", {
			className : "SearchBox_ImageClearCondition",
			innerHTML : this.msg.SEARCHBOX_2,
		}, domNode, "first");
	},
	
	_clearFilterCondition : function() {
		this.set("value", this._con.EMPTY_STRING);
		this.textbox.focus();
	},
	
	_checkValue : function(value) {
		dojo[(value != this._con.EMPTY_STRING && value != undefined ? "remove" : "add") + "Class"](
			this.clearFilterConditionLink, "dijitHidden"
		);
	}
});