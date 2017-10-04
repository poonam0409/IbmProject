dojo.provide("cwapp.tab_rdm.AbstractProgressDialog");

dojo.require("idx.layout.BorderContainer");
dojo.require("idx.dialogs");

dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.Dialog");
dojo.require("dijit.ProgressBar");
dojo.require("dijit.form.Button");

dojo.require("dojox.grid.DataGrid");
dojo.require("dojox.cometd");
dojo.require("dojox.timing");
dojo.require("dojox.grid.cells.dijit");
dojo.require("dojox.grid.cells._base");
dojo.require("dojox.grid.cells");

dojo.require("dojo.store.Memory");
dojo.require("dojo.data.ObjectStore");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.declare("cwapp.tab_rdm.AbstractProgressDialog", [], {
	
	// private members
	_done : false,
	_startingPage : null,
	_inMemStore : null,
	_dataGrid : null,
	_failedTimer: null,
	_lastOverallProgress: null,
	_lastTableProgress: null,
	_counter : 0, 
	
	postCreate : function() {
		this._setupGrid();
		dojo.connect(this.progressDialog, "onShow", this, "_myShow");
		dojo.connect(this.progressDialog, "onCancel", this, "_onDialogCancel");
		dojo.connect(this.progressDialog, "onHide", this, "_onDialogHide");
		dojo.connect(this.okButton, "onClick", this, "_onOK");
		dojo.connect(this.cancelButton, "onClick", this, "_onCancel");
		this.inherited(arguments);
	},
	
	start : function() {
		this.progressDialog.show();
	},
	
	setStartingPage : function(startingPage) {
		this._startingPage = startingPage;
	},
	
	_showErrorDialog : function(error) {
		if (this._failedTimer != null) {
			this._failedTimer.stop();
		}
		
		cwapp.main.Error.handleError(error);
		
		this._onDialogCancel();
	},
	
	// scroll the grid to display last row
	_scrollGridToBottom : function() {
		if (this._dataGrid.views.views[0].hasVScrollbar()) {
			this._dataGrid.render();
			this._dataGrid.scrollToRow(this._counter);
		}
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
	
	// do various display-related things whenever the 'onShow' event of this widget fires
	_myShow : function() {
		
		// lazy loading of header
		this._setupHeader();
		this._dataGrid.render();
	},
	
	_progressDone : function(finishMessage) {
		dojox.html.set(this.dialogContentHeader.domNode, finishMessage);
		
		if (this._failedTimer != null){
			this._failedTimer.stop();
		}
		
		this._closeCometd();
		dojo.setAttr(this.okButton, "style", "display: block");
		dojo.setAttr(this.cancelButton, "style", "display: none");
		this._done = true;
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

	// upon pressing the Cancel button all changes will be discarded and the dialog will be closed
	_onCancel : function(param) {
		
		//stop the fail timer so we will get no timeout
		if (this._failedTimer != null) {
			this._failedTimer.stop();
		}
		
		// check the type of the input parameter
		if (typeof param == "boolean") {
			bubbled = true;
		}
		
		dojo.xhrDelete({
			url : this._urls.cancel,
			handleAs : "json",
			preventCache : true,
			handle : dojo.hitch(this, function(data) {
				
				// we need to close the connection to the cometd / bayeux server
				this._closeCometd();
				dojo.setAttr(this.okButton, "style", "display: block");
				dojo.setAttr(this.cancelButton, "style", "display: none");
			})
		});
		
		dojox.html.set(this.dialogContentHeader.domNode, this.msg.CANCELLED);
	},
	
	// upon pressing the OK button the dialog will be hidden
	_onOK : function() {
		this.progressDialog.hide();
	},
	
	// upon pressing the X button in the upper right corner of the dialog
	// or the ESCAPE key we'll close the dialog depending on which state we're in
	_onDialogCancel : function() {
		if (this._done) {
			this._onOK();
		}
		else {
			this._onCancel(true);
		}
	},
	
	// sets the indicator for the failing to zero and starts the timer
	_initFailIndicators : function(){
		this._lastOverallProgress = 0;
		this._lastTableProgress = 0;
		this._failedTimer = new dojox.timing.Timer(this._serverTimeout);
		dojo.connect(this._failedTimer, "onTick", this,"_hasServerFailed");
		this._failedTimer.start();
	},
	
	// checks if there has been any progress since the last tick of the timer
	_hasServerFailed : function(){
		
		// the time for server fail ticked, we have to check if there has been any progress 
		if (this._lastOverallProgress == this.overallProgress.get("value")) {
			// there was no progress in the overallProgressbar
			// we check first if _tableProgress is zero, to make sure that there is a tableProgress
			if (this.tableProgress) {
				if (this._lastTableProgress == this.tableProgress.get("value")) {
					//there was no progress since the last tick of this timer so we can show an error
					this._showErrorDialog(Util.getTimeoutError());
					this._failedTimer.stop();
				}
				else {
					this._lastTableProgress = this.tableProgress.get("value");
				}
			}
			else {
				//there was no progress since the last tick of this timer so we can show an error
				this._showErrorDialog(Util.getTimeoutError());
				this._failedTimer.stop();
			}
		}
		else {
			this._lastOverallProgress = this.overallProgress.get("value");
			if (this.tableProgress) {
				this._lastTableProgress = this.tableProgress.get("value");
			}
		}
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
					
					//start the thread on the server
					url: this._urls.startThread,
					error: dojo.hitch(this, function(error){
						this._showErrorDialog(error);
					})
				});
				
				// initialize the progress bar
				this.overallProgress.set("maximum", data.maximum);
				this.overallProgress.set("value", 0);
				this._initFailIndicators();
			}),
			error: function(error) {
				this._showErrorDialog(error);
			}
		});
	},
	
	_abstractSetupGrid : function(memStore, struct){
		
		// as the memory store itself is not compatible with the dojo.data API we need to wrap it into an object store
		this._inMemStore = new dojo.data.ObjectStore({
			objectStore : memStore
		});
		
		// now we create the data grid
		this._dataGrid = new dojox.grid.DataGrid({
			store: this._inMemStore,
			structure: struct,
			formatterScope: this
		}, document.createElement('div'));

		// disable sortability of the grid
		this._dataGrid.canSort = function() {
			return false;
		};

		// now we append the data grid to the parent dom node
		this.gridParentContentPane.domNode.appendChild(this._dataGrid.domNode);

		// initially load and start the data grid
		this._dataGrid.store.fetch();
		this._dataGrid.startup();
	}
});
