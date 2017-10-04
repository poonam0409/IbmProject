dojo.provide("cwapp.tab_rdm.ReferenceDataExportProgressDialog");

//this class inherits from the abstract progress dialog
dojo.require("cwapp.tab_rdm.AbstractProgressDialog");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.ReferenceDataExportProgressDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin, cwapp.tab_rdm.AbstractProgressDialog ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm","templates/ReferenceDataExportProgressDialog.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	_cometdTopics : {
		TOPIC_EXPORT_ROW_COUNT : "",
		TOPIC_EXPORT_ROW_PROGRESS : "",
		TOPIC_EXPORT_TABLE_STATUS : "",
	},
	
	_urls: {
		cancel: Services.EXPORT_CANCEL_URL,
		postData : Services.EXPORT_URL,
		startThread: Services.EXPORT_STARTTHREAD, 
	},
	
	// private members
	_serverTimeout: 20000,
	_currentItem: null,

	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}
				
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	// private functions
		
	// setup the required infrastructure for the export status tree grid
	_setupGrid : function() {		

		// create the memory store which will hold the table export status
		var memStore = new dojo.store.Memory({
			idProperty: "number"
		});

		// as the memory store itself is not compatible with the dojo.data API we need to
		// wrap it into an object store
		var structure = [
			{name: this.msg.TABLE, field: "name", width: "25%", styles: "font-weight: bold;"},
			{name: this.msg.TYPE, field: "tableType", width: "15%", formatter: this._formatReferenceTableType},
			{name: this.msg.REFRNCDATAEXPPROGDLG_6, field: "rowcount", width: "15%", styles: "text-align: right;", headerStyles: "text-align: left;"},
			{name: this.msg.STATUS, field: "status", width: "35%", formatter: "_statusColumnFormatter"}
		];

		//call the abstract setup grid method and pass the structure and the store as argument
		//the abstract method will create the grid and start it
		this._abstractSetupGrid(memStore, structure);
		
		// start the import timer and initialize the cometd / bayeux protocol
		this._initCometd().then(dojo.hitch(this, function(result) {
			this._startReferenceDataExport();
		}));
	},
	
	// subscribe to server topics
	_openCometd : function() {
		var rowCount = dojox.cometd.subscribe(this._cometdTopics.TOPIC_EXPORT_ROW_COUNT, this, "_addNewRow");
		var rowProgress =  dojox.cometd.subscribe(this._cometdTopics.TOPIC_EXPORT_ROW_PROGRESS, this, "_updateTableRowProgress");
		var tableStatus = dojox.cometd.subscribe(this._cometdTopics.TOPIC_EXPORT_TABLE_STATUS, this, "_updateTableStatus");
		var result = new dojo.DeferredList([rowCount, rowProgress, tableStatus]);
		
		result.addErrback(function(err){
			this._showErrorDialog(error);
		});
		
		return result;
	},
	
	// setup the header title
	_setupHeader : function() {
		var loadingImage = myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loadingAnimation.gif";
		dojox.html.set(this.dialogContentHeader.domNode, "<img src='" + loadingImage + "' style='vertical-align:middle'>&nbsp;&nbsp;&nbsp;" + this.msg.REFRNCDATAEXPPROGDLG_2);
	},
	
	_onDialogHide : function() {
		// refresh the target data page now
		dojo.publish(Topics.RDM_REFRESH_TARGET_PAGE_NOW);
		// refresh the source data page when shown
		dojo.publish(Topics.RDM_REFRESH_SOURCE_PAGE_WHEN_SHOWN);
	},
	
	// start the reference data export
	// as this is an asynchronous operation on the server at this point
	// we'll only get the response for the starting operation 
	_startReferenceDataExport : function() {
		var openCometDDefList = this._openCometd();
		openCometDDefList.then(dojo.hitch(this, function(){
			this._postDataAndStartThread(this._startingPage._getSelection(),{"Content-Type" : "application/json"});
		}));
	},
	
	// received a new row from the server (including the row count)
	// update the progress bar and table and set bar maximum
	_addNewRow : function(message) {
	
		// add new row
		message.data.status = CometD.EXPORT_STATUS_IN_PROGRESS;
		this._inMemStore.newItem(message.data);
		this._inMemStore.save();
		this._scrollGridToBottom();
		
		if (message.data.rowcount == 0) {
			this.tableProgress.set("maximum", 1);
		}
		else {
			this.tableProgress.set("maximum", message.data.maxProgressCount);
		}
		
		this.tableProgress.set("value", 0);
	},
	
	// received a row progress update from the server
	// update the progress bar
	_updateTableRowProgress : function(message) {
		this.tableProgress.set("value", message.data.rowcount);
		this._scrollGridToBottom();
	},
	
	// received a table status update from the server
	// update the progress table and bar
	_updateTableStatus : function(message) {
		
		// look for the item in the data grid store
		this._inMemStore.fetchItemByIdentity({
			identity: message.data.number,
			onItem: dojo.hitch(this, function(item) {
				
				if (item == null) {
					return;
				}
				
				this._inMemStore.setValue(item, "status", message.data.status);
				this._inMemStore.save();
				
				this.tableProgress.set("value", this.tableProgress.get("maximum"));
				this.overallProgress.set("value", message.data.number);
				
				// see if we are finished
				if (this.overallProgress.get("value") == this.overallProgress.get("maximum")) {
					this._progressDone(this.msg.REFRNCDATAEXPPROGDLG_3);
				}
				
				this._counter = message.data.number;
			})
		});
	},
	
	// grid cell formatting function for the table export status
	// translates an integer status value into a string
	_statusColumnFormatter : function(value, rowIndex, cell) {
		if (value == CometD.EXPORT_STATUS_OK) {
			return this.msg.STATUS_OK;
		}
		else if (value == CometD.EXPORT_STATUS_INTERNAL_ERROR) {
			return this.msg.REFRNCDATAIMPPROGDLG_13;
		}
		else if (value == CometD.EXPORT_STATUS_TABLE_NOT_FOUND) {
			return this.msg.REFRNCDATAEXPPROGDLG_10;
		}
		else if (value == CometD.EXPORT_STATUS_IN_PROGRESS) {
			return this.msg.REFRNCDATAEXPPROGDLG_11;
		}
		else if (value == CometD.EXPORT_STATUS_SET_NOT_CHANGED){
			return this.msg.REFRNCDATAEXPPROGDLG_16;
		}
		
		return "";
	},

	_formatReferenceTableType : function(value, rowIndex, cell) {
		return Util.formatReferenceTableType(value, this.msg);
	}
});
