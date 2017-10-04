dojo.provide("cwapp.tab_bdr.bph.AddBusinessObjectDialog");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("dojox.data.JsonRestStore");
dojo.require("dojox.grid.DataGrid");

dojo.require("dijit.Dialog");
dojo.require("dijit.form.Button");

dojo.require("cwapp.main.SearchBox");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_bdr.bph.AddBusinessObjectDialog", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ], {
	
	// basic widget settings
	templateString : dojo.cache("cwapp.tab_bdr.bph", "templates/AddBusinessObjectDialog.html"),
	widgetsInTemplate : true,

	// nls support
	msg : {},

	// constants
	_con : {
		EMPTY_STRING : "",
		SEARCH_FILTER_ALL : "%",
	    BUSINESSOBJECT_IMG :
			'<img src="cwapp/img/bdr/business_object_16.png" title="{0}" width="16px" height="16px" alt="isBusinessObject" border="0px" align="left" style="float: left; margin-right: 10px;"/>',
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
		this.addBODialog.show();
		this._init();
	},
	
	// private functions
	_init : function() {
		this._dataStore = new dojox.data.JsonRestStore({
			target : Services.BDR_BO_ALL,
			idAttribute : "id",
			syncMode : true,
			preventCache : true,
		});
		
		this._grid = new dojox.grid.DataGrid({
			store: this._dataStore,
			structure: [
			   {"name": this.msg.NAME, "field": "name", "width": "100%",
				   "formatter": dojo.hitch(this, function(value, rowIndex, cell) {
					   return this._displayBusinessObjectNameColumn(value, rowIndex, cell);
				   }),
			   }
			],
			formatterScope: this
		}, document.createElement('div'));
		
		// we don't want to the user to try and attach the same BO twice
		// so that's why we take a look at the BOs that are already attached to
		// the given process step, compile a list of the ids and send it to the server
		// as an additional URL query parameter
		var excludedBusinessObjects = new Array();
		
		dojo.forEach(this._caller._selectedItem.usedBusinessObjects, function(businessObject, index) {
			excludedBusinessObjects.push(businessObject.id);
		});

		// if the 'excludedBusinessObjects' array has no entries
		// we simply skip setting the query parameter as there is nothing to exclude from
		// the BO result list
		if (excludedBusinessObjects.length != 0) {
			this._grid.setQuery("?exclude=" + excludedBusinessObjects.toString());
		}
		
		this._grid.set("formatterScope", this);
		this._grid.set("selectionMode", "single");
		this._grid.set("loadingMessage", this.msg.BPHTABLESTAB_2);
		this._grid.set("noDataMessage", "<span class=\"dojoxGridNoData\">" + this.msg.BphAddBODialog_NoData + "</span>");
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
		if (searchString != null && searchString != "") {
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
		dojo.publish(Topics.ATTACH_BO, [selection]);		
		this.addBODialog.hide();
	},

	_onCancel : function() {
		this.addBODialog.hide();
	},
	
	// custom datagrid cell formatter returning an image (aligned left) followed by a text
	_displayBusinessObjectNameColumn : function(value, rowIndex, cell) {
		
		// styling required to make the image look good in the cell
		cell.customClasses.push("AddBPHObjectDialog_mixedColumn");
		
		var imageString = "";
		var boName = "<div>" + value + "</div>";
		
		imageString = Util.formatMessage(this._con.BUSINESSOBJECT_IMG, this.msg.BphBusinessObject) + boName;
		
		return imageString;
	}
});
