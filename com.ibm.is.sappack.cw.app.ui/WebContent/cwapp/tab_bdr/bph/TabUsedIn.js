dojo.provide("cwapp.tab_bdr.bph.TabUsedIn");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");

dojo.require("dojo.store.Memory");
dojo.require("dojo.data.ObjectStore");

dojo.require("dojox.widget.Standby");

dojo.require("dijit.tree.ForestStoreModel");
dojo.require("dijit.Tree");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.TabUsedIn", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/TabUsedIn.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// private constants
	_con : {
		EMPTY_STRING : "",
	},

	// (very) private member
	__usedInTreeWidgetId : null,
	
	// private members
	_caller : null,
	_selectedObject : "",
	_usedInDataStore : null,
	_usedInTreeModel : null,
	_usedInTree : null,
	_standByOverlay : null,
	_subscribes : [],
	_loadUrl : null,
	
	// public functions
	constructor : function(args) {
		if(args) {
			dojo.mixin(this,args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	postCreate : function() {

		// generate a widget id for the 'used in' tree control
		// it's generally a bad idea to use string constants for javascript ids since
		// so we generate a random string which is unique for each instance of this widget
		this.__usedInTreeWidgetId = Math.random().toString(36).substring(7);

		// initialize standby overlay for the 'used in' tree
		this._initStandByOverlay();

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
	},
	
	update : function(selectedObject) {
		if (!selectedObject) {
			return;
		}
		
		// store the selected object
		this._selectedObject = selectedObject;
		
		// Show the tree, hide the "empty" message
		dojo.style(this.usedInTreeDiv, "display", "block");
		dojo.style(this.emptyMessage, "display", "none");
		
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
	
	// a new BPH object has been selected so we need to update the page completely
	_updatePage : function() {
		if (this._selectedObject != "") {
			
			switch(this._selectedObject.type) {
				case BdrTypes.BO : {
					this._loadUrl = Services.BDR_BO + "pathsFor/" + this._selectedObject.id;
					break;
				}
				case BdrTypes.TABLE : {
					this._loadUrl = Services.BDR_TABLE + "/pathsFor/" + this._selectedObject.id;
					break;
				}
				default : {
					this._loadUrl = this._con.EMPTY_STRING;
					break;
				}
			}
			
			// destroy any existing tree control and data store
			this._destroyTreeAndDataStore();
			
			// create the tree control
			if (!this._usedInTree && !this._usedInDataStore) {
				this._setupUsedInDataStore().then(dojo.hitch(this, function() {
					this._setupUsedInTree();
				}), dojo.hitch(this, function(error) {
					this._setupUsedInTree();
				}));
			}
		}
	},
	
	// initialize the standby overlay for the 'used in' tree
	_initStandByOverlay : function() {
		this._standByOverlay = new dojox.widget.Standby({
			target : this.usedInTreeDiv,
			image : myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loading.gif",
			onHide : dojo.hitch(this, function() {
				if (this._usedInTree) {
					this._usedInTree.refresh();
				}
			}),
		});
		
		document.body.appendChild(this._standByOverlay.domNode);
		
		this._standByOverlay.startup();
	},
	
	// destroy the existing tree control and corresponding data store
	_destroyTreeAndDataStore : function() {
		if (this._usedInTree) {
			this._usedInTree.destroy();
			delete this._usedInTree;
		}
		
		if (this._usedInTreeModel) {
			this._usedInTreeModel.destroy();
			delete this._usedInTreeModel;
		}
		
		if (this._usedInDataStore) {
			delete this._usedInDataStore;
		}
	},
	
	// setup the data store and model for the 'used in' tree
	_setupUsedInDataStore : function() {
		var deferred = new dojo.Deferred();
		
		// we use a memory store for the 'used in' tree
		this._usedInDataStore = new dojo.data.ObjectStore({
			objectStore : new dojo.store.Memory({
				id : "root",
			}),
		    
		    // retrieve the item labels for the tree
		    getLabel : function(item) {
		        return item.name;
		    }
		});
		
		this._usedInTreeModel = new dijit.tree.ForestStoreModel({
			store : this._usedInDataStore,
			rootId : "root",
			childrenAttrs : ["children"],
		});

		// display the overlay
		this._standByOverlay.show();
		
		// populate the 'used in' memory store with data
		dojo.xhrGet({
			url : this._loadUrl,
			handleAs : "json",
			load : dojo.hitch(this, function(data) {
				this._usedInDataStore.objectStore.setData(data);
				
				// hide the overlay
				this._standByOverlay.hide();
				
				// Handle empty result
				if (!data || data.length == 0) {
					dojo.style(this.usedInTreeDiv, "display", "none");
					dojo.style(this.emptyMessage, "display", "block");
				}

				deferred.resolve();
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
				this._usedInDataStore.objectStore.setData(new Array());
				
				// hide the overlay
				this._standByOverlay.hide();
				
				deferred.resolve();
			})
		});
		
		return deferred;
	},

	// setup the 'used in' tree control
	_setupUsedInTree : function() {
		this._usedInTree = new dijit.Tree({
			id : this.__usedInTreeWidgetId,
			model : this._usedInTreeModel,
			showRoot : false,
			autoExpand : true,
			getIconClass : Util.bdrIconFunc,
			dndParams : ["singular"],
			singular : true,
			persist : false,
			expandAll : function() {
				
				// expand all nodes recursively (starting at the root node)
		        this._expandNode(this.rootNode, true);
			},
			collapseAll : function() {
				
				// collapse all nodes recursively at the first level (NOT the root item)
				dojo.forEach(this.rootNode.getChildren(), dojo.hitch(this, function(item, index) {
			        this._collapseNode(item, true);
				}));
		    },
            refresh : function() {
                this.dndController.selectNone();
                this.model.store.clearOnClose = true;
                this.model.store.close();
                delete this._itemNodesMap;
                this._itemNodesMap = {};
                this.rootNode.state = "UNCHECKED";
                delete this.model.root.children;
                this.model.root.children = null;
                this.rootNode.destroyRecursive();
                this.model.constructor(dijit.byId(this.id).model);
                this.postMixInProperties();
                this._load();
            },
        });
		
		// connect event handlers for the tree
		dojo.connect(this._usedInTree, "onDblClick", this, "_itemSelected");
		
		// startup the tree
		this._usedInTree.startup();

		// put the newly created tree into the dom structure at the given div element
		this.usedInTreeDiv.appendChild(this._usedInTree.domNode); 
	},
    
    _itemSelected : function(selectedItem, selectedNode, evt) {
   		if (selectedItem) {
   			var hashObject = Util.getHashObject();
	    	hashObject[UrlParams.TYPE] = selectedItem.type;
	    	hashObject[UrlParams.ID] = selectedItem.id;
	    	Util.setHashObject(hashObject);
    	}
    },
});
