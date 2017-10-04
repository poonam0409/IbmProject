dojo.provide("cwapp.tab_rdm.DataMappingsCreationProgressDialog");

//this class inherits from the abstract progress dialog
dojo.require("cwapp.tab_rdm.AbstractProgressDialog");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.DataMappingsCreationProgressDialog", [ dijit._Widget, dijit._Templated, cwapp.tab_rdm.AbstractProgressDialog ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm","templates/DataMappingsCreationProgressDialog.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	_cometdTopics : {
		MAPPING_FINISHED_TOPIC : "",
	},
	
	_urls: {
		cancel: Services.CREATION_CANCEL_URL,
		postData : Services.CREATION_URL,
		startThread: Services.CREATION_STARTTHREAD, 
	},
	
	_serverTimeout : 20000,
	
	// public functions
	
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}	
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
		
	// private functions
	
	// setup the required infrastructure for the creation status tree grid
	_setupGrid : function() {	
		// create the memory store which will hold the table creation status
		var memStore = new dojo.store.Memory({
			idProperty : "Name",
		});		
		var structure =  [
	            {name: this.msg.DATAMAPNGSCRTPROGDLG_10, field: "Name", width: "auto", styles: "font-weight: bold;"},
	           	{name: this.msg.DATAMAPNGSCRTPROGDLG_12, field: "srcSetName", width: "auto"},
	           	{name: this.msg.DATAMAPNGSCRTPROGDLG_11, field: "tgtSetName", width: "auto"},
	           	{name: this.msg.STATUS,                  field: "STATUS", width: "auto", formatter : "_statusColumnFormatter"},
			];
		// call the abstract setup grid method and pass the sructure and the store as argument
		// the abstract method will create the grid and start it
		this._abstractSetupGrid(memStore, structure);
		// start the import timer and initialize the cometd / bayeux protocol
		this._initCometd().then(dojo.hitch(this, function(result) {
			this._startCreation();
		}));
	},
	
	// subscribe to server topics
	_openCometd : function() {
		var finished = dojox.cometd.subscribe(this._cometdTopics.MAPPING_FINISHED_TOPIC, this,"_updateMappingProgress");
		var result = new dojo.DeferredList([ finished]);
		 result.addErrback(function(err){
			 this._showErrorDialog(error);
		 });
		 return result;
	},
	
	// setup the header title
	_setupHeader : function() {
		var loadingImage = myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loadingAnimation.gif";
		dojox.html.set(this.dialogContentHeader.domNode, "<img src='" + loadingImage + "' style='vertical-align:middle'>&nbsp;&nbsp;&nbsp;" + this.msg.DATAMAPNGSCRTPROGDLG_2);
	},
	
	// the dialog is about to get hidden so we publish a topic about it
	// to anyone who wishes to be notified about it
	_onDialogHide : function() {
		// refresh the source page now
		dojo.publish(Topics.RDM_REFRESH_SOURCE_PAGE_NOW);
		// refresh the mapping page
		dojo.publish(Topics.RDM_REFRESH_MAPPING_PAGE_WHEN_SHOWN);
	},
	
	// start the mapping creation
	// as this is an asynchronous operation on the server at this point
	// we'll only get the response for the starting operation 
	_startCreation : function() {
		var requestData = this._startingPage.refTableGrid.selection.getSelected();
		this._counter = 0;
		var openCometDDefList = this._openCometd();
		openCometDDefList.then(dojo.hitch(this, function(){
			this._postDataAndStartThread(requestData, {"Content-Type" : "application/json"});
		}));
	},
	
	// Received a mapping progress
	_updateMappingProgress : function(message) {
		this._inMemStore.fetchItemByIdentity({identity:message.data.Name, onItem:dojo.hitch(this, function(item) {
			if (item){
				this._inMemStore.setValue(item, "STATUS", message.data.STATUS);
			} else {
				this._inMemStore.newItem(message.data);
			}
			this._inMemStore.save();
			this._counter = message.data.COUNT;
			this._scrollGridToBottom();
		})
		});
		this.overallProgress.set("value", message.data.COUNT);
		if (this.overallProgress.get("value") == this.overallProgress.get("maximum")) {
			this._progressDone(this.msg.DATAMAPNGSCRTPROGDLG_3);
		}
	},
	
	_statusColumnFormatter : function(value, rowIndex, cell) {
		if (value == CometD.InternalError) {
			return this.msg.REFRNCDATAIMPPROGDLG_13;
		} else if (value == CometD.UnAuthorized) {
			return this.msg.DATAMAPNGSCRTPROGDLG_6;
		} else if (value == CometD.OK) {
			return this.msg.STATUS_OK;
		} else if (value == CometD.CREATING){
			return this.msg.DATAMAPNGSCRTPROGDLG_14;
		} else if (value == CometD.SET_MISSING_IN_CWAPP){
			return this.msg.DATAMAPNGSCRTPROGDLG_15;
		} else if (value == CometD.SET_MISSING_IN_RDM_HUB){
			return this.msg.DATAMAPNGSCRTPROGDLG_18;
		} else if (value == CometD.CONFLICT){
			return this.msg.DATAMAPNGSCRTPROGDLG_16;
		} else if (value == CometD.MAPPING_EXISTS){
			return this.msg.DATAMAPNGSCRTPROGDLG_17;
		}
		return "";
	}
});
