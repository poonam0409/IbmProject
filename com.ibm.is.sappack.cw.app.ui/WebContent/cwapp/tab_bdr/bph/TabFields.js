var _entries='';
var checkedEntries= new Array();
var _GTEntries= new Array();
dojo.provide("cwapp.tab_bdr.bph.TabFields");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.layout.SplitContainer");

dojo.require("dojox.grid._EditManager");

dojo.require("idx.layout.ButtonBar");

dojo.require("idx.dialogs");

dojo.require("dijit.form.Button");
dojo.require("dijit.form.DropDownButton");
dojo.require("dijit.form.Select");

dojo.require("dijit.Menu");
dojo.require("dijit.MenuItem");
dojo.require("dijit.CheckedMenuItem");
dojo.require("dojox.grid.EnhancedGrid");

dojo.require("dojox.data.JsonRestStore");

dojo.require("dojo.store.Memory");
dojo.require("dojo.data.ObjectStore");
dojo.require("dojo.data.ItemFileReadStore");

dojo.require("dojox.cometd");

dojo.require("cwapp.main.Error");
dojo.require("cwapp.main.SearchBox");
dojo.require("cwapp.main.CustomCellWidget");
dojo.require("cwapp.tab_config.SapSystemPasswordDialog");
dojo.require("cwapp.tab_bdr.bph.AddFieldDialog");
dojo.require("cwapp.main.CustomCellRelLength");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");
dojo.require("dijit.Tooltip");

