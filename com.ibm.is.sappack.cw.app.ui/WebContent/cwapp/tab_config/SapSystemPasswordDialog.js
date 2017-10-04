dojo.provide("cwapp.tab_config.SapSystemPasswordDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.Dialog");

dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.Button");

dojo.require("dojox.html._base");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_config.SapSystemPasswordDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_config","templates/SapSystemPasswordDialog.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		EMPTY_STRING : "",
		STYLE_CLASS_INPUT_ERROR : "PasswordDialog_InputError",
		IMAGE_CLASS_QUESTION : "ImageQuestion_48",
		IMAGE_CLASS_WARNING : "ImageWarning_48",
	},
	
	// private members
	_legacySystem : null,
	_passwordCheckDeferred : null,
	
	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}
				
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	postCreate : function() {

		// load the dialog image
		dojo.create("img", {src: "cwapp/img/transparent.png", width: 48, height: 48}, this.passwordDialogImage);

		// connect the various elements to events
		dojo.connect(this.passwordDialog, "onShow", this, "_myShow");
		dojo.connect(this.passwordDialog, "onHide", this, "_onHide");
		dojo.connect(this.passwordDialog, "onSubmit", this, "_onOK");
		dojo.connect(this.passwordForm, "onSubmit", this, "_onOK");
		dojo.connect(this.okButton, "onClick", this, "_onOK");
		dojo.connect(this.cancelButton, "onClick", this, "_onCancel");
		dojo.connect(this.passwordDialogPasswordInput, "onKeyUp", this, "_passwordInputChanged");

		this.inherited(arguments);
	},
	
	// check to see whether a password exists for the given SAP system (in the current session)
	// and if not, display a popup dialog asking for the password accordingly
	// this function is asynchronous therefore we return a dojo.Deferred
	getPassword : function(legacySystem) {
		this._passwordCheckDeferred = new dojo.Deferred();
		
		this._legacySystem = legacySystem;
		
		// retrieve the password for the given SAP system from the session
		var password = this._retrieveSapSystemPassword(legacySystem);
		
		if (password != null && password != this._con.EMPTY_STRING) {
			// the password is already set so we can successfully resolve the dojo.Deferred
			this._legacySystem.sapPassword = password;
			this._passwordCheckDeferred.callback(true);
		} else {
			// No password set yet, display dialog
			this.passwordDialog.show();
		}
		
		return this._passwordCheckDeferred;
	},
	
	// private functions
	
	// workaround for initial display bug of idx.layout.HeaderPane
	_myShow : function() {
		
		// lazy loading of SAP system details
		this._setupDetails();
		
		// set the status image
		this._clearImageClasses();
		dojo.addClass(this.passwordDialogImage, this._con.IMAGE_CLASS_QUESTION);
	},
	
	// setup the SAP system details
	_setupDetails : function() {
		dojox.html.set(this.passwordDialogSapSystem, this._legacySystem.legacyId);
	//	dojox.html.set(this.passwordDialogUser, this._legacySystem.sapUser);
	},
	
	// retrieve the password for the given SAP system from the session context
	_retrieveSapSystemPassword : function(legacySystem) {
		//var password = null;
		  var password = "";
		
		//dojo.xhrGet({
		//	url: Services.SAPPASSWORDRESTURL + "/" + legacySystem.legacyId,
		//	handleAs: "text",
		//	preventCache: true,
		//	sync: true, // we're synchronous, so we can just return the response
		//	load: dojo.hitch(this, function(response) {
		//	    password = "";
		//	password = response;
		//	}),
		//	error: dojo.hitch(this, function(error) {
		//		this._onCancel();
		//		cwapp.main.Error.handleError(error);
		//	})
		//});*/
		
		return password; // will always return a blank string as password
	},
	
	// store the entered password for the selected SAP system in the session context
	_storeSapSystemPassword : function() {
		this._legacySystem.sapPassword = this.passwordDialogPasswordInput.get("value");
		
		//dojo.xhrPost({
		//	url: Services.SAPPASSWORDRESTURL + "/" + this._legacySystem.legacyId,
		//	handleAs: "text",
		//	postData: this._legacySystem.sapPassword,
		//	preventCache: true,
		//	sync: true,
		//	error: dojo.hitch(this, function(error) {
		//		this._onCancel();
		//		cwapp.main.Error.handleError(error);
		//	})
		//}); 
	},
	
	// enable / disable the OK button depending on the states of the password input text box
	_passwordInputChanged : function() {
		
		// hide the tooltip (if any)
		dijit.hideTooltip(this.passwordDialogPasswordInput.domNode);
		
		this._clearImageClasses();
		dojo.addClass(this.passwordDialogImage, this._con.IMAGE_CLASS_QUESTION);
		dojo.removeClass(this.passwordDialogInputElement, this._con.STYLE_CLASS_INPUT_ERROR);
		
		if (this.passwordDialogPasswordInput.get("value") != this._con.EMPTY_STRING) {
			this.okButton.set("disabled", false);
		}
		else {
			this.okButton.set("disabled", true);
		}
	},
	
	// update the look and feel of the password input text box
	// to indicate a wrong password in case the connection test has failed
	_updatePasswordInputStatus : function(connectionSuccessful) {
		dojo.removeClass(this.passwordDialogInputElement, this._con.STYLE_CLASS_INPUT_ERROR);
		dijit.hideTooltip(this.passwordDialogPasswordInput.domNode);
		
		if (!connectionSuccessful) {
			dojo.addClass(this.passwordDialogInputElement, this._con.STYLE_CLASS_INPUT_ERROR);
			dijit.showTooltip(this.msg.SAPPWDDLG_6, this.passwordDialogPasswordInput.domNode, ["above"], false);
		}
	},
	
	// upon pressing the OK button the SAP system password will be stored
	// and the dialog will be hidden
	_onOK : function(data) {
		data.preventDefault(); // Prevent the default submit behavior that would reload the page
		var legacySystem = Util.cloneLegacySystem(this._legacySystem);
		legacySystem.sapPassword = this.passwordDialogPasswordInput.get("value");

		// check if a connection can be made using the given password
		dojo.xhrPost({
			url: Services.SAPCONNECTIONTESTRESTURL,
			handleAs: "json",
			postData: dojo.toJson(legacySystem),
			preventCache: true,
			load: dojo.hitch(this, function(data) {

				// clear the password input text box tooltip
				this._updatePasswordInputStatus(true);

				// save the password
				this._storeSapSystemPassword();

				this.passwordDialog.hide();
				// the popup dialog has been closed so we successfully resolve
				// the dojo.Deferred that is bound to it
				this._passwordCheckDeferred.callback(true);
			}),
			error: dojo.hitch(this, function(error) {
				if (error.status != 401) {
					// Connection or internal error
					this._onCancel();
					cwapp.main.Error.handleError(error);
				} else {
					// Wrong credentials
					this._passwordIncorrect();
				}
			})
		});
	},
	
	// indicate that the password was incorrect
	_passwordIncorrect : function() {
		// have the password input text box display an error tooltip
		this._updatePasswordInputStatus(false);
		this.passwordDialogPasswordInput.focus();

		// set the status image
		this._clearImageClasses();
		dojo.addClass(this.passwordDialogImage, this._con.IMAGE_CLASS_WARNING);
		
		// clear the password
		this.passwordDialogPasswordInput.set("value", "");

		this.okButton.set("disabled", true);
		this.passwordDialogInput.focus();
	},
	
	// upon pressing the Cancel button all changes will be discarded
	// and the dialog will be closed
	_onCancel : function() {
		this.passwordDialog.onCancel();
		this._onHide();

		// report failure to get a password
		this._passwordCheckDeferred.callback(false);
	},
	
	// the dialog is about to be hidden
	_onHide : function() {
		this.passwordDialogPasswordInput.set("value", "");
		dijit.hideTooltip(this.passwordDialogPasswordInput.domNode);	
	},
	
	// remove all styling classes from the div containing the dialog status image
	_clearImageClasses : function() {
		dojo.removeClass(this.passwordDialogImage, this._con.IMAGE_CLASS_QUESTION);
		dojo.removeClass(this.passwordDialogImage, this._con.IMAGE_CLASS_WARNING);
	}

});
