dojo.provide("cwapp.tab_bdr.bph.BphImportDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.Dialog");

dojo.require("dijit.form.Button");

dojo.require("dojox.html._base");

dojo.require("dojox.form.Uploader");
dojo.require("dojox.form.uploader.plugins.IFrame");

dojo.require("dojox.cometd");
dojo.require("dojox.cometd.callbackPollTransport");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.BphImportDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/BphImportDialog.html"),
	widgetsInTemplate : true,

	// nls support
	msg : {},
	
	// constants
	_con : {
		EMPTY_STRING : "",
		FLASH_RESPONSE_PROPERTY_STRING : "name",
		MAX_FILE_SIZE : 80000000,
		UPLOAD_STATUS_CODE_FILE_SUCCESSFULLY_PARSED : 2000,
		UPLOAD_STATUS_CODE_NO_FILE_UPLOADED : 4000,
		UPLOAD_STATUS_CODE_FILE_NOT_VALID : 5000,
		UPLOAD_STATUS_CODE_INTERNAL_ERROR : 6000,
		UPLOAD_STATUS_CODE_UNKNOWN_ERROR : 0000,
		UPLOAD_STATUS_CODE_ABORTED : 7000,
		IMAGE_CLASS_ERROR : "ImageError_48",
		IMAGE_CLASS_OK : "ImageOK_48",
		IMAGE_CLASS_SPINNER : "ImageSpinner_50",
		IMAGE_CLASS_BROWSE : "ImageBrowse_48",
		IMAGE_CLASS_INFO: "ImageQuestion_48",
		STATUS_SUCCESS_SOLMAN : "success_solman",
		STATUS_SUCCESS_CSV_EMPTY : "success_csv_empty",
		STATUS_SUCCESS_CSV_PROCESSES : "success_csv_processes",
		STATUS_SUCCESS_CSV_BOS : "success_csv_bos",
		STATUS_ERROR_INVALID_FILE : "invalid_file",
		STATUS_ERROR_INVALID_FILE_TYPE : "invalid_file_type",
		STATUS_ERROR_THREAD_CANCELLED : "thread_cancelled",
		STATUS_INFO_NO_CHANGES : "no_changes",
		STATUS_ERROR_INVALID_DATA_FILE : "invalid_data_file",
		STATUS_ERROR_INVALID_HEADER : "invalid_data_header",
		STATUS_ERROR_INVALID_PROCESS_HIERARCHY : "invalid_process_hierarchy",
	},

	override : false,
	// private members
	_caller : null,
	_isUploaded : false,
	_cometdClosed : false,
	_uploadImportFileDeferred : null,
	_cometdTopics : {
		TOPIC_BPH_IMPORT_STARTED : CometD.TOPIC_BPH_IMPORT_STARTED,
		TOPIC_BPH_IMPORT_FINISHED : CometD.TOPIC_BPH_IMPORT_FINISHED,
	},
	
	// public functions
	
	constructor : function(args) {
		if (args) {
			dojo.mixin(this, args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},

	postCreate : function() {
		// load the dialog image
		dojo.create("img", {src: "cwapp/img/transparent.png", width: 50, height: 50}, this.importDialogImage);
		this.browseButton.set("url", Services.BDR_BPH_IMPORTFILE_UPLOAD);
		
		// attach to various events
		dojo.connect(this.browseButton, "onChange", this, "_browseButtonClicked");
		dojo.connect(this.browseButton, "onBegin", this, "_uploadBegins");
		dojo.connect(this.browseButton, "onComplete", this, "_uploadComplete");
		dojo.connect(this.okButton, "onClick", this, "_onCancel");
		dojo.connect(this.cancelButton, "onClick", this, "_onCancel");
		dojo.connect(this.bphImportDialog, "onShow", this, "_onShow");
		dojo.connect(this.bphImportDialog, "onCancel", this, "_onCancel");
		
		this.inherited(arguments);
	},

	show : function(caller) {
		this._caller = caller;
		
		switch (this.importType) {
			case BphImportTypes.CSV_COMPLETE : {
				this.importDialogMessage.innerHTML = this.msg.BPHIMPORTDIALOG_16;
				this.bphImportDialog.set("title", this.msg.BPHIMPORTDIALOG_14);
				break;
			}
			case BphImportTypes.CSV_BOS : {
				this.importDialogMessage.innerHTML = this.msg.BPHIMPORTDIALOG_16;
				this.bphImportDialog.set("title", this.msg.BPHIMPORTDIALOG_15);
				break;
			}
			case BphImportTypes.MPX : {
				this.importDialogMessage.innerHTML = this.msg.BPHIMPORTDIALOG_10;
				this.bphImportDialog.set("title", this.msg.BPHIMPORTDIALOG_13);
				break;
			}
			case BphImportTypes.CSV_PROCESSES : {
				this.importDialogMessage.innerHTML = this.msg.BPHIMPORTDIALOG_16;
				this.bphImportDialog.set("title", this.msg.BPHIMPORTDIALOG_24);
				break;
			}
			case BphImportTypes.CSV_TABLES : {
				this.importDialogMessage.innerHTML = this.msg.BPHIMPORTDIALOG_16;
				this.bphImportDialog.set("title", this.msg.BPHIMPORTDIALOG_25);
				break;
			}
			case BphImportTypes.CSV_SAPVIEW : { 
				this.importDialogMessage.innerHTML = this.msg.BPHIMPORTDIALOG_16;
				this.bphImportDialog.set("title", this.msg.BPHIMPORTDIALOG_27);
				dojo.setAttr(this.sapViewRadio, "style", "display: block");
				break;
			}
		}

		this.bphImportDialog.show();
	},
	
	// remove all styling classes from the div containing the dialog status image
	_clearImageClasses : function() {
		dojo.removeClass(this.importDialogImage, this._con.IMAGE_CLASS_ERROR);
		dojo.removeClass(this.importDialogImage, this._con.IMAGE_CLASS_OK);
		dojo.removeClass(this.importDialogImage, this._con.IMAGE_CLASS_SPINNER);
		dojo.removeClass(this.importDialogImage, this._con.IMAGE_CLASS_BROWSE);
	},
	
	_onShow : function() {
		this._initCometd();
	},
	
	_onCancel : function() {

		// close cometd
		if (!this._cometdClosed) {
			this._closeCometd();
		}

		this.bphImportDialog.hide();
	},
	
	_browseButtonClicked : function(selectedFiles) {
		
		// as we only allow to upload one file it is safe to retrieve the first
		// member of the selectedFiles array at this point
		var file = selectedFiles[0];
		this._isUploaded = false;
		
		// at this point we check for the maximum file size
		// if it exceeds MAX_FILE_SIZE we display an error and do not let the user
		// continue
		if (file.size > this._con.MAX_FILE_SIZE) {
			this._updateDialogStatus(this._con.IMAGE_CLASS_ERROR, this.msg.BPHIMPORTDIALOG_2);
			return;
		}
		
		var deferred = this._uploadImportFile();

		if (deferred != null  && this.importType!=BphImportTypes.CSV_SAPVIEW) { 
			deferred.then(dojo.hitch(this, function(data) {
				
				// start the BPH import
				var deferreds = this._openCometd();
				
				if (deferreds != null) {
					deferreds.then(dojo.hitch(this, function(data) {
						this._startBPHImport();
					}));
				}
			}));
		}
	},
	
	_updateDialogStatus : function(imageClass, message) {
		this._clearImageClasses();
		
		// set the status image
		dojo.addClass(this.importDialogImage, imageClass);
		dojox.html.set(this.importDialogMessage, message);
		if (imageClass == this._con.IMAGE_CLASS_OK
				|| imageClass == this._con.IMAGE_CLASS_ERROR
				|| imageClass == this._con.IMAGE_CLASS_INFO) {
			this._showOkButton();
		}
	},
	
	_uploadImportFile : function() {
		this._uploadImportFileDeferred = new dojo.Deferred();
		if (!this._isUploaded) {
			this.browseButton.upload();
		}
		return this._uploadImportFileDeferred;
	},
	
	_uploadBegins : function() {
		
		
		// disable browseButton
		this.browseButton.set("disabled", true);
		if(this.importType==BphImportTypes.CSV_SAPVIEW){

			if (this.mergeSapView.checked){
				this.browseButton.set("url", Services.BDR_SAPVIEWIMPORT_UPLOAD+"/Merge");
			}
			else{
				this.mergeSapView.set("checked", false);
				this.browseButton.set("url", Services.BDR_SAPVIEWIMPORT_UPLOAD+"/Overwrite");
			}
			this.mergeSapView.set("disabled", true);
			this.overwriteSapView.set("disabled", true);
		}
		// set the status image
		this._updateDialogStatus(this._con.IMAGE_CLASS_SPINNER, this.msg.BPHIMPORTDIALOG_9);
	},
	
	// Called by the browse button's onComplete event.
	_uploadComplete : function(resultList) {
		var result1 = resultList[0];
		
		var resultMessage = "";
		
		// check the list of results
		if (resultList != null) {
			
			// as we only allow to upload one file it is safe to retrieve the first
			// member of the resultList array at this point
			var result = resultList[0];

			// as the file upload done by the Flash version of dojox.form.uploader
			// (which is actually the case for Microsoft Internet Explorer) seems
			// to have its very own response object returned upon the 'uploadComplete'
			// event we have to handle it quite differently to the response given
			// by HTML5 or plain-HTML using browsers
			if (result.hasOwnProperty(this._con.FLASH_RESPONSE_PROPERTY_STRING)) {
				this._isUploaded = true;
				this._uploadImportFileDeferred.callback();
				// everything is fine so we're done
				return;
			}
			// HTML5 or plain-HTML response handling
			switch (result.statusCode) {
				case this._con.UPLOAD_STATUS_CODE_FILE_SUCCESSFULLY_PARSED:
					this._isUploaded = true;
					if(this.importType==BphImportTypes.CSV_SAPVIEW)
						{
						resultMessage = result.message;
						this._updateDialogStatus(this._con.IMAGE_CLASS_OK, resultMessage);
						return;						
						}
					else{
					this._uploadImportFileDeferred.callback();
					// everything is fine so we do not only 'break' here but 'return' from the routine completely
					return;}
				case this._con.UPLOAD_STATUS_CODE_NO_FILE_UPLOADED:
					resultMessage = this.msg.BPHIMPORTDIALOG_6;
					break;
				case this._con.UPLOAD_STATUS_CODE_FILE_NOT_VALID:
					resultMessage = this.msg.BPHIMPORTDIALOG_7;
					break;
				case this._con.UPLOAD_STATUS_CODE_INTERNAL_ERROR:
					resultMessage = this.msg.BPHIMPORTDIALOG_8;
					break;
				default:
					resultMessage = this.msg.BPHIMPORTDIALOG_5;
					break;
			}
		}
		else {
			resultMessage = this.msg.BPHIMPORTDIALOG_5;
		}
		
		// The upload was not successful
		this._updateDialogStatus(this._con.IMAGE_CLASS_ERROR, resultMessage);
		this._isUploaded = false;
		this._caller._updateImportExportButtons();
	},
	
	_deleteImportFileFromSession : function() {
		var deferred = dojo.xhrDelete({
			url: Services.BDR_BPH_IMPORTFILE_DELETE,
			handleAs: "text",
			preventCache: true
		});
		
		return deferred;
	},
	
	_initCometd : function() {
		dojo.xhrGet({
			url: Services.SESSIONRESTURL,
			handleAs: "text",
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				
				// configure the topics with the current session id
				for (var topic in this._cometdTopics) {
					this._cometdTopics[topic] = CometD[topic] + data;
				}
				
				// initialize the cometd / bayeux protocol
				dojox.cometd.init(Services.COMETD);
				
				this._cometdClosed = false;
			}),
			error: dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	_openCometd : function() {
		var started = dojox.cometd.subscribe(this._cometdTopics.TOPIC_BPH_IMPORT_STARTED, dojo.hitch(this, function(message) {
			// do nothing
		}));
		var finished = dojox.cometd.subscribe(this._cometdTopics.TOPIC_BPH_IMPORT_FINISHED, dojo.hitch(this, this._importFinished));
		
		return new dojo.DeferredList([started, finished]);
	},
	
	// Called via CometD message when the import is finished on the server
	_importFinished : function(message) {
		this._deleteImportFileFromSession();
		
		// Get given status from server
		var statusInfo = message.data.split(",");
		var posStatus = 0;
		var posCounterProcess = 1;
		var posCounterBo = 2;
		var posCounterTable = 3;
		var posCounterTableUsages = 4;
		// In case we have success, the standard success message will be enhanced
		var message = this.msg.BPHIMPORTDIALOG_12;
		
		// Handle status and open dialog with appropriate message
		if(statusInfo[posStatus] == this._con.STATUS_SUCCESS_CSV_EMPTY) {
			// Enlarge message with added Processes, BOs and Tables
			message += "<br>"+"<b>"+statusInfo[posCounterProcess]+"</b>"+this.msg.BPHIMPORTDIALOG_20
				+"<br>"+"<b>"+statusInfo[posCounterBo]+"</b>"+this.msg.BPHIMPORTDIALOG_21
				+"<br>"+"<b>"+statusInfo[posCounterTable]+"</b>"+this.msg.BPHIMPORTDIALOG_22
				+"<br>"+"<b>"+statusInfo[posCounterTableUsages]+"</b>"+this.msg.BPHIMPORTDIALOG_26;
			this._updateDialogStatus(this._con.IMAGE_CLASS_OK, message);
		}
		else if(statusInfo[posStatus] == this._con.STATUS_SUCCESS_CSV_BOS) {
			// Enlarge message with added BOs and Tables
			message += "<br>"+"<b>"+statusInfo[posCounterBo]+"</b>"+this.msg.BPHIMPORTDIALOG_21
				+"<br>"+"<b>"+statusInfo[posCounterTable]+"</b>"+this.msg.BPHIMPORTDIALOG_22
				+"<br>"+"<b>"+statusInfo[posCounterTableUsages]+"</b>"+this.msg.BPHIMPORTDIALOG_26;
			this._updateDialogStatus(this._con.IMAGE_CLASS_OK, message);
		}
		else if(statusInfo[posStatus] == this._con.STATUS_SUCCESS_SOLMAN
				|| statusInfo[posStatus] == this._con.STATUS_SUCCESS_CSV_PROCESSES) {
			// Enlarge message with added Processes
			message += "<br>"+"<b>"+statusInfo[posCounterProcess]+"</b>"+this.msg.BPHIMPORTDIALOG_20;
			this._updateDialogStatus(this._con.IMAGE_CLASS_OK, message);
		}
		else if(statusInfo[posStatus] == this._con.STATUS_ERROR_INVALID_FILE) {
			this._updateDialogStatus(this._con.IMAGE_CLASS_ERROR, this.msg.BPHIMPORTDIALOG_7);
		}
		else if(statusInfo[posStatus] == this._con.STATUS_ERROR_INVALID_FILE_TYPE) {
			this._updateDialogStatus(this._con.IMAGE_CLASS_ERROR, this.msg.BPHIMPORTDIALOG_18);
		}
		else if(statusInfo[posStatus] == this._con.STATUS_ERROR_INVALID_DATA_FILE) {
			this._updateDialogStatus(this._con.IMAGE_CLASS_ERROR, this.msg.BPHIMPORTDIALOG_28);
		}
		else if(statusInfo[posStatus] == this._con.STATUS_ERROR_INVALID_HEADER) {
			this._updateDialogStatus(this._con.IMAGE_CLASS_ERROR, this.msg.BPHIMPORTDIALOG_29);
		}
		else if(statusInfo[posStatus] == this._con.STATUS_ERROR_INVALID_PROCESS_HIERARCHY) {
			this._updateDialogStatus(this._con.IMAGE_CLASS_ERROR, this.msg.BPHIMPORTDIALOG_30);
		}
		else if(statusInfo[posStatus] == this._con.STATUS_ERROR_THREAD_CANCELLED) {
			this._updateDialogStatus(this._con.IMAGE_CLASS_ERROR, this.msg.BPHIMPORTDIALOG_19);
		}
		else if(statusInfo[posStatus] == this._con.STATUS_INFO_NO_CHANGES) {
			// Show info dialog, which tells that nothing has been imported
			this._updateDialogStatus(this._con.IMAGE_CLASS_INFO, this.msg.BPHIMPORTDIALOG_23);
		}
		else {
			// All the other errors are treated as an internal error
			this._updateDialogStatus(this._con.IMAGE_CLASS_ERROR, this.msg.BPHIMPORTDIALOG_8);
		}

		this._caller.refresh();
		
		// we need to close the connection to the cometd / bayeux server
		this._closeCometd();
	},
	
	_closeCometd : function() {
		for (var topic in this._cometdTopics) {
			setTimeout(dojo.hitch(this, function() {
				dojox.cometd.unsubscribe(this._cometdTopics[topic]);
			}), 100);
		}

		dojox.cometd.disconnect();
		
		this._cometdClosed = true;
	},
	
	_showOkButton : function() {
		dojo.setAttr(this.browseButton, "style", "display: none");
		dojo.setAttr(this.okButton, "style", "display: block");
		dojo.setAttr(this.cancelButton, "style", "display: none");
	},
	
	_startBPHImport : function() {
		dojo.xhrPost({
			url : Services.BDR_BPH_IMPORT + "/" + this.importType + "?override=" + this.override,
			
			handleAs: "json",
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				dojo.xhrPost({
					//start the thread on the server
					url: Services.BDR_BPH_IMPORT_START,
					error: dojo.hitch(this, function(error){
						cwapp.main.Error.handleError(error);
					})
				});
			}),
			error: function(error) {
				cwapp.main.Error.handleError(error);
			}
		});
	}
});