dojo.requireLocalization("cwapp", "CwApp");
dojo.declare("cwapp.tab_bdr.bph.TabFields", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/TabFields.html"),
	widgetsInTemplate : true,

	// nls support
	msg : {},
	
	// private constants
	_con : {
		EMPTY_STRING : "",
		SEARCH_FILTER_ALL : "%",
		SYSTEM_CHECKED_IMG :
			'<img src="cwapp/img/checkmark.png" title="{0}" width="20px" height="20px" alt="isChecked" border="0px" align="left"/>',
		SYSTEM_UNCHECKED_IMG :
			'<img src="cwapp/img/transparent.png" title="{0}" width="20px" height="20px" alt="isUnchecked" border="0px" align="left"/>',
		STATUS_WARNING_IMG :
			'<img src="cwapp/img/warning_16.png" title="{0}" width="16px" height="16px" alt="hasWarning" border="0px" align="left"/>',
		STATUS_ERROR_IMG :
			'<img src="cwapp/img/error_16.png" title="{0}" width="16px" height="16px" alt="hasError" border="0px" align="left"/>',
		STATUS_OK_IMG :
			'<img src="cwapp/img/transparent.png" title="{0}" width="20px" height="20px" alt="isOK" border="0px" align="left"/>',
		FIELD_IMPORT_ADD_MODE : "add",
		FIELD_IMPORT_REPLACE_MODE : "replace",
	},

	// private members
	_cometdTopics : {
		TOPIC_FIELD_IMPORT_STARTED : CometD.TOPIC_FIELD_IMPORT_STARTED,
		TOPIC_FIELD_IMPORT_FINISHED : CometD.TOPIC_FIELD_IMPORT_INISHED,
	},
	
	_caller : null,
	_usageMode : false, // False if we are editing a table; true if we are editing a table usage object
	_fieldStore : null,
	_usageStore : null,
	_dummyStore : null, // Used when no field is selected
	_fieldDataGrid : null,
	_usageDataGrid : null,
	_fieldDataGridRowCount : 0,
	_fieldDataGridIsDirty : false,
	_usageDataGridIsDirty : false,
	_selectedTable : null,
	_selectedField : null,
	_sapSystemPasswordDialog : null,
	_toolTipHandle : null, //having a reference to a tooltip dijit to be able to connect/disconnect it as needed	
	// public functions
	constructor : function(args) {
		if (args) {
			dojo.mixin(this, args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},

	postCreate : function() {
		
		// initialize the field import progress dialog
		this._setupFieldImportProgressDialog();
		
		// connect the various elements to events
		dojo.connect(window, "onresize", this, "_myResize");
		dojo.connect(this.addButton, "onClick", this, "_addButtonClicked");
		dojo.connect(this.removeButton, "onClick", this, "_removeButtonClicked");
		dojo.connect(this.gotoTableButton, "onClick", this, "_gotoTableButtonClicked");
		dojo.connect(this.fieldDataGridFilter, "onChange", this, "_filterFieldDataGrid");
		dojo.connect(this.fieldDataGridDropDown, "onClick", this, "_fieldDataGridDropDown");
		dojo.connect(this.applyFilterButton, "onClick", this, "_applyFilter");
		dojo.connect(this.addFieldDialog, "onHide", dojo.hitch(this, function() {
			this._fieldDataGrid.render();
		}));
		
		this._dummyStore = new dojo.data.ObjectStore({objectStore: new dojo.store.Memory()});
		
		this.inherited(arguments);
		
		//initialize the drop down buttons
		this._initDropDownButtons();
		
		// disable the add button in read-only mode
		if (General.ISREADONLY){
			this.addButton.set("disabled", true);
		}
	},

	update : function(selectedTable) {
		if (!selectedTable) {
			return;
		}
		
		// store the selected table
		this._selectedTable = selectedTable;
		
		// This tab has 2 modes: table and table usage. The operation is the same, but the field metadata is different.
		// Determine the mode of operation on the new object (table or usage)
		this._usageMode = (selectedTable.type == BdrTypes.TABLE_USAGE);

		// At the moment, we are the first tab for both tables and table usages.
		// The _myShow() method won't be called automatically because of this.
		// So we call it directly.
		this._myShow();
	},

	setCaller : function(caller) {
		this._caller = caller;
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
	
	addNewField : function(field) {
		
		// put the item in the store
		this._fieldStore.newItem(field);
		
		// save the store
		this._fieldStore.save();		
	},
	
	// save the changes that have been made in the field data grid
	saveButtonClicked : function() {
		this._fieldStore.save();
		this._usageStore.save();
		this._fieldDataGrid.update();
		this._usageDataGrid.update();
		this._fieldDataGridIsDirty = false;
		this._usageDataGridIsDirty = false;
		this._selectedField = null;
	},
	
	// discard the changes made in the field data grid and revert back to the
	// data that is stored on the server
	discardButtonClicked : function() {
		this._fieldStore.revert();
		this._usageStore.revert();
		this._usageDataGrid.update();
		this._fieldDataGrid.update();
		this._fieldDataGridIsDirty = false;
		this._usageDataGridIsDirty = false;
		this._selectedField = null;
	},

	// private functions
	_myShow : function() {
		_entries='';
		checkedEntries = new Array();
		// the 'onShow' event this function is hooked up to is not only shown
		// when the page is made visible by selecting the corresponding tab of the parent
		// tab container
		// to just ignore these early 'onShow' events that occur before the page has been
		// properly updated from the parent we check for the existence of a valid table selection
		if (this._selectedTable) {

			// setup the data stores
			this._setupStores();
			
			// create or re-configure the field data grid
			if (this._fieldDataGrid) {
				this._fieldDataGrid.set("store", this._fieldStore);
				this._fieldDataGrid.set("sortInfo", "0");
				this.fieldDataGridDropDown.dropDown.destroyDescendants(false);
				checkedEntries=new Array();
				_entries='';
				this._fieldDataGrid.render();
			}
			else {
				this._createFieldDataGrid();
			}
			
			// create the usage data grid
			if (!this._usageDataGrid) {
				this._createUsageDataGrid();
			} else{
				this._usageDataGrid.render();
			}
			
			// clear any grid selection
			this._fieldDataGrid.selection.clear();
			this._usageDataGrid.selection.clear();
			this._selectedField = null;
			this._fieldDataGridIsDirty = false;
			this._usageDataGridIsDirty = false;
			// update the toolbar buttons
			this._updateButtons();
	
			// initial size calculations
			this._myResize();
			
			// focus the add button to prevent the screen from scrolling down if the scrollbar is enabled
			this.addButton.focus();
		}
	},
	
	// resize the data grids whenever the window gets resized
	_myResize : function() {
		if (this.borderContainer) {
			this.borderContainer.resize();
		}
	},
	
	// updates the toolbar buttons
	_updateButtons : function() {
		
		// show/hide the toolbar buttons
		if (this._usageMode) {
			dojo.style(this.addButton.domNode, "display", "none");
			dojo.style(this.removeButton.domNode, "display", "none");
			dojo.style(this.gotoTableButton.domNode, "display", "inline-block");
			dojo.style(this.buttonBarSeparator1, "display", "");
			dojo.style(this.setScopeButton.domNode, "display", "inline-block");
			dojo.style(this.setRequiredButton.domNode, "display", "inline-block");
			dojo.style(this.importFieldsFromSapButton.domNode, "display", "none");
		}
		else {
			dojo.style(this.addButton.domNode, "display", "inline-block");
			dojo.style(this.removeButton.domNode, "display", "inline-block");
			dojo.style(this.gotoTableButton.domNode, "display", "none");
			dojo.style(this.buttonBarSeparator1, "display", "none");
			dojo.style(this.setScopeButton.domNode, "display", "none");
			dojo.style(this.setRequiredButton.domNode, "display", "none");
			// show the button bar separator
			dojo.style(this.buttonBarSeparator2, "display", "");
			dojo.style(this.importFieldsFromSapButton.domNode, "display", "inline-block");
			
			// initialize the 'import fields from SAP' button's list of SAP systems
			this._initImportFieldsButton();
		}
		
	},
	
	// initialize the drop down buttons
	_initDropDownButtons : function(){
		
		//define the children of the "Set Scope" drop down button
		var inScope = new dijit.MenuItem({
	        label: this.msg.TABLESFIELDPAGE_SET_IN_SCOPE,
	        onClick: dojo.hitch(this, function(){ this._setInScopeButtonClicked();})
	    });
		this.dropDownMenuScope.addChild(inScope);
		
		var notInScope = new dijit.MenuItem({
	        label: this.msg.TABLESFIELDPAGE_SET_NOT_IN_SCOPE,
	        onClick: dojo.hitch(this, function(){ this._setNotInScopeButtonClicked();})
	    });
		this.dropDownMenuScope.addChild(notInScope);
		
		var followUp = new dijit.MenuItem({
	        label: this.msg.TABLESFIELDPAGE_SET_FOLLOW_UP,
	        onClick: dojo.hitch(this, function(){ this._setFollowUpButtonClicked();})
	    });
		this.dropDownMenuScope.addChild(followUp);
		
		var blank = new dijit.MenuItem({
	        label: this.msg.TABLESFIELDPAGE_SET_BLANK,
	        onClick: dojo.hitch(this, function(){ this._setBlankButtonClicked();})
	    });
		this.dropDownMenuScope.addChild(blank);
				
		//define the children of the "Set Required" drop down button
		var isRequired = new dijit.MenuItem({
	        label: this.msg.TABLESFIELDPAGE_SET_REQUIRED,
	        onClick: dojo.hitch(this, function(){ this._setRequiredButtonClicked();})
	    });
		this.dropDownMenuRequired.addChild(isRequired);
		
		var isNotRequired = new dijit.MenuItem({
	        label: this.msg.TABLESFIELDPAGE_SET_NOT_REQUIRED,
	        onClick: dojo.hitch(this, function(){ this._setNotRequiredButtonClicked();})
	    });
		this.dropDownMenuRequired.addChild(isNotRequired);
			
	},
	
	// initialize the cometd / bayeux protocol in order to retrieve topic events from the server
	_initCometd : function() {
		var deferred = new dojo.Deferred();
		
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
				deferred.resolve();
			}),
			error: dojo.hitch(this, function(error) {
				deferred.errback(error);
			})
		});
		
		return deferred;
	},
	
	// subscribe to certain server topics
	_openCometd : function() {
		var started = dojox.cometd.subscribe(this._cometdTopics.TOPIC_FIELD_IMPORT_STARTED, dojo.hitch(this, function(message) {
			this.fieldImportProgressDialog.show();
		}));
		var finished = dojox.cometd.subscribe(this._cometdTopics.TOPIC_FIELD_IMPORT_FINISHED, dojo.hitch(this, function(message) {
			// Hide import dialog
			this.fieldImportProgressDialog.hide();
			
			// Check if table has been found
			if(message.data == "table_not_found") {
				// Show user message that nothing has happened
				idx.info(this.msg.TABLESFIELDPAGE_24);
			}
			else if(message.data == "failed") {
				// Unknown error
				idx.error(this.msg.TABLESFIELDPAGE_25);
			}
			
			this._fieldDataGrid.render();

			// refresh the BPH tree
			dojo.publish(Topics.REFRESH_BPH);

			// we need to close the connection to the cometd / bayeux server
			this._closeCometd();
		}));
		
		var deferreds = new dojo.DeferredList([started, finished]);
		
		return deferreds;
	},
	
	// unsubscribe from the server topics and close the cometd / bayeux connection
	_closeCometd : function() {
		for (var topic in this._cometdTopics) {
			setTimeout(dojo.hitch(this, function() {
				dojox.cometd.unsubscribe(this._cometdTopics[topic]);
			}), 100);
		}

		dojox.cometd.disconnect();
	},
	
	// setup the field import progress dialog
	_setupFieldImportProgressDialog : function() {
		this.fieldImportProgressDialog._onKey = function(){};
		dojo.style(this.fieldImportProgressDialog.closeButtonNode, "display", "none");
		this.fieldImportProgressDialog.startup();
	},

	// setup the different data stores
	_setupStores : function() {
		
		// main field store is set up depending on the mode (table or table usage)
		var target;
		var idAttribute;
		
		if (!this._usageMode) {
			target = Services.BDR_TABLE_FIELD + "/" + this._selectedTable.id;
			idAttribute = "fieldId";
		}
		else {
			target = Services.BDR_TABLE_USAGE_FIELD + "/" + this._selectedTable.id;
			idAttribute = "fieldUsageId";
		}
		
		this._fieldStore = new dojox.data.JsonRestStore({
			target : target,
			idAttribute : idAttribute,
			syncMode : true,
		});
		
		this._usageStore = this._dummyStore;
	},
	
	// create the field data grid and connect it to the REST store
	_createFieldDataGrid : function() {
				
		// define the data grid layout
		var layout = null;
		
		if (!this._usageMode) {
			
			// table mode layout
		    layout = [[
		        {"name": "&nbsp;", hidden: true},
				{"name": this.msg.TABLESFIELDPAGE_3,  "field": "name",        "width": "15%", "editable": false, "tooltip": this.msg.TABLESFIELDPAGE_TT_1},
				{"name": this.msg.TABLESFIELDPAGE_4,  "field": "checkTable",  "width": "15%", "editable": true, "tooltip": this.msg.TABLESFIELDPAGE_TT_2,
					"type": cwapp.main.CustomCellRelLength, maxLength: BdrAttributeLengths.CHECKTABLE},
				{"name": this.msg.TABLESFIELDPAGE_10, "field": "recommended", "width": "10%", "editable": true, "type": dojox.grid.cells.Bool, "tooltip": this.msg.TABLESFIELDPAGE_TT_6},
				{"name": this.msg.TABLESFIELDPAGE_5,  "field": "sapView",     "width": "15%", "editable": true, "tooltip": this.msg.TABLESFIELDPAGE_TT_3,
					"type": cwapp.main.CustomCellRelLength, maxLength: BdrAttributeLengths.SAP_VIEW},
				{"name": this.msg.TABLESFIELDPAGE_6,  "field": "description", "width": "25%", "editable": true, "tooltip": this.msg.TABLESFIELDPAGE_TT_4,
					"type": cwapp.main.CustomCellRelLength, maxLength: BdrAttributeLengths.DESCRIPTION},
			]];
		} else {
			// table usage mode layout
		    layout = [[
   				{"name": "&nbsp;",                    "field": "status",      "width": "3%",  "editable": false,
   					"formatter": dojo.hitch(this, function(value, rowIndex, cell) {
   						return this._displayFieldUsageStatusColumn(value, rowIndex, cell);
   					}),
   					"tooltip": this.msg.TABLESFIELDPAGE_TT_5
   				},
   				{"name": this.msg.TABLESFIELDPAGE_3,  "field": "name",        "width": "15%", "editable": false, "tooltip": this.msg.TABLESFIELDPAGE_TT_1}, 
   				{"name": this.msg.TABLESFIELDPAGE_4,  "field": "checkTable",  "width": "15%", "editable": false, "tooltip": this.msg.TABLESFIELDPAGE_TT_2,
   					"type": cwapp.main.CustomCellRelLength, maxLength: BdrAttributeLengths.CHECKTABLE},
   				{"name": this.msg.TABLESFIELDPAGE_5,  "field": "sapView",     "width": "10%", "editable": false, "tooltip": this.msg.TABLESFIELDPAGE_TT_3,
					"type": cwapp.main.CustomCellRelLength, maxLength: BdrAttributeLengths.SAP_VIEW},
   				{"name": this.msg.TABLESFIELDPAGE_10, "field": "recommended", "width": "8%", "editable": false,
   					"formatter": dojo.hitch(this, function(value, rowIndex, cell) {
   						return this._displayFieldUsageRecommendedColumn(value, rowIndex, cell);
   					}),
   					"tooltip": this.msg.TABLESFIELDPAGE_TT_6},
   				{"name": this.msg.TABLESFIELDPAGE_14, "field": "required",    "width": "8%", "editable": true, "type": dojox.grid.cells.Bool, "tooltip": this.msg.TABLESFIELDPAGE_TT_7},
   				{"name": this.msg.TABLESFIELDPAGE_11, "field": "useMode", "width": "10%", "editable": true, "type": dojox.grid.cells.Select, 
   					"values": [BdrFieldUsageScopeCodes.IN_SCOPE, BdrFieldUsageScopeCodes.NOT_IN_SCOPE, BdrFieldUsageScopeCodes.FOLLOW_UP, BdrFieldUsageScopeCodes.BLANK], 
   					"options": [this.msg.BphFieldUsageStatusInScope, this.msg.BphFieldUsageStatusNotInScope, this.msg.BphFieldUsageStatusFollowUp, this.msg.BphFieldUsageStatusBlank],			
   					"tooltip": this.msg.TABLESFIELDPAGE_TT_8
   				},
   				{"name": this.msg.TABLESFIELDPAGE_6,  "field": "description", "width": "20%", "editable": false, "tooltip": this.msg.TABLESFIELDPAGE_TT_4},
   				{"name": this.msg.TABLESFIELDPAGE_7,  "field": "comment",     "width": "18%", "editable": true, "tooltip": this.msg.TABLESFIELDPAGE_TT_9,
   					"type": cwapp.main.CustomCellRelLength, maxLength: BdrAttributeLengths.COMMENT},
   				{"name": this.msg.TABLESFIELDPAGE_27,  "field": "globalTemplate", "width": "5%", "editable": false,"formatter": dojo.hitch(this, function(value, rowIndex, cell) {
						return this._GTEntriesColumn(value, rowIndex, cell);
					}), "tooltip": this.msg.TABLESFIELDPAGE_TT_14}
   			]];
		}
		
		//make the columns read only in the user has read only rights
		if (General.ISREADONLY){
		    layout[0].forEach(dojo.hitch(this, function(column){
			column.editable = false;
		    }));
		}
		
	    // construct the data grid
	    // we have to do it here instead of defining it in the HTML template
	    // to get cell editing working
	    this._fieldDataGrid = new dojox.grid.EnhancedGrid({
	        store: this._fieldStore,
	        structure: layout,
	        rowSelector: "20px",
	        usageMode: this._usageMode,
	        editable: true,
	        selectable: false,
	        selectionMode: "extended",
	        loadingMessage: this.msg.BPHTABLESTAB_2,
	        noDataMessage: "<span class=\"dojoxGridNoData\">" + this.msg.BPHTABLESTAB_10 + "</span>",
	        sortInfo: "0",
	        fastScroll: false, // Slows down Chrome, so scrolling to top of the Grid is prevented
	        style: "overflow: inherit", // Makes vertical scrollbar visible
	        keepRows: 1000, // Keeps 1000 rows in cache for better performance
	        canSort: dojo.hitch(this, function(col) {
				return (Math.abs(col) > 1);
			}),
			canEdit: function(inCell, inRowIndex) {
				if(General.ISBDRSUPERUSER)
					return true;
				else
					return _GTEntries[inRowIndex];
	        },
	        onBlur: function() {
	        	
	        	// make sure that the in-cell editing is finished (and saved) whenever the focus of the grid is lost
	        	if (this.edit.isEditing()) {
	        		this.edit.apply();
	        	}
	        },
//	        updateRow: function(inRowIndex) {
//	        	
//	        	// override of _Grid.js/updateRow function due to a bug
//	        	// in conjunction with always editable select cells
//				inRowIndex = Number(inRowIndex);
//				
//				if (this.updating) {
//					this.invalidated[inRowIndex] = true;
//				}
//				else {
//					if(this.usageMode) {
//						// TODO: Works for Chrome, but has to be adjusted for Firefox
//						// and IE. this.edit is not reliable in Firefox
//						if (this.edit.info.cell && this.edit.info.cell.index == 4) {
//							// Don't update if selected cell is UseMode Selection
//							return;
//						}
//						else {
//							this.views.updateRow(inRowIndex);
//							this.scroller.rowHeightChanged(inRowIndex);
//						}
//					}
//					else {
//						this.views.updateRow(inRowIndex);
//						this.scroller.rowHeightChanged(inRowIndex);
//					}
//				}
//	        },
	        onApplyCellEdit: function(inValue, inRowIndex, inFieldIndex) {
	        	this.updateRow(inRowIndex);
	        },
	    });
	    
	    Util.fixGridResizing(this._fieldDataGrid);
    
	    // put the grid into the HTML structure
	    this._fieldDataGrid.placeAt(this.fieldDataGridDiv);
	    
	    // connect events to the grid
		dojo.connect(this._fieldDataGrid, "onSelectionChanged", this, "_fieldDataGridSelectionChanged");
		dojo.connect(this._fieldDataGrid, "onApplyCellEdit", this, "_fieldDataGridEditingStarted");
		dojo.connect(this._fieldDataGrid, "_onFetchComplete", dojo.hitch(this, function(items) {	  	  	
	        this._fieldDataGridRowCount = this._fieldDataGrid.rowCount; 
	        this._handleImportFieldsButtonEnablement();
		}));
		
        dojo.connect(this._fieldDataGrid, "onHeaderCellMouseOver", this, "_showDataGridHeaderTooltip");
        dojo.connect(this._fieldDataGrid, "onHeaderCellMouseOut", this, "_hideDataGridHeaderTooltip");

		this._fieldDataGrid.startup();		
	},
	
	// create the usage report grid
	_createUsageDataGrid : function() {
	    var layout = [[
	      	{"name": "&nbsp;",                    "field": "status",             "width": "3%",  "editable": false, "formatter": "_displayFieldUsageStatusColumn", "tooltip": this.msg.TABLESFIELDPAGE_TT_5},
			{"name": this.msg.TABLESFIELDPAGE_8,  "field": "processChain",       "width": "25%", "editable": false, "tooltip": this.msg.TABLESFIELDPAGE_TT_10},
			{"name": this.msg.TABLESFIELDPAGE_9,  "field": "businessObjectName", "width": "25%", "editable": false, "tooltip": this.msg.TABLESFIELDPAGE_TT_11},
			{"name": this.msg.TABLESFIELDPAGE_14, "field": "required",    "width": "20%", "editable": true, "type": dojox.grid.cells.Bool, "tooltip": this.msg.TABLESFIELDPAGE_TT_7},
			{"name": this.msg.TABLESFIELDPAGE_11, "field": "useMode", "width": "25%", "editable": true, "type": dojox.grid.cells.Select, 
					"values": [BdrFieldUsageScopeCodes.IN_SCOPE, BdrFieldUsageScopeCodes.NOT_IN_SCOPE, BdrFieldUsageScopeCodes.FOLLOW_UP, BdrFieldUsageScopeCodes.BLANK], 
					"options": [this.msg.BphFieldUsageStatusInScope, this.msg.BphFieldUsageStatusNotInScope, this.msg.BphFieldUsageStatusFollowUp, this.msg.BphFieldUsageStatusBlank],			
					"tooltip": this.msg.TABLESFIELDPAGE_TT_8
				},
				{"name": this.msg.TABLESFIELDPAGE_27,  "field": "globalTemplate", "width": "5%", "editable": false,"formatter": dojo.hitch(this, function(value, rowIndex, cell) {
					return this._GTEntriesForUsageColumn(value, rowIndex, cell);
				}), "tooltip": this.msg.TABLESFIELDPAGE_TT_14}	
		]];
	    
	    //make the columns read only in the user has read only rights
	    if (General.ISREADONLY){
	    	layout[0].forEach(dojo.hitch(this, function(column){
	    		column.editable = false;
	    	}));
	    }

	    this._usageDataGrid = new dojox.grid.EnhancedGrid({
	    	formatterScope: this,
	    	store: this._usageStore,
	        structure: layout,
	        rowSelector: "20px",
	        editable: false,
	        selectable: false,
	        selectionMode: "none",
	        loadingMessage: this.msg.BPHTABLESTAB_2,
	        noDataMessage: '<span class=\"dojoxGridNoData\">' + this.msg.BPHTABLESTAB_11 + "</span>",
	        canSort: function(col) {
	        	return false;
			},
			canEdit: function(inCell, inRowIndex) {
				if(General.ISBDRSUPERUSER)
					return true;
				else
					return _GTEntries[inRowIndex];
	        },
	    });

	    Util.fixGridResizing(this._usageDataGrid);
	    
	    // put the grid into the HTML structure
	    this._usageDataGrid.placeAt(this.usageDataGridDiv);

	    // connect events to the grid
	    dojo.connect(this._usageDataGrid, "onSelectionChanged", this, "_usageDataGridSelectionChanged");
		dojo.connect(this._usageDataGrid, "onApplyCellEdit", this, "_usageDataGridEditingStarted");
		
        dojo.connect(this._usageDataGrid, "onHeaderCellMouseOver", this, "_showDataGridHeaderTooltip");
        dojo.connect(this._usageDataGrid, "onHeaderCellMouseOut", this, "_hideDataGridHeaderTooltip");

		this._usageDataGrid.startup();
	},
	
	_usageDataGridSelectionChanged : function() {
		if (this._usageDataGrid.selection.getSelected().length > 0) {
		    if(!General.ISREADONLY){
			this.removeButton.set("disabled", false);
			this.setScopeButton.set("disabled", false);
			this.setRequiredButton.set("disabled", false);
		    }
		} else {
			this.removeButton.set("disabled", true);
			this.setScopeButton.set("disabled", true);
			this.setRequiredButton.set("disabled", true);
		}
	},
	
	// the selection for the field data grid has changed
	_fieldDataGridSelectionChanged : function() {
		if (this._fieldDataGrid.selection.getSelected().length > 0) {
		    if(!General.ISREADONLY){
			this.removeButton.set("disabled", false);
			this.setScopeButton.set("disabled", false);
			this.setRequiredButton.set("disabled", false);
		    }
		} else {
			this.removeButton.set("disabled", true);
			this.setScopeButton.set("disabled", true);
			this.setRequiredButton.set("disabled", true);
		}
		
		dojo.forEach(this._fieldDataGrid.selection.getSelected(), dojo.hitch(this, function(item, index) {
			var globalTemplate = this._fieldStore.getValue(item, "globalTemplate");
			if(globalTemplate=='X'){
				this.removeButton.set("disabled", true);
				this.setScopeButton.set("disabled", true);
				this.setRequiredButton.set("disabled", true);
				}
		}));
		// Update usage report grid
		if (this._usageDataGrid) {
			// close the usage data store
			this._usageDataGrid.store.close();
			// We allow multiple selection in the field grid but disable the usage report grid in this case
			if (this._fieldDataGrid.selection.getSelected().length == 1) {
				// Single field selected
				// check if a new field was selected
				var selectedField = this._fieldDataGrid.selection.getNextSelected();
				if (!this._selectedField || this._selectedField != selectedField.name) {
					// new field
					this._selectedField = selectedField.name;
					// update usage report table
					if (!this._fieldDataGridIsDirty) {
						this._setupUsageDataGrid(selectedField, this.msg.BPHTABLESTAB_14);
					}
				}
			}
			else {
				if (!this._fieldDataGridIsDirty) {
					// Multiple fields or no field selected
					this._setupUsageDataGrid(null, this.msg.BPHTABLESTAB_11);
				}
			}
		}
	},
	
	// the data of the field data grid has been edited
	// so we enable the buttons to save the changes or discard them
	_fieldDataGridEditingStarted : function(event) {
		if (!this._fieldDataGridIsDirty) {
			this._caller.enableButtons();
			this._fieldDataGridIsDirty = true;
			// while the selected row is being edited, we clear the usage report grid
			this._setupUsageDataGrid(null, this.msg.BPHTABLESTAB_15);
		}
	},
	
	_usageDataGridEditingStarted : function(event) {
		if (!this._usageDataGridIsDirty) {
			this._caller.enableButtons();
			this._usageDataGridIsDirty = true;
		}
	},
	
	// setup the field usage data grid
	_setupUsageDataGrid : function(selectedField, message) {
		if (!selectedField) {
			this._usageStore = this._dummyStore;
		} else {
			var storeUrl = null;
			if (!this._usageMode) {
				storeUrl = Services.BDR_TABLE_FIELD_REPORT + "/" + selectedField.fieldId;
			} else {
				storeUrl = Services.BDR_TABLE_FIELDUSAGE_REPORT + "/" + selectedField.fieldUsageId;
			}
			this._usageStore = new dojox.data.JsonRestStore({
				target : storeUrl,
				idAttribute : "reportId",
				syncMode : true,
			});
		}
		this._usageDataGrid.set("noDataMessage", "<span class=\"dojoxGridNoData\">" + message + "</span>");
		this._usageDataGrid.setStore(this._usageStore);
		this._usageDataGrid.render();
	},
	
	// initialize the dropdown button with a list of existing SAP systems
	_initImportFieldsButton : function() {
			
		dojo.xhrGet({
			url : Services.TARGETSAPSYSTEMRESTURL,
			handleAs : "json",
			load : dojo.hitch(this, function(response, ioArgs) {     
				// remove all menu entries that are already there to have them refreshed
				var menu = this.importFieldsFromSapButton.dropDown;
				menu.destroyDescendants(false);

				// only if the response is of length greater than zero
				// we enable the 'import from SAP system button' which is a dropdown button
				// presenting the user a list of SAP systems to import from.
				if (response && response.length > 0) { 
					//enable the drop down button
				    	if(!General.ISREADONLY){
				    	    this.importFieldsFromSapButton.set("disabled", false);
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
				            	this._importFieldsButtonClicked(entry);
				            }),
				        });
				        menu.addChild(menuItem);
					}));
					this.importFieldsFromSapButton.mode = this._con.FIELD_IMPORT_ADD_MODE;					
				} else { 
					//the 'import fields from SAP' button will be disabled if no target SAP system is configured
			        //in this case a tooltip will be shown that informs the user about circumstance 

					//disable the drop down button
					this.importFieldsFromSapButton.set("disabled", true);
					//add the tooltip to the importFieldsFromSap button to inform the user that no sap system exists
					if (!this._toolTipHandle) {
						this._toolTipHandle = new dijit.Tooltip({
							connectId: [this.importFieldsFromSapButton.domNode],
							label: this.msg.TARGETSAPSYSTEMSPG_34,
							position: ["above"]
						});
					}
				}
				
				this._handleImportFieldsButtonEnablement();
			}),
			error : dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},

	
	// data grid filtering based on a filter string gathered from the SearchBox widget
	_filterFieldDataGrid : function() {
		var searchString = this.fieldDataGridFilter.get("value");
		
		if (searchString) {
			this._fieldDataGrid.filter({
				name : searchString + this._con.SEARCH_FILTER_ALL,
			});
		}
		else {
			this._fieldDataGrid.filter({
				name : this._con.SEARCH_FILTER_ALL,
			});
		}
		
		setTimeout(dojo.hitch(this, function() {
			this.fieldDataGridFilter.focus();
		}), 100);
	},
	
	// a new row in the field data grid is added
	_addButtonClicked : function() {
		
		// first we need to clear any existing selection
		this._fieldDataGrid.selection.clear();
		
		this.addFieldDialog.show(this);		
	},
	
	// existing rows are removed from the field data grid and the corresponding store
	_removeButtonClicked : function() {
		
		// save the current selection for item removal procession
		var selection = this._fieldDataGrid.selection.getSelected();
		
		// clear the selection (beautification)
		this._fieldDataGrid.selection.clear();
		
		// remove every selected item one by one
		dojo.forEach(selection, dojo.hitch(this, function(item, index) {
			this._fieldStore.deleteItem(item);
			this._fieldStore.save();
		}));

		this._fieldDataGrid.render();
	},
	
	// Open the table object for the current table usage
	_gotoTableButtonClicked : function() {
		
		dojo.xhrGet({
			url: Services.BDR_TABLE_USAGE_TABLE + "/" + this._selectedTable.id,
			handleAs: "json",
			preventCache: true,
			load: dojo.hitch(this, function(data) {
			//get the BPH detailspanel and update it
				detailsPanel = dijit.byId("detailsPanel");
				detailsPanel.update(this, BdrTypes.TABLE, data.id, null,null,true);
			}),
			error: dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			})
		});
	},
	
	// Set the selected rows to "in scope"
	_setInScopeButtonClicked : function() {
		this._batchSetScopeForSelection(BdrFieldUsageScopeCodes.IN_SCOPE);
	},
	
	// Set the selected rows to "not in scope"
	_setNotInScopeButtonClicked : function() {
		this._batchSetScopeForSelection(BdrFieldUsageScopeCodes.NOT_IN_SCOPE);
	},
	
	// Set the selected rows to "follow up"
	_setFollowUpButtonClicked : function() {
		this._batchSetScopeForSelection(BdrFieldUsageScopeCodes.FOLLOW_UP);
	},
	
	// Set the selected rows to blank
	_setBlankButtonClicked : function() {
		this._batchSetScopeForSelection(BdrFieldUsageScopeCodes.BLANK);
	},
	
	// Sets the given scope attribute value on all selected rows
	_batchSetScopeForSelection : function(value) {
		dojo.forEach(this._fieldDataGrid.selection.getSelected(), dojo.hitch(this, function(item, index) {
			this._fieldStore.setValue(item, 'useMode', value);
		}));
		this._fieldDataGrid.update();
		this._fieldDataGridEditingStarted();
	},
	
	// Set the selected rows to required
	_setRequiredButtonClicked : function() {
		this._batchSetRequiredForSelection(true);
	},
	
	// Set the selected rows to not required
	_setNotRequiredButtonClicked : function() {
		this._batchSetRequiredForSelection(false);
	},
	
	// Sets the given value for "required" cell on all selected rows
	_batchSetRequiredForSelection : function(value){
		dojo.forEach(this._fieldDataGrid.selection.getSelected(), dojo.hitch(this, function(item, index) {
			this._fieldStore.setValue(item, 'required', value);
		}));
		this._fieldDataGrid.update();
		this._fieldDataGridEditingStarted();
	},
	
	// the field import has been triggered for a specific SAP system
	_importFieldsButtonClicked : function(sapSystem) {
		
		// initialize the cometd / bayeux protocol
		var deferred = this._initCometd();
		
		if (deferred != null) {
			deferred.then(dojo.hitch(this, function() {
				
				// Try to get the SAP password
				var pwDeferred = this._handleSapSystemPassword(sapSystem);
				
				if (pwDeferred != null) {
					pwDeferred.then(dojo.hitch(this, function(success) {
						if (success) {
							
							// start listening to topics from the cometd / bayeux server
							var deferreds = this._openCometd();
							
							if (deferreds != null) {
								deferreds.then(dojo.hitch(this, function() {
									
									// check for field import mode which can be 'add' or 'replace'
									var importMode = this.importFieldsFromSapButton.get("mode");
									
									// Insanity check
									if (importMode != this._con.FIELD_IMPORT_ADD_MODE && importMode != this._con.FIELD_IMPORT_REPLACE_MODE) {
										throw new Error(this.msg.INTERNAL_ERROR);
									}
									
									// construct the request payload
									var requestData = new Object();
									tables = new Array(); 
									tables.push(this._selectedTable);
									requestData.tables = tables;
									
									requestData.sapSystem = sapSystem;

									dojo.xhrPost({
										url: Services.BDR_FIELD_IMPORT,
										handleAs: "json",
										postData: dojo.toJson(requestData),
										preventCache: true,
										load: dojo.hitch(this, function(data) {
											
											// cleanup the request data object
											if (requestData) {
												delete requestData;
											}
											
											dojo.xhrPost({
												
												//start the thread on the server
												url: Services.BDR_FIELD_IMPORT_START,
												error: dojo.hitch(this, function(error){
													cwapp.main.Error.handleError(error);
												})
											});
										}),
										error: function(error) {
			
											// cleanup the request data object
											if (requestData) {
												delete requestData;
											}
			
											cwapp.main.Error.handleError(error);
										}
									});

								}), dojo.hitch(this, function(error) {
									cwapp.main.Error.handleError(error);
								}));
							}
						}
						else {
							throw new Error(this.msg.INTERNAL_ERROR);
						}
					}), dojo.hitch(this, function(error) {
						cwapp.main.Error.handleError(error);
					}));
				}
			}), dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
			}));
		}
	},
	
	// check if the SAP system selected via the 'import fields from SAP' button has a password
	// and if not, prompt for one
	// this function is asynchronous therefore we return a dojo.Deferred
	_handleSapSystemPassword : function(sapSystem) {
		if (!this._sapSystemPasswordDialog) {
			this._sapSystemPasswordDialog = new cwapp.tab_config.SapSystemPasswordDialog();
		}
		return this._sapSystemPasswordDialog.getPassword(sapSystem);	
	},
	
	// handle the enabling / disabling of the SAP import button depending
	// on whether the currently selected table has any fields attached to it or not
	_handleImportFieldsButtonEnablement : function() {
		if (this.importFieldsFromSapButton && this.importFieldsFromSapButton.domNode) {
			if (this._fieldDataGridRowCount != 0) {
				this.importFieldsFromSapButton.set("label", this.msg.TABLESFIELDPAGE_23);
				this.importFieldsFromSapButton.set("mode", this._con.FIELD_IMPORT_REPLACE_MODE);
			}
			else {
				this.importFieldsFromSapButton.set("label", this.msg.TABLESFIELDPAGE_1);
				this.importFieldsFromSapButton.set("mode", this._con.FIELD_IMPORT_ADD_MODE);
			}
		}		
	},

	// data grid cell formatting function which will
	// return an html <img> tag depending on the cell value
	_displayFieldUsageRequiredColumn : function(value, rowIndex, cell) {
		
		// styling required to make the image look good in the cell
		cell.customClasses.push("imageColumn");
		
		if (value) {
			return Util.formatMessage(this._con.SYSTEM_CHECKED_IMG, this.msg.TABLESFIELDPAGE_12);
		} else {
			return Util.formatMessage(this._con.SYSTEM_UNCHECKED_IMG, this.msg.TABLESFIELDPAGE_13);
		}
	},
	
	// data grid cell formatting function which will
	// return an html <img> tag depending on the cell value
	_displayFieldUsageRecommendedColumn : function(value, rowIndex, cell) {
		
		// styling required to make the image look good in the cell
		cell.customClasses.push("imageColumn");
		
		if (value) {
			return Util.formatMessage(this._con.SYSTEM_CHECKED_IMG, this.msg.TABLESFIELDPAGE_TT_12);
		} else {
			return Util.formatMessage(this._con.SYSTEM_UNCHECKED_IMG, this.msg.TABLESFIELDPAGE_TT_13);
		}
	},
	
