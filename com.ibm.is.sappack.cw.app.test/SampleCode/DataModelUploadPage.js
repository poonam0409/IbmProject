dojo.provide("cwapp.tab_rdm.referenceDataLoadWizard.DataModelUploadPage");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("idx.layout.HeaderPane");
dojo.require("idx.widget.SimpleIconDialog");
dojo.require("idx.dialogs");

dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.ProgressBar");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");

dojo.require("dojox.timing");
dojo.require("dojox.form.Uploader");
dojo.require("dojox.form.uploader.plugins.IFrame");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");


dojo.declare("cwapp.tab_rdm.referenceDataLoadWizard.DataModelUploadPage", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm.referenceDataLoadWizard","templates/DataModelUploadPage.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		EMPTY_STRING : "",
		FLASH_RESPONSE_PROPERTY_STRING : "name",
		MAX_FILE_SIZE : 80000000,
		UPLOAD_STATUS_CODE_FILE_SUCCESSFULLY_PARSED : 2000,
		UPLOAD_STATUS_CODE_SCHEMA_MISMATCH : 3000,
		UPLOAD_STATUS_CODE_NO_FILE_UPLOADED : 4000,
		UPLOAD_STATUS_CODE_FILE_NOT_VALID : 5000,
		UPLOAD_STATUS_CODE_INTERNAL_ERROR : 6000,
		UPLOAD_STATUS_CODE_UNKNOWN_ERROR : 0000,
		UPLOAD_STATUS_CODE_ABORTED : 7000,
	},

	// private members
	_parentWizard : null,	
	_uploadDataModelDeferred : null,
	_uploadTimer : null,
	_firstTick : false,
	_isUploaded : false,
	
	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	postCreate : function() {
		this._setupForm();
		this._setupProgressDialog();
		this._setupTimer();
		
		// connect the various elements to events
		dojo.connect(this, "onShow", this, "_myShow");
		dojo.connect(this.dataModelFileUploader, "onChange", this, "_fileUploadSelectionChanged");
		dojo.connect(this.dataModelFileUploader, "onClick", this, "_clearUploadSelection");
		dojo.connect(this.dataModelFileUploader, "onBegin", this, "_uploadBegins");
		dojo.connect(this.dataModelFileUploader, "onComplete", this, "_uploadComplete");
		dojo.connect(this.dataModelFileUploader, "onAbort", this, "_uploadAborted");
		dojo.connect(this.dataModelFileUploader, "onError", this, "_uploadError");
		dojo.connect(this._uploadTimer, "onTick", this, "_uploadTimerTick");
		dojo.connect(this._uploadTimer, "onStop", this, "_uploadTimerStopped");
		
		this.inherited(arguments);
	},
	
	uploadDataModel : function() {
		this._uploadDataModelDeferred = new dojo.Deferred();

		if (!this._isUploaded) {
			this.dataModelFileUploader.upload();
		}
		else {
			this._uploadDataModelDeferred.callback();
		}
		
		return this._uploadDataModelDeferred;
	},
	
	canNext : function() {
		return this._isUploaded;
	},
	
	setParentWizard : function(wizard) {
		this._parentWizard = wizard;
	},
	
	// setup the form by providing the 'action' parameter for actual upload(submit) handling
	_setupForm : function() {
		this.dataModelUploadForm.action = Services.DATAMODELUPLOADRESTURL;
	},
	
	// setup the upload progress dialog
	_setupProgressDialog : function() {
		this.uploadProgressDialog._onKey = function(){};
		dojo.style(this.uploadProgressDialog.closeButtonNode, "display", "none");
		this.uploadProgressDialog.startup();
	},
	
	//  create a new timer with a tick setting of 500ms
	_setupTimer : function() {
		this._uploadTimer = new dojox.timing.Timer(500);
	},
	
	// workaround for initial display bug of idx.layout.HeaderPane
	_myShow : function() {
		this.headerPane.resize();
	},
	
	// the file selection has changed so we set the input text box and publish
	// our own 'selection changed' topic
	_fileUploadSelectionChanged : function(selectedFiles) {

		// as we only allow to upload one file it is safe to retrieve the first
		// member of the selectedFiles array at this point
		var file = selectedFiles[0];
		this._isUploaded = false;
		
		// at this point we check for the maximum file size
		// if it exceeds MAX_FILE_SIZE we display an error and do not let the user
		// continue
		if (file.size > this._con.MAX_FILE_SIZE) {
			idx.error({
				messageId: null,
				summary: this.msg.DATAMDLUPLOADPG_9,
				detail: null,
				moreContent: null
			});
			
			this._clearUploadSelection();
			
			return;
		}

		this.dataModelFileSelectionInput.set("value", selectedFiles[0].name);
		dojo.publish(Topics.DATAMODELUPLOADSELECTIONCHANGED);
	},
	
	// the file selection is about to be changed so we clear the input text box and publish
	// our own 'selection cleared' topic
	_clearUploadSelection : function() {
		this._isUploaded = false;
		this._deleteDataModelFromSession();
		this._parentWizard.clearReferenceTableStore();
		this.dataModelFileSelectionInput.set("value", this._con.EMPTY_STRING);
		dojo.publish(Topics.DATAMODELUPLOADSELECTIONCLEARED);
	},
	
	// the file upload begins so we start the timer
	_uploadBegins : function() {
		this._uploadTimer.start();
	},
	
	// the upload is complete so we stop the timer, clear the file selection and
	// signal that it's ok to move to the next wizard page by resolving the dojo.Deferred
	// depending on the result of the upload request
	_uploadComplete : function(resultList) {
		var uploadError = null;
		
		// check the list of results
		if (resultList != null) {
			
			// as we only allow to upload one file it is safe to retrieve the first
			// member of the resultList array at this point
			var result = resultList[0];

			// as the file upload done by the Flash version of dojox.form.uploader
			// (which is actually the case for Microsoft Internet Explorer) seems
			// to have it's very own response object returned upon the 'uploadComplete'
			// event we have to handle it quite differently to the response given
			// by HTML5 or plain-HTML using browsers
			if (result.hasOwnProperty(this._con.FLASH_RESPONSE_PROPERTY_STRING)) {
				this._isUploaded = true;
				this._uploadTimer.stop();
				this._uploadDataModelDeferred.callback();
				
				return;
			}
			
			// HTML5 or plain-HTML response handling
			switch (result.statusCode) {
			case this._con.UPLOAD_STATUS_CODE_FILE_SUCCESSFULLY_PARSED:
				this._isUploaded = true;
				this._uploadTimer.stop();
				this._uploadDataModelDeferred.callback();

				// everything is fine so we do not only 'break' here but 'return' from the
				// routine completely
				return;
				
			case this._con.UPLOAD_STATUS_CODE_SCHEMA_MISMATCH:
				var schemaNames = result.message;
				uploadError = new Object({
					"statusCode" : this._con.UPLOAD_STATUS_CODE_SCHEMA_MISMATCH,
					"message" : Util.formatMessage(this.msg.DATAMDLUPLOADPG_11, schemaNames[0], schemaNames[1])
				});
				break;
			case this._con.UPLOAD_STATUS_CODE_NO_FILE_UPLOADED:
				uploadError = new Object({
					"statusCode" : this._con.UPLOAD_STATUS_CODE_NO_FILE_UPLOADED,
					"message" : this.msg.DATAMDLUPLOADPG_6
				});
				break;
			case this._con.UPLOAD_STATUS_CODE_FILE_NOT_VALID:
				uploadError = new Object({
					"statusCode" : this._con.UPLOAD_STATUS_CODE_FILE_NOT_VALID,
					"message" : this.msg.DATAMDLUPLOADPG_7
				});
				break;
			case this._con.UPLOAD_STATUS_CODE_INTERNAL_ERROR:
				uploadError = new Object({
					"statusCode" : this._con.UPLOAD_STATUS_CODE_INTERNAL_ERROR,
					"message" : this.msg.DATAMDLUPLOADPG_8
				});
				break;
			default:
				uploadError = new Object({
					"statusCode" : this._con.UPLOAD_STATUS_CODE_UNKNOWN_ERROR,
					"message" : this.msg.DATAMDLUPLOADPG_5
				});
				break;
			}
		}
		else {
			uploadError = new Object({
				"statusCode" : this._con.UPLOAD_STATUS_CODE_UNKNOWN_ERROR,
				"message" : this.msg.DATAMDLUPLOADPG_5
			});
		}
		
		this._isUploaded = false;
		this._uploadError(uploadError);
	},
	
	// the upload has or was aborted so we generate an error
	_uploadAborted : function() {
		this._uploadError(new Object({
			"statusCode" : this._con.UPLOAD_STATUS_CODE_ABORTED,
			"message" : this.msg.DATAMDLUPLOADPG_4
		}));
	},
	
	// an error has occurred during the upload so we stop the upload timer,
	// clear the file selection and reject the dojo.Deferred so that we won't be able
	// to go to the next wizard page
	_uploadError : function(error) {
		this._uploadTimer.stop();
		this._clearUploadSelection();
		this._uploadDataModelDeferred.errback(error);
	},
	
	// the upload timer has stopped, so we hide the upload progress dialog
	_uploadTimerStopped : function() {
		this.uploadProgressDialog.hide();
		this._firstTick = false;
	},
	
	// the upload timer interval (tick counter) fired
	// if this is the first tick (occurring after 500ms) we show the upload progress dialog
	_uploadTimerTick : function() {
		if (!this._firstTick) {
			this._firstTick = true;
			this.uploadProgressDialog.show();
		}
	},
	
	// delete the session parameter which holds the parsed data model 
	_deleteDataModelFromSession : function() {
		var deferred = dojo.xhrDelete({
			url: Services.DATAMODELDELETERESTURL,
			handleAs: "text",
			preventCache: true
		});
		
		return deferred;
	}
});
