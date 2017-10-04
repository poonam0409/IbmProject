dojo.provide("cwapp.tab_bdr.bph.DetailsPanel");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dojo.date.locale");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");
dojo.require("cwapp.main.FilteringSelectStatic");
dojo.require("cwapp.tab_bdr.bph.TabDescription");
dojo.require("cwapp.tab_bdr.bph.TabUsedIn");
dojo.require("cwapp.tab_bdr.bph.TabTransactions");
dojo.require("cwapp.tab_bdr.bph.TabFields");
dojo.require("cwapp.tab_bdr.bph.TabAccessInfoForProcessStep");
dojo.require("cwapp.tab_bdr.bph.TabAccessInfoForProcess");

dojo.require("cwapp.tab_bdr.bph.HandleUnsavedChangesDialog");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.DetailsPanel", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/DetailsPanel.html"),
	widgetsInTemplate : true,

	// nls support
	msg : {},
	
	// constants
	_con : {
		IMAGE_CLASS_ICON_BASE : "bdrHeaderIcon",
		IMAGE_CLASS_ICON_PROCESS : "ImageProcess_64",
		IMAGE_CLASS_ICON_PROCESS_STEP : "ImageProcessStep_64",
		IMAGE_CLASS_ICON_BO : "ImageBusinessObject_64",
		IMAGE_CLASS_ICON_TABLE : "ImageTable_64",
		IMAGE_CLASS_ICON_TABLE_USAGE : "ImageTableUsage_64",
		IMAGE_CLASS_STATUS_APPROVED : "DetailsPanel_approvedImage",
		IMAGE_CLASS_STATUS_PENDING_APPROVAL : "DetailsPanel_pendingImage",
		IMAGE_CLASS_STATUS_DRAFT : "DetailsPanel_draftImage",
		IMAGE_CLASS_STATUS_MIXED : "DetailsPanel_mixedImage",
		IMAGE_CLASS_STATUS_UNDEFINED : "",
		JSON_APPROVAL_STATUS_ADMIN : "cwapp/data/bdr_approval_status_administrator.json",
		JSON_APPROVAL_STATUS_ADMIN_DRAFT : "cwapp/data/bdr_approval_status_administrator_draft.json",
		JSON_APPROVAL_STATUS_ADMIN_PENDING : "cwapp/data/bdr_approval_status_administrator_pending.json",
		JSON_APPROVAL_STATUS_ADMIN_APPROVED : "cwapp/data/bdr_approval_status_administrator_approved.json",
		JSON_APPROVAL_STATUS_FDA : "cwapp/data/bdr_approval_status_fda.json",
		JSON_APPROVAL_STATUS_FDA_DRAFT : "cwapp/data/bdr_approval_status_fda_draft.json",
		JSON_APPROVAL_STATUS_FDA_PENDING : "cwapp/data/bdr_approval_status_fda_pending.json",
	},

	// private variables
	_caller : null,
	_url : null,
	_type : null,
	_parentObjectId : null, // The ID of the parent tree object for the newly created one (if creating one)
	_objectId : null, // The ID of the current object (if editing an existing object)
	_addMode : false, // True if we are creating a new object, false if we are editing an existing one
	_originalItem : null, // The complete original item that we are editing, if any
	_connects : [],

	
	
	// public methods
	
	constructor : function(args) {
		if (args) {
			dojo.mixin(this, args);
		}
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},

	postCreate : function() {
		
		// connect control elements to events
		dojo.connect(this.saveButton, "onClick", this, "_save");
		dojo.connect(this.discardButton, "onClick", this, "_discardChanges");
		dojo.connect(this.approvalButton, "onKeyUp", this, "_approvalStatusChanged");
		dojo.connect(this.approvalButton, "onChange", this, "_approvalStatusChanged");

		this.inherited(arguments);
	},
	
	inAddMode : function() {
		return this._addMode;
	},
	
	// Enables the save/discard buttons.
	// Should be called by the child tabs as soon as an edit has been made.
	enableButtons : function() {
		this.discardButton.set("disabled", this._addMode);
		this.saveButton.set("disabled", false);
	},
	
	// Disables the save/discard buttons.
	// Should be called by the child tabs.
	disableButtons : function() {
		this.discardButton.set("disabled", true);
		this.saveButton.set("disabled", true);
		
		// clear any selected value from the approval status filtering select
		if(this.approvalButton.get("value") != "") {
			this.approvalButton.reset();
		}
	},
	// Sets all internal variables.
	update : function(caller, type, objectId, parentObjectId, originalItem, skipHash) {

		this.startup();
		
		if (objectId != null) {
			// Editing existing object
			this._addMode = false;
		} else {
			// Creating new object
			this._addMode = true;
		}
		
		this._caller = caller;
		this._objectId = objectId;
		this._parentObjectId = parentObjectId;
		this._originalItem = originalItem;
		this._type = type;

		this._setupType();
		
		if (this._addMode) {
			this._reset();
		} else {
			this._load();
		}
	},
	
	// private methods
	
	// Sets up the widget according to the selected object type,
	// including URLs, child tabs etc.
	_setupType : function(){
		dojo.forEach(this._connects, function(handle, index) {
			dojo.disconnect(handle);
		});
		
		// Remove all child tabs, so we can add the relevant ones according to the object type
		var tabs = this.itemTabContainer.getChildren();
		if (tabs) {
			for (var i = 0; i < tabs.length; i++) {
				this.itemTabContainer.removeChild(tabs[i]);
			}
		}
		
		switch(this._type) {
			case BdrTypes.PROCESS:
				this._setIcon(this._con.IMAGE_CLASS_ICON_PROCESS);
				this.headerName.innerHTML = this.msg.BphTitleAddProcess;
				this.headerType.innerHTML = this.msg.BphProcess;
				this._url = Services.BDR_PROCESS;
				this._addDescriptionTab();
				this._addAccessInformationTab();
				break;
			case BdrTypes.PROCESS_STEP:
				this._setIcon(this._con.IMAGE_CLASS_ICON_PROCESS_STEP);
				this.headerName.innerHTML = this.msg.BphTitleAddProcessStep;
				this.headerType.innerHTML = this.msg.BphProcessStep;
				this._url = Services.BDR_PROCESS_STEP;
				this._addDescriptionTab();
				this.itemTabContainer.addChild(this.tabTransactions);
				this._addAccessInformationTab();
				break;
			case BdrTypes.BO:
				this._setIcon(this._con.IMAGE_CLASS_ICON_BO);
				this.headerName.innerHTML = this.msg.BphTitleAddBusinessObject;
				this.headerType.innerHTML = this.msg.BphBusinessObject;
				this._url = Services.BDR_BO;
				this._addDescriptionTab();
				this.itemTabContainer.addChild(this.tabUsedIn);
				break;
			case BdrTypes.TABLE:
				this._setIcon(this._con.IMAGE_CLASS_ICON_TABLE);
				this.headerName.innerHTML = this.msg.BphTitleAddTable;
				this.headerType.innerHTML = this.msg.BphTable;
				this._url = Services.BDR_TABLE;
				if (!this._addMode) {
					this.itemTabContainer.addChild(this.tabFields);
				}
				this._addDescriptionTab();
				this.itemTabContainer.addChild(this.tabUsedIn);
				break;
			case BdrTypes.TABLE_USAGE:
				this._setIcon(this._con.IMAGE_CLASS_ICON_TABLE_USAGE);
				this.headerName.innerHTML = "";
				this.headerType.innerHTML = this.msg.BphTableUsage;
				this._url = Services.BDR_TABLE_USAGE;
				this.itemTabContainer.addChild(this.tabFields);
				break;
		}
		
		// Set the caller on the child tabs
		dojo.forEach(this.itemTabContainer.getChildren(), dojo.hitch(this, function(child){
			 child.setCaller(this);
		}));
		
		this.itemTabContainer.resize();
				
		this._connects.push(dojo.connect(this.itemTabContainer, "selectChild", dojo.hitch(this, function(tab) {	dojo.publish(Topics.DETAILS_TAB_SELECTED, tab); })));
	},
	
	// Add the description tab
	_addDescriptionTab : function() {
		this.itemTabContainer.addChild(this.tabDescription);
		this.tabDescription.setType(this._type);
	},
	
	// Add the Access information tab if allowed
	_addAccessInformationTab : function() {
		
		// the access information tab is only added if it the currently authenticated user has the correct role
		if (General.ISBDRSUPERUSER) {
			switch (this._type) {
				case BdrTypes.PROCESS : {
					this.itemTabContainer.addChild(this.tabAccessInfoForProcess);
					break;
				}
				case BdrTypes.PROCESS_STEP : {
					this.itemTabContainer.addChild(this.tabAccessInfoForProcessStep);
					break;
				}
				default: {
					break;
				}
			}
		}
	},

	// Sets the header icon.
	_setIcon : function(iconClass) {
		dojo.removeClass(this.headerImage);
		dojo.addClass(this.headerImage, this._con.IMAGE_CLASS_ICON_BASE);
		dojo.addClass(this.headerImage, iconClass);
	},
	
	// Loads the details for the selected object from the server.
	// Calls update() on all child tabs with the new data.
	_load : function() {
		dojo.xhrGet({
			url: this._url + "/" + this._objectId,
			handleAs: "json",
			preventCache : true,
			sync : true,
			load : dojo.hitch(this, function(data) {
				this._updateContent(data);
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
				// TODO hide panel, probably switch to placeholder
			})
		});
	},
	
	// Resets the view.
	// Called when switching to "add new object" mode.
	_reset : function() {
		this.disableButtons();
		dojo.forEach(this.itemTabContainer.getChildren(), dojo.hitch(this, function(child){
			 child.reset();
		}));
	},
	
	// Update the view, including all child tabs.
	// Called when the selected object changes.
	_updateContent : function(item) {
		this.disableButtons();

		this.headerName.innerHTML = item.name;
		
		if (item.type == BdrTypes.TABLE_USAGE) {
			this.headerName.innerHTML = item.fullName;
		}
		
		this._setApprovalButtonStore(item.approvalStatus);
		if(General.ISREADONLY){
		    this.approvalButton.set("disabled", true);
		}
		
		if (!this._addMode) {
			dojo.style(this.approvalStatusIndicator, {visibility : "visible"});
			dojo.style(this.approvalButton.domNode, {visibility : "visible"});

			// Item type specific adjustments
			switch (item.type) {
				case BdrTypes.BO:
				case BdrTypes.TABLE:
				
					// The approval feature does not apply to tables and BOs
					dojo.style(this.approvalStatusIndicator, {visibility : "hidden"});
					dojo.style(this.approvalButton.domNode, {visibility : "hidden"});
					break;
				case BdrTypes.TABLE_USAGE:
				
					// Keep the selection dropdown visible for table usages
					break;
				default:
					
					dojo.style(this.approvalButton.domNode, {visibility : "hidden"});
					break;
			}
		}
		else {
			dojo.style(this.approvalStatusIndicator, {visibility : "hidden"});
			dojo.style(this.approvalButton.domNode, {visibility : "hidden"});
		}
		
		if (!item.updated) {
			this.headerLastUpdated.innerHTML = "&nbsp;";
		} else {
			this.headerLastUpdated.innerHTML = Util.formatMessage(this.msg.BphObjectLastUpdated, Util.formatDate(item.updated, null));
		}
		
		this._setApprovalStatus(item);

		// Call the update method on each child tab
		dojo.forEach(this.itemTabContainer.getChildren(), dojo.hitch(this, function(child){
			 child.update(item);
		}));
	},
	
	// changes that have been made by the user are discarded which means we simply reload
	// the data from the server
	_discardChanges : function() {
		switch (this._type) {
			case BdrTypes.TABLE :
			case BdrTypes.TABLE_USAGE : {
				if (!this.inAddMode()) {
					this.tabFields.discardButtonClicked();
				}
	
				break;
			}
			default : {
				
				// do nothing special
				break;
			}
		}
		
		this.disableButtons();
		this._load();
	},
	
	// Saves the selected item, including data from child tabs.
	_save : function() {
		switch (this._type) {
		case BdrTypes.PROCESS:
			this._saveData();
			break;
		case BdrTypes.PROCESS_STEP:
			this._saveData(this.tabAccessInfoForProcessStep);
			break;
		case BdrTypes.BO:
			this._saveData();
			break;
		case BdrTypes.TABLE:
		case BdrTypes.TABLE_USAGE:
			if(!this.inAddMode()) {
				this.tabFields.saveButtonClicked();
			}
			this._saveData();
		}
	},

	// Actually saves the item data
	_saveData : function(tabAccessInfoForProcessStep) {
		// gather the data that is about to be saved
		var saveData = dojo.toJson(this._collectData());
		var deferred = null;
		var url = this._url;
		if (!this._addMode) {
			url = this._url + "/" + this._objectId;
		}

		var xhrArgs = {
			url: url,
			headers: {"Content-Type": "application/json"},
			handleAs: "json",
			postData: saveData,
			preventCache : true,
			sync : true,
			load : dojo.hitch(this, function(data) {
			
				if(tabAccessInfoForProcessStep&&tabAccessInfoForProcessStep.isAvailable()){
					deferred = tabAccessInfoForProcessStep.save(data);
					deferred.then(dojo.hitch(this, function() {
						if (this._addMode) {
							// Refresh nav panel and highlight new item
							// (also refreshes this panel in the end)
							this._caller.refreshForAddedItem(data);

							this._objectId = data.id;
							this._addMode = false;
						} else {
							// Refresh nav panel
							this._caller.refresh();
						}

						// Refresh this panel
						this._load();
					}));
				}else{
					if (this._addMode) {
						// Refresh nav panel and highlight new item
						// (also refreshes this panel in the end)
						this._caller.refreshForAddedItem(data);

						this._objectId = data.id;
						this._addMode = false;
					} else {
						// Refresh nav panel
						this._caller.refresh();
					}

					// Refresh this panel
					this._load();
				}
				
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		};
			
		if (this._addMode) {
			// for adding a new item, we must use a POST request
			dojo.xhrPost(xhrArgs);
		} else {
			// for updating an existing item, a PUT request
			dojo.xhrPut(xhrArgs);
		}
	},

	// Collect object data from child tabs
	_collectData: function() {
		var saveData = {};
		
		if (this._originalItem) {
			saveData = this._originalItem;
		}
		
		switch(this._type) {
			case BdrTypes.PROCESS: {
				this._mergeData(saveData, this.tabDescription.getData());
				if (this._addMode && this._parentObjectId) {
					saveData.parentProcess = {id: this._parentObjectId};
				}
				break;
			}
			case BdrTypes.PROCESS_STEP: {
				this._mergeData(saveData, this.tabDescription.getData());
				this._mergeData(saveData, this.tabTransactions.getData());
				if (this._addMode) {
					saveData.parentProcess = {id: this._parentObjectId};
				}
				break;
			}
			case BdrTypes.BO: {
				this._mergeData(saveData, this.tabDescription.getData());
				break;
			}
			case BdrTypes.TABLE: {
				this._mergeData(saveData, this.tabDescription.getData());
				break;
			}
			case BdrTypes.TABLE_USAGE: {
				
				// if the approval status button has a value of "" it means it
				// isn't set which requires us to go on with the approvalStatus
				// of the table usage object that is currently set 
				if (this.approvalButton.get("value") != "") {
					this._mergeData(saveData, {approvalStatus : this.approvalButton.get("value")});
				}
				
				break;
			}
		}
		
		return saveData;
	},
	
	// Adds the data from the updated object to the original one, overwriting any overlaps
	_mergeData: function(original, update) {
		for (var attrName in update) {
			original[attrName] = update[attrName];
		}
	},
	
	// the approval status filtering select was changed so we need to trigger any save / discard buttons
	_approvalStatusChanged : function() {
		var approvalStatus = this.approvalButton.get("value");
		
		if (approvalStatus != "") {
			this.enableButtons();
		}
		else {
			this.disableButtons();
		}
	},
	
	// set the approval status indicator (icon and label)
	_setApprovalStatus : function(selectedItem) {
		this._clearImageClasses();

		this.approvalStatusLabel.innerHTML = this._translateApprovalStatus(selectedItem.approvalStatus);
		switch (selectedItem.approvalStatus) {
			case BdrApprovalStatusCodes.DRAFT : {
				dojo.addClass(this.approvalStatusImage, this._con.IMAGE_CLASS_STATUS_DRAFT);
				break;
			}
			case BdrApprovalStatusCodes.PENDING_APPROVAL : {
				dojo.addClass(this.approvalStatusImage, this._con.IMAGE_CLASS_STATUS_PENDING_APPROVAL);
				break;
			}
			case BdrApprovalStatusCodes.APPROVED : {
				dojo.addClass(this.approvalStatusImage, this._con.IMAGE_CLASS_STATUS_APPROVED);
				break;
			}
			case BdrApprovalStatusCodes.MIXED : {
				dojo.addClass(this.approvalStatusImage, this._con.IMAGE_CLASS_STATUS_MIXED);
				break;
			}
			case BdrApprovalStatusCodes.UNDEFINED : {
				dojo.addClass(this.approvalStatusImage, this._con.IMAGE_CLASS_STATUS_UNDEFINED);
				break;
			}
			default : {
				dojo.addClass(this.approvalStatusImage, this._con.IMAGE_CLASS_STATUS_UNDEFINED);
				break;
			}
		}
	},
	
	// remove any image classes from the approval status indicator icon
	_clearImageClasses : function() {
		dojo.removeClass(this.approvalStatusImage);
	},
	
	// retrieve the translated approval status message
	_translateApprovalStatus : function(statusCode) {
		switch (statusCode) {
			case 0 : {
				return this.msg.BphApprovalStatusDraft;
			}
			case 1 : {
				return this.msg.BphApprovalStatusPending;
			}
			case 2 : {
				return this.msg.BphApprovalStatusApproved;
			}
			case 3 : {
				return this.msg.BphApprovalStatusMixed;
			}
			case -1 : {
				return this.msg.BphApprovalStatusUndefined;
			}
			default : {
				return this.msg.BphApprovalStatusUndefined;
			}
		}
	},
	
	// set the store of the approval button depending on the currently selected approval status
	_setApprovalButtonStore : function(approvalStatus) {
		var approvalStoreUrl = "";

		switch (approvalStatus) {
			case 0 : {
				if (General.ISBDRSUPERUSER) {
					approvalStoreUrl = this._con.JSON_APPROVAL_STATUS_ADMIN_DRAFT;
				} else {
					approvalStoreUrl = this._con.JSON_APPROVAL_STATUS_FDA_DRAFT;
				}
				
				this.approvalButton.set("store", new dojo.data.ItemFileReadStore({url: approvalStoreUrl}));
				break;
			}
			case 1 : {
				if (General.ISBDRSUPERUSER) {
					approvalStoreUrl = this._con.JSON_APPROVAL_STATUS_ADMIN_PENDING;
				} else {
					approvalStoreUrl = this._con.JSON_APPROVAL_STATUS_FDA_PENDING;
				}
				
				this.approvalButton.set("store", new dojo.data.ItemFileReadStore({url: approvalStoreUrl}));
				break;
			}
			case 2 : {
				if (General.ISBDRSUPERUSER) {
					approvalStoreUrl = this._con.JSON_APPROVAL_STATUS_ADMIN_APPROVED;
					this.approvalButton.set("store", new dojo.data.ItemFileReadStore({url: approvalStoreUrl}));
				}
				
				break;
			}
			default : {
				if (General.ISBDRSUPERUSER) {
					approvalStoreUrl = this._con.JSON_APPROVAL_STATUS_ADMIN;
				} else {
					approvalStoreUrl = this._con.JSON_APPROVAL_STATUS_FDA;
				}
				
				this.approvalButton.set("store", new dojo.data.ItemFileReadStore({url: approvalStoreUrl}));
				break;
			}
		}
	},
		
	hideWidget : function() {
		dojo.style(this.domNode, "display", "none");
	},
	
	showWidget : function() {
	    	dojo.style(this.domNode, "display", "");
	},
	
	onHideAttempt : function(event, parentWidget) {
	    this._handleChanges(event, parentWidget);
	},
	
	// check if there are changes and handle them if exist
	_handleChanges : function(event, parentWidget) {
		//we check if there are unsaved changes
		if (this.saveButton.disabled == false){
		    var dialog = new cwapp.tab_bdr.bph.HandleUnsavedChangesDialog({
			parentWidget : this.id,
			widgetWithUnsavedData : parentWidget,
			triggeringElement : event.target
			});
		    dialog.show();
		}
	}
});