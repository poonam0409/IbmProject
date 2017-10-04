dojo.provide("cwapp.tab_bdr.TabBPH");

dojo.require("dojo.data.ObjectStore");
dojo.require("dojo.store.Memory");
dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.DropDownButton");
dojo.require("dijit.Menu");
dojo.require("dijit.MenuItem");
dojo.require("dijit.tree.ForestStoreModel");
dojo.require("dijit.Tree");

dojo.require("dojox.widget.Standby");
dojo.require("dojox.cometd");
dojo.require("dojox.cometd.callbackPollTransport");

dojo.require("idx.layout.BorderContainer");
dojo.require("idx.layout.ButtonBar");

dojo.require("cwapp.main.AddressableWidget");
dojo.require("cwapp.main.SearchBox");
dojo.require("cwapp.main.Error");
dojo.require("cwapp.main.CustomTreeNode");

dojo.require("cwapp.tab_bdr.bph.PlaceholderWidget");
dojo.require("cwapp.tab_bdr.bph.DetailsPanel");
dojo.require("cwapp.tab_bdr.bph.AddBusinessObjectDialog");
dojo.require("cwapp.tab_bdr.bph.DeleteDialog");
dojo.require("cwapp.tab_bdr.bph.BphImportDialog");
dojo.require("cwapp.tab_bdr.bph.ExportToCwdbDialog");
dojo.require("cwapp.tab_bdr.bph.ExportToCSVDialog");
dojo.require("cwapp.tab_bdr.bph.ImportFromCSVDialog");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.TabBPH", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin, cwapp.main.AddressableWidget ], {
	
	// basic widget settings 
    templateString : dojo.cache("cwapp.tab_bdr","templates/TabBPH.html"),
	widgetsInTemplate : true,
    
	// nls support
	msg : {},
	_type : "", //EXT230 : Type information variable is added 
	
	// constants
	_con : {
		SEARCH_FILTER_ALL : "%",
		STATUS_SUCCESSFUL : "success",
		STATUS_CANCEL : "cancel",
	},

	// private members
	_tree : null,
	_treeChildAttrs : null, // Tree item attributes containing child items, depends on user role
	_inMemStore : null,
	_dataStore : null,
	_treeModel : null,
	_selectedPaths : null,
	_expandedNodes : [],
	_urlHandlingDone : false, // Set to true once we have applied the URL parameters
	_urlId: null,
	_urlType: null,
	_selectedItem : null,
	_copiedItemType : null,
	_copiedItemId : null,
	_placeholderWidget : null,
	_detailsPanel : null,
	_standByOverlay : null,
	_cometdClosed : true,
	_refreshNeeded : true,
	_uploadImportFileDeferred : null,
	_cometdTopics : {
		TOPIC_BDR_EXPORT_STARTED : CometD.TOPIC_BDR_EXPORT_STARTED,
		TOPIC_BDR_EXPORT_FINISHED : CometD.TOPIC_BDR_EXPORT_FINISHED,
	},
	_treeConnects : [],
	
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
		this._init();
		dojo.connect(this, "onShow", this, "_myShow");
		dojo.connect(this, "onHide", this, "_unShow");

		dojo.connect(this.addTopLevelProcessButton, "onClick", this, "_addTopLevelProcess");
		dojo.connect(this.addProcessButton, "onClick", this, "_addProcess");
		dojo.connect(this.addProcessStepButton, "onClick", this, "_addProcessStep");
		dojo.connect(this.addBusinessObjectButton, "onClick", this, "_addBusinessObject");
		dojo.connect(this.removeButton, "onClick", this, "_removeButtonClicked");
		
		dojo.connect(this.importFromCSVButton, "onClick", this, "_importFromCSVFile");
		dojo.connect(this.importFromSAPSolMgrFileButton, "onClick", this, "_importFromSAPSolMgrFile");

		dojo.connect(this.loadEverythingIntoCwdbButton, "onClick", this, "_exportToCwdb");
		dojo.connect(this.exportToCSV, "onClick", this, "_exportToCSV");
		
		dojo.connect(this.filteringInput, "onKeyUp", this, "_filterTree");
	
		dojo.connect(this.refreshAction, "onClick", this, "_refresh");
		dojo.connect(this.copyButton, "onClick", this, "_copyButtonClicked");
		dojo.connect(this.pasteButton, "onClick", this, "_pasteButtonClicked");
		dojo.connect(this.movToGTButton, "onClick", this, "_movToGTButtonClicked");

		
		dojo.subscribe(Topics.REFRESH_BPH_WHEN_SHOWN, dojo.hitch(this, this._triggerRefresh));
		// subscribe to refresh topic
		dojo.subscribe(Topics.REFRESH_BPH, dojo.hitch(this, this.refresh));
		// subscribe to topic published by the AddBusinessObjectDialog
		dojo.subscribe(Topics.ATTACH_BO, dojo.hitch(this, this._attachBO));
		
		// initialize standby overlay
		this._initStandByOverlay();
		this._standByOverlay.show();
		
		this.inherited(arguments);
	},
	
	// Implements the "abstract" function from AddressableWidget
	getCurrentAddress: function () {
		if (this._tree.selectedItem && this._tree.selectedItem.id != "root") {
			return {
				type: this._tree.selectedItem.type,
				id: this._tree.selectedItem.id
			};
		}
		return null;
	},
	
	// Implements the "abstract" function from AddressableWidget
	handleHash: function (hashObject) {
		if (hashObject && hashObject.type && hashObject.id) {
			this._urlType = hashObject.type;
			this._urlId = hashObject.id;
		}
	},

	// Called for general tree updates and when an item has been edited.
    // Refreshes the tree and restores any previous selection.
    refresh : function() {
    	dojo.publish(Topics.REFRESH_BO_WHEN_SHOWN);
		this._saveTreeSelection(); 
		this._expandedNodes=[];
		this._saveExpandedRows(this._tree._itemNodesMap["root"][0].item);
    	this._refreshStore();
    },
    
    // Called when a new item has been added under the selected one.
    // Refreshes the tree and selects the new item.
    refreshForAddedItem : function(item) {
    	dojo.publish(Topics.REFRESH_BO_WHEN_SHOWN);
    	this._saveTreeSelection();
		// We've created a new item, make it the selected one
		this._selectedPaths[0].push(item);
    	this._refreshStore();
    },

    // Called when the selected item has been deleted.
    // Refreshes the tree and selects the parent item.
    // NOTE: the parent needs to be passed, not the deleted item.
    refreshForDeletedItem : function(item) {
    	dojo.publish(Topics.REFRESH_BO_WHEN_SHOWN);
    	this._saveTreeSelection();
    	this._selectedPaths[0].pop();
    	this._refreshStore();
    },	
    
	// Called by the remove/detach confirmation dialog, user has confirmed item removal
	removeItems : function() {
		var selectedItems = this._tree.paths;
		var multiple = (selectedItems.length > 1);
		var processStepArray = [];
		var boIndex = {};
		
		// Scan the selection array.
		// Delete any processes right away.
		// Build processStepArray and boIndex to separate the selected proess steps and BOs.
		dojo.forEach(selectedItems, dojo.hitch(this, function (path, index) {
			var item = path[path.length - 1];
			_type=item.type;
			switch (item.type) {
			case BdrTypes.PROCESS :
				// Delete process immediately
				var deleteUrl = Services.BDR_PROCESS + "/" + item.id;
				this._deleteItem(item, deleteUrl, multiple);
				break;
			case BdrTypes.PROCESS_STEP :
				// Add process step to array
				processStepArray.push(item);
				break;
			case BdrTypes.BO :
				// Push the BO into the index entry (array) under the parent process step's id
				if (!boIndex[path[path.length - 2].id]) {
					// Create a new index entry and add the process step itself as the first value
					boIndex[path[path.length - 2].id] = [];
					boIndex[path[path.length - 2].id].push(path[path.length - 2]);
				}
				boIndex[path[path.length - 2].id].push(item);
				break;
			}
		}));

		// Delete process steps
		for (var stepNr in processStepArray) {
			var processStep = processStepArray[stepNr];
			var deleteUrl = Services.BDR_PROCESS_STEP + "/" + processStep.id;
			this._deleteItem(processStep, deleteUrl, multiple);
			// Prevent attempts to detach BOs from deleted process step
			boIndex[processStep.id] = null;
		};
		
		// Detach BOs, grouped by parent process step
		for (var stepId in boIndex) {
			var indexEntry = boIndex[stepId];
			if (indexEntry == null) continue; // the process step may have been deleted just now
			// Get the process step itself
			var processStep = indexEntry[0];
			// Remove each selected BO from the process step object
			for (var boNr in indexEntry) {
				if (boNr == 0) continue; // Skip the process step itself
				var bo = indexEntry[boNr];
				Util.removeItemById(processStep.usedBusinessObjects, bo.id);
			}
			this._saveProcessStep(processStep, processStep, false, multiple);
		};
		
		if (multiple) {
			this._clearTreeSelection();
			this._refreshStore();
		}
		this._resetButtonBar();

		this.cleanHash();
	},

	// Private functions
	
	// Initial setup
	_init : function() {
		this._inMemStore = new dojo.store.Memory({id : "root"});
		this._dataStore = new dojo.data.ObjectStore({
			objectStore: this._inMemStore,
		    getLabel : function(item) {
		        return item.name;
		    }
		});
		
		// the tree model must be configured according to the currently authenticated user (administrator role or not)
		if (General.ISBDRSUPERUSER || General.ISREADONLY) {
			this._treeChildAttrs = ["childProcesses", "processSteps", "usedBusinessObjects", "usages", "children"];
		} else {
			this._treeChildAttrs = ["aclChildProcesses", "processSteps", "usedBusinessObjects", "usages", "children"];
		}
		if(!General.ISBDRSUPERUSER) {
			dojo.style(this.loadEverythingIntoCwdbButton.domNode, "display", "none");
			dojo.style(this.movToGTButton.domNode, "display", "none");
		}

		this._treeModel = new dijit.tree.ForestStoreModel({
			store: this._dataStore,
			rootId: "root",
			bphScope: this,
			newChildren: [],
			foundItems: [],
			childrenAttrs: this._treeChildAttrs,
			filteredPaths: [],

			
	        
	        //we get the path  to the filtered nodes
	        getPath : function(target, path, item) { 
	        	//save all nodes till we reach our target
	            path.push({items: item});
	            if (item == target) {  
	                return path;
	            }
	            var children = [];
	            for (var i = 0; i < this.childrenAttrs.length; i++) {
	            	if (item[this.childrenAttrs[i]]) {
	            		children = children.concat(item[this.childrenAttrs[i]]); 
	            	}
	            }
	            for (var i in children) { 
	                var branch = path.slice(0); 
	                var branchResult = this.getPath(target, branch, children[i]); 
	                if (branchResult) {
	                	return branchResult;
	                } 
	            } 
	            return undefined; 
	        },
	        //item the actual treenode
	        //rootItem is set just for the first run to identify the 1st lvl processes
	        
	        recursiveTreeSearch : function(item,firstRun){
	        	var found=false;
	        		if (firstRun==null&&item.name.toUpperCase().indexOf(this.filterCondition.toUpperCase())>-1) {
	    				this.foundItems.push(item);
	    				found=true;
	        		}
        			var children = [];
    		        for (var i = 0; i < this.childrenAttrs.length; i++) {
    		        	if (item[this.childrenAttrs[i]]) {
    		        		children = children.concat(item[this.childrenAttrs[i]]); 
    		        	}
    		        }
    		        for(i in children){
    		        	if(this.recursiveTreeSearch(children[i])){
	    					if(firstRun!=null){
	    	    				this.newChildren.push(children[i]);
	    	    				continue;
	    	    			}
		    				found= true;
	    				}
    		        }
	        	if(found){
	        		return true;
	        	}
	        	
	        	if(firstRun==undefined){
	        		return false;
	        	}
	    	},
	    	_resetFilter : function(item){
	    		if(this.filteredPaths.length!=0){
        			for(var i =0;i<this.filteredPaths.length;i++){
        				//We do not want to collapse the root node
        				for(var j =1; j< (this.filteredPaths[i].length);j++){
	        				nodes = this.bphScope._tree.getNodesByItem(this.filteredPaths[i][j].items);
	        				for(var k =0; k<nodes.length;k++){
	        					if(nodes[k]!=0&&nodes[k]!=null){
	        			    		nodes[k].labelNode.style.color="black";
		        					if(nodes[k].isExpanded){
			        					this.bphScope._tree._collapseNode(nodes[k]);
		        					}
	        					}
	        				}
        				}
        			}	
        			this.filteredPaths=[];
        		}
	    	},
	    	
	    	// utility function to set the filtering condition for the tree
	        filter : function(searchString) {
	        	this.filterCondition = searchString;
	        	var item=this.bphScope._tree._itemNodesMap["root"][0].item;
	        	if (this.filterCondition != null && this.filterCondition != "") {
		        		this.newChildren = [];
		        		var filteredPath= [];
		        		this.foundItems=[];
		        		var filteredPaths=[];

		        		//reset previous filter (collapse and change color)
		        		this._resetFilter(item);
		        		// search for items and get their 1st level processes
		        		this.recursiveTreeSearch(item,true);
		        		// trigger the tree update for the 'new' root item
		        		this.onChildrenChange(item, this.newChildren);
		        		if(this.foundItems.length>0){
		        		//prepare all found items and get their paths
		        		for(var i = 0 ; i<this.foundItems.length;i++){
		        			filteredPaths.push(this.getPath(this.foundItems[i],filteredPath,item));
		        			filteredPath=[];
		        		}
		        		//going through all paths
		        		for(var i =0; i < filteredPaths.length; i++){
		        			//going through all nodes of a path
		        			for(var j =0; j< (filteredPaths[i].length);j++){
		        				var nodes = this.bphScope._tree.getNodesByItem(filteredPaths[i][j].items);
		        				//highlight the last node
		        				if((j+1)!=(filteredPaths[i].length)){
		        					for(var k =0; k<nodes.length;k++){
			        					if(nodes[k]!=0&&nodes[k]!=null){
				        					if(!nodes[k].isExpanded){
					        					this.bphScope._tree._expandNode(nodes[k]);
					        				}
			        					}	
		        					}
		        				}
		        				//we do not want to expand the last node
		        				else{
		        					for(var k =0; k<nodes.length;k++){
			        					if(nodes[k]!=0&&nodes[k]!=null){
			        						nodes[k].labelNode.style.color="blue";
			        					}
		        					}
		        				}
		        		}
		        		this.filteredPaths=filteredPaths;
		        		}
		        	}
	        	} else {
	        			this.onChildrenChange(item, item.children);
	        			this._resetFilter(item);
	        			this.bphScope._tree.set("paths",this.bphScope._selectedPaths);
	        	}
	        }
		});
		
		this._setupTree();
	},
	
	_setupTree : function() {
		var newTree = new dijit.Tree({ 
            model : this._treeModel,
            getIconClass : Util.bdrIconFunc,
            id : "bphTree",
            showRoot : false,
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
            _createTreeNode : function(args) {
        	return new cwapp.main.CustomTreeNode(args);
            },          
        });
		
		this._tree.appendChild(newTree.domNode); 
		this._tree = newTree;
		dojo.addClass(this._tree.domNode, "bdrBphTree");
		dojo.connect(this._tree, "onClick", this, "_itemSelected");
		dojo.connect(this._tree.dndController, "onMouseDown", this, "_itemSelectionAttempt");
	},
	
	_initStandByOverlay : function() {
		this._standByOverlay = new dojox.widget.Standby({
			target : this._tree.domNode,
			image : myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loading.gif",
			color: "white",
		});
		document.body.appendChild(this._standByOverlay.domNode);
		this._standByOverlay.startup();
	},
	
	_initCometd : function() {
		dojo.xhrGet({
			url: Services.SESSIONRESTURL,
			handleAs: "text",
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				// configure the topics with the current session id
				for (var topic in this._cometdTopics) {
					this._cometdTopics[topic]  = CometD[topic] + data;
				}
				
				// initialize the cometd / bayeux protocol
				dojox.cometd.init(Services.COMETD);
				
				// open cometd communication
				this._openCometd();
				this._cometdClosed = false;
			}),
			error: dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	_openCometd : function() {
		var started = dojox.cometd.subscribe(this._cometdTopics.TOPIC_BDR_EXPORT_STARTED, dojo.hitch(this, function(message) {
			// do nothing
		}));
		var finished = dojox.cometd.subscribe(this._cometdTopics.TOPIC_BDR_EXPORT_FINISHED, dojo.hitch(this, function(message) {
			// handle status
			if (message.data == this._con.STATUS_SUCCESSFUL) {
				// file content is prepared, initiate the download request
				this._downloadExportFile();
			} else if (message.data == this._con.STATUS_CANCEL) {
				idx.info(this.msg.ExportToFile_Cancel);	
			} else {
				idx.error({
					messageId: null,
					summary: this.msg.ExportToFile_InternalError,
					detailocall: null,
					moreContent: null
				});
			}
			// we need to close the connection to the cometd / bayeux server
			this._closeCometd();
		}));
		
		return new dojo.DeferredList([started, finished]);
	},
	
	_closeCometd : function() {
		for (var topic in this._cometdTopics) {
			setTimeout(dojo.hitch(this, function() {
				dojox.cometd.unsubscribe(this._cometdTopics[topic]);
			}), 100);
		}

		dojox.cometd.disconnect();
		this._cometdClosed = true;
	},
	
	_unShow : function() {
		this._saveTreeSelection();
		this._clearTreeSelection();
		dojo.style(dojo.byId("detailsPanel"), "display", "none");
		dojo.style(dojo.byId("bphPlaceholderWidget"), "display", "");
	},

	_myShow : function() {
		this.borderContainer.resize(); // Workaround: content is not displayed otherwise
		
		this.removeButton.set("disabled", true);
		this.copyButton.set("disabled", true);
		this.pasteButton.set("disabled", true);
		this.addDropDownButton.set("disabled", true);
		this.importButton.set("disabled", true);
		this.movToGTButton.set("disabled", true);
		// disable / enable the bph tree manipulation buttons according to the role the currently authenticated user has
		if (General.ISBDRSUPERUSER) {
			// enable the tree manipulation buttons
			this.addDropDownButton.set("disabled", false);
			this.importButton.set("disabled", false);
			this.movToGTButton.set("disabled", false);
		}
		
		// make sure exportDropDownButton is disabled if export being processed
		// cometd is only open/used for communication related to the export
		if (!this._cometdClosed) {
			this._disableExportButton();
		}
		
		// initialize dependent widgets and show placeholder panel
		if (!this._detailsPanel) {
			this._detailsPanel = new cwapp.tab_bdr.bph.DetailsPanel({}, 'detailsPanel');
			dojo.style(dojo.byId("detailsPanel"), "display", "none");
		}
		
		if (!this._placeholderWidget) {
			this._placeholderWidget = new cwapp.tab_bdr.bph.PlaceholderWidget({}, 'bphPlaceholderWidget');
			dojo.style(dojo.byId("bphPlaceholderWidget"), "display", "");
		}
		
		// Refresh DetailsPanel
		if(this._tree.selectedItem && this._tree.selectedItem.id != "root") {
			this._itemSelected(this._tree.selectedItem);
		}
		
		if(this._refreshNeeded){
			this._refreshNeeded=false;
			this.refresh();	
		} else {
			this._restoreTreeSelection();
		}
		
	},
	
    _findItemPath : function(target, path, item) { 
        path.push({id: item.id});
        if (item.id == target) { 
            // Found our target item
            return path;
        }
        // Build child array using the known attribute names
        var children = [];
        for (var i = 0; i < this._treeChildAttrs.length; i++) {
        	if (item[this._treeChildAttrs[i]]) {
        		children = children.concat(item[this._treeChildAttrs[i]]); 
        	}
        }
        for (var i in children) { 
            // Start a new branch
            var branch = path.slice(0); 
            var branchResult = this._findItemPath(target, branch, children[i]); 
            if (branchResult) {
            	return branchResult;
            } 
        } 
        // No match found
        return undefined; 
    },
    
    _refresh : function(){
    	dojo.publish(Topics.REFRESH_BO_WHEN_SHOWN);
    	this._saveTreeSelection();  
    	this._expandedNodes=[];
    	this._saveExpandedRows(this._tree._itemNodesMap["root"][0].item);
    	this._refreshStore();
    	
    },
    
	
	// Refreshes the tree data and view
	_refreshStore : function() {
		this._standByOverlay.show();
		dojo.xhrGet({
			url: Services.BDR_GET_TREE,
			handleAs: "json",
			preventCache: true,
			load: dojo.hitch(this, function(treeData) {
				// Show tree when new tree is incoming
				this._inMemStore.setData(treeData);
				this._tree.refresh();
				
				// Handle URL params
				if (!this._urlHandlingDone) {
					this._resetTreeSelection();
					
					var newPath = this._findItemPath(this._urlId, new Array(), this._tree.model.root);
					if (newPath) {
			        	newPath.shift();
			         	this._selectedPaths[0] = this._selectedPaths[0].concat(newPath);
					}
					this._urlHandlingDone = true;
				}
				
				this._restoreExpandedRows();
				this._standByOverlay.hide();
				this._updateImportExportButtons();
				this.filteringInput.set("value","");
				this._restoreTreeSelection;
			}),
			error: dojo.hitch(this, function(error) {
				this._standByOverlay.hide();
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	_updateImportExportButtons : function() {
		// make sure exportDropDownButton is disabled if export being processed
		// cometd is only open/used for communication related to the export
		if (!this._cometdClosed) {
			this._disableExportButton();
		}
	},
	
	_addTopLevelProcess : function() {
		this._resetTreeSelection();
		dojo.style(dojo.byId("bphPlaceholderWidget"), "display", "none");
		dojo.style(dijit.byId("detailsPanel").domNode, "display", "");
		this._detailsPanel.update(this, BdrTypes.PROCESS, null, null, null, true);
	},
	
	_addProcess : function() {
		dojo.style(dojo.byId("bphPlaceholderWidget"), "display", "none");
		dojo.style(dijit.byId("detailsPanel").domNode, "display", "");
		this._detailsPanel.update(this, BdrTypes.PROCESS, null, this._tree.selectedItem.id, null, true);
	},
	
	_addProcessStep : function() {
		dojo.style(dojo.byId("bphPlaceholderWidget"), "display", "none");
		dojo.style(dijit.byId("detailsPanel").domNode, "display", "");
		this._detailsPanel.update(this, BdrTypes.PROCESS_STEP, null, this._tree.selectedItem.id, null, true);
	},
	
	_addBusinessObject : function() {
		var addBODialog = new cwapp.tab_bdr.bph.AddBusinessObjectDialog();
		addBODialog.show(this);
	},
	
	// Called via dojo topic, user has selected a BO to attach to the selected process step
	_attachBO : function(boArray) {
		var processStep = this._tree.selectedItem;
		dojo.forEach(boArray, dojo.hitch(this, function(bo, index) {
			processStep.usedBusinessObjects.push({id: bo.id});
		}));
		
		this._saveProcessStep(processStep, boArray[0], true);
	},
	
	// Opens the remove/detach confirmation dialog
	_removeButtonClicked : function() {
		var detach = (this._tree.selectedItem.type == BdrTypes.BO);
		dijit.byId("bdrDeleteDialog").show(this, this._tree.paths, detach);		
	},
	
	_copyButtonClicked : function() {
		this._copiedItemType= this._selectedItem.type;
		this._copiedItemId= this._selectedItem.id;
		this._selectedItem=null;
		if(General.ISBDRSUPERUSER && !this._copiedItemType==BdrTypes.PROCESS_STEP)
			this.pasteButton.set("disabled", false);
		else
			this.pasteButton.set("disabled", true);
		this._copied=true;
	},
	
	_pasteButtonClicked : function() {
		var url ="";
		if(this._selectedItem!=null)
			url = Services.BDR_PROCESS + "/" + this._copiedItemType+"~"+this._copiedItemId+"/"+this._selectedItem.type+"~"+this._selectedItem.id;
		else
			url = Services.BDR_PROCESS + "/" + this._copiedItemType+"~"+this._copiedItemId+"/none";
		this.copyButton.set("disabled", true);
		this.pasteButton.set("disabled", true);
		console.log("ajay called");
		dojo.xhrGet({
			url: url,
			handleAs: "json",
			preventCache : true,
			sync : true,
			load : function(data) {
					console.log("succccccccesssss:"+data);
			},
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
		this._copiedItemType= null;
		this._copiedItemId= null;
		this._refreshStore();
	},
	
	_movToGTButtonClicked : function()
	{
		var url = Services.BDR_PROCESS + "/moveToGT/move";
		var mssg=this.msg.MovedToGT;
		dojo.xhrGet({
				url: url,
				handleAs: "json",
				preventCache : true,
				sync : true,
				load : function(data) {
					idx.info(mssg+data);
				},
				error : dojo.hitch(this, function(error) {
					cwapp.main.Error.handleError(error);
				})
			});
	},
	
	// Actually deletes the given item by calling the given URL.
	// multiple: if this is part of a multi-deletion, don't attempt to select the parent item afterwards.
	_deleteItem : function(item, url, multiple) {
		var parentItem = null;
		if (!multiple) {
			parentItem = (this._tree.selectedNode.getParent().item.id === "root") ? null : this._tree.selectedNode.getParent().item;
		}

		dojo.xhrDelete({
			url: url,
			headers: {"Content-Type": "application/json"},
			handleAs: "json",
			postData: dojo.toJson(item),
			preventCache : true,
			sync : true,
			load : dojo.hitch(this, function(data) {
				if (!multiple){
					if (parentItem) {
						this.refreshForDeletedItem(parentItem);
					} else {
						this._clearTreeSelection();
						this._refreshStore();
					}
				}
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},

	// Used for attaching/detaching a BO
	// multiple: if this is part of a multi-deletion, don't attempt to select the parent item afterwards.
	_saveProcessStep : function(processStep, itemToDisplay, added, multiple) {
		dojo.xhrPut({
			url: Services.BDR_PROCESS_STEP + "/" + processStep.id,
			headers: {"Content-Type": "application/json"},
			handleAs: "json",
			postData: dojo.toJson(processStep),
			preventCache : true,
			sync : true,
			load : dojo.hitch(this, function(data) {
				if (added) {
					this.refreshForAddedItem(itemToDisplay);
				} else if (!multiple) {
					this.refreshForDeletedItem(itemToDisplay);
				}
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	_enableExportButton : function() {
		this.exportDropDownButton.set("disabled", false);
	},
	
	_disableExportButton : function() {
		this.exportDropDownButton.set("disabled", true);
	},
	
	_importFromCSVFile : function() {
	    var dialog = new cwapp.tab_bdr.bph.ImportFromCSVDialog({
	    	parentWidget : this
	    });
	    dialog.show();
	},

	_importFromSAPSolMgrFile : function() {
	    this._importBPH(BphImportTypes.MPX);
	},
	
	_importBPH : function (importType, override){
		var bphImportDialog = new cwapp.tab_bdr.bph.BphImportDialog({
			importType : importType,
			override : override
		});
		
		bphImportDialog.startup();
		bphImportDialog.show(this);
	},
	
	_prepareExportThread : function(queryParameters) {
		// disable export button as long as export is being processed
		this._disableExportButton();
		
		// initialize cometd topics
		this._initCometd();
		
		// inform user that export will be running in background
		idx.info(this.msg.ExportToFile_Dialog);
		var url=Services.BDR_PREPARE_EXPORT_TO_FILE;
		queryParameters.itemType=_type;
		setTimeout(dojo.hitch(this, function() {
			dojo.xhrGet({
				url: url,
				handleAs: "text",
				preventCache: true,		
				content: queryParameters,
				load: dojo.hitch(this, function(data) {
					// call request to start thread
					this._startExportThread();
				}),
				error: dojo.hitch(this, function(error) {
					this._closeCometd();
					this._enableExportButton();
					cwapp.main.Error.handleError(error);	
				})
			});
		}), 1000);
	},
	
	_startExportThread : function(){
		dojo.xhrPost({
			// service to start thread
			url: Services.BDR_START_EXPORT_TO_FILE,
			handleAs: "text",
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				// success
			}),
			error: dojo.hitch(this, function(error) {
				this._closeCometd();
				this._enableExportButton();
				cwapp.main.Error.handleError(error);
			})
		});		
	},
	
	_downloadExportFile : function (){
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
            url : Services.BDR_EXPORT_BDR_TO_FILE,
            handleAs : "html",
            method : "GET",
                  
            // dojo.io.iframe expects results wrapped
            // in textarea tags, thus callbacks might not be called
            load: dojo.hitch(this, function(data) {
        		// success
			}),
            
            error: dojo.hitch(this, function(error) {
    			cwapp.main.Error.handleError(error);
    		}),
		});
		
		// delay request to make sure file is downloaded before
		setTimeout(dojo.hitch(this, function() {
			this._removeExportFileFromSession();
		}), 5000);
		
		// export done, enable export button
		this._enableExportButton();

	},
	
	_removeExportFileFromSession : function() {
		// request to remove file content from session
		dojo.xhrGet({
			url: Services.BDR_REMOVE_EXPORT_FILE,
			handleAs: "text",
			preventCache: true,
			load: dojo.hitch(this, function(data) {
				// success
			}),
			error: dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});	
	},
	
	_exportToCwdb : function() {
		dijit.byId("exportToCwdbDialog").show();	
	},
	
	_exportToCSV : function(){
	    var dialog = new cwapp.tab_bdr.bph.ExportToCSVDialog({
		parentWidget : this
	    });
	    dialog.show();
	},
	
	// Displays the details panel for the selected item
	// Also adds or removes choices of the addDropdownButton depending on the type of the selected item
	_itemSelected : function(item) {
		this._selectedItem = item;
		this._selectedPaths = this._tree.get("paths");
		this.setHash();
		this._resetButtonBar();
		if((this._selectedItem.type==BdrTypes.PROCESS || this._selectedItem.type==BdrTypes.PROCESS_STEP) && !General.ISREADONLY){
			if(this._tree.selectedNode.getParent().item.id == "root" && !General.ISBDRSUPERUSER){
				this.copyButton.set("disabled", true);
				this.pasteButton.set("disabled", true);
			}else{
				this.copyButton.set("disabled", false);
			}
		}else{
			this.copyButton.set("disabled", true);
			this.pasteButton.set("disabled", true);
		}
		if(this._copiedItemType!=null && this._selectedItem.type==BdrTypes.PROCESS){
			this.pasteButton.set("disabled", false);
		} else if(this._copiedItemType==null && this._selectedItem.type==BdrTypes.PROCESS_STEP){
			this.pasteButton.set("disabled", true);
		} else
			this.pasteButton.set("disabled", true);
		if (this._tree.paths.length > 1) {
			// Multiselection
			dijit.byId("detailsPanel").hideWidget();
			dojo.style(dojo.byId("bphPlaceholderWidget"), "display", "");
			dijit.byId("bphPlaceholderWidget").setMessage(true);
			if (General.ISBDRSUPERUSER) {
				// Enable the remove button
				this.removeButton.set("disabled", false);
			}
			return;
		}

		// Hide placeholder
		dojo.style(dojo.byId("bphPlaceholderWidget"), "display", "none");
		
		// Open details panel
		dijit.byId("detailsPanel").showWidget();
		var parentObjectId = (this._tree.selectedNode.getParent().item.id === "root") ? null : this._tree.selectedNode.getParent().item.id;
		this._detailsPanel.update(this, item.type, item.id, parentObjectId, item, true);

		_type=item.type;
		switch(item.type) {
		case BdrTypes.PROCESS:
			if (General.ISBDRSUPERUSER)
				this.removeButton.set("disabled", false);
			// The process manipulation buttons are only displayed for the admin user
			if (!General.ISREADONLY){
				this.addDropDownButton.set("disabled", false);
				dojo.setAttr(this.addProcessButton, "style", "display: table-row");
				
				// Only allow adding subprocesses if the selected process doesn't have steps
				if (!item.processSteps || item.processSteps.length == 0) {
					dojo.setAttr(this.addProcessButton, "style", "display: table-row");
				}
				// Only allow adding steps if the selected process doesn't have subprocesses
				if (!item.childProcesses || item.childProcesses.length == 0) {
					dojo.setAttr(this.addProcessStepButton, "style", "display: table-row");
				}
			}
			break;
			
		case BdrTypes.PROCESS_STEP:
			if (General.ISBDRSUPERUSER)
				this.removeButton.set("disabled", false);
			// Enable the remove button for the admin user only
			if(!General.ISREADONLY){
				// Enable adding BOs
				this.addDropDownButton.set("disabled", false);
				dojo.setAttr(this.addBusinessObjectButton, "style", "display: table-row");
			}
			break;
			
		case BdrTypes.BO:
			if (General.ISBDRSUPERUSER) {
			// Enable the remove button
				this.removeButton.set("disabled", false);
				// Rename remove button to "Detach"
				this.removeButton.set("label", this.msg.BPHTAB_16);
			}
			break;
		}
	},
	
	// Remember the selection so we can restore it after refreshing
    _saveTreeSelection : function() {
    	if (this._tree.paths.length == 0) {
			this._resetTreeSelection();
		} else {
			this._selectedPaths = this._tree.paths;
		}
    },
    
    _itemSelectionAttempt : function(event) {
	this.onHideAttempt(event);	 
    },
	
    // Resets the button bar as if nothing were selected in the tree
    _resetButtonBar : function() {
		// Disable the add / remove buttons
		this.addDropDownButton.set("disabled", true);
		this.removeButton.set("disabled", true);
		// Reset the remove button label
		this.removeButton.set("label", this.msg.BPHTAB_2);
		
		// Hide submenu items
		dojo.setAttr(this.addTopLevelProcessButton, "style", "display: none");
		dojo.setAttr(this.addProcessButton, "style", "display: none");
		dojo.setAttr(this.addProcessStepButton, "style", "display: none");
		dojo.setAttr(this.addBusinessObjectButton, "style", "display: none");

		// Selectively enable buttons and submenu items

		// Top level processes can be added regardless of item selection (admin user only)
		if (General.ISBDRSUPERUSER) {
			this.addDropDownButton.set("disabled", false);
			dojo.setAttr(this.addTopLevelProcessButton, "style", "display: table-row");
		}
    },
    
    // Add the root element to the new selection path
    _resetTreeSelection : function() {
    	this._selectedPaths = [[{children: null, id: "root", label: "ROOT", root: true}]];
    	this._tree.set('paths', this._selectedPaths);
    },
    
    // Set the old selection
    _restoreTreeSelection : function () {
    	if (this._selectedPaths) {
	    	// Restore / set selection
			this._tree.set('paths', this._selectedPaths).then(dojo.hitch(this, function() {
				if (this._tree.selectedNode) {
					// Update dependent UI elements
					this._tree.onClick(this._tree.selectedNode.item);
					// Scroll to selection
					dojo.window.scrollIntoView(this._tree.selectedNode.domNode);
				}
			}));
    	}
    },
    
	_clearTreeSelection : function() {
	    this._tree.set("paths", "");
		// disable the 'remove' button
		this.removeButton.set("disabled", true);
		
		// hide the details panel
		dojo.style(dojo.byId("detailsPanel"), "display", "none");

		// show the placeholder
		dojo.style(dojo.byId("bphPlaceholderWidget"), "display", "");
		dijit.byId("bphPlaceholderWidget").setMessage(false);
	},
    
	_filterTree : function() {
		
		
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

		// save the tree selection
		this._saveTreeSelection();
		
		// filter the tree
		this._tree.model.filter(searchString);
		
		
		this.filteringInput.focus();
		
		
	},
	
	
	_saveExpandedRows : function(item){
		
		nodes = this._tree.getNodesByItem(item);
		if(nodes[0]!=0&&nodes[0]!=null){
			if(nodes[0].isExpanded){
				
				this._expandedNodes.push(item);
				var children = [];
		        for (var i = 0; i < this._treeChildAttrs.length; i++) {
		        	if (item[this._treeChildAttrs[i]]) {
		        		children = children.concat(item[this._treeChildAttrs[i]]); 
		        	}
		        }
		        for(i in children){
		        	this._saveExpandedRows(children[i]);
		        }
			}
			
		}
	
		  
	        
		
	},
	
	_restoreExpandedRows : function(){
		for(var i = 0 ; i<this._expandedNodes.length;i++){
			nodes= this._tree.getNodesByItem(this._expandedNodes[i]);
			if(nodes[0]!=0&&nodes[0]!=null){
				this._tree._expandNode(nodes[0]);
			}		
		}
	},
	_triggerRefresh: function(){
		this._refreshNeeded = true;
	},
	
	onHideAttempt : function(event){
	    var targetClass = event.target.className;
	    // we opend the dialog only when we haven't clicked the - or + expand buttons 
	    if(targetClass !="dijitTreeExpando dijitTreeExpandoClosed" && targetClass != "dijitTreeExpando dijitTreeExpandoOpened" ){
		this._detailsPanel.onHideAttempt(event, this);
	    }
	}
});