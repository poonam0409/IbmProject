dojo.provide("cwapp.tab_bdr.TabBO");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");

dojo.require("idx.layout.BorderContainer");
dojo.require("idx.layout.ButtonBar");

dojo.require("dijit.form.Button");
dojo.require("dijit.form.DropDownButton");

dojo.require("dojo.data.ObjectStore");
dojo.require("dojo.store.Memory");
dojo.require("dijit.tree.ForestStoreModel");
dojo.require("dijit.Tree");

dojo.require("dojox.widget.Standby");

dojo.require("dijit.Menu");
dojo.require("dijit.MenuItem");

dojo.require("cwapp.main.AddressableWidget");
dojo.require("cwapp.main.Error");
dojo.require("cwapp.main.SearchBox");

dojo.require("cwapp.tab_bdr.bph.DetailsPanel");
dojo.require("cwapp.tab_bdr.bph.PlaceholderWidget");
dojo.require("cwapp.tab_bdr.bph.AddTableDialog");


dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.TabBO", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin, cwapp.main.AddressableWidget ],
{
	// basic widget settings 
	widgetsInTemplate : true,
    templateString : dojo.cache("cwapp.tab_bdr","templates/TabBO.html"),
    
	// nls support
	msg : {},

	// constants
	_con : {
		ID_PROPERTY : "name",
		SEARCH_FILTER_ALL : "%",
		TOPIC_NEW_ITEM_ADDED_TO_STORE : "topicNewItemAddedToStore",
	},
	
	// private members
	_handleRefresh : null,
	_memoryStore : null,
	_detailsPanel : null,
	_treeDataStore : null,
	_treeModel : null,
	_tree : null,
	_selectedItem : null,
	_selectedPaths : null,
	_urlHandlingDone : false, // Set to true once we have applied the URL parameters
	_urlId: null,
	_urlType: null,
	_standByOverlay : null,
	_treeConnects : [],
	_treeSelection: [],
	_expandedNodes: [],
	_refreshNeeded: true,
	_hasConfiguredSapSystems : false,
	_toolTipHandle : null,
	_sapSystemPasswordDialog : null,

	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
		// Used in AddressableWidget
		this.addressParameters = ["type", "id"];
	},
	
	postCreate : function() {
		this._setupStore();
		dojo.connect(this, "onShow", this, "_myShow");
		dojo.connect(this, "onHide", this, "_unShow");
		dojo.connect(this.addBusinessObjectButton, "onClick", this, "_addBusinessObjectButton");
		dojo.connect(this.attachTableToBOButton, "onClick", this, "_attachTableToBOButton");
		dojo.connect(this.removeButton, "onClick", this, "_removeButton");
		dojo.connect(this.filteringInput, "onKeyUp", this, "_filterTree");
		
		// subscribe to topic published by the AddTableDialog component
		dojo.subscribe(Topics.TABLESTOATTACHSELECTED, dojo.hitch(this, this._attachTablesToBO));
		//subscribe to topic published by BPH Tab
		dojo.subscribe(Topics.REFRESH_BO_WHEN_SHOWN, dojo.hitch(this, this._triggerRefresh));
		//subscribe to topic published by FieldsFromSAPImportDialog component
		dojo.subscribe(Topics.REFRESH_DETAILSPANEL_BO, dojo.hitch(this, this._refreshDetailsPanel));
		// initialize standby overlay
		this._initStandByOverlay();
		
		if (this._standByOverlay) {
			this._standByOverlay.show();
		}
		
		// disable the add button in read-only mode
		if (General.ISREADONLY){
			this.addButton.set("disabled", true);
		}
		this.inherited(arguments);
	},
	
	getSelectedTables : function () {
		var selectedObjects = this._tree.dndController.getSelectedTreeNodes();
		var tablesToLoad = new Array();
		
		dojo.forEach(selectedObjects, function(entry, index) {
			if (entry.item.type == BdrTypes.BO) {
				dojo.forEach(entry.item.tables, function(table, index) {
					var isAlreadyAdded = false;
					dojo.forEach(tablesToLoad, function(t, index) {
						if (table.name == t.name) {
							isAlreadyAdded = true;
						}
					});
					if (!isAlreadyAdded) {
						tablesToLoad.push(table);
					}
				});
			}
			if (entry.item.type == BdrTypes.TABLE) {
				var isAlreadyAdded = false;
				dojo.forEach(tablesToLoad, function(t, index) {
					if (entry.item.name == t.name)
						isAlreadyAdded = true;
				});
				if (!isAlreadyAdded) {
					tablesToLoad.push(entry.item);
				}
			}
		});
		
		return tablesToLoad;
	},
	
	// Implements the "abstract" function from AddressableWidget
	getCurrentAddress: function () {
		if (this._tree && this._tree.selectedItem && this._tree.selectedItem.id != "root") {
			return {
				type: this._tree.selectedItem.type,
				id: this._tree.selectedItem.id
			};
		}
		return null;
	},
	
	// Implements the "abstract" function from AddressableWidget
	handleHash: function (hashObject) {
		if (hashObject && hashObject.type && hashObject.id && hashObject.type == BdrTypes.BO) {
			this._urlType = hashObject.type;
			this._urlId = hashObject.id;
		}
	},
	
	refresh : function(){
		dojo.publish(Topics.REFRESH_BPH_WHEN_SHOWN);
		this._getData();
	},
	
	refreshForAddedItem : function(item) {
		dojo.publish(Topics.REFRESH_BPH_WHEN_SHOWN);
    	if (item.type == BdrTypes.BO) {
			this._treeSelection = [[{children: null, id: "root", label: "ROOT", root: true}]];
    	} else {
    		this._saveTreeSelection();
    	}
    	
    	this._treeSelection[0].push(item);
    	this._getData();
    },
	    
    refreshForDeletedItem : function(item) {
    	//this function only handles tables because Bos have no real Parent
    	//we do not care about selection while deleting Bos
    	dojo.publish(Topics.REFRESH_BPH_WHEN_SHOWN);
    	this._treeSelection=[[{children: null, id: "root", label: "ROOT", root: true}]];

    	this._treeSelection[0].push(item);
    	this._restoreTreeSelection();
    	
    	this._getData();
    },

	// Removes the selected items
	removeItems : function() {
		var selectedItems = this._tree.paths;
		var multiple = (selectedItems.length > 1);
		var tableIndex = [];
		dojo.forEach(selectedItems, dojo.hitch(this, function (path, index) {
			var item = path[path.length - 1];
			switch(item.type) {
			case BdrTypes.BO :
				var url = Services.BDR_BO_DELETE + "/" + item.id;
				this._deleteItem(item, url, multiple);
				break;
			case BdrTypes.TABLE :
				// Push the table into the index entry (array) under the parent object's id
				if (!tableIndex[path[path.length - 2].id]) {
					// Create a new index entry and add the BO itself as the first value
					tableIndex[path[path.length - 2].id] = [];
					tableIndex[path[path.length - 2].id].push(path[path.length - 2]);
				}
				tableIndex[path[path.length - 2].id].push(item);
				break;
			}
		}));
		if (tableIndex.length>0){
			for (var boId in tableIndex) {
				var indexEntry = tableIndex[boId];
				if (indexEntry == null) continue; // the BO may have been deleted just now
				// Get the BO itself
				var bo = indexEntry[0];
				// Remove each selected table from the BO object
				for (var tableNr in indexEntry) {
					if (tableNr == 0) continue; // Skip the BO itself
					var table = indexEntry[tableNr];
					Util.removeItemById(bo.tables, table.id);
				}
				//parameters:
				//BO,ID,EditingTable,Table,detach
				this._updateBO(bo);
			};
		} else {
			if (multiple) {
				this.refresh();
			}		
		}
	},
	
    // private functions
	
	_getData : function(){
		this._saveTreeSelection();
		this._expandedNodes=[];
		this._saveExpandedNodes(this._tree._itemNodesMap["root"][0].item);
		this._standByOverlay.show();
		dojo.xhrGet({
			url: Services.BDR_BO_ALL,
			handleAs: "json",
			preventCache: true,
			load: dojo.hitch(this, function(treeData) {
				this._memoryStore.setData(treeData);
				this._tree.refresh();
				this._restoreTreeSelection();
				if (this._expandedNodes.length > 0) {
					this._restoreExpandedRows();
				}
				this.filteringInput.set("value","");
				this._unShow();
				this._standByOverlay.hide();
			}),
			error: dojo.hitch(this, function(error) {
				this._standByOverlay.hide();
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	_myShow : function() {
		this.borderContainer.resize();
		// initialize dependent widgets and show placeholder panel
		if (!this._detailsPanel) {
			this._detailsPanel = new cwapp.tab_bdr.bph.DetailsPanel({}, "boDetailsPanel");
			dojo.style(dojo.byId("boDetailsPanel"), "display", "none");
		}
		if (!this._placeholderWidget) {
			this._placeholderWidget = new cwapp.tab_bdr.bph.PlaceholderWidget({}, "boBphPlaceholderWidget");
			dojo.style(dojo.byId("boBphPlaceholderWidget"), "display", "");
		}
		// Refresh DetailsPanel
		if (this._tree.selectedItem && this._tree.selectedItem.id != "root") {
			this._itemSelected(this._tree.selectedItem);
		}
		if (this._refreshNeeded) {
			this._refreshNeeded = false;
			this._getData();
		} else {
			this._restoreTreeSelection();
		}
		this._initImportFromSapButton();
	},

	_handleUrlInTree: function () {
		if (this._urlId && this._urlType) {
			// set the tree selection to the selected item and click it
			this._tree.set("paths", [[{id : "root"}, {id:this._urlId, type:this._urlType}]]).then(dojo.hitch(this, function() {
				this._tree.onClick({id:this._urlId, type:this._urlType});
				this._tree._expandNode(this._tree.selectedNode, false);
				// scroll the saved item into view (just in case) 
				dojo.window.scrollIntoView(this._tree.selectedNode.domNode);
			}));
		}
	},
	
	_initImportFromSapButton : function() {
		dojo.xhrGet({
			url : Services.TARGETSAPSYSTEMRESTURL,
			handleAs : "json",
			load : dojo.hitch(this, function(response, ioArgs) {     
				// remove all menu entries that are already there to have them refreshed
				var menu = this.importFromSapButton.dropDown;
				menu.destroyDescendants(false);

				// only if the response is of length greater than zero
				// we enable the 'import from SAP system button' which is a dropdown button
				// presenting the user a list of SAP systems to import from.
				if (response && response.length > 0) { 
					//enable the drop down button
					this._hasConfiguredSapSystems = true;
					
					if(!General.ISREADONLY){
					    this.importFromSapButton.set("disabled", false);    
					}
					
					//detach and delete potentially existing tooltip which should be only attached when the button is disabled					
					if (this._toolTipHandle) {
						this._toolTipHandle.removeTarget(this.importFieldsFromSapButton.domNode);
						delete this._toolTipHandle;
						this._toolTipHandle = null;
					}
					
					//create a menu entry for each target sap system and add it to the menu
					dojo.forEach(response, dojo.hitch(this, function(entry, index) {
				        var menuItem = new dijit.MenuItem({
				            label : entry.legacyId,
				            onClick : dojo.hitch(this, function() {
				            	this._importFromSapButtonClicked(entry);
				            }),
				        });
				        menu.addChild(menuItem);
					}));
				} else { 
					//the 'import fields from SAP' button will be disabled if no target SAP system is configured
			        //in this case a tooltip will be shown that informs the user about circumstance 

					//disable the drop down button
					this.importFromSapButton.set("disabled", true);
					
					//add the tooltip to the importFieldsFromSap button to inform the user that no sap system exists
					if (!this._toolTipHandle) {
						this._toolTipHandle = new dijit.Tooltip({
							connectId: [this.importFromSapButton.domNode],
							label: this.msg.TARGETSAPSYSTEMSPG_34,
							position: ["above"]
						});
					}
				}
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	// Remember the selection so we can restore it after refreshing
    _saveTreeSelection : function() {
    	if (this._tree.paths.length != 0) {
			this._treeSelection = this._tree.paths;
		} else {
			if (this._treeSelection.length > 0) {
				return;
			}
			this._treeSelection = [[{children: null, id: "root", label: "ROOT", root: true}]];
		}
    },
    
    _restoreTreeSelection: function () {
    	if (this._treeSelection) {
	    	// Restore / set selection
			this._tree.set('paths', this._treeSelection).then(dojo.hitch(this, function() {
				if (this._tree.selectedNode) {
					// Update dependent UI elements
					this._tree.onClick(this._tree.selectedNode.item);
					// Scroll to selection
					dojo.window.scrollIntoView(this._tree.selectedNode.domNode);
				}
			}));
    	}
    },
	
	_initStandByOverlay : function() {
		this._standByOverlay = new dojox.widget.Standby({
			target : this.treeDiv,
			image : myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loading.gif",
			color: "white",
		});
		document.body.appendChild(this._standByOverlay.domNode);
		this._standByOverlay.startup();
	},
	
	_unShow : function() {
		this._saveTreeSelection();
		this._clearTreeSelection();
		this._hideAttachTableToBOButton(true);
		dojo.style(dojo.byId("boDetailsPanel"), "display", "none");
		dojo.style(dojo.byId("boBphPlaceholderWidget"), "display", "");
	},
	
	_setupStore : function() {
		this._memoryStore = new dojo.store.Memory({id : "root"});
		this._treeDataStore = new dojo.data.ObjectStore({
			objectStore: this._memoryStore,
		    getLabel : function(item) {
		    	return item.name;
		    },
		});
		
		this._treeModel =  new dijit.tree.ForestStoreModel({
			store: this._treeDataStore,
			rootId: "root",
			childrenAttrs : ["children", "tables"],
			boScope : this,
			newChildren :[],
			allTables : [],

	        filter : function(searchString){
	        	this.filterCondition = searchString;
	        	var item = this.boScope._tree._itemNodesMap["root"][0].item;
	        	if (this.filterCondition != null && this.filterCondition != "") {
	        		this.restoreTree(item);
	        		this.searchAndTrimTree(item);
	        	} else {
	        		this.restoreTree(item);
	        	}
	        },
	        
	        searchAndTrimTree : function(item){
				this.newChildren = [];
				expandNodes = [];
				for (var i = 0; i < item.children.length; i++) {
					newTables = [];
					foundBo = false;
					if (item.children[i].name.toUpperCase().indexOf(this.filterCondition.toUpperCase()) > -1) {
						this.newChildren.push(item.children[i]);
						foundBo = true;
					}
					for (var j = 0; j < item.children[i].tables.length; j++) {
						if (item.children[i].tables[j].name.toUpperCase().indexOf(this.filterCondition.toUpperCase()) > -1) {
							newTables.push(item.children[i].tables[j]);
						}
					}
	        		
	        		if (newTables.length > 0) {
	        			if (!foundBo) {
	        				// if one of its children nodes fits in our regexp,
	        				// we have to save the parent node as well 
	        				// But only if not the parent node itself does not fit in
	        				this.newChildren.push(item.children[i]);
	        			}
	        			// trim tables
	        			this.onChildrenChange(item.children[i],newTables);
	        			expandNodes.push(item.children[i]);
	        		}
	        	}
	        	// trim BOs
				this.onChildrenChange(item, this.newChildren);
				for (var i = 0; i < expandNodes.length; i++) {
					nodes = this.boScope._tree.getNodesByItem(expandNodes[i]);
					if (nodes[0] != null && !nodes[0].isExpanded) {
						// expand BOs if needed
						this.boScope._tree._expandNode(nodes[0]);
					}
				}
	        },
	        
	        restoreTree : function(item){
				this.onChildrenChange(item, item.children);
				for (var i = 0; i < item.children.length; i++) {
					this.onChildrenChange(item.children[i], item.children[i].tables);
					if (item.children[i] != undefined) {
						nodes = this.boScope._tree.getNodesByItem(item.children[i]);
						if (nodes[0] != null && nodes[0].isExpanded) {
							this.boScope._tree._collapseNode(nodes[0]);
						}
					}
				}
    			this.boScope._tree.set("paths",this.boScope._treeSelection);
	        }
		});
		this._setupTree();
	},
	
	_setupTree : function() {
		this._tree = new dijit.Tree({
			model : this._treeModel,
			showRoot : false,
			getIconClass : dojo.hitch(this, this._setTreeIcon),
			singular : false,
			persist : false,
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
		
		// put the newly created tree into the dom structure at the given div element
		this.treeDiv.appendChild(this._tree.domNode); 
		dojo.addClass(this._tree.domNode, "bdrBoTree");
		
		// connect event handlers for the tree
		dojo.connect(this._tree, "onClick", this, "_itemSelected");
		dojo.connect(this._tree.dndController, "onMouseDown", this, "_itemSelectionAttempt");
		
		// startup the tree
		this._tree.startup();
	},
	
	_setTreeIcon : function(treeItem) { 
		if (treeItem.type) {
			if (treeItem.type == BdrTypes.TABLE) {
				return "BPH_TreeIconTable";
			}
			if (treeItem.type == BdrTypes.BO) {
				return "BPH_TreeIconBusinessObject";
			}
		}
    },
    
    
    _itemSelected : function(selectedItem) {
    	this._selectedItem = selectedItem;
    	this._selectedPaths = this._tree.get("paths");
    	
		if (this._selectedPaths.length > 1) {
			// Multiselection
			dojo.style(dijit.byId("boDetailsPanel").domNode, "display", "none");
			dojo.style(dojo.byId("boBphPlaceholderWidget"), "display", "");
			dijit.byId("boBphPlaceholderWidget").setMessage(true);
			this._hideAttachTableToBOButton(true);
		}
		else{
			// hide the placeholder
			dojo.style(dojo.byId("boBphPlaceholderWidget"), "display", "none");
			// show details panel
			dojo.style(dojo.byId("boDetailsPanel"), "display", "");

			if(General.ISBDRSUPERUSER){
			    this.removeButton.set("disabled", false);
			}
			
			this.setHash();

			switch (selectedItem.type) {
				case BdrTypes.BO : {
					this.removeButton.set("label", this.msg.BPHTAB_2);
					this._hideAttachTableToBOButton(false);
					this._detailsPanel.update(this, BdrTypes.BO, selectedItem.id, null, null, true);
					break;
				}
				case BdrTypes.TABLE : {
					this.removeButton.set("label", this.msg.BPHTAB_16);
					this._hideAttachTableToBOButton(true);
					this._detailsPanel.update(this, BdrTypes.TABLE, selectedItem.id, null, null, true);
					break;
				}
			}
		}
    },
    
    _itemSelectionAttempt : function(event) {
    	this.onHideAttempt(event);	 
    },
    
	_clearTreeSelection : function() {
		this._tree.set("paths", "");
		// disable the 'remove' button
		this.removeButton.set("disabled", true);
		// hide the details panel
		dojo.style(dojo.byId("boDetailsPanel"), "display", "none");
		// show the placeholder
		dojo.style(dojo.byId("boBphPlaceholderWidget"), "display", "");
		dijit.byId("boBphPlaceholderWidget").setMessage(false);
	},

	_importFromSapButtonClicked : function(sapSystem) {
		var pwDeferred = this._handleSapSystemPassword(sapSystem);
		if (pwDeferred != null) {
			pwDeferred.then(dojo.hitch(this, function(success) {
				if (success) {
					var progressDialog = new cwapp.tab_bdr.bph.FieldsFromSAPImportProgressDialog({_idProperty : this._con.ID_PROPERTY, caller : BdrTypes.BO});
					progressDialog.setStartingPage(this);
					progressDialog.setSapSystem(sapSystem);
					progressDialog.startup();
					progressDialog.start();
				}
			}));
		}
	},   
    
    _addBusinessObjectButton : function() {
		// clear the tree selection (required)
		this._clearTreeSelection();
		// Disable attaching tables
		this._hideAttachTableToBOButton(true);
		dojo.style(dojo.byId("boBphPlaceholderWidget"), "display", "none");
		dojo.style(dojo.byId("boDetailsPanel"), "display", "");
		this._detailsPanel.update(this, BdrTypes.BO, null, null, null, false);
	},
	
	_attachTableToBOButton : function() {
		var attachTablesToBODialog = new cwapp.tab_bdr.bph.AddTableDialog();
		attachTablesToBODialog.show(this);
	},

	// Opens the remove/detach confirmation dialog
	_removeButton : function() {
		var detach = (this._tree.selectedItem.type == BdrTypes.TABLE);
		dijit.byId("bdrDeleteDialog").show(this, this._tree.paths, detach);		
	},
	
	_handleSapSystemPassword : function(sapSystem) {
		if (!this._sapSystemPasswordDialog) {
			this._sapSystemPasswordDialog = new cwapp.tab_config.SapSystemPasswordDialog();
		}
		return this._sapSystemPasswordDialog.getPassword(sapSystem);	
	},

	_hideAttachTableToBOButton : function(hide) {
		if (hide) {
			dojo.style(this.attachTableToBOButton.domNode, "display", "none");
		} else {
			dojo.style(this.attachTableToBOButton.domNode, "display", "");
		}
	},
	
	_attachTablesToBO : function(tableSelection) {
		var item = this._tree.get("selectedItem");
		var multiple = (tableSelection>0);
		// update the 'tables' array of the item
		dojo.forEach(tableSelection, dojo.hitch(this, function(table, index) {
			item.tables.push({id : table.id});
		}));
		if (!multiple) {
			this._updateBO(item,tableSelection[0]);
		} else {
			this._updateBO(item);
		}
	},

	_filterTree : function() {
		this._saveTreeSelection();
		
		var deleteLink =this.filteringInput.clearFilterConditionLink;
		if(deleteLink){
			deleteLink.setAttribute('onclick', 'return false');
			require([ "dojo/on" ], dojo.hitch(this, function(on) {
				on(deleteLink, "click", dojo.hitch(this, function(e) {
					this._filterTree();
				}));
			}));

		}
		var searchString = this.filteringInput.get("value");
		this._tree.model.filter(searchString);
		this.filteringInput.focus();
	},

	_mergeBusinessObjectItems : function(item, existingItem) {
		var updatedItem = new Object();
		updatedItem.id = existingItem.id;
		updatedItem.name = item.name;
		updatedItem.shortName = item.shortName;
		updatedItem.description = item.description;
		updatedItem.type = existingItem.type;
		updatedItem.tables = existingItem.tables;
		updatedItem.updated = existingItem.updated;
		updatedItem.usages = existingItem.usages;
		return updatedItem;
	},
	
	_deleteItem : function(item, url, multiple) {
		this._resetTreeSelection();
		dojo.xhrDelete({
			url: url,
			headers: {"Content-Type": "application/json"},
			handleAs: "json",
			postData: dojo.toJson(item),
			preventCache : true,
			sync : true,
			load : dojo.hitch(this, function(data) {
				if (!multiple){
					this.refresh();
				}
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
		dojo.style(dojo.byId("boDetailsPanel"), "display", "none");
		dojo.style(dojo.byId("boBphPlaceholderWidget"), "display", "");
		dijit.byId("boBphPlaceholderWidget").setMessage(false);
	},
	
	_updateBO : function(item,itemToDisplay) {
		//editTable tells server if he has to edit the Tables or not
	
		dojo.xhrPut({
			url : Services.BDR_BO_UPDATE+"/"+item.id,
			headers: {"Content-Type": "application/json"},
			handleAs: "json",
			postData: dojo.toJson(item),
			preventCache : true,
			sync : true,
			load : dojo.hitch(this, function(data) {
				//if we are detaching

				if(itemToDisplay==null){
					this.refreshForDeletedItem(item);
				}
				else{
					this.refreshForAddedItem(itemToDisplay);
				}
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});	
	},
	
	_saveExpandedNodes : function(item) {
		nodes = this._tree.getNodesByItem(item);
		if(nodes[0]!=0&&nodes[0]!=null){
			if(nodes[0].isExpanded){
				this._expandedNodes.push(item);
				var children = [];
		        if(item.id=="root"){
		        	children=item.children;
		        }
		        else{
		        	children=item.tables;
		        }
		        for(i in children){
		        	this._saveExpandedNodes(children[i]);
		        }
			}
		}
	},
	
	_restoreExpandedRows : function() {
		for (var i = 0; i < this._expandedNodes.length; i++) {
			nodes = this._tree.getNodesByItem(this._expandedNodes[i]);
			if (nodes[0] != 0 && nodes[0] != null) {
				this._tree._expandNode(nodes[0]);
			}
		}
	},
	
	_resetTreeSelection : function() {
    	this._treeSelection = [[{children: null, id: "root", label: "ROOT", root: true}]];
    	this._tree.set('paths', this._treeSelection);
    },
    
    _triggerRefresh : function() {
    	this._refreshNeeded= true;
    },
    
    _refreshDetailsPanel : function() {
    	this._itemSelected(this._selectedItem);
    },
    
    onHideAttempt : function(event) {
		var targetClass = event.target.className;
		// we open the dialog only when we haven't clicked the - or + expand buttons
		if(targetClass != "dijitTreeExpando dijitTreeExpandoClosed" && targetClass != "dijitTreeExpando dijitTreeExpandoOpened") {
		    this._detailsPanel.onHideAttempt(event, this);
		}
    }
});
