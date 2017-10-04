dojo.provide("cwapp.tab_config.SourceSystemsPage");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");

dojo.require("idx.layout.BorderContainer");
dojo.require("idx.layout.ButtonBar");

dojo.require("idx.dialogs");

dojo.require("dijit.form.Button");
dojo.require("dijit.form.CheckBox");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.ValidationTextBox");
dojo.require("dijit.form.DropDownButton");
dojo.require("dijit.form.FilteringSelect");

dojo.require("dijit.Menu");
dojo.require("dijit.MenuItem");

dojo.require("dojox.grid.DataGrid");
dojo.require("dojox.data.JsonRestStore");

dojo.require("dojo.data.ItemFileReadStore");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");


dojo.declare("cwapp.tab_config.SourceSystemsPage", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_config","templates/SourceSystemsPage.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		EMPTY_STRING : "",
	    REST_STATUS_INTERNAL_ERROR : 500,
	    REST_STATUS_CONNECTION_ERROR : 503,
	    REST_STATUS_UNAUTHORIZED : 401,
	    SYSTEM_CHECKED_IMG :
			'<img src="cwapp/img/checkmark.png" title="{0}" width="20px" height="20px" alt="isChecked" border="0px" align="left"/>',
		SYSTEM_UNCHECKED_IMG :
			'<img src="cwapp/img/transparent.png" title="{0}" width="20px" height="20px" alt="isUnchecked" border="0px" align="left"/>',
	},
	
	// private members
	_overallStore : null,
	_systemStore : null,
	_currentSystemSelectionIndex : null,
	_addMode : false,
	_currentSystemSelectedIsSapSystem : false,
	
	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}

		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	postCreate : function() {
		this._setupStores();
		this._initSourceSystemsTable();
		this._initLegacyIdInput();

		// connect the various elements to events
		dojo.connect(this, "onShow", this, "_myShow");
		dojo.connect(this.saveButton, "onClick", this, "_saveChanges");
		dojo.connect(this.revertButton, "onClick", this, "_revertChanges");
		dojo.connect(this.deleteButton, "onClick", this, "_deleteSourceSystem");
		dojo.connect(this.addSapSystemButton, "onClick", this, "_addSapSystem");
		dojo.connect(this.addNonSapSystemButton, "onClick", this, "_addNonSapSystem");
		dojo.connect(this.exportButton, "onClick", this, "_exportSourceSystem");
		dojo.connect(this.testConnectionButton, "onClick", this, "_testSapConnection");
		dojo.connect(this.sourceSystemsTable, "onSelected", this, "_sourceSystemSelectedInTable");
		dojo.connect(this.useLoadBalancingButton, "onChange", this, "_switchLoadBalancing");
		dojo.connect(this.legacyIdInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sourceDescriptionInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapMessageServerInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapSapSystemIdInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapGroupNameInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapRouterStringInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapHostInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapSystemNumberInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapUserInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapPasswordInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapClientInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapLanguageInput, "onKeyUp", this, "_sourceSystemDetailsChanged");
		dojo.connect(this.sapLanguageInput, "onChange", this, "_sourceSystemDetailsChanged");
		dojo.connect(window, "onresize", this, "_resizeContainer");
		
		dojo.setAttr(this.legacyIdInput, "maxlength", BdrAttributeLengths.CW_LEGACY_ID);
		
		this.inherited(arguments);
	},
	
	// private functions

	_myShow : function() {
		this.borderContainer.resize();
		this.sourceSystemsTable.render();
	},

	_resizeContainer: function(){
		var def = new dojo.Deferred();
		 
		setTimeout(function() {
			def.resolve({called: true});
		}, 300);
		 
		def.then(dojo.hitch(this, function() {
			this.borderContainer.resize();
		}));
	},
	
	// setup REST stores
	_setupStores : function() {
		this._systemStore = new dojox.data.JsonRestStore({
			target : Services.SOURCESYSTEMRESTURL,
			idAttribute : Services.SYSTEMIDATTRIBUTE,
			syncMode : "true",
		});
		
		this._overallStore = new dojox.data.JsonRestStore({
			target : Services.OVERALLSYSTEMRESTURL,
			idAttribute : Services.SYSTEMIDATTRIBUTE,
			syncMode : "true",
		});
	},
	
	// initialize the data grid by connecting it to the REST store
	_initSourceSystemsTable : function() {
		this.sourceSystemsTable.set("formatterScope", this);
		this.sourceSystemsTable.set("store", this._systemStore);
		this.sourceSystemsTable.set("sortInfo", "+1");
		this.sourceSystemsTable.set("selectionMode", "single");
		this.sourceSystemsTable.set("noDataMessage", this.msg.SOURCESYSTEMSPG_38);
		this.sourceSystemsTable.canSort = function(col) {
			if (Math.abs(col) == 2 || Math.abs(col) == 3) {
				return false;
			}
			else {
				return true;
			}
		};

		this.sourceSystemsTable.startup();
	},
	
	// initialize the legacy id input validation text box
	_initLegacyIdInput : function() {
		this.legacyIdInput.set("invalidMessage", this.msg.SOURCESYSTEMSPG_37);
		this.legacyIdInput.set("validator", dojo.hitch(this, function() {
			var legacyId = this.legacyIdInput.get("value");

			// see if an item with this legacy id already exists
			if (legacyId != "") {
				var item = this._findItemInOverallStoreByLegacyId(legacyId);
				
				if (item != null && item.length > 0) {
					this._enableSaveAndRevertButtons(false);
					return false;
				}
			}
			
			return true;
		}));
	},
	
	// store the changes a user has made
	_saveChanges : function() {
		var item = null;
		
		// if this is an update to an existing item we look for the item in the REST store using it's identity attribute
		if (this._currentSystemSelectionIndex != null && this._currentSystemSelectionIndex >= 0) {
			var id = this.sourceSystemsTable.getItem(this._currentSystemSelectionIndex).legacyId;
			this._systemStore.fetchItemByIdentity({
				identity: id,
				onItem: function(data) {
					item = data;
				}
			});
		}
		
		item = this._fromInput(item);
		this._savePassword(item);
		this._saveInStore(item, this._addMode);
		this._enableSaveAndRevertButtons(false);
		this.sourceSystemsTable.render();
	},
	
	// revert the changes a user has made by re-populating the input text boxes
	_revertChanges : function() {
		var deferred = null;
		
		this._resetInput();
		
		if (this._currentSystemSelectionIndex != null) {
			var item = this.sourceSystemsTable.getItem(this._currentSystemSelectionIndex);
			deferred = this._toInput(item);
		}
		
		if (deferred != null) {
			deferred.then(dojo.hitch(this, function() {
				this._sourceSystemDetailsChanged(undefined);
			}));		
		}
		else {
			this._sourceSystemDetailsChanged(undefined);
		}
	},
	
	// delete a selected source system
	_deleteSourceSystem : function() {
		if (this._currentSystemSelectionIndex != null) {
			var system = this.sourceSystemsTable.getItem(this._currentSystemSelectionIndex);
			var items = this._findItemInStore(system.legacyId);
		
			if (items.length != 0) {

				// we'll just grab the first of the returned items as the
				// result list normally should include only one item at all
				var item = items[0];
				this._systemStore.changing(item);
				this._systemStore.deleteItem(item);
				this._deletePassword(item);
				this._systemStore.save();
			}
		
			this.sourceSystemsTable.render();

			this._showPlaceHolderWidget(true);
		}
	},
	
	// populate the given item with the attributes read from the various
	// input text boxes or create a new item if the given item is null
	_fromInput : function(item) {
		if (item == null) {
			var system = new Object();

			if (this._currentSystemSelectedIsSapSystem) {
				system.isSapSystem = true;
			}
			else {
				system.isSapSystem = false;
			}
			
			system.description = this.sourceDescriptionInput.get("value");
			system.sapHost = this.sapHostInput.get("value");
			system.sapLanguage = this.sapLanguageInput.get("value");
			system.legacyId = this.legacyIdInput.get("value");
			system.sapClient = this.sapClientInput.get("value");
			system.sapPassword = this.sapPasswordInput.get("value");
			system.sapUseLoadBalancing = this.useLoadBalancingButton.get("checked");
			system.sapRouterString = this.sapRouterStringInput.get("value");
			system.sapUser = this.sapUserInput.get("value");
			system.sapSystemNumber = this.sapSystemNumberInput.get("value");
			system.sapSystemId = this.sapSapSystemIdInput.get("value");
			system.sapMessageServer = this.sapMessageServerInput.get("value");
			system.sapGroupName = this.sapGroupNameInput.get("value");

			return system;
		}
		else {
			if (this._currentSystemSelectedIsSapSystem) {
				item.isSapSystem = true;
			}
			else {
				item.isSapSystem = false;
			}
			
			item.description = this.sourceDescriptionInput.get("value");
			item.sapHost = this.sapHostInput.get("value");
			item.sapLanguage = this.sapLanguageInput.get("value");
			item.legacyId = this.legacyIdInput.get("value");
			item.sapClient = this.sapClientInput.get("value");
			item.sapPassword = this.sapPasswordInput.get("value");
			item.sapUseLoadBalancing = this.useLoadBalancingButton.get("checked");
			item.sapRouterString = this.sapRouterStringInput.get("value");
			item.sapUser = this.sapUserInput.get("value");
			item.sapSystemNumber = this.sapSystemNumberInput.get("value");
			item.sapSystemId = this.sapSapSystemIdInput.get("value");
			item.sapMessageServer = this.sapMessageServerInput.get("value");
			item.sapGroupName = this.sapGroupNameInput.get("value");
			
			return item;
		}
	},
	
	// populate the various text input boxes with attributes read from the given source system
	_toInput : function(legacySystem) {
		var deferred = new dojo.Deferred();

		if (legacySystem.isSapSystem) {
			this._systemIsSapSystem();
		}
		else {
			this._systemIsNonSapSystem();
		}
		
		this.sourceDescriptionInput.set("value", legacySystem.description);
		this.sapHostInput.set("value", legacySystem.sapHost);
		this.sapLanguageInput.set("value", legacySystem.sapLanguage);
		this.legacyIdInput.set("value", legacySystem.legacyId);
		this.sapClientInput.set("value", legacySystem.sapClient);
		this.useLoadBalancingButton.set("checked", legacySystem.sapUseLoadBalancing);
		this.sapRouterStringInput.set("value", legacySystem.sapRouterString);
		this.sapUserInput.set("value", legacySystem.sapUser);
		this.sapSystemNumberInput.set("value", legacySystem.sapSystemNumber);
		this.sapSapSystemIdInput.set("value", legacySystem.sapSystemId);
		this.sapMessageServerInput.set("value", legacySystem.sapMessageServer);
		this.sapGroupNameInput.set("value", legacySystem.sapGroupName);

		// retrieve session-stored SAP password via separate REST call
		dojo.xhrGet({
			url: Services.SAPPASSWORDRESTURL + "/" + legacySystem.legacyId,
			handleAs: "text",
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				this._setSapPasswordInput(data);
				deferred.callback();
			}),
			error: function(error) {
				idx.error({
					messageId: null,
					summary: error.responseText,
					detail: null,
					moreContent: null
				});
				
				deferred.errback();
			}
		});
		
		return deferred;
	},
	
	// reset (empty) the various text input boxes
	_resetInput : function() {
		this.sourceDescriptionInput.set("value", this._con.EMPTY_STRING);
		this.sapHostInput.set("value", this._con.EMPTY_STRING);
		this.sapLanguageInput.set("value", "EN");
		this.legacyIdInput.set("value", this._con.EMPTY_STRING);
		this.sapClientInput.set("value", this._con.EMPTY_STRING);
		this.sapPasswordInput.set("value", this._con.EMPTY_STRING);
		this.useLoadBalancingButton.set("checked", false);
		this.sapRouterStringInput.set("value", this._con.EMPTY_STRING);
		this.sapUserInput.set("value", this._con.EMPTY_STRING);
		this.sapSystemNumberInput.set("value", this._con.EMPTY_STRING);
		this.sapSapSystemIdInput.set("value", this._con.EMPTY_STRING);
		this.sapMessageServerInput.set("value", this._con.EMPTY_STRING);
		this.sapGroupNameInput.set("value", this._con.EMPTY_STRING);
		
		if (this._currentSystemSelectedIsSapSystem) {
			this._systemIsSapSystem();
		}
		else {
			this._systemIsNonSapSystem();
		}
		
		// force check of input text boxes to compute en- / disablement of various buttons
		this._sourceSystemDetailsChanged(undefined);
	},
	
	// persist a setting in the REST store
	_saveInStore : function(sourceSystem, addMode) {
		if (addMode) {
			this._systemStore.newItem(sourceSystem);
		}
		else {
			this._systemStore.changing(sourceSystem);
		}

		this._systemStore.save({alwaysPostNewItems: true});
		this.sourceSystemsTable.render();
		
		// switch to 'editing' mode by selecting the just saved entry in the grid
		if (addMode) {
			this._systemStore.fetch({
				query: {},
				onBegin: dojo.hitch(this, function(size, request) {
					this.sourceSystemsTable.selection.setSelected(size - 1, true);
				}),
				start: 0,
				count: 0
			});
		}
	},
	
	// look for specifically named items in the source legacy system REST store
	_findItemInStore : function(legacyId) {
		var item = null;
		
		// we'll query for the given id and return all found items
		this._systemStore.fetch({
			query:{legacyId:legacyId},
			onComplete: function(data) {
				item = data;
			}
		});
		
		return item;
	},
	
	// look for specifically id'd items in the overall (target + source) legacy system REST store
	_findItemInOverallStoreByLegacyId : function(legacyId) {
		var item = null;
		
		// we'll query for the given id and return all found items
		this._overallStore.fetch({
			query:{legacyId:legacyId},
			onComplete: function(data) {
				item = data;
			}
		});
		
		return item;
	},

	// upon clicking the Add button the user will be routed to
	// the various input text boxes by deselecting any item from the data grid
	// as well as setting the focus to the first input text box
	_addSourceSystem : function() {
		this._showPlaceHolderWidget(false);
		
		this._addMode = true;
		this.sourceSystemsTable.selection.clear();
		this.legacyIdInput.set("disabled", false);
		this.exportButton.set("disabled", true);
		this.deleteButton.set("disabled", true);
		this._enableSaveAndRevertButtons(false);
		this.revertButton.set("disabled", true);
		this._currentSystemSelectionIndex = null;
		this._resetInput();
	},
	
	// upon selecting a source system in the data grid the row index
	// will be saved in a member variable and different UI elements
	// will be modified
	_sourceSystemSelectedInTable : function(rowIndex) {
		this._showPlaceHolderWidget(false);
		
		this._addMode = false;

		if (rowIndex >= 0) {
			this.deleteButton.set("disabled", false);
			this._enableSaveAndRevertButtons(false);
		}
		
		this._currentSystemSelectionIndex = rowIndex;
		var item = this.sourceSystemsTable.getItem(rowIndex);
		
		// only enable the export button when a SAP system is selected
		if (item.isSapSystem) {
			this._currentSystemSelectedIsSapSystem = true;
			this.exportButton.set("disabled", false);
		}
		else {
			this._currentSystemSelectedIsSapSystem = false;
			this.exportButton.set("disabled", true);
		}
		
		var deferred = this._toInput(item);
		
		deferred.then(dojo.hitch(this, function() {
			this.legacyIdInput.set("disabled", true);
			this._enableSaveAndRevertButtons(false);
			this._checkForSapConnectionTestAvailability();
		}));
	},
	
	// enable / disable some of the text input boxes depending
	// on the usage of load balancing mode for a given SAP system
	_switchLoadBalancing : function(value) {
		this.sapMessageServerInput.set("disabled", !value);
		this.sapSapSystemIdInput.set("disabled", !value);
		this.sapGroupNameInput.set("disabled", !value);
		this.sapHostInput.set("disabled", value);
		
		this._sourceSystemDetailsChanged(undefined);
	},

	// check the values of the various input text boxes and enable / disable buttons accordingly
	_sourceSystemDetailsChanged : function(event) {
		var host = this.sapHostInput.get("value");
		var legacyId = this.legacyIdInput.get("value");
		var client = this.sapClientInput.get("value");
		var user = this.sapUserInput.get("value");
		var systemNumber = this.sapSystemNumberInput.get("value");
		var language = this.sapLanguageInput.get("value");
		var messageServer = this.sapMessageServerInput.get("value");
		var sapSystemId = this.sapSapSystemIdInput.get("value");
		var groupName = this.sapGroupNameInput.get("value");
		
		if (event == undefined || event == this._con.EMPTY_STRING) {
			this.revertButton.set("disabled", true);
		}
		else {
			this.revertButton.set("disabled", false);
		}
		
		if (this._currentSystemSelectedIsSapSystem) {
			if (this.useLoadBalancingButton.get("checked")) {
				if (host == "" || legacyId == "" || client == "" ||  user == ""
					|| systemNumber == "" || language == "" || messageServer == "" || sapSystemId == "" || groupName == "") {
						this._enableSaveAndRevertButtons(false);
				}
				else {
					if (event != undefined && event != this._con.EMPTY_STRING) {
						if (this._currentSystemSelectionIndex != null && this._currentSystemSelectionIndex >= 0) {
							if (this.sapSystemNumberInput.isValid(false) && this.sapClientInput.isValid(false)) {
								this._enableSaveAndRevertButtons(true);
							}
							else {
								this._enableSaveAndRevertButtons(false);
							}
						}
						else {
							if (this.legacyIdInput.isValid(false) && this.sapSystemNumberInput.isValid(false) && this.sapClientInput.isValid(false)) {
								this._enableSaveAndRevertButtons(true);
							}
							else {
								this._enableSaveAndRevertButtons(false);
							}
						}
					}
				}
			}
			else {
				if (host == "" || legacyId == ""
					|| client == "" ||  user == "" || systemNumber == "" || language == "") {
						this._enableSaveAndRevertButtons(false);
				}
				else {
					if (event != undefined && event != this._con.EMPTY_STRING) {
						if (this._currentSystemSelectionIndex != null && this._currentSystemSelectionIndex >= 0) {
							if (this.sapSystemNumberInput.isValid(false) && this.sapClientInput.isValid(false)) {
								this._enableSaveAndRevertButtons(true);
							}
							else {
								this._enableSaveAndRevertButtons(false);
							}
						}
						else {
							if (this.legacyIdInput.isValid(false) && this.sapSystemNumberInput.isValid(false) && this.sapClientInput.isValid(false)) {
								this._enableSaveAndRevertButtons(true);
							}
							else {
								this._enableSaveAndRevertButtons(false);
							}
						}
					}
				}
			}
		}
		else {
			if (legacyId == "") {
					this._enableSaveAndRevertButtons(false);
			}
			else {
				if (event != undefined && event != this._con.EMPTY_STRING) {
					if (this._currentSystemSelectionIndex != null && this._currentSystemSelectionIndex >= 0) {
						this._enableSaveAndRevertButtons(true);
					}
					else {
						if (this.legacyIdInput.isValid(false)) {
							this._enableSaveAndRevertButtons(true);
						}
						else {
							this._enableSaveAndRevertButtons(false);
						}
					}
				}
			}
		}
		
		this._checkForSapConnectionTestAvailability();
	},
	
	// enables / disables the save and revert buttons depending on the state
	// of various input text boxes and whether the app is in addMode or not
	_enableSaveAndRevertButtons : function(enabled) {
		this.saveButton.set("disabled", !enabled);
		this.revertButton.set("disabled", !enabled);
	},
	
	// enabled / disables the test connection button depending on the state
	// of various input text boxes
	_checkForSapConnectionTestAvailability : function() {
		var useLb = this.useLoadBalancingButton.get("checked");
		
		if (useLb) {
			var language = this.sapLanguageInput.get("value");
			var client = this.sapClientInput.get("value");
			var password = this.sapPasswordInput.get("value");
			var user = this.sapUserInput.get("value");
			var sapSystemId = this.sapSapSystemIdInput.get("value");
			var messageServer = this.sapMessageServerInput.get("value");
			var groupName = this.sapGroupNameInput.get("value");
			
			if (language == "" || client == "" || password == ""
				|| user == "" || sapSystemId == ""	|| messageServer == "" || groupName == "") {
				this.testConnectionButton.set("disabled", true);
			}
			else {
				this.testConnectionButton.set("disabled", false);
			}
		}
		else {
			var host = this.sapHostInput.get("value");
			var systemNumber = this.sapSystemNumberInput.get("value");
			var user = this.sapUserInput.get("value");
			var language = this.sapLanguageInput.get("value");
			var client = this.sapClientInput.get("value");
			var password = this.sapPasswordInput.get("value");
			
			if (host == "" || systemNumber == "" || user == "" || language == ""
				|| client == "" || password == "") {
				this.testConnectionButton.set("disabled", true);
			}
			else {
				this.testConnectionButton.set("disabled", false);
			}
		}
	},

	// save the SAP password in the session context
	_savePassword : function(item) {
		dojo.xhrPost({
			url: Services.SAPPASSWORDRESTURL + "/" + item.legacyId,
			handleAs: "text",
			postData: item.sapPassword,
			preventCache: true,
			load: function(data) {
			},
			error: function(error) {
				idx.error({
					messageId: null,
					summary: error.responseText,
					detail: null,
					moreContent: null
				});
			}
		});
	},
	
	// remove the SAP password from the session context
	_deletePassword : function(item) {
		dojo.xhrDelete({
			url: Services.SAPPASSWORDRESTURL + "/" + item.legacyId,
			handleAs: "text",
			preventCache: true,
			load: function(data) {
			},
			error: function(error) {
				idx.error({
					messageId: null,
					summary: error.responseText,
					detail: null,
					moreContent: null
				});
			}
		});		
	},
	
	// test the connection to the SAP system
	_testSapConnection : function() {
		var sapsystem = new Object();
		sapsystem = this._fromInput(sapsystem);
		
		// show loading dialog while trying to contact server
		idx.showProgressDialog(this.msg.SOURCESYSTEMSPG_40);

		dojo.xhrPost({
			url: Services.SAPCONNECTIONTESTRESTURL,
			handleAs: "json",
			postData: dojo.toJson(sapsystem),
			preventCache: true,
			load: dojo.hitch(this, "_displayConnectionTestSuccessInfo"),
			error: dojo.hitch(this, function(error) {
				this._displayConnectionTestFailure(error);
			})
		});
	},
	
	// display info dialog upon successful SAP connection test
	_displayConnectionTestSuccessInfo : function() {
		idx.hideProgressDialog();
		idx.info(this.msg.SOURCESYSTEMSPG_25);
	},
	
	// display error dialog upon unsuccessful RDM connection test
	_displayConnectionTestFailure : function(error) {
		var message = null;
		
		idx.hideProgressDialog();
		
		// depending on the response status we have to figure out the NLS message to display
		switch (error.status) {
		case this._con.REST_STATUS_INTERNAL_ERROR:
			message = this.msg.SOURCESYSTEMSPG_27;
			break;
		case this._con.REST_STATUS_CONNECTION_ERROR:
			message = this.msg.SOURCESYSTEMSPG_28;
			break;
		case this._con.REST_STATUS_UNAUTHORIZED:
			message = this.msg.SOURCESYSTEMSPG_26;
			break;
		default:
			message = this.msg.SOURCESYSTEMSPG_27;
			break;
		}
		
		// actually display the error dialog
		idx.error({
			messageId: null,
			summary: message,
			detail: null,
			moreContent: null
		});
	},

	// set the SAP password input text box to the given value
	_setSapPasswordInput : function(data) {
		this.sapPasswordInput.set("value", data);
	},
	
	_systemIsSapSystem : function() {
		this._currentSystemSelectedIsSapSystem = true;
		dojo.style(dojo.byId("sapSystemProperties"), {visibility: "visible"});
	},
	
	_systemIsNonSapSystem : function() {
		this._currentSystemSelectedIsSapSystem = false;
		dojo.style(dojo.byId("sapSystemProperties"), {visibility: "hidden"});
	},
	
	// data grid cell formatting function which will
	// return an html <img> tag depending on the cell value
	_displaySystemTypeColumn : function(value, rowIndex, cell) {
		
		// styling required to make the image look good in the cell
		cell.customClasses.push("imageColumn");
		
		var imageString = "";
		
		switch(value) {
		case true:
			imageString = Util.formatMessage(this._con.SYSTEM_CHECKED_IMG, this.msg.SOURCESYSTEMSPG_31);
			break;
		case false:
		default:
			imageString = Util.formatMessage(this._con.SYSTEM_UNCHECKED_IMG, this.msg.SOURCESYSTEMSPG_32);
			break;
		}
		
		return imageString;
	},
	
	// export the currently selected system into a CXP file that can be read by the
	// DataStage SAP Administrator tool
	_exportSourceSystem : function() {
		if (this._currentSystemSelectionIndex != null) {
			var item = this.sourceSystemsTable.getItem(this._currentSystemSelectionIndex);

			// as a dojo.io.iframe can be used only ONCE for sending a request
			// we need to find out whether such an iframe already exists and destroy
			// it before sending another dojo.io.iframe.send
			if (dojo.io.iframe["_frame"]) {
				dojo.destroy(dojo.io.iframe['_frame']);
				var frameName = dojo.io.iframe._iframeName;
				dojo.io.iframe["_frame"] = window[frameName] = null;
				if (window.frames) {
					window.frames[frameName] = null;
				}
			}
			
			dojo.io.iframe.send({
                url : Services.EXPORTSYSTEMTOCXPURL + "/" + item.legacyId,
                handleAs : "text",
                method : "GET"
            });
		}
	},
	
	_addSapSystem : function() {
		this._currentSystemSelectedIsSapSystem = true;
		this._addSourceSystem();
	},
	
	_addNonSapSystem : function() {
		this._currentSystemSelectedIsSapSystem = false;
		this._addSourceSystem();
	},
	
	_showPlaceHolderWidget : function(show) {
		if (show) {
			dojo.style(this.border.domNode, {visibility : "hidden"});
			dojo.style(dojo.byId("sapSystemProperties"), {visibility : "hidden"});
			dojo.style(dojo.byId("sourceSapSystemPlaceHolderWidget"), {display : "inline-block"});
		}
		else {
			dojo.style(dojo.byId("sourceSapSystemPlaceHolderWidget"), {display : "none"});
			dojo.style(this.border.domNode, {visibility : "visible"});
		}
	}
});
