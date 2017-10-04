dojo.provide("cwapp.tab_bdr.TabTables");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");

dojo.require("idx.layout.BorderContainer");
dojo.require("idx.layout.ButtonBar");

dojo.require("dijit.form.Button");

dojo.require("dojox.data.JsonRestStore");
dojo.require("dojox.grid.EnhancedGrid");
dojo.require("dojox.widget.Standby");

dojo.require("cwapp.main.AddressableWidget");
dojo.require("cwapp.main.SearchBox");

dojo.require("cwapp.tab_bdr.bph.DetailsPanel");
dojo.require("cwapp.tab_bdr.bph.PlaceholderWidget");
dojo.require("cwapp.tab_bdr.bph.FieldsFromSAPImportProgressDialog");

dojo.require("dojo.DeferredList");
dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.TabTables", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin, cwapp.main.AddressableWidget ], {
	
	// basic widget settings
	templateString : dojo.cache("cwapp.tab_bdr", "templates/TabTables.html"),
	widgetsInTemplate : true,

	// nls support
	msg : {},
	
	// constants
	_con : {
		ID_PROPERTY : "name",
		EMPTY_STRING : "",
		SEARCH_FILTER_ALL : "%",
	    TABLE_IMG :
			'<img src="cwapp/img/bdr/table_16.png" title="{0}" width="16px" height="16px" alt="isTable" border="0px" align="left" style="float: left; margin-right: 10px;"/>',
	},
	
	_tableStore : null,
	_detailsPanel : null,
	_placeholderWidget : null,
	_gridSelection : null,
	_standByOverlay : null,
	_urlHandlingDone : false, // Set to true once we have applied the URL parameters
	_urlId: null,
	_urlType: null,
	_gridConnects : [],
	_hasConfiguredSapSystems : false,
	_toolTipHandle : null,
	_sapSystemPasswordDialog : null,
	
	// public functions
	constructor : function(args) {
		if (args) {
			dojo.mixin(this, args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
		// Used in AddressableWidget
		this.addressParameters = ["type", "id"];
	},

	postCreate : function() {
		dojo.connect(this, "onShow", this, "_myShow");
		dojo.connect(this, "onHide", this, "_unShow");
		dojo.connect(this.addButton, "onClick", this, "_addButtonClicked");
		dojo.connect(this.removeButton, "onClick", this, "_removeButtonClicked");
		dojo.connect(this.filteringInput, "onChange", this, "_filterGrid");
		this._addGridConnects();
		dojo.connect(window, "onresize", this, "_resizeTablesGrid");
		dojo.connect(this.importSapViewButton, "onClick", this, "_importSapViewButtonOnClick");
		//subscribe to topic published by FieldsFromSAPImportDialog component
		
		dojo.subscribe(Topics.REFRESH_DETAILSPANEL_TABLES, dojo.hitch(this,this._refreshDetailsPanel));
		
		// initialize standby overlay for the table data grid
		this._initStandByOverlay();
		this._standByOverlay.show();
		
		// disable the add button in read-only mode
		if (General.ISREADONLY){
			this.addButton.set("disabled", true);
		}
		
		this.inherited(arguments);
	},
	
	getSelectedTables : function () {
		var selectedTables = [];
		
		dojo.forEach(this._grid.selection.getSelected(), function(item, index){
			
			selectedTables.push({
				"description" : item.description,
				"id" : item.id,
				"name" : item.name,
				"type" : item.type,
				"updated": item.updated
			});
		});
		return selectedTables;
	},
	
	// Implements the "abstract" function from AddressableWidget
	getCurrentAddress: function () {
		if (this._grid.selection.getSelectedCount() > 0) {
			var selectedItem = this._grid.selection.getSelected()[0];
			return {
				type: selectedItem.type,
				id: selectedItem.id
			};
		}
		return null;
	},
	
	// Implements the "abstract" function from AddressableWidget
	handleHash: function (hashObject) {
		if (hashObject && hashObject.type && hashObject.id && hashObject.type == BdrTypes.TABLE) {
			this._urlType = hashObject.type;
			this._urlId = hashObject.id;
		}
	},
	
    // Called when an item has been updated.
    // Refreshes the list and restores any previous selection.
    refresh : function() {
    	dojo.publish(Topics.REFRESH_BO_WHEN_SHOWN);
    	dojo.publish(Topics.REFRESH_BPH_WHEN_SHOWN);
    	this._refreshStore();
    },
    
    // Called when a new item has been added or when handling a URL.
    // Refreshes the list and selects the passed item.
    refreshForAddedItem : function(item) {
    	dojo.publish(Topics.REFRESH_BO_WHEN_SHOWN);
    	dojo.publish(Topics.REFRESH_BPH_WHEN_SHOWN);
    	this._refreshStore(item);
    },
	
	// Removes the selected items
	removeItems : function() {
		dojo.publish(Topics.REFRESH_BO_WHEN_SHOWN);
		dojo.publish(Topics.REFRESH_BPH_WHEN_SHOWN);
		var selection = this._grid.selection.getSelected();
		
		// clear the selection
		this._clearGridSelection();
		
		// remove selected items
		dojo.forEach(selection, dojo.hitch(this, function(item, index) {
			this._tableStore.deleteItem(item);
			this._tableStore.save();
		}));

		this._grid.render();
		
		dojo.style(dojo.byId("tableDetailsPanel"), "display", "none");
		dojo.style(dojo.byId("tableBphPlaceholderWidget"), "display", "");
	},

	// Private functions
	
	_myShow : function() {
		
		if(General.ISBDRSUPERUSER){
		    this.importSapViewButton.set("disabled", false);
		}else{
			this.importSapViewButton.set("disabled", true);
		}
		if(!General.ISREADONLY){
		    this.importFromSapButton.set("disabled", false); 
		}else{
			this.importFromSapButton.set("disabled", true);
		}
		this.borderContainer.resize();
		
		// if the data store is invalid ('null') then we have to populate it
		if (this._tableStore == null) {
			this._setupStore();
		}
		
		this._setupGrid(this._tableStore);
		this._grid.render();
		this._restoreGridSelection();

		// initialize dependent widgets and show placeholder panel
		if (!this._detailsPanel) {
			this._detailsPanel = new cwapp.tab_bdr.bph.DetailsPanel({}, "tableDetailsPanel");
			dojo.style(dojo.byId("tableDetailsPanel"), "display", "none");
		}
		
		if (!this._placeholderWidget) {
			this._placeholderWidget = new cwapp.tab_bdr.bph.PlaceholderWidget({}, "tableBphPlaceholderWidget");
			dojo.style(dojo.byId("tableBphPlaceholderWidget"), "display", "");
		}
		
		if (!this._urlHandlingDone) {
			if (this._urlId) {
				this._refreshStore({id: this._urlId});
			}
			this._urlHandlingDone = true;
		}
		this._initimportFromSapButton();
	},
	
	_saveGridSelection: function () {
		if (this._grid.selection.getSelectedCount() > 0) {
			this._previousGridSelection = this._grid.selection.getSelected();
		} else {
			this._previousGridSelection = null;
		}
	},
	
	_restoreGridSelection: function () {
		if (this._previousGridSelection) {
			this._grid.selection.select(this._grid.getItemIndex(this._previousGridSelection[0]));
    	}
	},
	
	_unShow : function() {
		// clear the grid selection
		this._saveGridSelection();
		this._clearGridSelection();
		
		dojo.style(dojo.byId("tableDetailsPanel"), "display", "none");
		dojo.style(dojo.byId("tableBphPlaceholderWidget"), "display", "");
	},
	
	_initStandByOverlay : function() {
		this._standByOverlay = new dojox.widget.Standby({
			target : this._grid.domNode,
			image : myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loading.gif",
			color: "white",
		});
		
		document.body.appendChild(this._standByOverlay.domNode);
		this._standByOverlay.startup();
	},

	// connect event handlers for the tree
	_addGridConnects : function() {
		this._gridConnects.push(dojo.connect(this._grid, "onSelected", this, "_itemSelected"));
		this._gridConnects.push(dojo.connect(this._grid, "onDeselected", this, "_itemSelected"));
		this._gridConnects.push(dojo.connect(this._grid, "onMouseDown", this, "_itemSelectionAttempt"));
	},

	_resizeTablesGrid : function() {
		this._grid.resize();
		this._grid.update();
	},
	
	_setupGrid : function(dataStore) {
		this._grid.set("formatterScope", this);
		this._grid.set("store", dataStore);
		this._grid.set("sortInfo", "+1");
		this._grid.set("editable", false);
		this._grid.set("selectable", false);
		this._grid.set("queryOptions", { ignoreCase: true }),
		this._grid.set("loadingMessage", this.msg.BPHTABLESTAB_2);
		this._grid.set("noDataMessage", "<span class=\"dojoxGridNoData\">" + this.msg.BPHTABLESTAB_3 + "</span>");
		
		this._grid.startup();
		this._standByOverlay.hide();
	},
	
	_initimportFromSapButton : function() {
		
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
	
	_setupStore : function() {
		this._tableStore = new dojox.data.JsonRestStore({
			target : Services.BDR_TABLE,
			idAttribute : "id",
			syncMode : "true",
			queryOptions: { ignoreCase: true },
		});
	},
	
	_refreshStore : function(item) {
		if (item) {
			// check if item exists (this may not be the case when using a URL parameter)
			this._tableStore.fetch({query:{id:item.id}, onItem: dojo.hitch(this, function(item) {
				if (item) {
					
					// retrieve item
					this._tableStore.fetchItemByIdentity({
						identity : item.id,
						onItem : dojo.hitch(this, function(item){
							this._grid.render();
							this._grid.scrollToRow(this._grid.getItemIndex(item));
							this._grid.selection.select(this._grid.getItemIndex(item));
						}),
					});
				}
			})});
		} else {
			this._tableStore.fetch({
				onComplete : dojo.hitch(this, function(items){ 
					this._grid.render();
					this._grid.scrollToRow(this._grid.getItemIndex(this._gridSelection[0]));
					this._grid.selection.select(this._grid.getItemIndex(this._gridSelection[0]));
				}),
			});
		}
	},

	_itemSelected : function(rowIndex) {
    	
		// disconnect all event handlers from the grid
		dojo.forEach(this._gridConnects, dojo.disconnect);

		this._itemSelectedDeferred(rowIndex).then(dojo.hitch(this, function() {

			// connect event handlers for the tree
			this._addGridConnects();
		}), dojo.hitch(this, function(error) {
			cwapp.main.Error.handleError(error);

			// connect event handlers for the tree
			this._addGridConnects();
		}));
	},
	
	_itemSelectedDeferred : function(rowIndex) {
		var deferred = new dojo.Deferred();
		
		this._gridSelection = this._grid.selection.getSelected();
		
		if (this._gridSelection.length == 0) {
			deferred.resolve();
			return deferred;
		}
		
		if (this._gridSelection.length > 1) {
			// Multiselection
			dojo.style(dijit.byId("tableDetailsPanel").domNode, "display", "none");
			dojo.style(dojo.byId("tableBphPlaceholderWidget"), "display", "");
			dijit.byId("tableBphPlaceholderWidget").setMessage(true);
			
			deferred.resolve();
			return deferred;
		}
		
		var item = this._gridSelection[0];
		this.setHash();
		
		// enable the 'remove' button
		if (!General.ISREADONLY){
		    this.removeButton.set("disabled", false);
		}
		
		// hide the placeholder
		dojo.style(dojo.byId("tableBphPlaceholderWidget"), "display", "none");
		
		// show details panel
		dojo.style(dojo.byId("tableDetailsPanel"), "display", "");
		this._detailsPanel.update(this, BdrTypes.TABLE, item.id, null, null, true);
		
		deferred.resolve();
		
		return deferred;
	},
	
	_clearGridSelection : function() {
		this._grid.selection.clear();
		
		// enable the 'remove' button
		this.removeButton.set("disabled", true);
		
		// hide the details panel
		dojo.style(dojo.byId("tableDetailsPanel"), "display", "none");

		// show the placeholder
		dojo.style(dojo.byId("tableBphPlaceholderWidget"), "display", "");
		dijit.byId("tableBphPlaceholderWidget").setMessage(false);
	},
	
	_itemSelectionAttempt : function (event){
	    this.onHideAttempt(event);
	},
	
	_filterGrid : function() {	
		var searchString = this.filteringInput.get("value");
		
		// FIXME
		// clear the current selection
		this._clearGridSelection();
		
		if (searchString != null && searchString != this._con.EMPTY_STRING) {
			this._grid.filter({
				name : searchString + this._con.SEARCH_FILTER_ALL,
			});
		}
		else {
			this._grid.filter({
				name : this._con.SEARCH_FILTER_ALL,
			});
		}
		
		this.filteringInput.focus();
		this._removeGridHeader();
	},
	
	// removes the grid header
	_removeGridHeader : function() {
		var header = this._grid.domNode.firstElementChild;
	    header.style.display = "none";
	},

	_addButtonClicked : function() {

		// clear the selection (required)
		this._clearGridSelection();
		
		dojo.style(dojo.byId("tableBphPlaceholderWidget"), "display", "none");
		dojo.style(dojo.byId("tableDetailsPanel"), "display", "");
		this._detailsPanel.update(this, BdrTypes.TABLE, null, null, null, false);
	},
	
	// opens the remove/detach confirmation dialog
	_removeButtonClicked : function() {
		dijit.byId("bdrDeleteDialog").show(this, this._grid.selection.getSelected()[0], false);		
	},
	
	_importFromSapButtonClicked : function(sapSystem) {
		var pwDeferred = this._handleSapSystemPassword(sapSystem);
		if (pwDeferred != null) {
			pwDeferred.then(dojo.hitch(this, function(success) {
				if (success) {
					var progressDialog = new cwapp.tab_bdr.bph.FieldsFromSAPImportProgressDialog({_idProperty : this._con.ID_PROPERTY, caller : BdrTypes.TABLE});
					progressDialog.setStartingPage(this);
					progressDialog.setSapSystem(sapSystem);
					progressDialog.startup();
					progressDialog.start();
					}
				}));
			}
	},
	
	_handleSapSystemPassword : function(sapSystem) {
		if (!this._sapSystemPasswordDialog) {
			this._sapSystemPasswordDialog = new cwapp.tab_config.SapSystemPasswordDialog();
		}
		return this._sapSystemPasswordDialog.getPassword(sapSystem);	
	},
	
	// custom datagrid cell formatter returning an image (aligned left) followed by a text
	_displayTableNameColumn : function(value, rowIndex, cell) {
		
		// styling required to make the image look good in the cell
		cell.customClasses.push("TabTables_mixedColumn");
		
		var imageString = "";
		var tableName = "<div>" + value + "</div>";
		
		imageString = Util.formatMessage(this._con.TABLE_IMG, this.msg.BphTable) + tableName;
		
		return imageString;
	},
	_refreshDetailsPanel : function(){
	    this._itemSelected(0);
	},
	
	onHideAttempt : function(event){
		this._detailsPanel.onHideAttempt(event, this);
	},
	
	
	_importSapViewButtonOnClick : function(){
		    this._importData();
		    this.hide();
		},



	_importData : function(){
		    var mode = BphImportTypes.CSV_SAPVIEW;
			this._importSAPView(mode, true);
		},

		_importSAPView : function (importType, override){
			console.log("_importSAPView");
			var bphImportDialog = new cwapp.tab_bdr.bph.BphImportDialog({
				importType : importType,
				override : override
			});
			bphImportDialog.startup();
			bphImportDialog.show(this);
		}
	
	
});
