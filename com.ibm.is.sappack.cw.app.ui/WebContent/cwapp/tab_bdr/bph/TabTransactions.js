dojo.provide("cwapp.tab_bdr.bph.TabTransactions");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.layout.ContentPane");

dojo.require("dijit.form.Button");

dojo.require("dojo.store.Memory");
dojo.require("dojo.data.ObjectStore");

dojo.require("dojox.grid.EnhancedGrid");

dojo.require("cwapp.main.SearchBox");
dojo.require("cwapp.main.CustomCellRelLength");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.TabTransactions", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/TabTransactions.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// private constants
	_con : {
		EMPTY_STRING : "",
		SEARCH_FILTER_ALL : "*",
	},

	// private members
	_caller : null,
	_selectedProcessStep : "",
	_transactionDataGridRowCount : 0,
	_transactionDataGrid : null,
	_transactionStore : null,	
	
	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		
	},
	
	postCreate : function() {
		
		// connect the various elements to events
		dojo.connect(this, "onShow", this, "_myShow");
		dojo.connect(window, "onresize", this, "_myResize");
		dojo.connect(this.addButton, "onClick", this, "_addButtonClicked");
		dojo.connect(this.removeButton, "onClick", this, "_removeButtonClicked");
		dojo.connect(this.transactionDataGridFilter, "onChange", this, "_filterTransactionDataGrid");
		
		if(General.ISREADONLY){
		    this.addButton.set("disabled", true);
		}
		
		this.inherited(arguments);
	},
	
	setCaller : function(caller) {
		this._caller = caller;
	},
	
	update : function(selectedProcessStep) {
		if (!selectedProcessStep) {
			return;
		}
		
		// store the selected process step
		this._selectedProcessStep = selectedProcessStep;
		
		// if the page is currently shown we need to refresh the store and the associated data grid
		if (dojo.getStyle(this.controlButton.domNode, "display") != "none") {
			this._myShow();
		}
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
		if (this._transactionStore && this._transactionDataGrid) {
			// Save the grid store content
			this._transactionStore.save();
			// Remove any unnamed rows
			this._transactionDataGrid.selection.clear();
			var items = this._transactionStore.objectStore.data;
			dojo.forEach(items, dojo.hitch(this, function(item, index) {
				if (!item.name) {
					this._transactionStore.deleteItem(item);
				}
			}));
			this._transactionStore.save();
			this._transactionDataGrid.update();
			
			return {transactions : this._transactionStore.objectStore.data};
		}
		else {
			return new Object();
		}
	},
	
	// workaround for initial display bug
	_myShow : function() {
		
		// the 'onShow' event this function is hooked up to is not only shown
		// when the page is made visible by selecting the corresponding tab of the parent
		// tab container
		// to just ignore these early 'onShow' events that occur before the page has been
		// properly updated from the parent we check for the existence of a valid process step selection
		if (this._selectedProcessStep != "") {
		
			// setup the data store
			this._setupStore();
			
			// create or re-configure the transaction data grid
			if (this._transactionDataGrid) {
				this._transactionDataGrid.set("store", this._transactionStore);
				this._transactionDataGrid.render();
			}
			else {
				this._createTransactionDataGrid();
			}
			
			// clear any grid selection
			this._transactionDataGrid.selection.clear();
			
			// initial size calculations
			this._myResize();
		}
	},
	
	// resize the data grid whenever the window gets resized
	_myResize : function() {
		if (this.borderContainer) {
			this.borderContainer.resize();
		}
	},
	
	// setup the data store for the grid
	_setupStore : function() {
		this._transactionStore = new dojo.data.ObjectStore({
			objectStore : new dojo.store.Memory({
				idProperty : "transactionId",
				data : this._selectedProcessStep.transactions,
			}),
			onNew : dojo.hitch(this, function(newItem) {
				
				// by defining the 'onNew' handler method we can control whatever happens when
				// a new item is created in the store via the newItem() method
				// in this case we would like the field data grid to set the selection to the new row
				// (which is always at position '0' meaning the top row of the grid)
				// and enter cell editing mode right away
				// as the data grid needs a little bit of time to react to the store changes (rendering etc.)
				// we use a delay method
			    window.setTimeout(dojo.hitch(this, function() {
			        this._transactionDataGrid.focus.setFocusIndex(0, 0);
			        this._transactionDataGrid.edit.setEditCell(this._transactionDataGrid.focus.cell, 0);
			        if (this._transactionDataGrid.selection) {
			        	// If there is a currently selected row, deselect it now
			        	this._transactionDataGrid.selection.clear();
				        // Set selection to the newly created item
				        this._transactionDataGrid.selection.setSelected(0, true);
					}
			    }), 10);
			}),
		});		
	},
	
	// create the transaction data grid and connect it to the object store
	_createTransactionDataGrid : function() {
				
		// define the data grid layout
	    var layout = [[
			{"name": this.msg.TABTRANSACTIONS_2, "field": "name",    "width": "40%", "editable": true,
				"type": cwapp.main.CustomCellRelLength, maxLength: BdrAttributeLengths.TRANSACTION_NAME},
			{"name": this.msg.TABTRANSACTIONS_3, "field": "comment", "width": "60%", "editable": true,
				"type": cwapp.main.CustomCellRelLength, maxLength: BdrAttributeLengths.COMMENT},
		]];
	    
		//make the columns read only in the user has read only rights
		if (General.ISREADONLY){
		    layout[0].forEach(dojo.hitch(this, function(column){
			column.editable = false;
		    }));
		}
		
	    // construct the data grid
	    // we have to do it here instead of defining it in the HTML template
	    // to get cell editing working
	    this._transactionDataGrid = new dojox.grid.EnhancedGrid({
	    	formatterScope: this,
	        store: this._transactionStore,
	        structure: layout,
	        rowSelector: "20px",
	        editable: true,
	        selectable: false,
	        singleClickEdit: false,
	        selectionMode: "extended",
	        queryOptions: {ignoreCase: true},
	        loadingMessage: this.msg.BPHTABLESTAB_2,
	        noDataMessage: "<span class=\"dojoxGridNoData\">" + this.msg.TABTRANSACTIONS_1 + "</span>",
	        sortInfo: "+1",
	        onBlur: function() {
	        	
	        	// make sure that the in-cell editing is finished (and saved) whenever the focus of the grid is lost
	        	if (this.edit.isEditing()) {
	        		this.edit.apply();
	        	}
	        },
	        onApplyCellEdit: function(inValue, inRowIndex, inFieldIndex) {
	        	// Override of method. When editing has finished, the row needs
	        	// to be updated and in case of empty inValue the else statement
	        	// will delete the row.
	        	if(inFieldIndex == "name" && (inValue == null || inValue == "")) {
	        		// Get the created item with empty name and delete it
	        		var item = this.selection.getSelected()[0];
	        		this.store.deleteItem(item);
	    			this.store.save();
	    			this.render();
	        	}
	        	else {
	        		this.updateRow(inRowIndex);
	        	}
	        },
	    });
	    
	    Util.fixGridResizing(this._transactionDataGrid);

	    // put the grid into the HTML structure
	    this._transactionDataGrid.placeAt(this.transactionDataGridDiv);
	    
	    // connect various events to the newly created data grid
		dojo.connect(this._transactionDataGrid, "onSelectionChanged", this, "_transactionDataGridSelectionChanged");
		dojo.connect(this._transactionDataGrid, "onStartEdit", this, "_transactionDataGridEditingStarted");
		dojo.connect(this._transactionDataGrid, "onApplyEdit", this, "_transactionDataGridEditingApplied");
		dojo.connect(this._transactionDataGrid, "_onFetchComplete", dojo.hitch(this, function(items) {	  	  	
	        this._transactionDataGridRowCount = this._transactionDataGrid.rowCount; 
		}));
		
		// startup the grid and have it rendered
		this._transactionDataGrid.startup();
	},

	// the selection for the transaction data grid has changed
	_transactionDataGridSelectionChanged : function() {
		if (this._transactionDataGrid.selection.getSelected().length != 0) {
		    if(!General.ISREADONLY){
			this.removeButton.set("disabled", false);
		    }
		}
		else {
			this.removeButton.set("disabled", true);
		}
	},
	
	// data grid filtering based on a filter string gathered from the SearchBox widget
	_filterTransactionDataGrid : function() {
		var searchString = this.transactionDataGridFilter.get("value");
		
		if (searchString != null && searchString != this._con.EMPTY_STRING) {
			this._transactionDataGrid.filter({
				name : searchString + this._con.SEARCH_FILTER_ALL,
			});
		}
		else {
			this._transactionDataGrid.filter({
				name : this._con.SEARCH_FILTER_ALL,
			});
		}
		
		this.transactionDataGridFilter.focus();
	},
	
	// a new row in the transaction data grid is added
	_addButtonClicked : function() {
		
		// first we need to clear any existing selection
		this._transactionDataGrid.selection.clear();
		
		// have the store create a new blank item
		var newItem = {
				transactionId : Math.floor(Math.random() * (new Date().getTime())),
				name : "",
				comment : ""
		};
		this._transactionStore.newItem(newItem);
		
		this._transactionStore.save();
		
		// update the data grid
		this._transactionDataGrid.render();
	},
	
	// existing rows are removed from the transaction data grid and the corresponding store
	_removeButtonClicked : function() {
		
		// save the current selection for item removal procession
		var selection = this._transactionDataGrid.selection.getSelected();
		
		// clear the selection (beautification)
		this._transactionDataGrid.selection.clear();
		
		// remove every selected item one by one
		dojo.forEach(selection, dojo.hitch(this, function(item, index) {
			this._transactionStore.deleteItem(item);
			this._transactionStore.save();
		}));

		this._transactionDataGrid.render();
		
		this._caller.enableButtons();
	},
	
	// the data of the transaction data grid has been edited
	// so we enable the buttons to save the changes or discard them
	_transactionDataGridEditingStarted : function() {
		this._caller.enableButtons();
	},
	
	// save the store when the in-cell editing of the data grid is applied
	_transactionDataGridEditingApplied : function() {
		this._transactionStore.save();
	}
});
