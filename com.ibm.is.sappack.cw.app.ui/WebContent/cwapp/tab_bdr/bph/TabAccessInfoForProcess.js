dojo.provide("cwapp.tab_bdr.bph.TabAccessInfoForProcess");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");

dojo.require("idx.layout.BorderContainer");

dojo.require("dijit.form.Button");

dojo.require("dojo.store.Memory");
dojo.require("dojo.data.ObjectStore");

dojo.require("dojox.grid.DataGrid");
dojo.require("dojox.widget.Standby");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");



dojo.declare("cwapp.tab_bdr.bph.TabAccessInfoForProcess", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/TabAccessInfoForProcess.html"),
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
	_wasRegistryDataStore : null,
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
		
		// initialize the was grant registry data grid with the write store
		this._initGrid(this.wasRegistryGrantGrid, this._wasRegistryDataStore);
		
		// initialize the was registry data grid with the write store
		this._initGrid(this.wasRegistryGrid, this._wasRegistryDataStore);
		
		// startup the enclosed border container
		this.borderContainer.startup();
		
		// connect to various events
		dojo.connect(window, "onResize", this, "_myResize");
		
		dojo.connect(this.wasRegistryGrid, "onSelectionChanged", this, "_wasRegistryGridRowSelected");
		dojo.connect(this.wasRegistryGrantGrid, "onSelectionChanged", this, "_wasRegistryGrantGridRowSelected");
		
		dojo.connect(this.grantButton, "onClick", this, "_grantButtonClicked");
		dojo.connect(this.revokeButton, "onClick", this, "_revokeButtonClicked");

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
	
	isAvailable : function() {
		return (dojo.getStyle(this.controlButton.domNode, "display") != "none");
	},
	
	_myShow : function() {
		// layout the enclosed border container
		this._myResize();
    	
		this.wasRegistryGrid.selection.clear();
		this.wasRegistryGrantGrid.selection.clear();

		if (this._selectedObject != "") {
			
			// destroy any existing data stores
			this._destroyDataStore();
			
			// create the store
			if (!this._wasRegistryDataStore) {
				
				if (this._isActiveTab)  {
					this._setupWASRegistryGrantDataStore(this._isActiveTab);
					this._setupWASRegistryDataStore(this._isActiveTab); 
					this._setupGrid();
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
	_destroyDataStore : function() {
		if (this._wasRegistryDataStore) {
			delete this._wasRegistryDataStore;
		}
	},
	
	_setupGrid : function() {
		//	the object access data grid only needs to be refreshed with the new data store
		this.wasRegistryGrid.set("store", this._wasRegistryDataStore);
		this.wasRegistryGrantGrid.set("store", this._wasRegistryGrantDataStore);
		this.wasRegistryGrid.render();
		this.wasRegistryGrantGrid.render();
	},
	
	_initGrid : function(grid, store){
		grid.set("store", new dojo.data.ObjectStore({
			objectStore : store
		}));
		grid.set("formatterScope", this);
		grid.set("editable", false);
		grid.set("selectable", false);
		grid.set("selectionMode", "extended");
		grid.selection.clear();
		// startup the grid and have it rendered
		grid.startup();
	},
	
	//compare method for two objects of the object Store. First sorts after the type and then alphabetic
	_compareObjectStoreItems : function(a,b){
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
	
	_setupWASRegistryDataStore : function(renderDataGrid) {
		
		// create a data store for the selectable objects
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
			}
		});
			
		// populate the was registry data store with data
		dojo.xhrGet({
			url : Services.BDR_ACCESSINFO_SERVICE+"/"+ this._selectedObject.id,
			handleAs : "json",
			sync : true,
			load : dojo.hitch(this, function(data) {
				data.sort(this._compareObjectStoreItems);
				this._wasRegistryDataStore.objectStore.setData(data);
				
				if (renderDataGrid) {
					this.wasRegistryGrid.render();
				}
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
		
		},
		

    _setupWASRegistryGrantDataStore  : function(renderDataGrid) {

		
		this._wasRegistryGrantDataStore = new dojo.data.ObjectStore({
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
			}
		});
		
		
		// populate the was registry data store with Grant data
		dojo.xhrGet({
			url : Services.BDR_ACCESSINFO_SERVICE + "/grantInfo/" + this._selectedObject.id,
			handleAs : "json",
			sync : true,
			load : dojo.hitch(this, function(data) {
				data.sort(this._compareObjectStoreItems);
				this._wasRegistryGrantDataStore.objectStore.setData(data);
				
				if (renderDataGrid) {
					this.wasRegistryGrantGrid.render();
				}
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
		
	},	
    
    _wasRegistryGridRowSelected : function() {
    	this._wasRegistryGridSelection = this.wasRegistryGrid.selection.getSelected();

    	if (this._wasRegistryGridSelection && this._wasRegistryGridSelection.length != 0) {
    		this.grantButton.set("disabled", false);
    	}
    	else {
    		this.grantButton.set("disabled", true);
    	}
    },
    
    _wasRegistryGrantGridRowSelected : function() {
    	this._wasRegistryGridSelection = this.wasRegistryGrantGrid.selection.getSelected();
    	
    	if (this._wasRegistryGridSelection && this._wasRegistryGridSelection.length != 0) {
    		this.revokeButton.set("disabled", false);
    	}
    	else {
    		this.revokeButton.set("disabled", true);
    	}
    },
    
    _grantButtonClicked : function() {
    	if (this._wasRegistryGridSelection) {
  
    		var items = this._wasRegistryGridSelection;
    		dojo.forEach(items, dojo.hitch(this, function(item, index) {
        		
    			dojo.xhrPut({
    				url: Services.BDR_ACCESSINFO_SERVICE + "/grantToACL/" + this._selectedObject.id,
    				headers: {"Content-Type": "application/json"},
    				handleAs: "json",
    				postData: dojo.toJson(item),
    				preventCache : true,
    				sync : true,
    				error : dojo.hitch(this, function(error) {
    					cwapp.main.Error.handleError(error);
    				})
    			});
    		}));
    		this._wasRegistryGridSelection = null;
    		this._renderGrids();
    		this._myShow();
    		this._resetButtonStates();
    	}
    },

    
    _revokeButtonClicked : function() {
    	if (this._wasRegistryGridSelection) { 
  
    		var items = this._wasRegistryGridSelection;
    		dojo.forEach(items, dojo.hitch(this, function(item, index) {
        		
    			dojo.xhrPut({
    				url: Services.BDR_ACCESSINFO_SERVICE + "/revokeFromACL/" + this._selectedObject.id,
    				headers: {"Content-Type": "application/json"},
    				handleAs: "json",
    				postData: dojo.toJson(item),
    				preventCache : true,
    				sync : true,
    				error : dojo.hitch(this, function(error) {
    					cwapp.main.Error.handleError(error);
    				})
    			});
    		
    		}));
    		this._wasRegistryGridSelection = null;
    		this._renderGrids();
    		this._resetButtonStates();
    		this._myShow();
    	}
    },

	_discardButtonClicked : function() {
		this._wasRegistryGridSelection = null;
		if(this._wasRegistryTree != null) {
			this._wasRegistryTree.set("paths", this._con.EMPTY_STRING);
		}
		
		// set button states
		this._resetButtonStates();
		this._caller.disableButtons();
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
	
	
	_renderGrids : function(){
    	this.wasRegistryGrid.selection.clear();
    	this.wasRegistryGrid.render();
    	this.wasRegistryGrantGrid.selection.clear();
    	this.wasRegistryGrantGrid.render();
	},
	_resetButtonStates : function() {
		this.grantButton.set("disabled", true);
		this.revokeButton.set("disabled", true);
	}
});