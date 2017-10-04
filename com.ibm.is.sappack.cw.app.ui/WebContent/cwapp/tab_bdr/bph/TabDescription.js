dojo.provide("cwapp.tab_bdr.bph.TabDescription");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.form.Form");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.Textarea");

dojo.require("dojo.i18n");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.TabDescription", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/TabDescription.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	constants: {},
	
	// private variables
	_caller : null,
	_type : null,
	_nameOriginal : "",
	_shortNameOriginal : "",
	_descriptionOriginal : "",
	
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
		// Make the global field length constants visible for the HTML template
		dojo.mixin(this.constants, BdrAttributeLengths);
	},
	
	postCreate : function() {
		dojo.connect(this.name, "onKeyUp", this, "_keyPressed");
		dojo.connect(this.shortName, "onKeyUp", this, "_keyPressed");
		dojo.connect(this.description, "onKeyUp", this, "_keyPressed");
		dojo.connect(this.descriptionForm, "onSubmit", this, "_onSubmit");
		
		if(General.ISREADONLY){
		    Util.changeWidgetsDisabledState([this.name, this.shortName, this.description], true);
		}
		
		this.inherited(arguments);
	},
	
	setCaller : function(caller) {
		this._caller = caller;
	},
	
	// configure the input fields of the panel depending on the type of the calling object
	setType : function(type) {
		this._type = type;
		switch (type) {
			case BdrTypes.PROCESS:
				this.name.set("maxLength", BdrAttributeLengths.PROCESS_NAME);
				dojo.style(this.shortNameDiv, "display", "none");
				break;
			case BdrTypes.PROCESS_STEP:
				this.name.set("maxLength", BdrAttributeLengths.PROCESS_STEP_NAME);
				dojo.style(this.shortNameDiv, "display", "none");
				break;
			case BdrTypes.BO:
				this.name.set("maxLength", BdrAttributeLengths.BO_NAME);
				dojo.style(this.shortNameDiv, "display", "");
				break;
			case BdrTypes.TABLE:
				this.name.set("maxLength", BdrAttributeLengths.TABLE_NAME);
				dojo.style(this.shortNameDiv, "display", "none");
				break;
			default:
				break;
		}
	},
	
	// Updates the view.
	// Called by the parent panel on update, save and discard events. The item type is not impacted.
	update: function(item) {
		this.name.set('value', item.name);
		this._nameOriginal = item.name;
		if (item.shortName) {
			this.shortName.set('value', item.shortName);
			this._shortNameOriginal = item.shortName;
		}
		this.description.set('value', item.description);
		this._descriptionOriginal = item.description;
	},

	// Resets the view.
	// Called by the parent panel when switching to "add new object" mode.
	reset: function() {
		this.name.set('value', "");
		this._nameOriginal = "";
		this.shortName.set('value', "");
		this._shortNameOriginal = "";
		this.description.set('value', "");
		this._descriptionOriginal = "";
		
		// set the focus to the first input field
		this.name.focus();
	},
	
	// Returns all the data from this tab.
	// Called by the parent panel when the save button is clicked.
	getData : function() {
		var data = {};
		data.name = this.name.get("value");
		data.description = this.description.get("value");
		if (this._type == BdrTypes.BO) {
			data.shortName = this.shortName.get("value");
		}
		return data;
	},
	
	// Event callback for activating the parent panel's save/discard buttons.
	_keyPressed: function() {
		var curName = this.name.get("value");
		var curShortName = this.shortName.get("value");
		var curDescription = this.description.get("value");
		
		if (curName == "" || this._type == BdrTypes.BO && curShortName == "") {
			// Required values missing
			this._caller.disableButtons();
		} else {
			if (curName != this._nameOriginal || curShortName != this._shortNameOriginal || curDescription != this._descriptionOriginal) {
				this._caller.enableButtons();
			}
		}
	},
	
	// Event callback for form submission (called when user presses Enter)
	_onSubmit : function(event) {
		event.preventDefault(); // Prevent the default submit behaviour that would reload the page
		var curName = this.name.get("value");
		if(curName != "" && (this._type != BdrTypes.BO || curShortName != "")) {
			this._caller._save();
		}
	}
});
