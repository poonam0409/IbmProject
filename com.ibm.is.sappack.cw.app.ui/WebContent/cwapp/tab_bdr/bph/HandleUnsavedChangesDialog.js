dojo.provide("cwapp.tab_bdr.bph.HandleUnsavedChangesDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.Dialog");

dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.Button");

dojo.require("dojox.html._base");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.HandleUnsavedChangesDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph","templates/HandleUnsavedChangesDialog.html"),
	widgetsInTemplate : true,
	
	parentWidget : null,
	triggeringElement : null,
	widgetWithUnsavedData : null,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
	    IMAGE_CLASS_WARNING : "ImageWarning_48",
	},
	
	// public functions
	constructor : function(args) {
	    if(args) {
		dojo.mixin(this,args);
	    }
	    dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},
	
	postCreate : function() {
	    dojo.connect(this.saveButton, "onClick", this, "_onSave");
	    dojo.connect(this.discardButton, "onClick", this, "_onDiscard");
	    dojo.connect(this.cancelButton, "onClick", this, "_onCancel");
	    this._setIcon(this._con.IMAGE_CLASS_WARNING);
	    this.inherited(arguments);
	},
	
	show : function() {
	    this.saveDialog.show();
	    dojo.connect(this.saveDialog, "onCancel", this, "_changeTreeSelection");
	},
	
	hide : function(){
	    this.saveDialog.hide();
	    this.destroy();
	},
	
	// private functions
	// Sets the icon
	_setIcon : function(iconClass) {
	    dojo.removeClass(this.icon);
	    dojo.addClass(this.icon, iconClass);
	},
	
	_onSave : function(data) {
	    dijit.byId(this.parentWidget)._save();
	    this._fireEvent(this.triggeringElement, "click");
	    this.hide();
	},
	
	_onDiscard : function() {
	    dijit.byId(this.parentWidget)._discardChanges();
	    this._fireEvent(this.triggeringElement, "click");
	    this.hide();
	},
	
	_onCancel : function() {
	    // we need that because the tree selection changes onmousedown and not onclick
	    if(this.widgetWithUnsavedData._tree){
		this._changeTreeSelection();
	    }
	    
	    this.hide();
	},
	
	//fires am event from a given element
	_fireEvent : function(element, event) {
	    if (document.createEvent) {
	        //for firefox and other
	        var evt = document.createEvent("HTMLEvents");
	        evt.initEvent(event, true, true ); // event type,bubbling,cancelable
	        return !element.dispatchEvent(evt);
	    } else {
	        //for IE
	        var evt = document.createEventObject();
	        return element.fireEvent('on'+event,evt);
	    }
	 },
	 
	 _changeTreeSelection : function(){
	     if(this.widgetWithUnsavedData._tree){
		 this.widgetWithUnsavedData._tree.set("paths", this.widgetWithUnsavedData._selectedPaths);
	     }
	 }
});
