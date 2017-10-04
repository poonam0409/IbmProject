dojo.provide("cwapp.tab_rdm.referenceDataLoadWizard.ReferenceDataImportSummaryPage");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("idx.layout.BorderContainer");

dojo.require("dijit.layout.ContentPane");

dojo.require("dijit.form.CheckBox");

dojo.require("dojox.grid.DataGrid");
dojo.require("dojox.html._base");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");

dojo.declare("cwapp.tab_rdm.referenceDataLoadWizard.ReferenceDataImportSummaryPage", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm.referenceDataLoadWizard","templates/ReferenceDataImportSummaryPage.html"),
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
			MISSING_IN_CW : 2
		}
	},

	// private members
	_parentWizard : null,
	_referenceTableStore : null,

	// subscription handlers
	_handlers : {
		hdlREFERENCEDATALOADWIZARD_NEXT : null,
	},
	
	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}
		
		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));		

		// save a reference to this
		// (actually needed for the custom data grid getter function)
		_this = this;
	},
	
	postCreate : function() {

		// connect the various elements to events
		dojo.connect(this, "onShow", this, "_myShow");

		// subscribe to user defined topics
		this._handlers.hdlREFERENCEDATALOADWIZARD_NEXT = dojo.subscribe(Topics.REFERENCEDATALOADWIZARD_NEXT, dojo.hitch(this, this._wizardNextButtonPressed));

		this.inherited(arguments);
	},

	destroy : function() {

		// unsubscribe from user defined topics
		dojo.unsubscribe(this._handlers.hdlREFERENCEDATALOADWIZARD_NEXT);

		this.inherited(arguments);
	},

	setParentWizard : function(wizard) {
		this._parentWizard = wizard;
	},

	// private functions
	
	_myShow : function() {
		this.borderContainer.resize(); // Workaround: content is not properly displayed otherwise
		
		// lazy loading of header
		this._setupHeader();
		
		if (this._referenceTableStore != null) {
			this._setupGrid(this._referenceTableStore);
		}
		
		this.referenceDataSummaryGrid.render();
	},
	
	// setup the data grid by attaching it to the store and initializing it
	_setupGrid : function(memoryStore) {
		
		// we override the standard 'onStyleRow' method of the data grid 
		// to disable row styling when the grid is hovered over,
		// only keeping the odd row styling
		this.referenceDataSummaryGrid.set("onStyleRow", function(inRow) 
		{
			var i = inRow;
			i.customClasses += (i.odd?" dojoxGridRowOdd":"");
			this.focus.styleRow(inRow);
			this.edit.styleRow(inRow);
		});
		
		this.referenceDataSummaryGrid.set("formatterScope", this);
		this.referenceDataSummaryGrid.set("store", new dojo.data.ObjectStore({objectStore: memoryStore}));
		this.referenceDataSummaryGrid.set("rowsPerPage", memoryStore.data.length);
	    this.referenceDataSummaryGrid.set("sortInfo", "+2");
	    this.referenceDataSummaryGrid.startup();
	},
	
	// setup the header title by loading a part from the NLS catalog and attaching
	// the SAP system name to it
	_setupHeader : function() {
		var message = Util.formatMessage(this.msg.REFRNCDATAIMPSUMPG_2, this._parentWizard.getSapSystem().legacyId);
		message += "<br>" + Util.formatMessage(this.msg.REFRNCDATAIMPSUMPG_3, this._parentWizard.getRollout());
		message += "<br>" + Util.formatMessage(this.msg.REFRNCDATAIMPSUMPG_4, this._parentWizard.getBOForDisplay());
		dojox.html.set(this.sapSystemHeader.domNode, message);
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
			imageString = Util.formatMessage(this._con.REFERENCETABLE_IS_LOADED_IMG, this.msg.REFRNCDATAIMPSUMPG_7);
			break;
		case this._con.TABLE_STATUS.NOT_LOADED:
			imageString = Util.formatMessage(this._con.REFERENCETABLE_IS_NOT_LOADED_IMG, this.msg.REFRNCDATAIMPSUMPG_8);
			break;
		default:
			// The previous page is in charge of item status validation, only handle the 2 valid cases here
			imageString = '<img src="" alt="undefined"/>';
			break;
		}
		
		return imageString;
	},
			
	// the 'next' button of the parent wizard has been pressed
	// so we can react to it and get the reference table data store
	// from the ReferenceDataTableSelectionPage
	_wizardNextButtonPressed : function(pageIndex, page) {
		
		// we're coming from the second page of the wizard
		if (pageIndex == 1) {
			page._setSelectedReferenceTableStore();
			this._referenceTableStore = this._parentWizard.getReferenceTableStore();
			
			// because this handler always comes AFTER the 'show' event
			// for this page we need to call the 'myShow' function
			// again for the data grid to render successfully
			this._myShow();
		}
	},

	_formatReferenceTableType : function(value, rowIndex, cell) {
		return Util.formatReferenceTableType(value, this.msg);
	}
});
