dojo.provide("cwapp.tab_bdr.bph.AddTableDialog");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dijit.Dialog");

dojo.require("dijit.form.Button");

dojo.require("dojox.data.JsonRestStore");
dojo.require("dojox.grid.DataGrid");

dojo.require("cwapp.main.SearchBox");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.AddTableDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/AddTableDialog.html"),
	widgetsInTemplate : true,

	// nls support
	msg : {},
	
	// constants
	_con : {
		EMPTY_STRING : "",
		SEARCH_FILTER_ALL : "%",
	    TABLE_IMG :
			'<img src="cwapp/img/bdr/table_16.png" title="{0}" width="16px" height="16px" alt="isTable" border="0px" align="left" style="float: left; margin-right: 10px;"/>',
	},

	// private members
	_caller : null,
	_dataStore : null,
	_grid : null,
	
	// public functions
	constructor : function(args) {
		if (args) {
			dojo.mixin(this, args);
		}

		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},

	postCreate : function() {
		dojo.connect(this.filteringInput, "onChange", this, "_filterGrid");
		dojo.connect(this.okButton, "onClick", this, "_onOK");
		dojo.connect(this.cancelButton, "onClick", this, "_onCancel");
		
		this.inherited(arguments);
	},

	show : function(caller) {
		this._caller = caller;
		this.addTableDialog.show();
		this._init();
	},
	
	// private functions
	_init : function() {
		this._dataStore = new dojox.data.JsonRestStore({
			target : Services.BDR_TABLE,
			idAttribute : "id",
			syncMode : true,
			preventCache : true,
		});
		
		this._grid = new dojox.grid.DataGrid({
			store: this._dataStore,
			structure: [
			   {"name": this.msg.NAME, "field": "name", "width": "100%",
				   "formatter": dojo.hitch(this, function(value, rowIndex, cell) {
					   return this._displayTableNameColumn(value, rowIndex, cell);
				   }),
			   }
			],
			formatterScope: this
		}, document.createElement('div'));

		// we don't want to the user to try and attach the same BPH table twice
		// so that's why we take a look at the tables that are already attached to
		// the given BO, compile a list of the ids and send it to the server
		// as an additional URL query parameter
		var excludedTables = new Array();
		
		dojo.forEach(this._caller._selectedItem.tables, function(table, index) {
			excludedTables.push(table.id);
		});

		// if the 'excludedTables' array has no entries
		// we simply skip setting the query parameter as there is nothing to exclude from
		// the BPH table result list
		if (excludedTables.length != 0) {
			this._grid.setQuery("?exclude=" + excludedTables.toString());
		}

		this._grid.set("formatterScope", this);
		this._grid.set("selectionMode", "single");
		this._grid.set("loadingMessage", this.msg.BPHTABLESTAB_2);
		this._grid.set("noDataMessage", "<span class=\"dojoxGridNoData\">" + this.msg.BphAddTableDialog_NoData + "</span>");
		this._grid.set("sortInfo", "+1");
		
		// Append the data grid to the parent dom node
		this.gridParent.domNode.appendChild(this._grid.domNode);

		// Add selection callback
		dojo.connect(this._grid, "onSelectionChanged", this, "_gridRowSelected");

		this._grid.startup();
		this._grid.render();
		
		this.filteringInput.domNode.blur();
	},

	_gridRowSelected : function() {
		if (this._grid.selection.getSelected().length != 0) {
			this.okButton.set("disabled", false);
		} else {
			this.okButton.set("disabled", true);
		}
	},
	
	_filterGrid : function() {	
		var searchString = this.filteringInput.get("value");
		if (searchString != null && searchString != this._con.EMPTY_STRING) {
			this._grid.filter({
				name : searchString + this._con.SEARCH_FILTER_ALL,
			});
		} else {
			this._grid.filter({
				name : this._con.SEARCH_FILTER_ALL,
			});
		}
	},
	
	_onOK : function() {
		var selection = this._grid.selection.getSelected();
		dojo.publish(Topics.TABLESTOATTACHSELECTED, [selection]);		
		this.addTableDialog.hide();
	},

	_onCancel : function() {
		this.addTableDialog.hide();
	},
	
	// custom datagrid cell formatter returning an image (aligned left) followed by a text
	_displayTableNameColumn : function(value, rowIndex, cell) {
		
		// styling required to make the image look good in the cell
		cell.customClasses.push("AddBPHObjectDialog_mixedColumn");
		
		var imageString = "";
		var tableName = "<div>" + value + "</div>";
		
		imageString = Util.formatMessage(this._con.TABLE_IMG, this.msg.BphTable) + tableName;
		
		return imageString;
	}
});
