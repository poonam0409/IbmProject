dojo.provide("cwapp.tab_bdr.bph.ExportToCSVDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dojox.html._base");

dojo.require("idx.dialogs");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.ExportToCSVDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph","templates/ExportToCSVDialog.html"),
	widgetsInTemplate : true,
	
	parentWidget : null,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
	    LABEL_IDS : new Array("l3", "l4", "l5"),
	},
	
	// public functions
	constructor : function(args) {
	    if(args) {
	    	dojo.mixin(this,args);
	    }
	    dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},
	
	postCreate : function() {
	    dojo.connect(this.exportDialog, "onCancel", this, "destroyDialog");
	    dojo.connect(this.exportButton, "onClick", this, "_exportButtonOnClick");
	    dojo.connect(this.cancelButton, "onClick", this, "_cancelButtonOnClick");
	    dojo.connect(this.exportBDRCheckbox, "onClick", this, "_enableButtons");
	    dojo.connect(this.exportTableMetadataCheckbox, "onClick", this, "_disableButtons");
	    dojo.connect(this.onlyCheckbox, "onClick", this, "_enableDropdown");
	    dojo.connect(this.allFieldsCheckbox, "onClick", this, "_disableDropdown");
	    
		// only enable BDR export if tree isn't empty
	    var thereIsATree = false;
		if (this.parentWidget._tree.model.store.objectStore.data.length == 0) {
			this.exportBDRCheckbox.set("disabled", true);
			dojo.addClass("lg1c2", "DisabledLabel");
			this.exportTableMetadataCheckbox.set("checked", true);
		    this._changeCheckboxStates(true);
		} else {
			thereIsATree = true;
			this._changeCheckboxStates(false);
		}
		
		// only enable BDR export if there are tables
		dojo.xhrGet({
			url: Services.BDR_TABLE,
			handleAs: "json",
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				if (data.length == 0) {
					// There are no tables
					this.exportTableMetadataCheckbox.set("disabled", true);
					dojo.addClass("lg1c1", "DisabledLabel");
					if (!thereIsATree) { // No tree, no tables
						this.exportButton.set("disabled", true);
					}
				}
			}),
			error: dojo.hitch(this, function(error) {
				this._standByOverlay.hide();
				cwapp.main.Error.handleError(error);
			})
		});
		
	    this.inherited(arguments);
	},
	
	show : function() {
	    this.exportDialog.show();
	},
	
	hide : function(){
	    this.exportDialog.hide();
	    this.exportDialog.destroyDescendants();
	},
	
	destroyDialog : function(){
	    this.exportDialog.destroyDescendants();
	},
	
	_cancelButtonOnClick : function() {
	    this.hide();
	},
	
	_exportButtonOnClick : function(){
	    this._exportData();
	    this.hide();
	},
	
	_enableButtons : function(){
	    this._changeCheckboxStates(false);
	},
	
	_disableButtons : function(){ 
	    this._changeCheckboxStates(true);
	},
	
	_changeCheckboxStates : function(disabled){
		//TODO: add all elements again after selection export is fixed
	    //var checkboxes = new Array(this.complPrHierarchyCheckbox, this.selectionCheckbox, this.allFieldsCheckbox, this.onlyCheckbox);
	    var checkboxes = new Array(this.allFieldsCheckbox, this.onlyCheckbox);
	    Util.changeWidgetsDisabledState(checkboxes, disabled);
	    
	    // change the style of the checkbox label 
		if (disabled) {
			this._con.LABEL_IDS.forEach(dojo.hitch(this, function(id) {
				dojo.addClass(id, "DisabledLabel");
			}));
		} else {
			this._con.LABEL_IDS.forEach(dojo.hitch(this, function(id) {
				dojo.removeClass(id, "DisabledLabel");
			}));
		}
//		dojo.addClass("l1", "DisabledLabel"); // EXT230: Enabling the "Complete BDR" option in GUI
//		dojo.addClass("l2", "DisabledLabel"); // EXT230: Enabling the "Selection" option in GUI
	},
	
	_enableDropdown : function(){
	    this.selectionDropdown.set("disabled", false);
	},
	
	_disableDropdown : function(){
	    this.selectionDropdown.set("disabled", true);
	},
	
	_onCancel : function() {
	    this.hide();
	},
	
	_exportData : function(){
	    exportParameters = new Object();
	    if (this.exportTableMetadataCheckbox.checked){
		
		exportParameters.exportOnlyTables = true;
		if (this.selectionCheckbox.checked){
		exportParameters.exportNodes = this._getTablesToExtract();
		}
	    
	    } else {
			// when we export only the selected items in the hierarchy
			if (this.selectionCheckbox.checked){
			    exportParameters.exportNodes = this._getNodesToExtract();
			}
			
			// when we export only special scoped fields
			if (this.onlyCheckbox.checked){
			    var scopeType = this.selectionDropdown.value;
			    switch(scopeType){
			    case "InScope" :
				exportParameters.exportMode = "IN_SCOPE_FIELDS_ONLY";
			    	break;
			    case "NotInScope" : 
				exportParameters.exportMode = "NOT_IN_SCOPE_FIELDS_ONLY";
			    	break;
			    case "FollowUp" : 
				exportParameters.exportMode = "FOLLOW_UP_FIELDS_ONLY";
			    	break;
			    }
			}
	    }
	    
	    this.parentWidget._prepareExportThread(exportParameters);
	},
	
	_getNodesToExtract : function(){
	    
	    var paths = this.parentWidget._selectedPaths;
	    var selectedNodes = "";
	    
	    dojo.forEach(paths, dojo.hitch(this, function(path){
		var selectedNode = "";
		//WI:230 : Create selectedNode dynamically based on the selected node in the BDR gui
		
		dojo.forEach(path,function(item, i){
			if(i>0)
		    selectedNode+=item.name+",";
		  });
		selectedNodes += selectedNode.substring(0, selectedNode.length-1);	
		selectedNodes += ";";
	    }));
	    
	    return selectedNodes;
	    
	},
	_getTablesToExtract : function() {
		var paths = this.parentWidget._selectedPaths;
	    var selectedNodes = "";
	    
	    dojo.forEach(paths, dojo.hitch(this, function(path){
		var selectedNode = "";
		
		dojo.forEach(path,function(item, i){
			if(i>0)
		    selectedNode=item.name;
		  });
		selectedNodes += selectedNode;	
		selectedNodes += ",";
	    }));
	    
	    return selectedNodes;
		
	}
});