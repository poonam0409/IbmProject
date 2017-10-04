dojo.provide("cwapp.tab_rdm.referenceDataLoadWizard.ParametersPage");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("idx.dialogs");

dojo.require("dojox.grid.DataGrid");
dojo.require("dojox.data.JsonRestStore");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.referenceDataLoadWizard.ParametersPage", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm.referenceDataLoadWizard","templates/ParametersPage.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		BO_WILDCARD : "*"
	},
	
	// private members
	_parentWizard : null,
	_systemStore : null,
	_currentSelectedSapSystem : null,

	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this, args);
		}
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	postCreate : function() {
		this._setupStore();
		this._initSapSystemsTable();
		
		dojo.connect(this, "onShow", this, "_myShow");
		dojo.connect(this.sapSystemsTable, "onSelected", this, "_sapSystemSelectedInTable");
		dojo.connect(this.rolloutInput, "onChange", this, "_rolloutSelected");
		dojo.connect(this.boInput, "onChange", this, "_boSelected");

		this.inherited(arguments);
	},
	
	setParentWizard : function(wizard) {
		this._parentWizard = wizard;
	},
	
	// private functions
	
	_myShow : function() {
		this.borderContainer.resize(); // Workaround: content is not properly displayed otherwise
		// Select the first SAP system if none is selected
		if (this.sapSystemsTable.rowCount > 0 && this.sapSystemsTable.selection.selectedIndex == -1) {
			this.sapSystemsTable.selection.select(0);
		}
	},
	
	// setup REST store
	_setupStore : function() {
		this._systemStore = new dojox.data.JsonRestStore({
			target : Services.TARGETSAPSYSTEMRESTURL,
			idAttribute : Services.SYSTEMIDATTRIBUTE,
			syncMode : "true",
		});
	},
	
	// initialize the data grid by connecting it to the REST store
	_initSapSystemsTable : function() {
	    var layout = [
	    	{name: this.msg.NAME, width: '30%', field: 'legacyId', },
	    	{name: this.msg.HOST, width: '30%', field: 'sapHost', },
	    	{name: this.msg.DESCRIPTION, width: '40%', field: 'description', },
   	    ];

   	    this.sapSystemsTable.set("structure", layout);
		this.sapSystemsTable.set("store", this._systemStore);
		this.sapSystemsTable.set("sortInfo", "+1");
		this.sapSystemsTable.set("selectionMode", "single");
		this.sapSystemsTable.set("noDataMessage", this.msg.WizardParameters_NoSapSystems);
		this.sapSystemsTable.startup();
	},

	// upon selecting a SAP system in the data grid the selection
	// will be saved in a private member variable
	_sapSystemSelectedInTable : function(rowIndex) {
		var sapSystem = this.sapSystemsTable.getItem(rowIndex);
		this._currentSelectedSapSystem = sapSystem;
		this._parentWizard.setSapSystem(sapSystem);
		this._getRollouts(sapSystem);
	},
	
	_getRollouts : function(sapSystem) {
		dojo.xhrGet({
			url : Services.RDM_LOAD_GET_ROLLOUTS + "/" + sapSystem.legacyId,
			handleAs : "json",
			load : dojo.hitch(this, function(response) {
	        	this._fillRolloutValues(response);
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	_fillRolloutValues : function(valueArray) {
		this.rolloutInput.set("value", "");
		this.rolloutInput.removeOption(this.rolloutInput.getOptions());
		if (valueArray.length) {
			this.rolloutInput.set('disabled', false);
			for (var value in valueArray) {
				this.rolloutInput.addOption({name: valueArray[value], value: valueArray[value], label: valueArray[value]});
			}
		} else {
			this.rolloutInput.set('disabled', true);
			this.rolloutInput.addOption({label: this.msg.WizardParameters_NoDataForSystem});
		}
	},
	
	_rolloutSelected : function() {
		this._parentWizard.setRollout(this.rolloutInput.get("value"));
		this._getBOs();
	},
	
	_getBOs : function() {
		dojo.xhrGet({
			url : Services.RDM_LOAD_GET_BOS + "/" + this._currentSelectedSapSystem.legacyId + "/" + this.rolloutInput.get("value"),
			handleAs : "json",
			load : dojo.hitch(this, function(response) {
	        	this._fillBOValues(response);
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	_fillBOValues : function(valueArray) {
		this.boInput.set("value", "");
		this.boInput.removeOption(this.boInput.getOptions());
		if (valueArray.length) {
			this.boInput.set('disabled', false);
			this.boInput.addOption({value: this._con.BO_WILDCARD, label: this.msg.WizardParameters_AllBOs});
			for (var value in valueArray) {
				this.boInput.addOption({value: valueArray[value], label: valueArray[value]});
			}
			// We have BO values, the first one is selected, enable the "next" button
			this._parentWizard.enableNextButton();
		} else {
			this.boInput.set('disabled', true);
			this.boInput.addOption({label: this.msg.WizardParameters_NoDataForSystem});
			// We have no BO values, disable the "next" button
			this._parentWizard.disableNextButton();
		}
	},
	
	_boSelected : function() {
		this._parentWizard.setBO(this.boInput.get("value"));
	}
});
