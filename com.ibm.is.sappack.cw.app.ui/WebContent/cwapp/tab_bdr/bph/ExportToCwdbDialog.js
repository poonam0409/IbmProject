dojo.provide("cwapp.tab_bdr.bph.ExportToCwdbDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dojo.store.Memory");

dojo.require("dijit.Dialog");

dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.Button");

dojo.require("dojox.html._base");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.ExportToCwdbDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph","templates/ExportToCwdbDialog.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		STATE_INITIAL : 0,
		STATE_CONFIRM_OVERWRITE : 1,
		STATE_IN_PROGRESS : 2,
		STATE_DONE : 3,
		STATE_ERROR : 4,
		IMAGE_CLASS_QUESTION : "ImageQuestion_48",
		IMAGE_CLASS_ERROR : "ImageError_48",
		IMAGE_CLASS_OK : "ImageOK_48",
		IMAGE_CLASS_SPINNER : "ImageSpinner_50",
		STATUS_SUCCESSFUL : "success",
		STATUS_CANCEL : "cancel"
	},
	
	_cometdTopics : {
		TOPIC_BDR_EXPORT_STARTED : "",
		TOPIC_BDR_EXPORT_FINISHED : "",
	},
	
	_urls: {
		postData : Services.BDR_EXPORT_TO_CWDB,
		startThread: Services.BDR_START_EXPORT_TO_CWDB, 
	},
	
	// private members

	_rolloutValues : null,
	_state : null, // Current state of the dialog(s)
	_postData : null,
	
	// public functions
	constructor : function(args) {
		if(args) {
			dojo.mixin(this,args);
		}
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},
	
	postCreate : function() {
		dojo.connect(this.inputDialog, "onSubmit", this, "_onOK");
		dojo.connect(this.form, "onSubmit", this, "_onOK");
		dojo.connect(this.okButton, "onClick", this, "_onOK");
		dojo.connect(this.infoOkButton, "onClick", this, "_onOK");
		dojo.connect(this.cancelButton, "onClick", this, "_onCancel");
		dojo.connect(this.infoCancelButton, "onClick", this, "_onCancel");
		dojo.connect(this.rolloutInput, "onKeyUp", this, "_inputChanged");
		dojo.connect(this.rolloutInput, "onChange", this, "_inputChanged");

		dojo.setAttr(this.rolloutInput, "maxlength", BdrAttributeLengths.ROLLOUT);
		
		this.inherited(arguments);
	},
	
	show : function() {
		this.inputDialog.show();
		this._initSapSystemsTable();
		this._setState(this._con.STATE_INITIAL);
	},
	
	// private functions
	
	// initialize the data grid by connecting it to the REST store
	_initSapSystemsTable : function() {
		if (!this._systemStore) {
			this._systemStore = new dojox.data.JsonRestStore({
				target : Services.TARGETSAPSYSTEMRESTURL,
				idAttribute : Services.SYSTEMIDATTRIBUTE,
				syncMode : true,
				preventCache : true,
			});
			
		    var layout = [
		    	{name: this.msg.NAME, width: '30%', field: 'legacyId'},
		    	{name: this.msg.HOST, width: '30%', field: 'sapHost'},
		    	{name: this.msg.DESCRIPTION, width: '40%', field: 'description'},
	   	    ];
	
	   	    this.sapSystemsTable.set("structure", layout);
			this.sapSystemsTable.set("store", this._systemStore);
			this.sapSystemsTable.set("sortInfo", "+1");
			this.sapSystemsTable.set("selectionMode", "single");
			this.sapSystemsTable.set("noDataMessage", this.msg.WizardParameters_NoSapSystems);

		}
		this.sapSystemsTable.startup();
		this.sapSystemsTable.render();
		
		dojo.connect(this.sapSystemsTable, "onSelected", this, "_sapSystemSelected");
		
		if (this.sapSystemsTable.rowCount > 0) {
			this.sapSystemsTable.selection.select(0);
		}
	},
	
	// Table selection callback
	_sapSystemSelected : function(rowIndex) {
		this._getParams(this.sapSystemsTable.getItem(rowIndex));
	},
	
	_getParams : function(sapSystem) {
		dojo.xhrGet({
			url : Services.BDR_EXPORT_TO_CWDB_GET_PARAMS + "/" + sapSystem.legacyId,
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
		var inputArray = [];
		for (var value in valueArray) {
			inputArray.push({name: valueArray[value]});
		}
		this.rolloutInput.set("store", new dojo.store.Memory({data: inputArray}));
	},
	
	// enable / disable the OK button depending on the state of the input form
	_inputChanged : function() {
		if (this.rolloutInput.get("value")) {
			this.okButton.set("disabled", false);
		} else {
			this.okButton.set("disabled", true);
		}
	},
	
	// Sets the dialog state
	_setState : function(state) {
		this._state = state;
		switch(state) {
			case this._con.STATE_INITIAL:
				dojo.style(this.infoCancelButton.domNode, "display", "inline-block");
				break;
			case this._con.STATE_CONFIRM_OVERWRITE:
				this._setIcon(this._con.IMAGE_CLASS_QUESTION);
				this.message.innerHTML = this.msg.ExportToCwdb_OverwriteConfirmation;
				this.infoOkButton.set("label", this.msg.ExportToCwdb_OverwriteButton);
				break;
			case this._con.STATE_IN_PROGRESS:
				this._setIcon(this._con.IMAGE_CLASS_SPINNER);
				this.message.innerHTML = this.msg.ExportToCwdb_InProgress;
				dojo.style(this.infoOkButton.domNode, "display", "none");
				dojo.style(this.infoCancelButton.domNode, "display", "none");
				break;
			case this._con.STATE_DONE:
				this._setIcon(this._con.IMAGE_CLASS_OK);
				this.message.innerHTML = this.msg.ExportToCwdb_Complete;
				this.infoOkButton.set("label", this.msg.OK);
				dojo.style(this.infoOkButton.domNode, "display", "inline-block");
				break;
			case this._con.STATE_ERROR:
				this._setIcon(this._con.IMAGE_CLASS_ERROR);
				this.message.innerHTML = this.msg.INTERNAL_ERROR;
				this.infoOkButton.set("label", this.msg.OK);
				dojo.style(this.infoOkButton.domNode, "display", "inline-block");
				break;
		}
	},
	
	// Sets the icon
	_setIcon : function(iconClass) {
		dojo.removeClass(this.icon);
		dojo.addClass(this.icon, iconClass);
	},
	
	// Checks if there is existing exported data in the tables
	_checkExisting : function() {
		dojo.xhrGet({
			url: Services.BDR_EXPORT_TO_CWDB_CHECK_EXISTING + "/" + this.rolloutInput.get("value"),
			preventCache: true,
			sync: true,
			load: dojo.hitch(this, function(data) {
				if (data) {
					// Existing data
					this._setState(this._con.STATE_CONFIRM_OVERWRITE);
					this.inputDialog.hide();
					this.infoDialog.show();
				} else {
					// No existing data
					this._setState(this._con.STATE_IN_PROGRESS);
					this.inputDialog.hide();
					this.infoDialog.show();
					this._export();
				}
			}),
			error: dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	// Actually exports the data
	_export : function() {
		this._postData = {
			legacyId : this.sapSystemsTable.selection.getSelected()[0].legacyId,
			rollout : this.rolloutInput.get("value"),
			separateScopes : this.separateScopesCheckbox.get("checked")
		};
		
		// initialize the cometd / bayeux protocol and start the thread
		this._initCometd().then(dojo.hitch(this, function(result) {
			this._startExportThread();
		}));
	},
	
	// initialize the cometd / bayeux protocol in order to retrieve topic events from the server
	_initCometd : function() {
		return dojo.xhrGet({
			url : Services.SESSIONRESTURL,
			handleAs : "text",
			preventCache : true,
			load : dojo.hitch(this, function(data) {
				
				// configure the topics with the current session id
				for (var topic in this._cometdTopics) {
					this._cometdTopics[topic] = CometD[topic] + data;
				}
				
				// initialize the cometd / bayeux protocol
				dojox.cometd.init(Services.COMETD);
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	_openCometd : function() {
		var started = dojox.cometd.subscribe(this._cometdTopics.TOPIC_BDR_EXPORT_STARTED, dojo.hitch(this, function(message) {
			// do nothing
		}));
		var finished = dojox.cometd.subscribe(this._cometdTopics.TOPIC_BDR_EXPORT_FINISHED, dojo.hitch(this, function(message) {
			if (message.data == this._con.STATUS_SUCCESSFUL) {
				console.log("received success");
				this._setState(this._con.STATE_DONE);
			} else if (message.data == this._con.STATUS_CANCEL) {
			} else {
				this._setState(this._con.STATE_ERROR);
				this._enableExportButton();
				cwapp.main.Error.handleError(error);
			}
			// we need to close the connection to the cometd / bayeux server
			this._closeCometd();
		}));
		
		return new dojo.DeferredList([started, finished]);
	},

	// start the reference data export
	// as this is an asynchronous operation on the server at this point
	// we'll only get the response for the starting operation 
	_startExportThread : function() {
		var openCometDDefList = this._openCometd();
		this._setState(this._con.STATE_IN_PROGRESS);
		openCometDDefList.then(dojo.hitch(this, function(){
			this._postDataAndStartThread(this._postData, {"Content-Type" : "application/json"});
		}));
	},
	
	// Posts the requestData to an URL that is specified in the private members of the concrete class
	_postDataAndStartThread : function(requestData, header) {
		dojo.xhrPost({
			url: this._urls.postData,
			handleAs: "json",
			headers: header,
			postData: dojo.toJson(requestData),
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				dojo.xhrPost({
					url: this._urls.startThread,
					error: dojo.hitch(this, function(error){
						this._showErrorDialog(error);
					})
				});
			}),
			error: function(error) {
				cwapp.main.Error.handleError(error);
			}
		});
	},
	
	// unsubscribe from the server topics and close the cometd / bayeux connection
	_closeCometd : function() {
		for (var topic in this._cometdTopics) {
			setTimeout(dojo.hitch(this, function() {
				dojox.cometd.unsubscribe(this._cometdTopics[topic]);
			}), 100);
		}
		
		dojox.cometd.disconnect();
	},
	
	_onOK : function(data) {
		data.preventDefault(); // Prevent the default submit behavior that would reload the page
		
		switch(this._state) {
		case this._con.STATE_INITIAL:
			this._checkExisting();
			break;
		case this._con.STATE_CONFIRM_OVERWRITE:
			// Overwrite confirmed
			this._setState(this._con.STATE_IN_PROGRESS);
			this._export();
			break;
		case this._con.STATE_DONE:
			this.infoDialog.hide();
			break;
		case this._con.STATE_ERROR:
			this.infoDialog.hide();
			break;
		}
	},
	
	_onCancel : function() {
		switch(this._state) {
		case this._con.STATE_INITIAL:
			this.inputDialog.hide();
			break;
		case this._con.STATE_CONFIRM_OVERWRITE:
			// Overwrite confirmed
			this._setState(this._con.STATE_INITIAL);
			this.inputDialog.show();
			this.infoDialog.hide();
			break;
		case this._con.STATE_DONE:
			this.infoDialog.hide();
			break;
		case this._con.STATE_ERROR:
			this.infoDialog.hide();
			break;
		}

		//dijit.hideTooltip(this.rolloutInput.domNode);	
	},
});
