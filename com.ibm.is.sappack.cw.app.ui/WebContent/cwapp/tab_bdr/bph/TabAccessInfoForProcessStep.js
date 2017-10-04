dojo.provide("cwapp.tab_bdr.bph.TabAccessInfoForProcessStep");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");

dojo.require("idx.layout.BorderContainer");

dojo.require("dijit.form.Button");

dojo.require("dojo.domReady!");

dojo.require("dojo.store.Memory");
dojo.require("dojo.data.ObjectStore");

dojo.require("dojox.grid.DataGrid");
dojo.require("dojox.widget.Standby");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.TabAccessInfoForProcessStep", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/TabAccessInfoForProcessStep.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// private constants
	_con : {
		EMPTY_STRING : "",
		REGISTRY_OBJECT_USER : "user",
		REGISTRY_OBJECT_GROUP : "group",
	    USER_IMG :
			'<img src="cwapp/img/bdr/user_16.png" title="{0}" width="16px" height="16px" alt="isUser" border="0px" align="left"/>',
		GROUP_IMG :
			'<img src="cwapp/img/bdr/group_16.png" title="{0}" width="16px" height="16px" alt="isGroup" border="0px" align="left"/>',
	},
	
	// private members
	_caller : null,
	_connects : [],
	_subscribes : [],
	_selectedObject : "",
	_registryDataCopy : null,
	_wasRegistryDataStore : null,
	_objectAccessDataStore : null,
	_objectAccessGridSelection : null,
	_wasRegistryGridSelection : null,
	_isActiveTab : false,
	
	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this, args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	postCreate : function() {
		
		// startup the enclosed border container
		this.borderContainer.startup();
		
		// initialize the object access data grid with the write store
		this._initGrid(this.objectAccessGrid, this._objectAccessDataStore);
		
		// initialize the was registry data grid with the write store
		this._initGrid(this.wasRegistryGrid, this._wasRegistryDataStore);
		
		// connect to various events
		dojo.connect(window, "onResize", this, "_myResize");
		
		dojo.connect(this.wasRegistryGrid, "onSelectionChanged", this, "_wasRegistryGridRowSelected");
		dojo.connect(this.wasRegistryGrid, "_onFetchComplete", dojo.hitch(this, function() {	  	  	
	        if (this.wasRegistryGrid.rowCount != 0) {
	        	this.addAllButton.set("disabled", false);
	        }
	        else {
	        	this.addAllButton.set("disabled", true);
	        }
		}));
		
		dojo.connect(this.objectAccessGrid, "onSelectionChanged", this, "_objectAccessGridRowSelected");
		dojo.connect(this.objectAccessGrid, "_onFetchComplete", dojo.hitch(this, function() {	  	  	
	        if (this.objectAccessGrid.rowCount != 0) {
	        	this.removeAllButton.set("disabled", false);
	        }
	        else {
	        	this.removeAllButton.set("disabled", true);
	        	this.removeButton.set("disabled", true);
	        }
		}));
		
		dojo.connect(this.addButton, "onClick", this, "_addButtonClicked");
		dojo.connect(this.addAllButton, "onClick", this, "_addAllButtonClicked");
		dojo.connect(this.removeButton, "onClick", this, "_removeButtonClicked");
		dojo.connect(this.removeAllButton, "onClick", this, "_removeAllButtonClicked");

		var tabSelectedSub = dojo.subscribe(Topics.DETAILS_TAB_SELECTED, dojo.hitch(this, function(tab) {
			if (tab.id == this.id) {
				this._isActiveTab = true;
				this._myShow();
			}
			else {
				this._isActiveTab = false;
			}
		}));
		this._subscribes.push(tabSelectedSub);
		
		this.inherited(arguments);
	},
	
	uninitialize : function() {
		dojo.forEach(this._subscribes, function(handler, index) {
			dojo.unsubscribe(handler);
		});

		this.inherited(arguments);
	},
	
	setCaller : function(caller) {
		this._caller = caller;
		
		// clear any existing connections
		dojo.forEach(this._connects, function(entry, index) {
			dojo.disconnect(entry);
		});
		
		// empty the connection handles array
		this._connects = [];
		
		if (!this._caller.inAddMode()) {
			// connect to the caller's buttons for change handling
			this._connects.push(dojo.connect(this._caller.discardButton, "onClick", this, "_discardButtonClicked"));
		}
	},
	
	update : function(selectedObject) {
		if (!selectedObject) {
			return;
		}
		
		// store the selected table
		this._selectedObject = selectedObject;
		this._updatePage();
	},

	reset : function() {

		// the parent object calls the 'reset' method whenever it is set to 'add' mode in which
		// case it does not make sense at all to display this tab page
		// therefore we hide it
		// (note: should there ever be the need to manually enable the display of the tab
		//        at another time we should use this function:
		//        dojo.style(this.controlButton.domNode, {display: "inline-block"})
		// )
		dojo.style(this.controlButton.domNode, {display: "none"});
	},
	
	getData : function() {

		// do nothing
	},
	
    save : function(itemToBeSaved) {
    	var deferred = new dojo.Deferred();
    	
    	dojo.xhrPost({
			url : Services.BDR_ACCESSINFO_SERVICE + "/" + itemToBeSaved.type + "/" + itemToBeSaved.id,
			headers: {"Content-Type" : "application/json"},
			handleAs : "json",
			postData: dojo.toJson(this._objectAccessDataStore.objectStore.data),
			load : dojo.hitch(this, function(response, ioArgs) {
				deferred.callback();
			}),
			error : dojo.hitch(this, function(error) {
				deferred.errback(error);
			})
		});
    	
    	return deferred;
	},
	
	isAvailable : function() {
		if(this.controlButton)
			return (dojo.getStyle(this.controlButton.domNode, "display") != "none");
		else 
			return null;
	},
	
	_myShow : function() {
		
		// layout the enclosed border container
		this._myResize();
		this._clearGridSelections();
		
		if (this._selectedObject != "") {
			
			// destroy any existing data stores
			this._destroyDataStores();
			
			// create the stores
			if (!this._wasRegistryDataStore && !this._objectAccessDataStore) {
				this._setupStores(this._isActiveTab);
				
				if (this._isActiveTab)  {
					this._setupGrids();
				}
			}
		}
	},
	
	_myResize : function() {
		// layout the enclosed border container
		this.borderContainer.resize();
	},
	
	// a new BPH object has been selected so we need to update the page completely
	_updatePage : function() {
		this._myShow();
		this._resetButtonStates();
	},
	
	// destroy the existing data stores
	_destroyDataStores : function() {
		if (this._wasRegistryDataStore) {
			delete this._wasRegistryDataStore;
		}

		if (this._objectAccessDataStore) {
			delete this._objectAccessDataStore;
		}
	},
	
	// //compare method for two objects of the object Store. First sorts after the type and then alphabetic
	_compareObjectStoreItems : function (a,b){
		if (a.type == b.type){
			return(a.securityName==b.securityName)?0:(a.securityName>b.securityName)?1:-1;
		}
		else{
			if (a.type == "user"){
				return 1;
			}
			return -1;
		}		
	},
	
	_setupGrids : function() {
		//	the object access data grid only needs to be refreshed with the new data store
		this.wasRegistryGrid.set("store", this._wasRegistryDataStore);
		this.wasRegistryGrid.render();
		
		// the object access data grid only needs to be refreshed with the new data store
		this.objectAccessGrid.set("store", this._objectAccessDataStore);
		this.objectAccessGrid.render();
	},
	
	_setupStores : function(tabIsActive) {
		this._setupObjectAccessDataStore(tabIsActive);
		this._setupWASRegistryDataStore(tabIsActive);
		
		//remove the unnecessary data
		this._removeUnnecessaryData(this._wasRegistryDataStore, this._objectAccessDataStore);
	},
	
	// removes data from data store A store, which exists at the data store B also
	_removeUnnecessaryData : function(dataStoreA, dataStoreB){
		dojo.forEach(dataStoreB.objectStore.data, function(item, index) {
			if (dataStoreA.objectStore.get(item.uniqueId)){
				dataStoreA.objectStore.remove(item.uniqueId);
			}
		});	
	},
	
	_initGrid : function(grid, store){
		grid.set("store", new dojo.data.ObjectStore({
			objectStore : store
		}));
		grid.set("formatterScope", this);
		grid.set("editable", false);
		grid.set("selectable", false);
		grid.set("selectionMode", "extended");

		// startup the grid and have it rendered
		grid.startup();
	},
	
	_setupObjectAccessDataStore : function(renderDataGrid) {
		
		// create a access data store for the current selected object
		this._objectAccessDataStore = new dojo.data.ObjectStore({
			objectStore : new dojo.store.Memory({
				idProperty : "uniqueId"
			}),
			hasItem : function(item) {
				var deferred = new dojo.Deferred;
				var found = false;
				
				this.fetch({
					onComplete : dojo.hitch(this, function(items) { 
						dojo.forEach(items, function(entry, index) {
							if (entry.uniqueId == item.uniqueId) {
								found = true;
							}
						});
						
						deferred.callback({found : found});
					}), 
					onError : function(error){ 
						deferred.errback(error);
					}
				});
				
				return deferred;
			},
			onNew : dojo.hitch(this, function() {
				
				// whenever an item has been added to the object access data store we enable the 'save/discard' buttons
				this._caller.enableButtons();
			}),
			onDelete : dojo.hitch(this, function() {
				
				// whenever an item has been removed from the object access data store we enable the 'save/discard' buttons
				this._caller.enableButtons();
			})
		});
		
		// populate the object access memory store with data
		dojo.xhrGet({
			url : Services.BDR_ACCESSINFO_SERVICE + "/" + this._selectedObject.type + "/" + this._selectedObject.id,
			handleAs : "json",
			sync : true,
			load : dojo.hitch(this, function(data) {
				data.sort(this._compareObjectStoreItems);
				this._objectAccessDataStore.objectStore.setData(data);
				// the data is not empty so we can safely enable the 'removeAllButton'
				if (data.length > 0) {
					this.removeAllButton.set("disabled", false);
				}
				
				if (renderDataGrid) {
					this.objectAccessGrid.render();
				}
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	_setupWASRegistryDataStore : function(renderDataGrid) {
		
		// create a was registry data store
		this._wasRegistryDataStore = new dojo.data.ObjectStore({
			objectStore : new dojo.store.Memory({
				idProperty : "uniqueId"
			}),
			hasItem : function(item) {
				var deferred = new dojo.Deferred;
				var found = false;
				
				this.fetch({
					onComplete : dojo.hitch(this, function(items) { 
						dojo.forEach(items, function(entry, index) {
							if (entry.uniqueId == item.uniqueId) {
								found = true;
							}
						});
						
						deferred.callback({found : found});
					}), 
					onError : function(error){ 
						deferred.errback(error);
					}
				});
				
				return deferred;
			},
		});
		
		// populate the was registry data store with data
		dojo.xhrGet({
			url : Services.BDR_ACCESSINFO_SERVICE,
			handleAs : "json",
			sync : true,
			load : dojo.hitch(this, function(data) {
				data.sort(this._compareObjectStoreItems);
				//make a copy of the data for later use
				this._registryDataCopy = dojo.clone(data);
				this._wasRegistryDataStore.objectStore.setData(data);
				// the data is not empty so we can safely enable the 'removeAllButton'
				if (data.length > 0) {
					this.addAllButton.set("disabled", false);
				}
				
				if (renderDataGrid) {
					this.wasRegistryGrid.render();
				}
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
    
    _objectAccessGridRowSelected : function() {
    	this._objectAccessGridSelection = this.objectAccessGrid.selection.getSelected();
    	
    	if (this._objectAccessGridSelection && this._objectAccessGridSelection.length != 0) {
    		this.removeButton.set("disabled", false);
    	}
    	else {
    		this.removeButton.set("disabled", true);
    	}
    },
    
    _wasRegistryGridRowSelected : function() {
    	this._wasRegistryGridSelection = this.wasRegistryGrid.selection.getSelected();
    	
    	if (this._wasRegistryGridSelection && this._wasRegistryGridSelection.length != 0) {
    		this.addButton.set("disabled", false);
    	}
    	else {
    		this.removeButton.set("disabled", true);
    	}
    },

    _addButtonClicked : function() {
    	this._manageAddActivities(this._wasRegistryGridSelection);
    	this.addButton.set("disabled", true);
    },
    
    _addAllButtonClicked : function() {
    	this._manageAddActivities(dojo.clone(this._registryDataCopy));
    	this._resetButtonStates();
    	this.removeAllButton.set("disabled", false);
    },
    
    //adds items to the access data store and removes them from the registry data store
    _manageAddActivities : function(items){
    	if (items && items != 0) {
    		dojo.forEach(items, dojo.hitch(this, function(item, index) {
    			this._wasRegistryDataStore.deleteItem(item);
    			this._wasRegistryDataStore.save();
    		
    			if(this._objectAccessDataStore.hasItem(item))
    			{
    				this._objectAccessDataStore.newItem(item);
    				this._objectAccessDataStore.save({alwaysPostNewItems: true});			
    			}
    		}));
    		this._objectAccessDataStore.objectStore.data.sort(this._compareObjectStoreItems);
    		this._objectAccessDataStore.objectStore.setData(dojo.clone(this._objectAccessDataStore.objectStore.data));
    		this._objectAccessDataStore.save();
    		this._renderGrids();
    	}
    },
    
    _removeButtonClicked : function() {
    	this._manageRemoveActivities(this._objectAccessGridSelection);
    },

    _removeAllButtonClicked : function() {
    	this._manageRemoveActivities(dojo.clone(this._registryDataCopy));
    	this._resetButtonStates();
    	this.addAllButton.set("disabled", false);
    },	
	
    //removes items from the access data store and adds them to the registry data store
    _manageRemoveActivities : function(items) {
    	if (items && items != 0) {
    		dojo.forEach(items, dojo.hitch(this, function(item, index) {
    			
    			this._objectAccessDataStore.deleteItem(item);
    			this._objectAccessDataStore.save();
    			
    			if(this._wasRegistryDataStore.hasItem(item))
    			{
    				this._wasRegistryDataStore.newItem(item);
    				this._wasRegistryDataStore.save({alwaysPostNewItems: true});
    			}
    		}));
        	this._wasRegistryDataStore.objectStore.data.sort(this._compareObjectStoreItems);
        	this._wasRegistryDataStore.objectStore.setData(dojo.clone(this._wasRegistryDataStore.objectStore.data));
    		this._wasRegistryDataStore.save();
    		this._renderGrids();
    	
    	}
    },
    
	_discardButtonClicked : function() {
	if (this._wasRegistryDataStore.objectStore.data.length != 0){
		this.addAllButton.set("disabled", false);
	}
	if (this._objectAccessDataStore.objectStore.data.length != 0){
		this.removeAllButton.set("disabled", false);
	}
	
	},
	
	_displayUserRegistryObjectTypeColumn : function(value, rowIndex, cell) {
		
		// styling required to make the image look good in the cell
		cell.customClasses.push("imageColumn");
		
		var imageString = "";
		
		switch(value) {
		case this._con.REGISTRY_OBJECT_GROUP:
			imageString = Util.formatMessage(this._con.GROUP_IMG, "GROUP");
			break;
		case this._con.REGISTRY_OBJECT_USER:
		default:
			imageString = Util.formatMessage(this._con.USER_IMG, "USER");
			break;
		}
		
		return imageString;
	},
	
	_clearGridSelections : function(){
    	this.objectAccessGrid.selection.clear();
    	this.wasRegistryGrid.selection.clear();
	},
	
	_renderGrids : function(){
		this._clearGridSelections();
    	this.objectAccessGrid.render();
    	this.wasRegistryGrid.render();
	},
	
	_resetButtonStates : function() {
		this.addButton.set("disabled", true);
		this.addAllButton.set("disabled", true);
		this.removeButton.set("disabled", true);
		this.removeAllButton.set("disabled", true);
	}
});