dojo.provide("cwapp.tab_rdm.SourceReferenceDataPage");

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
dojo.require("cwapp.tab_rdm.SourceReferenceDataExportProgressDialog");
dojo.require("cwapp.tab_rdm.DataMappingsCreationProgressDialog");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.SourceReferenceDataPage", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm","templates/SourceReferenceDataPage.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		YELLOW_FLAG : '<img src="cwapp/img/state_acceptable_16.png" class="prefix" title="{0}" width="12px" height="12px" border="0px" align="left"/>',
		TRANSPARENT_FLAG : '<img src="cwapp/img/transparent.png" class="prefix" width="12px" height="12px" border="0px" align="left"/>'
	},
	
	// private members
	_inMemStore : null,
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
		dojo.connect(this.selectAllButton, "onClick", this, "_selectAllRows");
		dojo.connect(this.deselectAllButton, "onClick", this, "_deselectAllRows");
		dojo.connect(this.refTableGrid, "onSelected", this, "_updateButtons");
		dojo.connect(this.refTableGrid, "onDeselected", this, "_updateButtons");
		dojo.connect(this.refreshAction, "onClick", this, "_refreshGrid");
		dojo.connect(this.exportButton, "onClick", this, "_prepareExport");
		dojo.connect(window, "onresize", this, "_resizeGrid");
		dojo.connect(this.createInitialMappingsButton, "onClick", this, "_prepareCreateMappings");
		
		// subscribe to "refresh now" topic
		dojo.subscribe(Topics.RDM_REFRESH_SOURCE_PAGE_NOW, dojo.hitch(this, this._refreshGrid));
		// subscribe to "refresh when shown" topic
		dojo.subscribe(Topics.RDM_REFRESH_SOURCE_PAGE_WHEN_SHOWN, dojo.hitch(this, this._triggerRefresh));
		
		this._setupStore();
		this._setupGrid();
		
		this.inherited(arguments);
	},
	
	// private functions
	
	// initialize the in-memory store which will serve as the data store
	// for the reference table data grid
	_setupStore : function() {
		this._inMemStore = new dojo.store.Memory({
			idProperty: "uid"
		});
	},
	
	// setup the reference table data grid
	_setupGrid : function() {
	    var layout = [
  	    	{name: this.msg.SRCREFRNCDATAPG_7,  width:'10%', field:'legacyIdDescription', styles:'font-weight: bold;', headerStyles:'font-weight: normal;'},
  	    	{name: this.msg.SRCREFRNCDATAPG_8,  width:'15%', field:'refTable', styles:'font-weight: bold;', headerStyles:'font-weight: normal;', formatter: this._formatTableName},
  	    	{name: this.msg.TYPE,  				width:'5%',  field:'tableType', formatter: this._formatReferenceTableType},
  	    	{name: this.msg.DESCRIPTION, 		width:'20%', field:'desc'},
  	    	{name: this.msg.SRCREFRNCDATAPG_11, width:'15%', field:'sourceSet'},
  	    	{name: this.msg.SRCREFRNCDATAPG_12, width:'15%', field:'targetSet'},
  	    	{name: this.msg.SRCREFRNCDATAPG_13, width:'15%', field:'mapping'},
   	    ];
	      	    
	    this.refTableGrid.set("structure", layout);
		this.refTableGrid.set("store", new dojo.data.ObjectStore({objectStore: this._inMemStore}));
	    this.refTableGrid.set("editable", true);
		this.refTableGrid.set("formatterScope", this);
		this.refTableGrid.set("sortInfo", "+2");
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
			url: Services.SOURCE_DATA,
			handleAs: "json",
			preventCache: true,
			load: dojo.hitch(this, function(tableData) {
				if (!tableData || tableData.length == 0) {
					this._checkForSourceSystems();
				}
				this._inMemStore.setData(tableData);
			    this.refTableGrid.render();
	    		this._updateButtons();
	    		
			    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.SRCREFRNCDATAPG_14, tableData.length));
			}),
			error: function(error) {
				cwapp.main.Error.handleError(error);
			}
		});
	},
	
	// If we receive no data, check if there are any source systems and change the "no data" message if necessary
	_checkForSourceSystems : function() {
		dojo.xhrGet({
			url: Services.SOURCESYSTEMRESTURL,
			handleAs: "json",
			preventCache: true,
			sync: true,
			load: dojo.hitch(this, function(data) {
				if (!data || data.length == 0) {
					this.refTableGrid.set("noDataMessage", "<span class=\"dojoxGridNoData\">" + this.msg.SOURCESYSTEMSPG_38 + "</span>");
				} else {
					this.refTableGrid.set("noDataMessage", "<span class=\"dojoxGridNoData\">" + this.msg.RDMPageEmpty + "</span>");
				}
				this.refTableGrid.render();
			}),
			error: function(error) {
				cwapp.main.Error.handleError(error);
			}
		});
	},
	
	// Called using a Dojo topic when the data changes
	_triggerRefresh : function() {
		this._refreshNeeded = true;
	},
	
	// resize the page
	_resizeGrid : function() {
		var def = new dojo.Deferred();
		setTimeout(function() {def.resolve({called: true});}, 300);
		def.then(dojo.hitch(this, function() {
			this.borderContainer.resize();
		}));
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
				
			    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.SRCREFRNCDATAPG_15, items.length, this.refTableGrid.selection.getSelected().length));				
			})
		});
	},
	
	// deselect all items / rows in the data grid
	_deselectAllRows : function() {
		this.refTableGrid.selection.deselectAll();
		this._updateButtons();
		
		this.refTableGrid.store.fetch({
			onComplete : dojo.hitch(this, function(items) {
			    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.SRCREFRNCDATAPG_14, items.length));				
			})
		});
	},
	
	// see if any row is selected and update button states and status bar
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
		    	    this.exportButton.set("disabled", false);
		    	    this.createInitialMappingsButton.set("disabled", false);
		    	}
		    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.SRCREFRNCDATAPG_15, numOfTables, this.refTableGrid.selection.getSelected().length));				
		} else {
			this.exportButton.set("disabled", true);
			this.createInitialMappingsButton.set("disabled", true);
			
		    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.SRCREFRNCDATAPG_14, numOfTables));							
		}
	},

	_getSelection: function() {
		var request = [];
		dojo.forEach(this.refTableGrid.selection.getSelected(), function(item, index) {
			request.push({legacyId:item.legacyIdName, legacyIdDescription:item.legacyIdDescription, referenceTableId:item.referenceTableId});
		});
		return request;
	},

	// Callback for the "Export to RDM Hub" button
	_prepareExport: function() {
		Util.prepareRDM(this, dojo.hitch(this, this._export));
	},
	
	// Actual RDM Hub export function. Opens the progress dialog.
	_export : function() {
		var exportProgressDialog = new cwapp.tab_rdm.SourceReferenceDataExportProgressDialog();
		exportProgressDialog.setStartingPage(this);
		exportProgressDialog.start();
	},
	
	// Callback for the Create Mappings button
	_prepareCreateMappings: function() {
		Util.prepareRDM(this, dojo.hitch(this, this._createInitialMappings));
	},
	
	// Actual Create Mappings function. Opens the progress dialog.
	_createInitialMappings : function(){
		var creationDialog = new cwapp.tab_rdm.DataMappingsCreationProgressDialog();
		creationDialog.setStartingPage(this);
		creationDialog.start();
	},

	_formatTableName: function(value, rowIndex, cell) {
		var cellHtml = '';
		var item = this.refTableGrid.getItem(rowIndex);

		if(item.noRules != null){
			cellHtml = Util.formatMessage(this._con.YELLOW_FLAG, this.msg.SourceReferenceDataPage_NoRulesWarning);
		} else {
			cellHtml = this._con.TRANSPARENT_FLAG;
		}
		cellHtml += item.refTable;
		
		return cellHtml;
	},
	
	_formatReferenceTableType : function(value, rowIndex, cell) {
		return Util.formatReferenceTableType(value, this.msg);
	}
});
