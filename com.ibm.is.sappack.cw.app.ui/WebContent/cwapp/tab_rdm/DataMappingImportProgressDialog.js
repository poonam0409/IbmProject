dojo.provide("cwapp.tab_rdm.DataMappingImportProgressDialog");

//this class inherits from the abstract progress dialog
dojo.require("cwapp.tab_rdm.AbstractProgressDialog");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.DataMappingImportProgressDialog", [ dijit._Widget, dijit._Templated, cwapp.tab_rdm.AbstractProgressDialog ], {
	
	// basic widget settings
	widgetsInTemplate : true,
	templateString : dojo.cache("cwapp.tab_rdm", "templates/DataMappingImportProgressDialog.html"),
	
	// nls support
	msg : {},

	// constants
	_con : {

	},
	
	// private members
	_currentItem : null,
	_serverTimeout : 15000,
	
	_cometdTopics : {
		TABLE_STATUS_ROW_COUNT_TOPIC : "",
		TABLE_FINISHED_TOPIC : "",
		MAPPING_FINISHED_TOPIC : "",
	},
	
	_urls: {
		cancel: Services.MAPPINGDELETERESTURL,
		postData : Services.MAPPINGRESTURL,
		startThread: Services.MAPPINGTHREADSTARTURL, 
	},
	
	// public functions
	constructor : function(args) {
		if (args) {
			dojo.mixin(this, args);
		}

		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},
	
	// private functions
	_formatName : function(value) {
		if (typeof value !== "undefined") {
			if (value.length > 0) {
				var response = "";
				for(var i = 0; i < value.length; i++){
					response += value[i].Name;
					if ((i + 1) < value.length) {
						response += ", ";
					}
				}
				return response;
			}
			return "-";
		}
		return " ";
	},
	
	// setup the required infrastructure for the import status tree grid
	_setupGrid : function() {
		
		// we subscribe on the onclick event of the cancel button
		dojo.connect(this.cancelButton, "onClick", this, "_onMyCancel");
		
		// create the memory store which will hold the table import status
		var memStore = new dojo.store.Memory({
			idProperty : "TTName",
		});
		
		var structure = [
            { name: this.msg.DATAMAPNGIMPPROGDLG_2, field: "TTName", width: "auto", /*formatter: this._formatName*/ },
            { name: this.msg.DATAMAPNGIMPPROGDLG_3, field: "MAPPINGSREPORT", width: "auto", formatter: this._formatName },
           	{ name: this.msg.DATAMAPNGIMPPROGDLG_4, field: "MappedSrcValues", width: "auto", /*formatter: this._formatValues*/ },
           	{ name: this.msg.DATAMAPNGIMPPROGDLG_5, field: "SrcValues", width: "auto", /*formatter: this._formatValues*/ },
           	{ name: this.msg.STATUS,                field: "STATUS", width: "auto", formatter: dojo.hitch(this, this._displayStatusColumn) },
		];
		
		// call the abstract setup grid method and pass the structure and the store as argument
		// the abstract method will create the grid and start it
		this._abstractSetupGrid(memStore, structure);
		
		// start the import timer and initialize the cometd / bayeux protocol
		this._initCometd().then(dojo.hitch(this, function(result) {
			this._startDataMappingImport();
		}));
	},


	// subscribe to certain server topics
	_openCometd : function() {
		var rows = dojox.cometd.subscribe(this._cometdTopics.TABLE_STATUS_ROW_COUNT_TOPIC, this, "_updateTableRowCount");
		var tables = dojox.cometd.subscribe(this._cometdTopics.TABLE_FINISHED_TOPIC, this, "_updateTableStatus");
		var mappingDone = dojox.cometd.subscribe(this._cometdTopics.MAPPING_FINISHED_TOPIC, this, "_updateTableProgress");
		
		// make a deferredList, so we can wait for the result of all 2 asynch calls
		var result = new dojo.DeferredList([rows, tables, mappingDone]);
		result.addErrback(function(err) {
			this._showErrorDialog(err);
		});
		
		return result;
	},

	// setup the header title by loading a part from the NLS catalog
	_setupHeader : function() {
		var loadingImage = myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loadingAnimation.gif";
		dojox.html.set(this.dialogContentHeader.domNode, "<img src='" + loadingImage + "' style='vertical-align:middle'>&nbsp;&nbsp;&nbsp;" + this.msg.DATAMAPNGIMPPROGDLG_7);
	},
	
	_onDialogHide: function() {
		// refresh the mapping page now
		dojo.publish(Topics.RDM_REFRESH_MAPPING_PAGE_NOW);
	},
	
	// start the reference data import
	// as this is an asynchronous operation on the server at this point we'll only get the response for the starting operation
	_startDataMappingImport : function() {
		var requestData = this._startingPage.dataMappingsGrid.selection.getSelected();
		
		// start listening to topics from the cometd / bayeux
		// server we will wait here until we got the result of the subscribing
		// NOTICE: there will be no error even when there is no channel we want to listen to
		var subscribingDefList = this._openCometd();
		subscribingDefList.then(dojo.hitch(this, function() {
			this._postDataAndStartThread(requestData,{"Content-Type" : "application/json"});
		}));
	},
	
	// we received the message that a mapping import finished
	_updateTableProgress: function(message) {
		this.tableProgress.set("value", message.data.rowcount);
	},
	
	// we received a status update from the server (the table row count has been computed)
	// so we update the progress bar accordingly
	_updateTableRowCount : function(message) {
		this._currentItem = {TTName: message.data.TTName, STATUS: "Importing"};
		this._inMemStore.newItem(this._currentItem);
		this._inMemStore.save();
		
		if (message.data.COUNT == 0) {
			this.tableProgress.set("maximum", 1);
		}
		else {
			this.tableProgress.set("maximum", message.data.COUNT);
		}
		
		this._scrollGridToBottom();
		this.tableProgress.set("value", 0);
	},

	// we received a status update from the server (a table has been imported)
	// so we update the text area and the progress bar accordingly
	_updateTableStatus : function(message) {
		this._inMemStore.fetchItemByIdentity({
			identity: message.data.TTName,
			onItem: dojo.hitch(this, function(item) {
				if (item != null) {
					this._inMemStore.setValue(item,"STATUS", message.data.STATUS);
					this._inMemStore.setValue(item, "MAPPINGSREPORT", message.data.MAPPINGSREPORT);
					this._inMemStore.setValue(item, "MappedSrcValues", message.data.MappedSrcValues);
					this._inMemStore.setValue(item, "SrcValues", message.data.SrcValues);
				}
				else{
					this._inMemStore.newItem(message.data);
				}
			})
		});
		
		this._inMemStore.save();
		this.overallProgress.set("value", message.data.OVERALLPROGRESS);
		this._counter = message.data.OVERALLPROGRESS;
		this._scrollGridToBottom();
		this.tableProgress.set("value", this.tableProgress.get("maximum"));
		
		if (this.overallProgress.get("value") == this.overallProgress.get("maximum")) {
			this._progressDone(this.msg.DATAMAPNGIMPPROGDLG_8);
		}
	},
	
	_hideTooltipFailed : function(e) {
		dijit.hideTooltip(e.cellNode);
	},
	
	_onMyCancel : function (e){
		
		//we wont get the "cancelled" message from the server, because we 
		//unsubscribe from the topics immediately after the user pressed cancel
		//the status of the currentItem is still "Importing", so we set it to cancelled
		this._inMemStore.setValue(this._currentItem,"STATUS", "CANCELLED");
	},

	// tree grid cell formatting function which will
	// return a translated string for a given boolean cell value
	_displayStatusColumn : function(value, rowIndex, cell) {
		if (value == "OK") {
			return this.msg.STATUS_OK;
		}
		if (value == "DuplicateRelationMsg") {
			return this.msg.DATAMAPNGIMPPROGDLG_10;
		}
		if (value == "InvalidMappingMsg") {
			return this.msg.DATAMAPNGIMPPROGDLG_11;
		}
		if (value == "SQLExceptionMsg") {
			return this.msg.DATAMAPNGIMPPROGDLG_12;
		}
		if (value == "ConflictMappingMsg") {
			return this.msg.DATAMAPNGIMPPROGDLG_13;
		}
		if (value == "NoTranscodingtableMsg") {
			return this.msg.DATAMAPNGIMPPROGDLG_14;
		}
		if (value == "EmptyTTMsg"){
			return this.msg.DATAMAPNGIMPPROGDLG_15;
		}
		if (value == "InternalErrorMsg") {
			return this.msg.INTERNAL_ERROR;
		}
		if (value == "Importing") {
			return this.msg.DATAMAPNGIMPPROGDLG_21;
		}
		if (value == "CANCELLED") {
			return this.msg.DATAMAPNGIMPPROGDLG_22;
		}
		if (value =="RdmLoginMsg") {
			return this.msg.DATAMAPNGIMPPROGDLG_23;
		}
	}
});
