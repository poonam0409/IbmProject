dojo.provide("cwapp.tab_config.RdmPasswordDialog");

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

dojo.declare("cwapp.tab_config.RdmPasswordDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_config","templates/RdmPasswordDialog.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		STYLE_CLASS_INPUT_ERROR : "PasswordDialog_InputError",
		IMAGE_CLASS_QUESTION : "ImageQuestion_48",
		IMAGE_CLASS_WARNING : "ImageWarning_48",
	},
	
	// private members
	_rdmHost : null,
	_rdmPort : null,
	_rdmUser : null,
	_password: null,
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
		dojo.connect(this.rdmPasswordDialog, "onShow", this, "_myShow");
		dojo.connect(this.rdmPasswordDialog, "onHide", this, "_onHide");
		dojo.connect(this.rdmPasswordDialog, "onSubmit", this, "_onOK");
		dojo.connect(this.passwordForm, "onSubmit", this, "_onOK");
		dojo.connect(this.okButton, "onClick", this, "_onOK");
		dojo.connect(this.cancelButton, "onClick", this, "_onCancel");
		dojo.connect(this.passwordDialogInput, "onKeyUp", this, "_passwordInputChanged");

		this.inherited(arguments);
	},
	
	// check to see whether the password is known (in the session)
	// if not, display a popup dialog asking for the password
	getPassword : function(rdmHost, rdmPort, rdmUser) {
		this._rdmHost = rdmHost;
		this._rdmPort = rdmPort;
		this._rdmUser = rdmUser;
		
		// retrieve the password from the session
		this._retrievePassword();

		this._passwordCheckDeferred = new dojo.Deferred();
		
		// if the password is not set, display dialog
		if (!this._password) {
			this.rdmPasswordDialog.show();
		} else {
			this._passwordCheckDeferred.callback(true);
		}
		return this._passwordCheckDeferred;
	},
	
	// private functions
	
	_myShow : function() {
		// lazy loading of header
		this._setupHeader();
		
		this._clearImageClasses();
		dojo.addClass(this.passwordDialogImage, this._con.IMAGE_CLASS_QUESTION);
		dojo.removeClass(this.passwordDialogInputElement, this._con.STYLE_CLASS_INPUT_ERROR);
	},
	
	// setup the header title by loading a part from the NLS catalog and attaching
	// the RDM user name to it
	_setupHeader : function() {
		var message = Util.formatMessage(this.msg.RDMPASSWDDLG_4, this._rdmUser);
		dojox.html.set(this.tableColumnHeader, message);
	},
	
	// retrieve the password from the session context
	_retrievePassword : function() {
		dojo.xhrGet({
			url: Services.RDMPASSWORDRESTURL,
			handleAs: "text",
			preventCache: true,
			sync: true,
			load: dojo.hitch(this, function(password) {
				this._password = password;
			}),
			error: dojo.hitch(this, function(error) {
				this._onCancel();
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	// store the entered password in the session context
	_storePassword : function() {
		dojo.xhrPost({
			url: Services.RDMPASSWORDRESTURL,
			handleAs: "text",
			postData: this._password,
			sync: true,
			preventCache: true,
			error: dojo.hitch(this, function(error) {
				this._onCancel();
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	// enable / disable the OK button depending on the states of the password input text box
	_passwordInputChanged : function() {
		if (this.passwordDialogInput.get("value") != "") {
			this.okButton.set("disabled", false);
			dijit.hideTooltip(this.passwordDialogInput.domNode);
			this._clearImageClasses();
			dojo.addClass(this.passwordDialogImage, this._con.IMAGE_CLASS_QUESTION);
			dojo.removeClass(this.passwordDialogInputElement, this._con.STYLE_CLASS_INPUT_ERROR);
		} else {
			this.okButton.set("disabled", true);
		}
	},
	
	// indicate that the password was incorrect
	_passwordIncorrect : function() {
		dojo.addClass(this.passwordDialogInputElement, this._con.STYLE_CLASS_INPUT_ERROR);
		dijit.showTooltip(this.msg.RDMPASSWDDLG_5, this.passwordDialogInput.domNode, ["above"], false);
		this._clearImageClasses();
		dojo.addClass(this.passwordDialogImage, this._con.IMAGE_CLASS_WARNING);

		// clear the password
		this._password = null;
		this.passwordDialogInput.set("value", "");

		this.okButton.set("disabled", true);
		this.passwordDialogInput.focus();
	},
	
	_onOK : function(data) {
		data.preventDefault(); // Prevent the default submit behavior that would reload the page
		this._password = this.passwordDialogInput.get("value");
		var credentials = {
			host : this._rdmHost,
			port : this._rdmPort,
			user : this._rdmUser,
			pwd  : this._password
		};
		// check if a connection can be made using the given password
		dojo.xhrPost({
			url: Services.RDMCONNECTIONTESTRESTURL,
			handleAs: "json",
			postData: dojo.toJson(credentials),
			preventCache: true,
			sync: true,
			load: dojo.hitch(this, function(data) {
				// success, save the password
				this._storePassword();

				this.rdmPasswordDialog.hide();
				
				// Callback to return to the main thread
				this._passwordCheckDeferred.callback(true);
			}),
			error: dojo.hitch(this, function(error) {
				// have the password input text box display an error tooltip
				// check first if there was a login error or something other is wrong
				// e.g. an internal error occurred
				if (error.status != 401) {
					this._onCancel();
					cwapp.main.Error.handleError(error);
				} else {
					this._passwordIncorrect();
				}
			})
		});
	},
	
	_onCancel : function() {
		this.rdmPasswordDialog.onCancel();
		this._onHide();
		
		// Callback to return to the main thread
		this._passwordCheckDeferred.callback(false);
	},
	
	_onHide : function() {
		this._password = null;
		this.passwordDialogInput.set("value", "");
		dijit.hideTooltip(this.passwordDialogInput.domNode);	
	},
	
	// remove all styling classes from the div containing the dialog status image
	_clearImageClasses : function() {
		dojo.removeClass(this.passwordDialogImage, this._con.IMAGE_CLASS_QUESTION);
		dojo.removeClass(this.passwordDialogImage, this._con.IMAGE_CLASS_WARNING);
	},
});
