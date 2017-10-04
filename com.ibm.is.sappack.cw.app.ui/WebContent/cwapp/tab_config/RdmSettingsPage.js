dojo.provide("cwapp.tab_config.RdmSettingsPage");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");

dojo.require("idx.layout.ButtonBar");
dojo.require("idx.layout.BorderContainer");

dojo.require("idx.dialogs");

dojo.require("dijit.form.Form");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.FilteringSelect");

dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dojox.data.JsonRestStore");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_config.RdmSettingsPage", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_config","templates/RdmSettingsPage.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		EMPTY_STRING : "",
		MSG_ERROR_UNAUTHORIZED : "rdmConnectionTestServiceFailedUnauthorized",
		MSG_ERROR_WITH_STATUS : "rdmConnectionTestServiceFailedWithStatus",
	    MSG_ERROR_INTERNAL_ERROR : "rdmConnectionTestServiceFailedInternalError",
	    REST_STATUS_INTERNAL_ERROR : 500,
	},
	
	// private members
	_settingStore : null,
	_initPhaseComplete :false,
	_oldHostname : null,
	_oldPort : null,
	_oldUser : null,
	_oldPassword : null,
	_oldLanguage : null,
	
	// public functions
	constructor : function(args) {  
		if(args) {dojo.mixin(this,args);}	
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	postCreate : function() {
		this._setupStore();
		this._loadSettings();
		
		dojo.connect(this, "onShow", this, "_myShow");
		dojo.connect(this.revertButton, "onClick", this, "_revertChanges");
		dojo.connect(this.saveButton, "onClick", this, "_saveChanges");
		dojo.connect(this.testConnectionButton, "onClick", this, "_testRdmConnection");
		dojo.connect(this.rdmHostInput, "onKeyUp", this, "_checkIfSettingsChanged");
		dojo.connect(this.rdmPortInput, "onKeyUp", this, "_checkIfSettingsChanged");
		dojo.connect(this.rdmUserInput, "onKeyUp", this, "_checkIfSettingsChanged");
		dojo.connect(this.rdmPasswordInput, "onKeyUp", this, "_checkIfSettingsChanged");
		dojo.connect(this.rdmLanguageInput, "onKeyUp", this, "_checkIfSettingsChanged");
		dojo.connect(this.rdmLanguageInput, "onChange", this, "_checkIfSettingsChanged");
		dojo.connect(window, "onresize", this, "_resizeContainer");
		
		this.inherited(arguments);
	},
	
	// private functions
	
	_myShow : function() {
		this.borderContainer.resize();
		this._loadSettings();
		this._rememberOldValues();
	},
	
	// resize the page
	_resizeContainer: function(){
		setTimeout(dojo.hitch(this, function() {
			this.borderContainer.resize();
		}), 300);
	},
	
	// setup REST store
	_setupStore : function() {
		this._settingStore = new dojox.data.JsonRestStore({
			target : Services.SETTINGRESTURL,
			idAttribute : Services.SETTINGIDATTRIBUTE,
			syncMode : "true",
		});
	},
	
	// populate the input text boxes
	_loadSettings : function() {
		// Disable buttons since we are reverting to saved values
		this.saveButton.set("disabled", true);
		this.revertButton.set("disabled", true);
		
		// retrieve values stored in JPA backend via REST calls
		var settings = this._settingStore.fetch().results;
		for (var i = 0; i < settings.length; i++) {
			var setting = settings[i];
			switch (setting.name) {
			case Settings.RDM_HOST_NAME:
				this.rdmHostInput.set("value", setting.value);
				break;
			case Settings.RDM_PORT_NAME:
				this.rdmPortInput.set("value", setting.value);
				break;
			case Settings.RDM_USER_NAME:
				this.rdmUserInput.set("value", setting.value);
				break;
			case Settings.RDM_LANGUAGE_NAME:
				this.rdmLanguageInput.set("value", setting.value);
				break;
			}
		}
		
		// retrieve session-stored RDM password via separate REST call
		dojo.xhrGet({
			url: Services.RDMPASSWORDRESTURL,
			handleAs: "text",
			load: dojo.hitch(this, function(data) {
				this.rdmPasswordInput.set("value", data);
				this._updateConnectionTestButton();
			}),
			error: function(error) {
				cwapp.main.Error.handleError(error);
			}
		});
	},

	_revertChanges : function() {
		this._initPhaseComplete = false;
		this._loadSettings();
	},

	_saveChanges : function() {
		this._saveInStore(Settings.RDM_HOST_NAME);
		this._saveInStore(Settings.RDM_PORT_NAME);
		this._saveInStore(Settings.RDM_USER_NAME);
		this._saveInStore(Settings.RDM_LANGUAGE_NAME);
		this._savePassword();
		// refresh the RDM mappings page
		dojo.publish(Topics.RDM_REFRESH_MAPPING_PAGE_WHEN_SHOWN);
	},

	_rememberOldValues : function() {
		this._oldHostname = this.rdmHostInput.get("value");
		this._oldPort = this.rdmPortInput.get("value");
		this._oldUser = this.rdmUserInput.get("value");
		this._oldPassword = this.rdmPasswordInput.get("value");
		this._oldLanguage = this.rdmLanguageInput.get("value");
	},

	_findItemInStore : function(settingName) {
		var result = null;
		this._settingStore.fetch({
			query:{name:settingName},
			onComplete: function(data) {
				result = data;
			}
		});
		return result;
	},
	
	// persist a setting in the REST store
	_saveInStore : function(settingName) {
		var items = this._findItemInStore(settingName);
		if (items.length > 0) {
			var item = items[0];
			this._settingStore.changing(item);
			item.value = this._getSettingFromPage(settingName);
		} else {
			var item = new Object();
			item.name = settingName;
			item.value = this._getSettingFromPage(settingName);
			this._settingStore.newItem(item);
		}
		this._settingStore.save();
		this._rememberOldValues();
		this._checkIfSettingsChanged();
	},
	
	_getSettingFromPage : function(settingName) {
		switch (settingName) {
		case Settings.RDM_HOST_NAME:
			return this.rdmHostInput.get("value");
		case Settings.RDM_PORT_NAME:
			return this.rdmPortInput.get("value");
		case Settings.RDM_USER_NAME:
			return this.rdmUserInput.get("value");
		case Settings.RDM_LANGUAGE_NAME:
			return this.rdmLanguageInput.get("value");
		}
	},
	
	// save the RDM password in the session context
	_savePassword : function() {
		dojo.xhrPost({
			url: Services.RDMPASSWORDRESTURL,
			handleAs: "text",
			postData: this.rdmPasswordInput.get("value"),
			preventCache: true,
			error: function(error) {
				cwapp.main.Error.handleError(error);
			}
		});		
	},
	
	// test the connection to the RDM server using the given credentials
	_testRdmConnection : function() {
		var credentials = new Object();
		credentials.host = this.rdmHostInput.get("value");
		credentials.port = this.rdmPortInput.get("value");
		credentials.user = this.rdmUserInput.get("value");
		credentials.pwd = this.rdmPasswordInput.get("value");
		
		// show loading dialog while trying to contact server
		idx.showProgressDialog(this.msg.SOURCESYSTEMSPG_40);
		
		dojo.xhrPost({
			url: Services.RDMCONNECTIONTESTRESTURL,
			handleAs: "json",
			postData: dojo.toJson(credentials),
			preventCache: true,
			load: dojo.hitch(this, "_displayConnectionTestSuccessInfo"),
			error: dojo.hitch(this, function(error) {
				this._displayConnectionTestFailure(error);
			})
		});
	},

	// display info dialog upon successful RDM connection test
	_displayConnectionTestSuccessInfo : function() {
		idx.hideProgressDialog();
		idx.info(this.msg.RDMSETTINGSPG_12);
	},
	
	// display error dialog upon unsuccessful RDM connection test
	_displayConnectionTestFailure : function(error) {
		var message = null;
		var responseText = error.responseText;
		
		idx.hideProgressDialog();
		// depending on the response text we have to figure out the NLS message to display
		if (responseText == this._con.MSG_ERROR_WITH_STATUS) {
			message = this.msg.RDMSETTINGSPG_15 + "<br>" + this.msg.ERROR_CODE + error.status;
		}
		else {
			if (error.status != this._con.REST_STATUS_INTERNAL_ERROR) {
				message = this.msg.RDMSETTINGSPG_13;
			}
			else {
				message = this.msg.RDMSETTINGSPG_14;
			}
		}
		
		// actually display the error dialog
		idx.error({
			messageId: null,
			summary: message,
			detail: null,
			moreContent: null
		});
	},
	
	// check the contents of the input text boxes and trigger the
	// enablement of the various buttons correspondingly
	_checkIfSettingsChanged : function() {
		var changed = false;
		
		if (this._initPhaseComplete && (this._oldHostname != this.rdmHostInput.get("value") || this._oldPort != this.rdmPortInput.get("value")
				|| this._oldUser != this.rdmUserInput.get("value") || this._oldPassword != this.rdmPasswordInput.get("value")
				|| this._oldLanguage != this.rdmLanguageInput.get("value"))) {
			changed = true;
			this._oldPassword = null; // delete password after first change to prevent guessing
		}
		// The first time this function is called is triggered by the initial setting of the language dropdown value,
		// ignore it
		this._initPhaseComplete = true;

		var lang = this.rdmLanguageInput.get("value");
		var invalid = false;
		
		this._updateConnectionTestButton();
		
		// Delay the evaluation because the FilteringSelect (combo box) validation takes a short while
		setTimeout(dojo.hitch(this, function() {
			if (lang == "" || this.rdmLanguageInput.state == "Error") {
				invalid = true;
			}
			this.saveButton.set("disabled", invalid || !changed);
			this.revertButton.set("disabled", !changed);
		}), 100);
	},
	
	// enable / disable the RDM connection test button depending on presence of values
	_updateConnectionTestButton : function(enabled) {
		var host = this.rdmHostInput.get("value");
		var port = this.rdmPortInput.get("value");
		var user = this.rdmUserInput.get("value");
		var pwd = this.rdmPasswordInput.get("value");
		if (host == "" || port == "" || user == "" || pwd == "") {
			this.testConnectionButton.set("disabled", true);
		} else {
			this.testConnectionButton.set("disabled", false);
		}
	},
});
