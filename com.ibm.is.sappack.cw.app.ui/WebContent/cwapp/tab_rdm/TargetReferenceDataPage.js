dojo.provide("cwapp.tab_rdm.TargetReferenceDataPage");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("idx.layout.HeaderPane");
dojo.require("idx.layout.ButtonBar");
dojo.require("idx.layout.BorderContainer");

dojo.require("idx.form.Link");

dojo.require("dijit.form.Button");
dojo.require("dijit.layout.ContentPane");

dojo.require("dojox.grid.DataGrid");

dojo.require("dojo.store.Memory");
dojo.require("dojo.data.ObjectStore");

dojo.require("cwapp.main.Error");
dojo.require("cwapp.tab_config.RdmPasswordDialog");
dojo.require("cwapp.tab_config.SapSystemPasswordDialog");
dojo.require("cwapp.tab_rdm.ReferenceDataImportProgressDialog");
dojo.require("cwapp.tab_rdm.ReferenceDataExportProgressDialog");
dojo.require("cwapp.tab_rdm.ReferenceDataEraseDialog");

dojo.require("cwapp.tab_rdm.ReferenceDataLoadWizard");

dojo.require("dojox.cometd");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.TargetReferenceDataPage", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm","templates/TargetReferenceDataPage.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		ID_PROPERTY : "referenceTableId",
		CHECKMARK_IMG :	'<img src="cwapp/img/checkmark.png" width="13px" height="13px" alt="selected" border="0px" style="vertical-align:middle"/>',
		YELLOW_FLAG : '<img src="cwapp/img/state_acceptable_16.png" class="prefix" title="{0}" width="12px" height="12px" border="0px" align="left"/>',
		TRANSPARENT_FLAG : '<img src="cwapp/img/transparent.png" class="prefix" width="12px" height="12px" border="0px" align="left"/>'
	},
	
	// private members
	_inMemStore : null,
	_referenceDataImportDialog : null,
	_sapSystemPasswordDialog : null,
	_sapSystemsUniqueArray : null,
	_refreshNeeded: true,

	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},
	
	postCreate : function() {
		dojo.connect(this, "onShow", this, "_myShow");
		dojo.connect(this.loadButton, "onClick", this, "_showWizard");
		dojo.connect(this.reloadButton, "onClick", this, "_reloadButtonClicked");
		//dojo.connect(this.exportButton, "onClick", this, "_prepareExport");
		dojo.connect(this.eraseButton, "onClick", this, "_eraseReferenceData");
		dojo.connect(this.selectAllButton, "onClick", this, "_selectAllRows");
		dojo.connect(this.deselectAllButton, "onClick", this, "_deselectAllRows");
		dojo.connect(this.refTableGrid, "onSelected", this, "_updateButtons");
		dojo.connect(this.refTableGrid, "onDeselected", this, "_updateButtons");
		dojo.connect(this.refreshAction, "onClick", this, "_refreshGrid");
		dojo.connect(window, "onresize", this, "_resizeGrid");
		
		// subscribe to "refresh now" topic
		dojo.subscribe(Topics.RDM_REFRESH_TARGET_PAGE_NOW, dojo.hitch(this, this._refreshGrid));
		
		dojo.subscribe(Topics.RUNSAPSYSTEMPASSWORDCHECK, dojo.hitch(this, this._runSapSystemPasswordCheck));
		dojo.subscribe(Topics.SAPSYSTEMPASSWORDCHECKDONE, dojo.hitch(this, this._sapSystemPasswordCheckDone));
		
		this._setupStore();
		this._setupGrid();
		
		if (General.ISREADONLY){
		    this.loadButton.set("disabled", true);
		}
		
		this.inherited(arguments);
	},
	
	getSapSystem : function() {
		return null;
	},
	
	getReferenceTableStore: function() {
		var selectedItemsForReload = [];
		
		dojo.forEach(this.refTableGrid.selection.getSelected(), function(item, index) {
			selectedItemsForReload.push(item);
		});
				
		return new Object({data: selectedItemsForReload});
	},
	
	// private functions
	
	// initialize the in-memory store which will serve as the data store
	// for the reference table data grid
	_setupStore : function() {
		this._inMemStore = new dojo.store.Memory({
			idProperty: "referenceTableId" 
		});
	},
	
	// setup the reference table data grid
	_setupGrid : function() {
	    var layout = [
	    	{name: this.msg.TGTREFRNCDATAPG_9,  width: '20%', field: 'name', styles: 'font-weight: bold;', headerStyles: 'font-weight: normal;'},						
  	    	{name: this.msg.TYPE,  				width: '10%', field: 'tableType', formatter: this._formatReferenceTableType},
	    	{name: this.msg.TGTREFRNCDATAPG_11,	width: '5%',  field: 'rowCount', styles: 'text-align: right;', headerStyles: 'text-align: left;'},
	    	{name: this.msg.DESCRIPTION,        width: '20%', field: 'description'},
	    	{name: this.msg.TGTREFRNCDATAPG_13,	width: '10%', field: 'lastLoad', formatter: Util.formatDate},
	    	{name: this.msg.TGTREFRNCDATAPG_14, width: '10%', field: 'legacyId'},
	    	{name: this.msg.TGTREFRNCDATAPG_15,	width: '25%', field: 'targetRdmSetName', formatter: this._formatRdmSetStatus},
   	    ];
     	    
   	    this.refTableGrid.set("structure", layout);
		this.refTableGrid.set("store", new dojo.data.ObjectStore({objectStore: this._inMemStore}));
	    this.refTableGrid.set("editable", true);
		this.refTableGrid.set("formatterScope", this);
		this.refTableGrid.set("sortInfo", "+1");
		this.refTableGrid.set("noDataMessage", "<span class=\"dojoxGridNoData\">" + this.msg.RDMPageEmpty + "</span>");
		this.refTableGrid.startup();
	},
	
	_myShow : function() {
		// workaround for initial display bug of idx.layout.HeaderPane
		this.borderContainer.resize();
		if (this._refreshNeeded) {
			this._refreshGrid();
			this._refreshNeeded = false;
		}
	},

	// Refreshes the data.
	// Also, callback for the Refresh button/link.
	_refreshGrid : function() {
		this._deselectAllRows();
		
		dojo.xhrGet({
			url: Services.REFTABLERESTURL,
			handleAs: "json",
			preventCache: true,
			load: dojo.hitch(this, function(tableData) {
				this._inMemStore.setData(tableData);
			    this.refTableGrid.render();
	    		this._updateButtons();
	    		
			    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.TGTREFRNCDATAPG_16, tableData.length));
			}),
			error: function(error) {
				cwapp.main.Error.handleError(error);
			}
		});
	},
	
	// resize the page
	_resizeGrid : function() {
		var def = new dojo.Deferred();
		setTimeout(function() {def.resolve({called: true});}, 300);
		def.then(dojo.hitch(this, function() {
			this.borderContainer.resize();
		}));
	},
	
	// see if any row is selected and update button states
	_updateButtons: function() {
		var numOfTables = 0;
		
		if (this.refTableGrid.store != null) {
			this.refTableGrid.store.fetch({
				onComplete : dojo.hitch(this, function(items) {
					numOfTables = items.length;
				})
			});
		}
		
		if (this.refTableGrid.selection.getSelected().length > 0) {
		    if(!General.ISREADONLY){					 
			this.reloadButton.set("disabled", false);
			//this.exportButton.set("disabled", false);
			this.eraseButton.set("disabled", false);
		    }
			
		    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.TGTREFRNCDATAPG_17, numOfTables, this.refTableGrid.selection.getSelected().length));				
		} else {
			this.reloadButton.set("disabled", true);
			//this.exportButton.set("disabled", true);
			this.eraseButton.set("disabled", true);
			
		    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.TGTREFRNCDATAPG_16, numOfTables));							
		}
	},
	
	// select all items / rows in the data grid
	_selectAllRows : function() {
		this.refTableGrid.store.fetch({
			onComplete : dojo.hitch(this, function(items) {
				for (var i = 0; i < items.length; i++) {
					this.refTableGrid.selection.addToSelection(i);
				}
				this.refTableGrid.update();
				this._updateButtons();
				
			    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.TGTREFRNCDATAPG_17, items.length, this.refTableGrid.selection.getSelected().length));				
			})
		});
	},
	
	// deselect all items / rows in the data grid
	_deselectAllRows : function() {
		this.refTableGrid.selection.deselectAll();
		this._updateButtons();
		
		this.refTableGrid.store.fetch({
			onComplete : dojo.hitch(this, function(items) {
			    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.TGTREFRNCDATAPG_16, items.length));				
			})
		});
	},
	
	_getSelection: function() {
		var request = [];
		dojo.forEach(this.refTableGrid.selection.getSelected(), function(item, index) {
			request.push(item.referenceTableId);
		});
		return request;
	},
	
	// launch the reference data import wizard
	_showWizard: function() {
		this._referenceDataImportDialog = new cwapp.tab_rdm.ReferenceDataLoadWizard();
		this._referenceDataImportDialog.startup();
		this._referenceDataImportDialog.show();
	},
	
	// Callback for the "Export to RDM Hub" button
	_prepareExport: function() {
		Util.prepareRDM(this, dojo.hitch(this, this._export));
	},
	
	// Actual RDM Hub export function. Opens the progress dialog.
	_export : function() {
		var exportProgressDialog = new cwapp.tab_rdm.ReferenceDataExportProgressDialog();
		exportProgressDialog.start();
		exportProgressDialog.setStartingPage(this);
	},
	
	_eraseReferenceData : function() {
		var eraseDialog = new cwapp.tab_rdm.ReferenceDataEraseDialog();
		eraseDialog.start(this._getSelection());
	},
	
	// start the reference data reload
	_reloadButtonClicked : function() {		
		var sapSystems = [];
		
		// we'll need to get a list of SAP systems for every selected
		// reference table (the ones that should be reloaded)
		dojo.xhrGet({
			url: Services.TARGETSAPSYSTEMRESTURL,
			handleAs: "json",
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				dojo.forEach(this.refTableGrid.selection.getSelected(), function(item, index) {
					dojo.forEach(data, function(system, sysIndex) {
						if (item.legacyId == system.legacyId) {
							sapSystems.push(system);
						}
					});
				});
				
			    // we filter the array of SAP systems for unique items and
			    // store the newly created array
			    this._sapSystemsUniqueArray = Util.filterArray(sapSystems);
			    
			    // see if the passwords for the SAP systems exist and prompt for them if necessary
			    this._runSapSystemPasswordCheck();				
			}),
			error: function(error) {
				cwapp.main.Error.handleError(error);
			}
		});
	},
	
	// run a SAP system password check
	_runSapSystemPasswordCheck : function() {
		var item = this._sapSystemsUniqueArray.pop();
		
		if (item != null) {
			var deferred = this._handleSapSystemPassword(item);
			
			deferred.then(dojo.hitch(this, function(success) {
				if (success) {
					
					// as the display of the SAP password dialog takes some time (fade-in / fade-out)
					// we simply delay the execution of the next check for a few milliseconds
					setTimeout('dojo.publish("' + Topics.RUNSAPSYSTEMPASSWORDCHECK + '")', 500);
				}
			}), dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
		    }));
		} else {
			dojo.publish(Topics.SAPSYSTEMPASSWORDCHECKDONE);
		}
	},
	
	// the SAP system password check has finished
	_sapSystemPasswordCheckDone : function() {
		var progressDialog = new cwapp.tab_rdm.ReferenceDataImportProgressDialog({_idProperty : this._con.ID_PROPERTY});
		progressDialog.setStartingPage(this);
		progressDialog.startup();
		progressDialog.start();
	},

	// gather the password for a given SAP system
	// this function is asynchronous therefore we return a dojo.Deferred
	_handleSapSystemPassword : function(sapSystem) {
		if (!this._sapSystemPasswordDialog) {
			this._sapSystemPasswordDialog = new cwapp.tab_config.SapSystemPasswordDialog();
		}
		return this._sapSystemPasswordDialog.getPassword(sapSystem);
	},
	
	_formatRdmSetStatus: function(value, rowIndex, cell) {
		var imageString = '';
		var item = this.refTableGrid.getItem(rowIndex);

		if(item.targetRdmSet != null){
			var condition = item.targetRdmSet.uptodate;
			
			if(!condition){
				imageString =  Util.formatMessage(this._con.YELLOW_FLAG, this.msg.TGTREFRNCDATAPG_18);
			}
			else{
				imageString = this._con.TRANSPARENT_FLAG;
			}
			imageString += item.targetRdmSet.name;
		}
		
		return imageString;
	},
	
	_formatReferenceTableType : function(value, rowIndex, cell) {
		return Util.formatReferenceTableType(value, this.msg);
	}
});
