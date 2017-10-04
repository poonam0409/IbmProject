dojo.provide("cwapp.tab_bdr.bph.AddFieldDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.Dialog");

dojo.require("dijit.form.Form");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.ValidationTextBox");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.Textarea");

dojo.require("idx.layout.BorderContainer");

dojo.require("dijit.layout.ContentPane");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.AddFieldDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/AddFieldDialog.html"),
	widgetsInTemplate : true,

	// nls support
	msg : {},
	
	// constants
	_con : {
		EMPTY_STRING : "",
	},

	// private members
	_caller : null,
	_connects : [],

	// events
	onHide : function() {},
	
	// public functions
	constructor : function(args) {
		if (args) {
			dojo.mixin(this, args);
		}

		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},

	postCreate : function() {
		this._connects.push(dojo.connect(this.saveButton, "onClick", this, "_onSave"));
		this._connects.push(dojo.connect(this.cancelButton, "onClick", this, "_onCancel"));
		this._connects.push(dojo.connect(this.addFieldDialog, "onOK", this, "_onSave"));
		this._connects.push(dojo.connect(this.addFieldDialog, "onCancel", this, "_onCancel"));
		this._connects.push(dojo.connect(this.addFieldDialog, "onHide", this, "onHide"));
		this._connects.push(dojo.connect(this.fieldNameInput, "onKeyUp", this, "_fieldNameInputChanged"));
		
		this.inherited(arguments);
	},
	
	destroyRecursive : function(preserveDom) {
		dojo.forEach(this._connects, dojo.disconnect);
		this.inherited(arguments);		
	},
	
	show : function(caller) {
		this._caller = caller;
		this._init();
		this.addFieldDialog.show();
	},
	
	_init : function() {
		this.fieldAttributesForm.reset();
		this.saveButton.set("disabled", true);
	},
	
	_onSave : function() {
		this.saveButton.set("disabled", true);
		var field = this._fromInput();	
		this._caller.addNewField(field);
		this.addFieldDialog.hide();
	},

	_onCancel : function() {
		this.addFieldDialog.hide();
	},
	
	_fromInput : function() {
		var item = new Object();
		
		item.name = this.fieldNameInput.get("value");
		item.checkTable = this.checkTableInput.get("value");
		item.sapView = this.sapViewInput.get("value");
		item.description = this.descriptionInput.get("value");
		
		return item;
	},
	
	_fieldNameInputChanged : function() {
		if (this.fieldNameInput.isValid()) {
			this.saveButton.set("disabled", false);
		}
		else {
			this.saveButton.set("disabled", true);
		}
	}
});