_GTEntriesForUsageColumn : function(value, rowIndex, cell) {
		if (value=='X') 
			_GTEntries[rowIndex]=false;
		else
			_GTEntries[rowIndex]=true;
		
		cell.customClasses.push("imageColumn");
		
		if (value=='X') {
			return Util.formatMessage(this._con.SYSTEM_CHECKED_IMG, this.msg.TABLESFIELDPAGE_TT_12);
		} else {
			return Util.formatMessage(this._con.SYSTEM_UNCHECKED_IMG, this.msg.TABLESFIELDPAGE_TT_13);
		}
	},
	
	_GTEntriesColumn : function(value, rowIndex, cell) {
		
		if (value=='X') 
			_GTEntries[rowIndex]=false;
		else
			_GTEntries[rowIndex]=true;
		
		cell.customClasses.push("imageColumn");
		
		if (value=='X') {
			return Util.formatMessage(this._con.SYSTEM_CHECKED_IMG, this.msg.TABLESFIELDPAGE_TT_12);
		} else {
			return Util.formatMessage(this._con.SYSTEM_UNCHECKED_IMG, this.msg.TABLESFIELDPAGE_TT_13);
		}
	},

	// data grid cell formatting function
	_displayFieldUsageUseColumn : function(value, rowIndex, cell) {		
		var message = this._con.EMPTY_STRING;
		
		switch (value) {
			case BdrFieldUsageScopeCodes.READ : { //deprecated
				message = this.msg.BphFieldUsageStatusRead;
				break;
			}
			case BdrFieldUsageScopeCodes.WRITE : { //deprecated
				message = this.msg.BphFieldUsageStatusWrite;
				break;
			}
			case BdrFieldUsageScopeCodes.FOLLOW_UP : {
				message = this.msg.BphFieldUsageStatusFollowUp;
				break;
			}
			case BdrFieldUsageScopeCodes.IN_SCOPE : {
				message = this.msg.BphFieldUsageStatusInScope;
				break;
			}
			case BdrFieldUsageScopeCodes.NOT_IN_SCOPE : {
				message = this.msg.BphFieldUsageStatusNotInScope;
				break;
			}
			case BdrFieldUsageScopeCodes.BLANK : {
				message = this.msg.BphFieldUsageStatusBlank;
				break;
			}
			default : { //deprecated
				message = this.msg.BphFieldUsageStatusUnused;
				break;
			}
		}
		
		return message;
	},
	
	// data grid cell formatting function which will
	// return an html <img> tag depending on the cell value
	_displayFieldUsageStatusColumn : function(value, rowIndex, cell) {
		
		// styling required to make the image look good in the cell
		cell.customClasses.push("imageColumn");
		
		var imageString = this._con.EMPTY_STRING;
		
		switch (value) {
			case BdrFieldUsageStatusCodes.REQUIRED_BUT_NEVER_WRITTEN : {
				imageString = Util.formatMessage(this._con.STATUS_WARNING_IMG, this.msg.TABLESFIELDPAGE_20);
				break;
			}
			case BdrFieldUsageStatusCodes.READ_BUT_NEVER_WRITTEN : {
				imageString = Util.formatMessage(this._con.STATUS_WARNING_IMG, this.msg.TABLESFIELDPAGE_19);
				break;
			}
			case BdrFieldUsageStatusCodes.MULTIPLE_WRITES : {
				imageString = Util.formatMessage(this._con.STATUS_WARNING_IMG, this.msg.TABLESFIELDPAGE_18);
				break;
			}
			case BdrFieldUsageStatusCodes.REQUIRED_BUT_FOLLOWUP : {
				imageString = Util.formatMessage(this._con.STATUS_ERROR_IMG, this.msg.TABLESFIELDPAGE_17);
				break;
			}
			case BdrFieldUsageStatusCodes.REQUIRED_BUT_NOT_IN_SCOPE : {
				imageString = Util.formatMessage(this._con.STATUS_ERROR_IMG, this.msg.TABLESFIELDPAGE_21);
				break;
			}
			case BdrFieldUsageStatusCodes.REQUIRED_BUT_BLANK : {
				imageString = Util.formatMessage(this._con.STATUS_ERROR_IMG, this.msg.TABLESFIELDPAGE_26);
				break;
			}
			case BdrFieldUsageStatusCodes.OK :
			default : {
				imageString = Util.formatMessage(this._con.STATUS_OK_IMG, this.msg.TABLESFIELDPAGE_16);
				break;
			}
		}
		
		return imageString;
	},
	
	_showDataGridHeaderTooltip : function(event) {
    	dijit.showTooltip(event.cell.tooltip, event.cellNode, ["above"]); 
	},
	
	_hideDataGridHeaderTooltip : function(event) {
        dijit.hideTooltip(event.cellNode);		
	},

	_fieldDataGridDropDown : function() {
		var url="";
		this.fieldDataGridDropDown.dropDown.destroyDescendants(false);
		var menu = this.fieldDataGridDropDown.dropDown;
		if (!this._usageMode) 
			url= Services.BDR_TABLE_FIELD_SAPVIEW+ "/" + this._selectedTable.id;
		else
			url= Services.BDR_TABLE_USAGE_FIELD_SAPVIEW+ "/" + this._selectedTable.id;
		dojo.xhrGet({
			
			url : url,
			handleAs : "json",
			load : dojo.hitch(this, function(response, ioArgs) {     
				// remove all menu entries that are already there to have them refreshed
				
				// only if the response is of length greater than zero
				// we enable the 'import from SAP system button' which is a dropdown button
				// presenting the user a list of SAP systems to import from.
				if (response && response.length > 0) { 
					//enable the drop down button
				    	if(!General.ISREADONLY){
				    	    this.fieldDataGridDropDown.set("disabled", false);
				    	}
					
					//detach and delete potentially existing tooltip which should be only attached when the button is disabled			
					if (this._toolTipHandle) {
						this._toolTipHandle.removeTarget(this.fieldDataGridDropDown.domNode);
						delete this._toolTipHandle;
						this._toolTipHandle = null;
					}
					//create a menu entry for each response and add it to the menu
					dojo.forEach(response, dojo.hitch(this, function(entry, index) {
						var menuItem;
						if(index==0){//This is expilicitly for --Remove Filter-- functionality
							checkedEntries[index]=false;
							menuItem = new dijit.MenuItem({
				            label : entry, 
				            onClick: dojo.hitch(this, function(){ this._SAPViewButtonClicked(entry);
				            checkedEntries=new Array();_entries='';
				            })
				            ,
				        });	
						}else{
								menuItem = new dijit.CheckedMenuItem({
					            label : entry, 
					            onChange : function (checked){
					            	if(checked){
					            		checkedEntries[index]=true;
					                	_entries=_entries+","+entry;
					                    tools.style.display = "block";
					                }else {
					                	checkedEntries[index]=false;
					                	_entries=_entries.replace(","+entry,'');
					                        tools.style.display = "none";
					                }
					        },
					        });
						}
						
						if(checkedEntries[index])
						menuItem.set('checked',checkedEntries[index]);
						menu.addChild(menuItem);
					}));
					this.fieldDataGridDropDown.mode = this._con.FIELD_IMPORT_ADD_MODE;

				} else { 
					//add the tooltip to the importFieldsFromSap button to inform the user that no sap system exists
					if (!this._toolTipHandle) {
						this._toolTipHandle = new dijit.Tooltip({
							connectId: [this.fieldDataGridDropDown.domNode],
							label: this.msg.TARGETSAPSYSTEMSPG_37,
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
	
	_applyFilter : function() {
		var searchStr=_entries.slice(1, _entries.length);
		this._SAPViewButtonClicked(searchStr);//this sends the sapview filter keyword to webservices
		
	},
	
		
	_SAPViewButtonClicked : function(searchString){
		if (searchString) {
			this._fieldDataGrid.filter({
				sapView : searchString,
			});
		}
		else {
			return false;
		}
		setTimeout(dojo.hitch(this, function() {
			this.fieldDataGridFilter.setValue("");
		}), 100);
		this.fieldDataGridDropDown.dropDown.destroyDescendants(false);
	},
	
	_resetFilterDataGrid : function(searchString){
		this._fieldDataGrid.selection.clear();
		
		
	}
});



