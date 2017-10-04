dojo.provide("cwapp.tab_rdm.referenceDataLoadWizard.ReferenceDataTableSelectionPage");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("idx.layout.BorderContainer");
dojo.require("idx.layout.ButtonBar");

dojo.require("dojox.grid.DataGrid");

dojo.require("dojo.store.Memory");
dojo.require("dojo.data.ObjectStore");

dojo.require("dijit.form.Button");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("cwapp.main.Error");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.referenceDataLoadWizard.ReferenceDataTableSelectionPage", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm.referenceDataLoadWizard", "templates/ReferenceDataTableSelectionPage.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		REFERENCETABLE_IS_LOADED_IMG :
			'<img src="cwapp/img/state_acceptable_16.png" title="{0}" width="12px" height="12px" alt="isLoaded" border="0px" align="left"/>',
		REFERENCETABLE_IS_NOT_LOADED_IMG :
			'<img src="cwapp/img/state_good_16.png" title="{0}" width="12px" height="12px" alt="isNotLoaded" border="0px" align="left"/>',
		REFERENCETABLE_IS_MISSING_IN_CW_IMG :
			'<img src="cwapp/img/state_bad_16.png" title="{0}" width="12px" height="12px" alt="isMissing" border="0px" align="left"/>',
			TABLE_STATUS : {
			LOADED : 0,
			NOT_LOADED : 1,
			MISSING_IN_CW : 2,
			TEXT_TABLE_MISSING_IN_CW : 3
		}
	},

	// private members
	_parentWizard : null,
	_inMemStore : null,
	_tableStatusNumbers : {
		numberOfTablesOK : 0,
		numberOfTablesWarning : 0,
		numberOfTablesError : 0,
	},
	_gridEventHandleSelect : null,
	_gridEventHandleDeselect : null,
	
	// subscription handlers
	_handlers : {
		hdlREFERENCEDATALOADWIZARD_NEXT : null,
		hdlREFERENCEDATALOADWIZARD_BACK : null,
	},
		
	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},
	
	postCreate : function() {
		
		// connect the various elements to events
		dojo.connect(this.selectAllButton, "onClick", this, "_selectAllRowsInDataGrid");
		dojo.connect(this.deselectAllButton, "onClick", this, "_deselectAllRowsInDataGrid");

		// subscribe to user defined topics
		this._handlers.hdlREFERENCEDATALOADWIZARD_NEXT = dojo.subscribe(Topics.REFERENCEDATALOADWIZARD_NEXT, dojo.hitch(this, this._wizardNextButtonPressed));
		this._handlers.hdlREFERENCEDATALOADWIZARD_BACK = dojo.subscribe(Topics.REFERENCEDATALOADWIZARD_BACK, dojo.hitch(this, this._wizardBackButtonPressed));

		this._setupStandby();
		
		this.inherited(arguments);
	},
	
	destroy : function() {

		// unsubscribe from user defined topics
		dojo.unsubscribe(this._handlers.hdlREFERENCEDATALOADWIZARD_NEXT);
		dojo.unsubscribe(this._handlers.hdlREFERENCEDATALOADWIZARD_BACK);

		this.inherited(arguments);
	},
	
	setParentWizard : function(wizard) {
		this._parentWizard = wizard;
	},

	// private functions
	
	// the 'next' button of the parent wizard has been pressed
	_wizardNextButtonPressed : function(pageIndex) {
		if (pageIndex == 0) {
			// Coming from first page, initial setup, nothing is selected
			this.borderContainer.resize(); // Workaround: content is not properly displayed otherwise
			this._parentWizard.disableNextButton();
			this._clearTableNumbers();
			this._fetchDataAndInitStore();
		}
	},
	
	// the 'back' button of the parent wizard has been pressed
	_wizardBackButtonPressed : function(pageIndex) {
		if (pageIndex == 2) {
			// Returning from last page
			this._parentWizard.enableNextButton();
		} else {
			// Leaving this page
			this._invalidateStore();
		}
	},
	
	// setup in memory store which is attached to the data grid
	_setupStore : function() {
		this._inMemStore = new dojo.store.Memory({
			idProperty: "name"
		});
	},
	
	// setup the data grid by attaching it to the store and initializing it
	_setupGrid : function(memoryStore) {
	    var layout = [
  	    	{name: '&nbsp;', 						width: '12px',	field: 'tableStatus', formatter: this._displayColumnImage, },						
  	    	{name: this.msg.TABLE, 					width: '20%', 	field: 'name', },
  	    	{name: this.msg.TYPE,					width: '12%',	field: 'tableType', formatter: this._formatReferenceTableType, },
  	    	{name: this.msg.DESCRIPTION,			width: '28%',	field: 'description', },
  	    	{name: this.msg.REFRNCDATATBLSELPG_7,	width: '20%', 	get: this._displayTextTableName, },
  	    	{name: this.msg.REFRNCDATATBLSELPG_13,  width: '20%',   get: this._displayTranscodingTableName, }
   	    ];
     	      	      	    
   	    this.referenceDataTableDataGrid.set("structure", layout);
		this.referenceDataTableDataGrid.set("formatterScope", this);
	    this.referenceDataTableDataGrid.set("editable", true);
		this.referenceDataTableDataGrid.set("store", new dojo.data.ObjectStore({objectStore: memoryStore}));
		this.referenceDataTableDataGrid.set("rowsPerPage", memoryStore.data.length);
	    this.referenceDataTableDataGrid.set("sortInfo", "+2");
		this.referenceDataTableDataGrid.set("noDataMessage", this.msg.WizardTableSelection_NoTables);
	    this.referenceDataTableDataGrid.startup();
	
	    this._gridEventHandleSelect = dojo.connect(this.referenceDataTableDataGrid, "onSelected", this, "_checkSelection");
	    this._gridEventHandleDeselect = dojo.connect(this.referenceDataTableDataGrid, "onDeselected", this, "_checkSelection");
	    
	    // go through the base data for the data grid and simply count the numbers
	    // of tables with different statuses
	    dojo.forEach(this._inMemStore.data, dojo.hitch(this, function(item, i) {
	    	switch(item.tableStatus) {
	    	case this._con.TABLE_STATUS.NOT_LOADED:
	    		this._tableStatusNumbers.numberOfTablesOK++;
	    		break;
	    	case this._con.TABLE_STATUS.LOADED:
	    		this._tableStatusNumbers.numberOfTablesWarning++;
	    		break;
	    	case this._con.TABLE_STATUS.MISSING_IN_CW:
	    	case this._con.TABLE_STATUS.TEXT_TABLE_MISSING_IN_CW:
				this._tableStatusNumbers.numberOfTablesError++;
	    		break;
	    	default:
	    		break;
	    	}
	    }));
	    
	    // after we have counted the table numbers we update the status bar message
	    var message;
	    if (this._tableStatusNumbers.numberOfTablesWarning == 0 && this._tableStatusNumbers.numberOfTablesError == 0) {
	    	message = this.msg.REFRNCDATATBLSELPG_12;
	    } else if (this._tableStatusNumbers.numberOfTablesError == 0) {
			message = Util.formatMessage(this.msg.REFRNCDATATBLSELPG_11,
				this._tableStatusNumbers.numberOfTablesOK,
				this._tableStatusNumbers.numberOfTablesWarning);
	    } else {
			message = Util.formatMessage(this.msg.REFRNCDATATBLSELPG_11,
				this._tableStatusNumbers.numberOfTablesOK,
				this._tableStatusNumbers.numberOfTablesWarning)
				+ this.msg.REFRNCDATATBLSELPG_11_JoiningComma
				+ General.ERROR_HTML_BEGIN
				+ Util.formatMessage(this.msg.REFRNCDATATBLSELPG_11_Errors,
				this._tableStatusNumbers.numberOfTablesError)
				+ General.ERROR_HTML_END;
	    }
	    
		dojox.html.set(this.referenceDataTableStatusBar.domNode, message);
	},
	
	// Setup the overlay image shown while loading data
	_setupStandby : function() {
		var loadingImage = myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loading.gif";
		if (!this._standby) {
			this._standby = new dojox.widget.Standby({target: this.referenceDataTableDataGrid.domNode, color:"#ffffff", image: loadingImage});
			document.body.appendChild(this._standby.domNode);
			this._standby.startup();
		}
	},
	
	// data grid cell content display function which will
	// return a string from the underlying data store
	_displayTextTableName : function(rowIndex, item) {
		if (item != null) {
			if (item.textTable != null) {
				return item.textTable.name;
			}
		}
		
		return null;
	},
	
	// data grid cell content display function which will
	// return a string from the underlying data store
	_displayTranscodingTableName : function(rowIndex, item) {
		if (item != null) {
			if (item.transcodingTable != null) {
				return item.transcodingTable.name;
			}
		}
		
		return null;
	},
	
	// data grid cell formatting function which will
	// return an html <img> tag depending on the cell value
	_displayColumnImage : function(value, rowIndex, cell) {
		
		// styling required to make the image look good in the cell
		cell.customClasses.push("imageColumn");
		
		var imageString = "";
		
		switch(value) {
		case this._con.TABLE_STATUS.LOADED:
			imageString = Util.formatMessage(this._con.REFERENCETABLE_IS_LOADED_IMG, this.msg.REFRNCDATATBLSELPG_8);
			break;
		case this._con.TABLE_STATUS.NOT_LOADED:
			imageString = Util.formatMessage(this._con.REFERENCETABLE_IS_NOT_LOADED_IMG, this.msg.REFRNCDATATBLSELPG_9);
			break;
		case this._con.TABLE_STATUS.MISSING_IN_CW:
			imageString = Util.formatMessage(this._con.REFERENCETABLE_IS_MISSING_IN_CW_IMG, this.msg.REFRNCDATATBLSELPG_10);
			break;
		case this._con.TABLE_STATUS.TEXT_TABLE_MISSING_IN_CW:
			imageString = Util.formatMessage(this._con.REFERENCETABLE_IS_MISSING_IN_CW_IMG, this.msg.REFRNCDATATBLSELPG_10_TextTable);
			break;
		default:
			imageString = '<img src="" alt="undefined"/>';
			break;
		}
		
		return imageString;
	},
	
	// invalidate the data grid in memory store by setting it to null
	_invalidateStore : function() {
		this._inMemStore = null;
		this.referenceDataTableDataGrid.set("store", null);
		// Disconnect the events so that we don't disable the Next button after deselecting everything
	    dojo.disconnect(this._gridEventHandleSelect);
	    dojo.disconnect(this._gridEventHandleDeselect);
		this.referenceDataTableDataGrid.selection.deselectAll();
		this.referenceDataTableDataGrid._refresh();
	},
	
	// initialize the in memory store, fill it with data and attach
	// it to the data grid
	_fetchDataAndInitStore : function() {
		this._standby.show();
		this._setupStore();
		
		dojo.xhrGet({
			url: Services.RDM_LOAD_PREVIEW + "/" + this._parentWizard.getSapSystem().legacyId + "/" + this._parentWizard.getRollout() + "/" + this._parentWizard.getBO(),
			handleAs: "json",
			preventCache: true,
			load: dojo.hitch(this, function(tableData) {
				if (tableData) {
					this._inMemStore.setData(tableData);
				}
				this._setupGrid(this._inMemStore);
			    this.referenceDataTableDataGrid.render();
				this._standby.hide();
			}),
			error: function(error) {
				this._standby.hide();
				cwapp.main.Error.handleError(error);
			}
		});
	},
	
	// Check if there are valid selected items.
	// if so, we trigger the wizard 'next' button to be enabled
	// if not, the 'next' button will be disabled
	_checkSelection : function() {
		var selectedItems = this.referenceDataTableDataGrid.selection.getSelected();
		
	    if (dojo.some(selectedItems, dojo.hitch(this, function(item, i) {
				if (item.tableStatus != this._con.TABLE_STATUS.MISSING_IN_CW && item.tableStatus != this._con.TABLE_STATUS.TEXT_TABLE_MISSING_IN_CW) {
					return true; // breaks the loop once it finds a valid item
				}
			}))) {
			this._parentWizard.enableNextButton();	    	
		} else {
			this._parentWizard.disableNextButton();
		}
	},
	
	// select all items / rows in the data grid
	_selectAllRowsInDataGrid : function() {
		this.referenceDataTableDataGrid.store.fetch({
			onComplete : dojo.hitch(this, function(items) {
				for (var i = 0; i < items.length; i++) {
					this.referenceDataTableDataGrid.selection.addToSelection(i);
				}
				
				this.referenceDataTableDataGrid.update();

				// check the existing selections in order to be able to
				// enable / disable the wizard's 'next' button
				this._checkSelection();
			})
		});
	},
	
	// deselect all items / rows in the data grid by unsetting the check boxes
	_deselectAllRowsInDataGrid : function() {
		this.referenceDataTableDataGrid.selection.deselectAll();

		// check the existing selections in order to be able to
		// enable / disable the wizard's 'next' button
		this._checkSelection();
	},

	// save the store with the selected reference tables in the wizard reference
	_setSelectedReferenceTableStore : function() {
		
		// create a new store and populate it with the current data grid selection
		var selectedStore = new dojo.store.Memory({idProperty: "name"});
		var selectedTables = this.referenceDataTableDataGrid.selection.getSelected();
		var validSelectedTables = dojo.filter(selectedTables, dojo.hitch(this, function(item) {
			if (item.tableStatus != this._con.TABLE_STATUS.MISSING_IN_CW && item.tableStatus != this._con.TABLE_STATUS.TEXT_TABLE_MISSING_IN_CW) {
				return true;
			}
			return false;
		}));
		selectedStore.setData(validSelectedTables);
		
		this._parentWizard.setReferenceTableStore(selectedStore);
	},
	
	// reset the reference table number counters
	_clearTableNumbers : function() {
		this._tableStatusNumbers.numberOfTablesOK = 0;
		this._tableStatusNumbers.numberOfTablesWarning = 0;
		this._tableStatusNumbers.numberOfTablesError = 0;
	},
	
	_formatReferenceTableType : function(value, rowIndex, cell) {
		return Util.formatReferenceTableType(value, this.msg);
	}
});
