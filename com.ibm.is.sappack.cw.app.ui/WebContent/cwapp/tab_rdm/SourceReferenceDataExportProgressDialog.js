dojo.provide("cwapp.tab_rdm.SourceReferenceDataExportProgressDialog");

//this class inherits from the abstract progress dialog
dojo.require("cwapp.tab_rdm.AbstractProgressDialog");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.SourceReferenceDataExportProgressDialog", [ dijit._Widget, dijit._Templated, cwapp.tab_rdm.AbstractProgressDialog ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm","templates/SourceReferenceDataExportProgressDialog.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	_cometdTopics : {
		TOPIC_EXPORT_ROW_COUNT : "",
		TOPIC_EXPORT_ROW_PROGRESS : "",
		TOPIC_EXPORT_TABLE_STATUS : "",
	},
	
	_urls: {
		postData: Services.SOURCE_DATA_EXPORT,
		startThread: Services.SOURCE_DATA_EXPORT_STARTTHREAD,
		cancel: Services.SOURCE_DATA_EXPORT_CANCEL,
	},
	
	// private members
	_serverTimeout: 20000,
	_currentItem: null,
	_placeholder: null, // Empty placeholder grid row used in a workaround to enable scrolling to the bottom
	                    // so that the last line is fully visible

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
		// create the memory store that will hold the table export status
		var memStore = new dojo.store.Memory({
			idProperty: "number"
		});
		
		// as the memory store itself is not compatible with the dojo.data API we need to
		// wrap it into an object store
		var structure = [
		  	    {name: this.msg.SRCREFRNCDATAPG_7, field: "legacyId", width: "25%", styles: "font-weight: bold;"},
		        {name: this.msg.TABLE, field: "name", width: "30%", styles: "font-weight: bold;"},
	           	{name: this.msg.REFRNCDATAEXPPROGDLG_6, field: "rowcount", width: "10%", styles: "text-align: right;", headerStyles: "text-align: left;"},
	           	{name: this.msg.STATUS, field: "status", width: "35%", formatter: "_statusColumnFormatter"}
			];
			// call the abstract setup grid method and pass the sructure and the store as argument
			// the abstract method will create the grid and start it
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
		// refresh the source data page now
		dojo.publish(Topics.RDM_REFRESH_SOURCE_PAGE_NOW);
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
	
	// Received a new row from the server
	// Update the progress bar and table and set bar maximum
	_addNewRow : function(message) {
		// Remove placeholder row
		if (this._placeholder) {
			this._inMemStore.deleteItem(this._placeholder);
			this._inMemStore.save();
		}
		
		// Add new row
		message.data.status = CometD.EXPORT_STATUS_IN_PROGRESS;
		this._inMemStore.newItem(message.data);
		
		// Add an empty row to enable the last actual row to fully appear when scrolling down (workaround)
		this._placeholder = this._inMemStore.newItem({number:"placeholder1", name:"", rowcount:"", status:9});
		
		this._inMemStore.save();
		this._scrollGridToBottom();
		this.tableProgress.set("value", 0);
	},
	
	// Received a row progress update from the server
	// Update the progress bar
	_updateTableRowProgress : function(message) {
		this.tableProgress.set("value", message.data.rowcount);
		this._scrollGridToBottom();
	},
	
	// Received a table status update from the server
	// Either display the row count (if this is an intermediate count update)
	// or update the progress table and bar (if this is the final status update)
	_updateTableStatus : function(message) {
		// look for the item in the data grid store
		this._inMemStore.fetchItemByIdentity({
			identity: message.data.number,
			onItem: dojo.hitch(this, function(item) {
				if (item == null) {
					return;
				}
				this._inMemStore.setValue(item, "status", message.data.status);
				if (message.data.status == CometD.EXPORT_STATUS_IN_PROGRESS) {
					// Received intermediate update with the row count
					this._inMemStore.setValue(item, "rowcount", message.data.rowcount);
					if (message.data.rowcount == 0) {
						this.tableProgress.set("maximum", 1);
					} else {
						this.tableProgress.set("maximum", message.data.rowcount);
					}
				} else {
					// Received final update with the status
					this.tableProgress.set("value", this.tableProgress.get("maximum"));
					this.overallProgress.set("value", message.data.number);
					
					// See if we are finished
					if (this.overallProgress.get("value") == this.overallProgress.get("maximum")) {
						this._inMemStore.deleteItem(this._placeholder);
						this._inMemStore.save();
						this._placeholder = 0;
						this._progressDone(this.msg.REFRNCDATAEXPPROGDLG_3);
					}
				}
				this._inMemStore.save();
				this._counter = message.data.number;
			})
		});
	},
	
	// Grid cell formatting function for the table export status
	// translates an integer status value into a string
	_statusColumnFormatter : function(value, rowIndex, cell) {
		if (value == CometD.EXPORT_STATUS_OK) {
			return this.msg.STATUS_OK;
		} else if (value == CometD.EXPORT_STATUS_INTERNAL_ERROR) {
			return this.msg.REFRNCDATAIMPPROGDLG_13;
		} else if (value == CometD.EXPORT_STATUS_TABLE_NOT_FOUND) {
			return this.msg.REFRNCDATAEXPPROGDLG_10;
		} else if (value == CometD.EXPORT_STATUS_IN_PROGRESS) {
			return this.msg.REFRNCDATAEXPPROGDLG_11;
		}else if(value == CometD.EXPORT_STATUS_SET_NOT_CHANGED){
			return this.msg.REFRNCDATAEXPPROGDLG_16;
		}
		return "";
	}
});
