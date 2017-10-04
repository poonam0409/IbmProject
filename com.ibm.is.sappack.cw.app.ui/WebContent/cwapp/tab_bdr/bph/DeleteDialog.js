dojo.provide("cwapp.tab_bdr.bph.DeleteDialog");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.Dialog");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.Button");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.DeleteDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	// basic widget settings
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/DeleteDialog.html"),
	widgetsInTemplate : true,

	// nls support
	msg : {},

	// private members
	_caller : null,
	
	// public functions
	
	constructor : function(args) {
		if (args) 
			dojo.mixin(this, args);
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},

	postCreate : function() {
		dojo.connect(this.okButton, "onClick", this, "_onOK");
		dojo.connect(this.cancelButton, "onClick", this, "_onCancel");
		dojo.connect(this.dialog, "onCancel", this, "_onCancel");
		this.inherited(arguments);
	},

	// detach: if true, item is only being detached, not deleted, so show a different message
	show : function(caller, items, detach) {
		this._caller = caller;
		
		if (items.length == 1) {
			// Deleting a single item
			var item = items[0][items[0].length - 1];
			switch (item.type) {
			case BdrTypes.PROCESS:
				this.message.innerHTML = this.msg.DeleteDialog_Process;
				break;
			case BdrTypes.PROCESS_STEP:
				this.message.innerHTML = this.msg.DeleteDialog_ProcessStep;
				break;
			case BdrTypes.BO:
				if (detach) {
					this.message.innerHTML = this.msg.DeleteDialog_Detach_BO;
				} else {
					this.message.innerHTML = this.msg.DeleteDialog_BO;
				}
				break;
			case BdrTypes.TABLE:
				if (detach) {
					this.message.innerHTML = this.msg.DeleteDialog_Detach_Table;
				} else {
					this.message.innerHTML = this.msg.DeleteDialog_Table;
				}
				break;
			default:
				break;
			}
		} else {
			// Deleting multiple items, use a generic message
			this.message.innerHTML = this.msg.DeleteDialog_Multi;
		}
		
		this.dialog.show();
	},

	// private functions
	
	_onOK : function() {
		this._caller.removeItems();
	},

	_onCancel : function() {
		this.dialog.hide();
	},
});
