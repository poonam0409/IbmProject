dojo.provide("cwapp.tab_bdr.bph.ImportFromCSVDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dojox.html._base");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.ImportFromCSVDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph","templates/ImportFromCSVDialog.html"),
	widgetsInTemplate : true,
	
	parentWidget : null,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
	    LABEL_IDS : new Array("l1", "l2", "l3"),
	},
	
	// public functions
	constructor : function(args) {
	    if(args) {
		dojo.mixin(this,args);
	    }
	    dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},
	
	postCreate : function() {
	    dojo.connect(this.importDialog, "onCancel", this, "destroyDialog");
	    dojo.connect(this.importButton, "onClick", this, "_importButtonOnClick");
	    dojo.connect(this.cancelButton, "onClick", this, "_cancelButtonOnClick");
	    dojo.connect(this.importBDRCheckbox, "onClick", this, "_enableButtons");
	    dojo.connect(this.importTableMetadataCheckbox, "onClick", this, "_disableButtons");
	    
	    this._changeCheckboxStates(false);
	    this.inherited(arguments);
	},
	
	show : function() {
	    this.importDialog.show();
	},
	
	hide : function(){
	    this.importDialog.hide();
	    this.importDialog.destroyDescendants();
	},
	
	destroyDialog : function(){
	    this.importDialog.destroyDescendants();
	},
	
	_cancelButtonOnClick : function() {
	    this.hide();
	},
	
	_importButtonOnClick : function(){
	    this._importData();
	    this.hide();
	},
	
	_enableButtons : function(){
	    this._changeCheckboxStates(false);
	},
	
	_disableButtons : function(){ 
	    this._changeCheckboxStates(true);
	},
	
	_changeCheckboxStates : function(state){
	    var checkboxes = new Array(this.complBDRCheckbox, this.processAndStepsCheckbox, this.bOsWithTablesCheckbox);
	    Util.changeWidgetsDisabledState(checkboxes, state);
	    
	    // change the style of the checkbox labes
	    if(state){
		this._con.LABEL_IDS.forEach(dojo.hitch(this, function(id){
		    dojo.addClass(id, "DisabledLabel");
		}));
	    }
	    else{
		this._con.LABEL_IDS.forEach(dojo.hitch(this, function(id){
		    dojo.removeClass(id, "DisabledLabel");
		}));
	    }
	},
	
	_onCancel : function() {
	    this.hide();
	},
	
	_importData : function(){
	    var mode;
		    if (this.importTableMetadataCheckbox.checked) {
			mode = BphImportTypes.CSV_TABLES;
		} else {
			if (this.complBDRCheckbox.checked) {
				mode = BphImportTypes.CSV_COMPLETE;
			} else if (this.processAndStepsCheckbox.checked) {
				mode = BphImportTypes.CSV_PROCESSES;
			} else {
				mode = BphImportTypes.CSV_BOS;
			}
		}
	    
	    this.parentWidget._importBPH(mode, this.overrideCheckbox.checked);
	}
});