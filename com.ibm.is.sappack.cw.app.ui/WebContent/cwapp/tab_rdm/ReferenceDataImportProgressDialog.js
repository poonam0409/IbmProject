dojo.provide("cwapp.tab_rdm.ReferenceDataImportProgressDialog");

//this class inherits from the abstract progress dialog
dojo.require("cwapp.tab_rdm.AbstractProgressDialog");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.ReferenceDataImportProgressDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin, cwapp.tab_rdm.AbstractProgressDialog ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm","templates/ReferenceDataImportProgressDialog.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		REFERENCE_TABLE_TYPE : "reftable",
		TEXT_TABLE_TYPE : "txttable",
		EMPTY_STRING : "",
		STRING_DELIM : ", ",
	},
	
	// private members
	_serverTimeout : 20000,
	_uniqueSapSystems : null,
	_currentItem : null,

	_cometdTopics : {
		TABLE_STATUS_ROW_NUMBER_TOPIC : "",
		TABLE_STATUS_ROW_COUNT_TOPIC : "",
		TABLE_STATUS_TOPIC : "",
	},
	
	_urls: {
		postData : Services.RDM_LOAD_INIT,
		startThread: Services.RDM_LOAD_START_THREAD,
		cancel: Services.RDM_LOAD_CANCEL,
	},
	
	// public functions
	constructor : function(args) {
		if(args) {
			dojo.mixin(this,args);
		}
				
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	// private functions
	
	// setup the required infrastructure for the import status data grid
	_setupGrid : function() {
		
		// create the memory store which will hold the table import status
		var memStore = new dojo.store.Memory({
			idProperty: this._idProperty
		});
		
		var	structure = [
	            {name: this.msg.TABLE, field: "name", width: "25%", styles: "font-weight: bold;"},
	            {name: this.msg.TYPE, field: "tableType", width: "15%", formatter: this._formatReferenceTableType },
	            {name: this.msg.REFRNCDATAIMPPROGDLG_8, field: "txttable", width: "25%"},
	           	{name: this.msg.REFRNCDATAIMPPROGDLG_9, field: "rowcount", width: "10%", styles: "text-align: right;", headerStyles: "text-align: left;"},
	           	{name: this.msg.STATUS, field: "status", width: "25%", formatter: "_displayStatusColumn" }
		];
		
		//call the abstract setup grid method and pass the structure and the store as argument
		//the abstract method will create the grid and start it
		this._abstractSetupGrid(memStore, structure);
		
		// start the import timer and initialize the cometd / bayeux protocol
		this._initCometd().then(dojo.hitch(this, function(result) {
			this._startReferenceDataImport();
		}));
	},
	
	// subscribe to certain server topics
	_openCometd : function() {
		var rowNumber = dojox.cometd.subscribe(this._cometdTopics.TABLE_STATUS_ROW_NUMBER_TOPIC, this, "_updateTableRowNumber");
		var rowCount = dojox.cometd.subscribe(this._cometdTopics.TABLE_STATUS_ROW_COUNT_TOPIC, this, "_updateTableRowCount");
		var tableOK = dojox.cometd.subscribe(this._cometdTopics.TABLE_STATUS_TOPIC, this, "_updateTableStatus");
		var result = new dojo.DeferredList([rowNumber, rowCount, tableOK]);
		
		result.addErrback(function(err){
			this._showErrorDialog(err);
		});
	
		return result;		
	},
	
	// setup the header title by loading a part from the NLS catalog and attaching
	// the SAP system name to it
	_setupHeader : function() {
		var sapSystem = this._startingPage.getSapSystem();
		
		// if the returned SAP system is null it means that we're getting our information
		// not from the reference data import wizard but from the reference data starting page
		if (sapSystem == null) {
			var sapSystems = [];
			
			dojo.xhrGet({
				url: Services.TARGETSAPSYSTEMRESTURL,
				handleAs: "json",
				preventCache: true,
				load: dojo.hitch(this, function(data) {
					dojo.forEach(this._startingPage.getReferenceTableStore().data, function(item, index) {
						dojo.forEach(data, function(system, sysIndex) {
							if (item.legacyId == system.legacyId) {
								sapSystems.push(system);
							}
						});
					});
					
				    this._uniqueSapSystems = Util.filterArray(sapSystems);
					var message = Util.formatMessage(this.msg.REFRNCDATAIMPPROGDLG_3, this._multipleSapSystemsToNames(this._uniqueSapSystems));
					var loadingImage = myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loadingAnimation.gif";
					dojox.html.set(this.dialogContentHeader.domNode, "<img src='" + loadingImage + "' style='vertical-align:middle'>&nbsp;&nbsp;&nbsp;" + message);
				}),
				error: function(error) {
					cwapp.main.Error.handleError(error);
				}
			});
		}
		else {
			var message = Util.formatMessage(this.msg.REFRNCDATAIMPPROGDLG_2, sapSystem.legacyId);
			var loadingImage = myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loadingAnimation.gif";
			dojox.html.set(this.dialogContentHeader.domNode, "<img src='" + loadingImage + "' style='vertical-align:middle'>&nbsp;&nbsp;&nbsp;" + message);
		}
	},
	
	_onDialogHide: function() {
		// refresh the target data page now
		dojo.publish(Topics.RDM_REFRESH_TARGET_PAGE_NOW);
		// refresh the source data page when shown
		dojo.publish(Topics.RDM_REFRESH_SOURCE_PAGE_WHEN_SHOWN);
		// refresh the mapping page when shown
		dojo.publish(Topics.RDM_REFRESH_MAPPING_PAGE_WHEN_SHOWN);
	},
	
	// start the reference data import
	// as this is an asynchronous operation on the server at this point
	// we'll only get the response for the starting operation 
	_startReferenceDataImport : function() {
		this._counter = 0;
		var subscribingDefList = this._openCometd();
		
		subscribingDefList.then(dojo.hitch(this, function() {
			var requestData = new Object();
			requestData.tables = this._startingPage.getReferenceTableStore().data;
			var sapSystem = this._startingPage.getSapSystem();		
			
			// if the returned SAP system is null it means that we're getting our information
			// not from the reference data import wizard but from the reference data starting page,
			// which means we will be reloading previously loaded tables
			if (sapSystem == null) {
				requestData.legacySystems = this._uniqueSapSystems;
				requestData.rollout = "";
				requestData.reload = true;
			}
			else {
				requestData.legacySystems = Util.cloneLegacySystem(sapSystem);
				requestData.rollout = this._startingPage.getRollout();
				requestData.reload = false;
			}
			
			this._postDataAndStartThread(requestData);
		}));
	},
	
	// we received a row number update from the server (the current row of the table has changed)
	// so we update the progress bar accordingly
	_updateTableRowNumber : function(message) {
		this.tableProgress.set("value", message.data.rowcount);
	},
	
	// we received a status update from the server (the table row count has been computed)
	// so we update the progress bar accordingly
	_updateTableRowCount : function(message) {	
		if (message.data.rowcount == 0) {
			this.tableProgress.set("maximum", 1);
		}
		else {
			this.tableProgress.set("maximum", message.data.rowcount);
		}
		this.tableProgress.set("value", 0);	
	},
	
	// we received a status update from the server (a table has been imported) so we
	// update the data grid and the progress bar accordingly
	_updateTableStatus : function(message) {
		
		// look for the item in the data grid store
		this._inMemStore.fetchItemByIdentity({
			identity: message.data[this._idProperty],
			onItem: dojo.hitch(this, function(item) {
				if(item != null){
					this._inMemStore.setValue(item, "status", message.data.status);
					this._inMemStore.setValue(item, "rowcount", message.data.rowcount);
				}
				else{
					
					// we are in this method also when a table just started
					// in this case the item is always null
					// the reason to use just one method is
					// that there can be problems when two callback methods are
					// trying to write in the store
					this._inMemStore.newItem(message.data);
					this.tableProgress.set("value", 0);
				}
			})
		});
		
		this._inMemStore.save();
		this._counter = message.data.number;
		this._scrollGridToBottom();
		this.overallProgress.set("value", message.data.number);
		
		if (this.overallProgress.get("value") == this.overallProgress.get("maximum")){
			this.tableProgress.set("value", this.tableProgress.get("maximum"));
			this._tableImportDone();
		}
	},
	
	// we received a topic that said 'done' so the table import was finished
	_tableImportDone : function() {
		
		// update the header title
		var sapSystem = this._startingPage.getSapSystem();
		
		// if the returned SAP system is null it means that we're getting our information
		// not from the reference data import wizard but from the reference data starting page
		var message;
		
		if (sapSystem == null) {
			 message = Util.formatMessage(this.msg.REFRNCDATAIMPPROGDLG_5, this._multipleSapSystemsToNames(this._uniqueSapSystems));
		}
		else {
			 message = Util.formatMessage(this.msg.REFRNCDATAIMPPROGDLG_4, sapSystem.legacyId);
		}
		
		// call the done method in the abstract class
		this._progressDone(message);
	},
	
	// data grid cell formatting function which will
	// return a translated string for a given integer cell value
	_displayStatusColumn : function(value, rowIndex, cell) {
		switch(value) {
		case 0:
			return this.msg.REFRNCDATAIMPPROGDLG_11;
		case 1:
			return this.msg.STATUS_OK;
		case 2:
			return this.msg.REFRNCDATAIMPPROGDLG_13;
		case 3:
			return this.msg.REFRNCDATAIMPPROGDLG_19;
		default:
			return this.msg.INTERNAL_ERROR;
		}
	},
	
	// extract the names of SAP systems from an array and return it a delimited string
	_multipleSapSystemsToNames : function(sapSystemArray) {
		var systemIds = this._con.EMPTY_STRING;
		
	    dojo.forEach(sapSystemArray, dojo.hitch(this, function(item, i) {
	    	if (systemIds != this._con.EMPTY_STRING) {
	    		systemIds = systemIds + this._con.STRING_DELIM + item.legacyId;
	    	}
	    	else {
	    		systemIds = item.legacyId;
	    	}
	    }));
	    
	    return systemIds;
	},

	_formatReferenceTableType : function(value, rowIndex, cell) {
		return Util.formatReferenceTableType(value, this.msg);
	}
});
