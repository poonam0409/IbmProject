dojo.provide("cwapp.tab_bdr.bph.FieldsFromSAPImportProgressDialog");

//this class inherits from the abstract progress dialog
dojo.require("cwapp.tab_rdm.AbstractProgressDialog");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.FieldsFromSAPImportProgressDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin, cwapp.tab_rdm.AbstractProgressDialog ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph","templates/FieldsFromSAPImportProgressDialog.html"),
	widgetsInTemplate : true,
	caller : null,
	_sapSystem : null,
	
	setSapSystem : function(sapSystem){
		this._sapSystem = sapSystem;
	},
	
	// nls support
	msg : {},
	
	// private members
	_serverTimeout : 20000,
	_uniqueSapSystems : null,
	_currentItem : null,

	// private members
	_cometdTopics : {
		TOPIC_TABLE_STATUS_UPDATE : CometD.TOPIC_TABLE_STATUS_UPDATE,
		TOPIC_TABLE_LOAD_UPDATE : CometD.TOPIC_TABLE_LOAD_UPDATE
	},
	
	_urls: {
		postData : Services.BDR_FIELD_IMPORT,
		startThread: Services.BDR_FIELD_IMPORT_START,
		cancel: Services.BDR_FIELD_CANCEL
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
			idProperty: "name"
		});
		
		var	structure = [
	            {name: this.msg.TABLE, field: "name", width: "33%", styles: "font-weight: bold;"},
	           	{name: this.msg.FIELDS_ADDED, field: "rows", width: "33%"},
	            {name: this.msg.STATUS, field: "status", width: "33%", formatter: "_displayStatusColumn" }
		];
		
		//call the abstract setup grid method and pass the structure and the store as argument
		//the abstract method will create the grid and start it
		this._abstractSetupGrid(memStore, structure);
		
		// start the import timer and initialize the cometd / bayeux protocol
		this._initCometd().then(dojo.hitch(this, function(result) {
			this._startFieldsFromSapImport();
		}));
	},
	
	// subscribe to certain server topics
	_openCometd : function() {
		var tableUpdate = dojox.cometd.subscribe(this._cometdTopics.TOPIC_TABLE_STATUS_UPDATE, this, "_updateTableStatus"); 
		var tableLoad = dojox.cometd.subscribe(this._cometdTopics.TOPIC_TABLE_LOAD_UPDATE, this, "_tableLoadStatusUpdate");

		var deferreds = new dojo.DeferredList([tableUpdate, tableLoad]);
		
		deferreds.addErrback(function(err){
			this._showErrorDialog(err);
		});	
		return deferreds;
	},
	
	// setup the header title with the chosen SAP system
	_setupHeader : function() {
		var sapSystem = this._sapSystem;
		var message = Util.formatMessage(this.msg.FIELDSIMPORTDIALOG_6, sapSystem.legacyId);
		var loadingImage = myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loadingAnimation.gif";
		dojox.html.set(this.dialogContentHeader.domNode, "<img src='" + loadingImage + "' style='vertical-align:middle'>&nbsp;&nbsp;&nbsp;" + message);
	},
	
	_onDialogHide: function() {
		// refresh the target data page now
		dojo.publish(Topics.RDM_REFRESH_TARGET_PAGE_NOW);
		// refresh the source data page when shown
		dojo.publish(Topics.RDM_REFRESH_SOURCE_PAGE_WHEN_SHOWN);
		// refresh the mapping page when shown
		dojo.publish(Topics.RDM_REFRESH_MAPPING_PAGE_WHEN_SHOWN);
		//refresh the detailspanel
		switch(this.caller){
		case BdrTypes.TABLE:
			dojo.publish(Topics.REFRESH_DETAILSPANEL_TABLES);
			break;
		case BdrTypes.BO:
			dojo.publish(Topics.REFRESH_DETAILSPANEL_BO);
			break;
		}
		
		

	},
	
	// start the reference data import
	// as this is an asynchronous operation on the server at this point
	// we'll only get the response for the starting operation 
	_startFieldsFromSapImport : function() {
		this._counter = 0;
		var subscribingDefList = this._openCometd();
		
		subscribingDefList.then(dojo.hitch(this, function() {
			
			var requestData = new Object();
			
			var tables = new Array(); 
			dojo.forEach(this._startingPage.getSelectedTables(), function(table, index) {
				tables.push(table);
			});
			requestData.tables = tables;
			requestData.sapSystem = Util.cloneLegacySystem(this._sapSystem);
			
			this._postDataAndStartThread(requestData);
		}));
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
			identity: message.data["name"],
			onItem: dojo.hitch(this, function(item) {
				if(item != null){
					this._inMemStore.setValue(item, "status", message.data.status);
					this._inMemStore.setValue(item, "rows", message.data.rows);
				}
				else{
					
					// we are in this method also when a table just started
					// in this case the item is always null
					// the reason to use just one method is
					// that there can be problems when two callback methods are
					// trying to write in the store
					message.data.rows = "";
					this._inMemStore.newItem(message.data);
					this.tableProgress.set("value", 0);
				}
			})
		});
		
		this._inMemStore.save();
		this._counter = message.data.number;
		this._scrollGridToBottom();
		this.overallProgress.set("maximum", message.data.maximum);
		this.overallProgress.set("value", message.data.progress);
		
		if (this.overallProgress.get("value") == this.overallProgress.get("maximum")){
			this.tableProgress.set("value", this.tableProgress.get("maximum"));
			this._tableImportDone();
		}
	},
	
	_tableLoadStatusUpdate : function(message) {
//		we received a topic with load information, so we need to refresh the table load status
		
		this.tableProgress.set("maximum", message.data.maximum);
		this.tableProgress.set("value", message.data.progress);
	},
	
	// we received a topic that said 'done' so the table import was finished
	_tableImportDone : function() {
		var message;
		message = Util.formatMessage(this.msg.FIELDSIMPORTDIALOG_8, this._sapSystem.legacyId);
		
		// call the done method in the abstract class
		this._progressDone(message);
	},
	
	// data grid cell formatting function which will
	// return a translated string for a given integer cell value
	_displayStatusColumn : function(value, rowIndex, cell) {
		switch(value) {
		case 0:
			return this.msg.FIELDSIMPORTDIALOG_1;
		case 1:
			return this.msg.STATUS_OK;
		case 2:
			return this.msg.FIELDSIMPORTDIALOG_3;
		case 3:
			return this.msg.FIELDSIMPORTDIALOG_4;
		default:
			return this.msg.INTERNAL_ERROR;
		}
	}
});
