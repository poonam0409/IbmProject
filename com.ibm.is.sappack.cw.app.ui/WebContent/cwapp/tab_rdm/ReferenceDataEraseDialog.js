dojo.provide("cwapp.tab_rdm.ReferenceDataEraseDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.Dialog");

dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.Button");

dojo.require("dojox.html._base");

dojo.require("dojox.cometd");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.ReferenceDataEraseDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm","templates/ReferenceDataEraseDialog.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		IMAGE_CLASS_QUESTION : "ImageQuestion_48",
		IMAGE_CLASS_ERROR : "ImageError_48",
		IMAGE_CLASS_OK : "ImageOK_48",
		IMAGE_CLASS_SPINNER : "ImageSpinner_50",
		STATUS_DONE : "done",
		STATUS_FAILED : "failed",
	},
	
	// private members
	_referenceTablesArray : null,
	_isFinished : false,
	_isRunning : false,
	
	_cometdTopics : {
		TOPIC_CLEANUP_FINISHED : "",
	},
	
	// public functions
	constructor : function(args) {
		if(args) {
			dojo.mixin(this,args);
		}
				
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},
	
	postCreate : function() {
		
		// load the dialog image
		dojo.create("img", {src: "cwapp/img/transparent.png", width: 50, height: 50}, this.eraseDialogImage);

		// connect the various elements to events
		dojo.connect(this.eraseDialog, "onShow", this, "_myShow");
		dojo.connect(this.eraseDialog, "onHide", this, "_onDialogHide");
		dojo.connect(this.eraseDialog, "onCancel", this, "_onDialogCancel");
		dojo.connect(this.eraseButton, "onClick", this, "_onErase");
		dojo.connect(this.cancelButton, "onClick", this, "_onCancel");

		this.inherited(arguments);
	},
	
	start : function(referenceTablesArray) {
		this._referenceTablesArray = referenceTablesArray;
		this.eraseDialog.show();
	},
	
	// private functions
	
	// do various display-related things whenever the 'onShow' event of this widget fires
	_myShow : function() {
		
		// initialize the cometd / bayeux protocol
		this._initCometd().then(dojo.hitch(this, function() {

			// lazy loading of initial dialog message
			this._setupInitialMessage();
			
			// set the initial status image
			this._clearImageClasses();
			dojo.addClass(this.eraseDialogImage, this._con.IMAGE_CLASS_QUESTION);
		}),
		dojo.hitch(this, function(error) {
			cwapp.main.Error.handleError(error);
		}));
	},
	
	// set the initial message to be displayed when the erase dialog pops up
	_setupInitialMessage : function() {
		var message = Util.formatMessage(this.msg.REFRNCDATAERSDLG_2, this._referenceTablesArray.length);
		dojox.html.set(this.eraseDialogMessage, message);
	},
	
	// remove all styling classes from the div containing the dialog status image
	_clearImageClasses : function() {
		dojo.removeClass(this.eraseDialogImage, this._con.IMAGE_CLASS_QUESTION);
		dojo.removeClass(this.eraseDialogImage, this._con.IMAGE_CLASS_ERROR);
		dojo.removeClass(this.eraseDialogImage, this._con.IMAGE_CLASS_OK);
		dojo.removeClass(this.eraseDialogImage, this._con.IMAGE_CLASS_SPINNER);
	},
	
	// initialize the cometd / bayeux protocol in order to retrieve topic events from the server
	_initCometd : function() {
		var deferred = new dojo.Deferred();
		
		dojo.xhrGet({
			url: Services.SESSIONRESTURL,
			handleAs: "text",
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				
				// configure the topics with the current session id
				this._cometdTopics.TOPIC_CLEANUP_FINISHED = CometD.TOPIC_CLEANUP_FINISHED + data;
				
				// initialize the cometd / bayeux protocol
				dojox.cometd.init(Services.COMETD);
				deferred.resolve();
			}),
			error: dojo.hitch(this, function(error) {
				deferred.errback(error);
			})
		});
		
		return deferred;
	},
	
	// subscribe to certain server topics
	_openCometd : function() {
		dojox.cometd.subscribe(this._cometdTopics.TOPIC_CLEANUP_FINISHED, this, "_tableCleanupDone");
	},
	
	// unsubscribe from the server topics and close the cometd / bayeux connection
	_closeCometd : function() {
		dojox.cometd.unsubscribe(this._cometdTopics.TOPIC_CLEANUP_FINISHED);
		dojox.cometd.disconnect();
	},

	// upon pressing the erase button the selected reference tables will be cleared
	_onErase : function() {
		var requestData = this._referenceTablesArray;

		// start listening to topics from the cometd / bayeux server
		this._openCometd();

		// hide the 'erase' button
		dojo.style(this.eraseButton.domNode, "display", "none");

		// start the cleanup
		dojo.xhrDelete({
			url: Services.REFTABLECLEANUPRESTURL,
			handleAs: "json",
			headers: {"Content-Type": "application/json"},
			postData: dojo.toJson(requestData),
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				
				// indicate that the cleanup action is running
				this._isRunning = true;

				// set the status image
				this._clearImageClasses();
				dojo.addClass(this.eraseDialogImage, this._con.IMAGE_CLASS_SPINNER);
				dojox.html.set(this.eraseDialogMessage, this.msg.REFRNCDATAERSDLG_3);
			}),
			error: dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);

				// set the status image
				this._clearImageClasses();
				dojo.addClass(this.eraseDialogImage, this._con.IMAGE_CLASS_ERROR);
				dojox.html.set(this.eraseDialogMessage, this.msg.REFRNCDATAERSDLG_5);
			})
		});
	},
	
	// we received a 'done' topic so the table cleanup was finished
	_tableCleanupDone : function(message) {
		this._isRunning = false;
		this._isFinished = true;
		
		// re-label the 'cancel' button
		this.cancelButton.set("label", this.msg.OK);

		// set the status image
		this._clearImageClasses();
		
		// the cleanup status can either be 'done' or 'failed' so we have to set the UI accordingly
		if (message.data == this._con.STATUS_DONE) {
			dojo.addClass(this.eraseDialogImage, this._con.IMAGE_CLASS_OK);
			dojox.html.set(this.eraseDialogMessage, this.msg.REFRNCDATAERSDLG_4);
		}
		else {
			dojo.addClass(this.eraseDialogImage, this._con.IMAGE_CLASS_ERROR);
			dojox.html.set(this.eraseDialogMessage, this.msg.REFRNCDATAERSDLG_5);
		}
		
		// we need to close the connection to the cometd / bayeux server
		this._closeCometd();
	},
	
	// upon pressing the X button in the upper right corner of the dialog
	// or the ESCAPE key we'll close the dialog depending on which state we're in
	_onDialogCancel : function() {
		if (this._isFinished) {
			this._onOK();
		}
		else {
			this._onCancel(true);
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
	
	// upon pressing the 'OK' button the dialog will be hidden
	_onOK : function() {
		this.eraseDialog.hide();
	},
	
	// upon pressing the 'cancel' button all changes will be discarded
	// and the dialog will be closed
	_onCancel : function(param) {
		var bubbled = false;
		
		// check the type of the input parameter
		if (typeof param == "boolean") {
			bubbled = true;
		}
		
		// the 'cancel' button acts as 'OK' button when the cleanup finished flag is set
		if (this._isFinished) {
			this._onOK();
		}
		else {
			
			// we need to act differently depending on the state of the cleanup action
			if (this._isRunning) {
				dojo.xhrDelete({
					url: Services.REFERENCEDATACANCELRESTURL,
					handleAs: "text",
					preventCache: true,
					handle: dojo.hitch(this, function(data) {
						
						// we need to close the connection to the cometd / bayeux server
						this._closeCometd();
						
						// if we're coming from an event which bubbles up (e.g. by pressing the
						// X button in the upper right corner or the ESCAPE key) we do
						// not need to hide the erase dialog explicitely as the event
						// bubbling will do this for us
						if (!bubbled) {
							this.eraseDialog.hide();
						}
					})
				});
			}
			else {
				if (!bubbled) {
					this.eraseDialog.hide();
				}
			}
		}
	}
});
