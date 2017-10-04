dojo.provide("cwapp.tab_rdm.DataMappingsPage");

dojo.require("dijit._Widget");
dojo.require("dijit._TemplatedMixin");
dojo.require("dijit._WidgetsInTemplateMixin");

dojo.require("idx.layout.HeaderPane");
dojo.require("idx.layout.ButtonBar");
dojo.require("idx.layout.BorderContainer");
dojo.require("idx.dialogs");

dojo.require("idx.form.Link");

dojo.require("dojox.grid.DataGrid");
dojo.require("dojox.grid.cells._base");
dojo.require("dojox.grid.cells.dijit");
dojo.require("dojox.widget.Standby");

dojo.require("dojo.store.JsonRest");
dojo.require("dojo.store.Memory");
dojo.require("dojo.data.ObjectStore");

dojo.require("dijit.form.Button");
dojo.require("dijit.layout.ContentPane");

dojo.require("dojo.store.Cache");

dojo.require("dijit.form.Form");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.ValidationTextBox");
dojo.require("dijit.form.ComboBox");
dojo.require("dijit.form.CheckBox");

dojo.require("cwapp.main.Error");

dojo.require("cwapp.tab_config.RdmPasswordDialog");
dojo.require("cwapp.tab_rdm.DataMappingImportProgressDialog");

dojo.require("dijit.Dialog");

dojo.require("dojox.cometd");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.requireLocalization("cwapp", "CwApp");


dojo.declare("cwapp.tab_rdm.DataMappingsPage", [ dijit._Widget, dijit._TemplatedMixin, dijit._WidgetsInTemplateMixin ],
{
	// basic widget settings 
	templateString : dojo.cache("cwapp.tab_rdm","templates/DataMappingsPage.html"),
	widgetsInTemplate : true,
	
	// nls support
	msg : {},
	
	// constants
	_con : {
		TRANSCODINGTABLE_IS_HALFFULL_IMG : '<img src="cwapp/img/state_acceptable_16.png" class="prefix" width="12px" height="12px" border="0px" align="left"/>',
		TRANSCODINGTABLE_IS_MISSING_IMG : '<img src="cwapp/img/state_bad_16.png" class="prefix" width="12px" height="12px" border="0px" align="left"/>',
		TRANSPARENT_FLAG : '<img src="cwapp/img/transparent.png" width="12px" height="12px" border="0px" align="left"/>'
	},
	
	// private members
	_standby : null,
	_inMemStore : null,
	_refreshNeeded: true,
	
	// public functions
	constructor : function(args) {  
		if(args) {
			dojo.mixin(this,args);
		}		

		dojo.mixin(this.msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	},
	
	postCreate : function() {		
		// connect elements to events
		dojo.connect(this, "onShow", this, "_myShow");
		dojo.connect(this.dataMappingsGrid, "onSelected", this, "_updateButtons");
		dojo.connect(this.dataMappingsGrid, "onCellMouseOver", this, "_showTooltip");
		dojo.connect(this.dataMappingsGrid, "onCellMouseOut", this, "_hideTooltipConflict");
		dojo.connect(this.dataMappingsGrid, "onStyleRow", this, "_styleRow");
		dojo.connect(window, "onresize", this, "_resizeGrid");
		
		// connect buttons to events
		dojo.connect(this.importButton, "onClick", this, "_checkImport");
		dojo.connect(this.selectAllButton, "onClick", this, "_selectAllRowsInDataGrid");
		dojo.connect(this.deselectAll, "onClick", this, "_deselectAllRowsInDataGrid");
		dojo.connect(this.refreshAction, "onClick", this, "_refreshGrid");

		// subscribe to "refresh now" topic
		dojo.subscribe(Topics.RDM_REFRESH_MAPPING_PAGE_NOW, dojo.hitch(this, this._refreshGrid));
		// subscribe to "refresh when shown" topic
		dojo.subscribe(Topics.RDM_REFRESH_MAPPING_PAGE_WHEN_SHOWN, dojo.hitch(this, this._triggerRefresh));
		
		this._setupStandby();
		this._setupStore();
		this._setupGrid();
		
		if (General.ISREADONLY){
		    this.importButton.set("disabled", true);
		}
		
		this.inherited(arguments);
	},
	
	// private functions
	
	// Setup the overlay image shown while loading data
	_setupStandby : function() {
		var loadingImage = myApp.config.idxLocation + "/idx/themes/" + myApp.config.theme + "/images/loading.gif";
		this._standby = new dojox.widget.Standby({target: "DataMappingsPage_dataGrid", color:"#ffffff", image: loadingImage});
		document.body.appendChild(this._standby.domNode);
		this._standby.startup();
	},
	
	_setupStore : function() {
		this._inMemStore = new dojo.store.Memory({
			id : "Name",
		});
	},
	
	_setupGrid : function() {
	    var layout = [
	    	{name: this.msg.DATAMAPNGSPG_6,  width: '15%', field: 'TTName', formatter: this._formatTTTablecondition},						
	    	{name: this.msg.DATAMAPNGSPG_7,  width: '15%', field: 'Name'},
	    	{name: this.msg.DATAMAPNGSPG_8,  width: '8%',  field: 'STATUS', formatter: dojo.hitch(this, this._formatStatus)},
	    	{name: this.msg.DATAMAPNGSPG_11, width: '12%', field: 'LEGACYID' /*, formatter: this._formatLegacy*/ },
	    	{name: this.msg.DATAMAPNGSPG_9,  width: '6%',  field: 'AvailableVersion'},
	    	{name: this.msg.DATAMAPNGSPG_10, width: '6%',  field: 'LoadedVersion'},
	    	{name: this.msg.DATAMAPNGSPG_12, width: '12%', field: 'DateLoaded', formatter: Util.formatDate},
	    	{name: this.msg.DATAMAPNGSPG_13, width: '8%',  field: 'MappedSrcValues'},
            {name: this.msg.DATAMAPNGSPG_14, width: '8%',  field: 'SrcValues'},
	    ];
	    
	    this.dataMappingsGrid.set("structure", layout);
		this.dataMappingsGrid.set("store", new dojo.data.ObjectStore({objectStore: this._inMemStore}));
	    this.dataMappingsGrid.set("editable", true);
	    this.dataMappingsGrid.set("sortInfo", "+1");	
		this.dataMappingsGrid.set("formatterScope", this);
		this.dataMappingsGrid.set("noDataMessage", "<span class=\"dojoxGridNoData\">" + this.msg.RDMPageEmpty + "</span>");

	    this.dataMappingsGrid.startup();
	},
	
	// workaround for initial display bug of idx.layout.HeaderPane
	_myShow : function() {
		this.borderContainer.resize();
		if (this._refreshNeeded) {
			this._refreshGrid();
			this._refreshNeeded = false;
		}
	},

	// Refreshes the data.
	// Also, callback for the Refresh button/link.
	_refreshGrid: function() {
		this._disableSelectButtons();
		this.importButton.set("disabled", true);
		Util.prepareRDM(this, dojo.hitch(this, this._fetchData));
	},
	
	// Part 2 of the refresh sequence. Called after the RDM Hub password has been obtained.
	_fetchData : function() {
		this._standby.show();
		dojo.xhrGet({
			url: Services.MAPPINGRESTURL,
			handleAs: "json",
			preventCache: true,
			load: dojo.hitch(this, function(tableData) {
				this._inMemStore.setData(tableData);
				this._deselectAllRowsInDataGrid();
			    this.dataMappingsGrid.render();
			    
			    dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.DATAMAPNGSPG_23, tableData.length));
			    
			    this._standby.hide();
			    this._enableSelectButtons();
			    this._updateButtons();
			}),
			error: dojo.hitch(this, function(error) {
				cwapp.main.Error.handleError(error);
				this._standby.hide();
			})
		});
	},
	
	// Called using a Dojo topic when the data changes
	_triggerRefresh : function() {
		this._refreshNeeded = true;
	},
	
	_resizeGrid : function() {
		var def = new dojo.Deferred();
		setTimeout(function(){def.resolve({called: true});}, 300);
		def.then(dojo.hitch(this,function() {
			this.borderContainer.resize();
		}));
	},
	
	// refreshes the status of the de-, selectall buttons and refreshes the mappings count of the statusbar
	_updateButtons: function() {
		var numOfTables = 0;
		
		if (this.dataMappingsGrid.store != null) {
			this.dataMappingsGrid.store.fetch({
				onComplete : dojo.hitch(this, function(items) {
					numOfTables = items.length;
				})
			});
		}
		
		if (this.dataMappingsGrid.selection.getSelected().length > 0) {
		    	if (!General.ISREADONLY){
		    	    this.importButton.set("disabled", false);
		    	}
			dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.DATAMAPNGSPG_24, numOfTables, this.dataMappingsGrid.selection.getSelected().length));
		} else {
			this.importButton.set("disabled", true);
			dojox.html.set(this.statusBar.domNode, Util.formatMessage(this.msg.DATAMAPNGSPG_23, numOfTables));
		}
	},
	
	_enableSelectButtons: function() {
		this.selectAllButton.set("disabled", false);
		this.deselectAll.set("disabled",  false);
	},
	
	_disableSelectButtons: function() {
		this.selectAllButton.set("disabled", true);
		this.deselectAll.set("disabled",  true);
	},
	
	// row coloring depending on the previous row (same color when same TT) else even and odd
	_styleRow : function(row) {
		var item = this.dataMappingsGrid.getItem(row.index);
		var preRowNode = null;
		
		// Set row styles - merge rows with same transcoding table
		if(row.index>0){
			preItem = this.dataMappingsGrid.getItem(row.index-1);
			preRowNode = this.dataMappingsGrid.getRowNode(row.index-1);
		}
		if(preRowNode){
			//previous row is a row with style rowOdd
			if(preRowNode.className.indexOf("rowOdd")!= -1)
				//current and previous row have the same transcoding table
				if(this.dataMappingsGrid.getCell(0).get(0, item) == this.dataMappingsGrid.getCell(0).get(0, preItem)){
					row.customClasses = " rowOdd " + row.customClasses;
				}
				else{
					row.customClasses += " rowEven";
				}
			//previous row is a row with style rowEven
			else{
				//current and previous row have the same transcoding table
				if(this.dataMappingsGrid.getCell(0).get(0, item) == this.dataMappingsGrid.getCell(0).get(0, preItem)){
					row.customClasses += " rowEven";
				}
				else{
					row.customClasses += " rowOdd";
				}
			}
		}
		else{
			row.customClasses += " rowEven";
		}
	},

	// adds custom class to "Source Set Legacy ID", to highlight it, if empty
	_formatLegacy: function (inValue, row, cell){
		if(inValue==""){
			cell.customClasses.push("DataMappingsPage_missingLegacy");
			return "";
		}
		return inValue;
	},
	
	_formatStatus: function (inValue, rowIndex, cell){
		// adds custom class to "RDM Status", to highlight it, if tehre's a conflict
		if(inValue=="CONFLICT"){
			var item = this.dataMappingsGrid.getItem(rowIndex);  
           	var conflict = this.dataMappingsGrid.store.getValue(item, "Conflicts");	//Array of conflicting items
           	for (var confElement in conflict){
           		if(conflict[confElement].STATUS=="NEW"){
           			cell.customClasses.push("DataMappingsPage_StatusConflictLoading");
           		}else{
           			//item is in conflict with a loaded one
           			cell.customClasses.push("DataMappingsPage_StatusConflictIsLoaded");
           			break;
           		}
            }
           	return this.msg.DATAMAPNGSPG_18;
		}
		if(inValue=="INVALID"){
			cell.customClasses.push("DataMappingsPage_StatusConflictIsLoaded");
			return this.msg.DATAMAPNGSPG_21;
		}
		if(inValue=="NEW"){
			return this.msg.DATAMAPNGSPG_19;
		}
		if(inValue=="LOADED"){
			return this.msg.DATAMAPNGSPG_20;
		}
		if(inValue=="REMOVED"){
			return this.msg.DATAMAPNGSPG_22;
		}
		return inValue;
	},
	
	// adds warning flags, when TT is missing or not full filled
	_formatTTTablecondition : function(value, rowIndex, cell) {
		var item = this.dataMappingsGrid.getItem(rowIndex);
		var condition = this.dataMappingsGrid.store.getValue(item, "TABLECONDITION");
		var imageString = '';
		switch(condition){
			case (60):
				imageString = this._con.TRANSCODINGTABLE_IS_HALFFULL_IMG + value;
				break;	
			case (31):
				imageString = this._con.TRANSCODINGTABLE_IS_MISSING_IMG + value;
				break;	
			default:
				imageString = this._con.TRANSPARENT_FLAG + value;
				break;			
		}
		if(this.dataMappingsGrid.store.getValue(item, "IsTTInCWDB") == false) {
			imageString = this._con.TRANSCODINGTABLE_IS_MISSING_IMG + value;
		}
		return imageString;
	},
	
	_selectAllRowsInDataGrid : function() {
		this.dataMappingsGrid.store.fetch({
			onComplete : dojo.hitch(this, function(items) {
				for (var i = 0; i < items.length; i++) {
					this.dataMappingsGrid.selection.addToSelection(i);
				}
				this.dataMappingsGrid.update();
				this._updateButtons();
			})
		});
	},

	_deselectAllRowsInDataGrid : function() {
		this.dataMappingsGrid.selection.deselectAll();
		this._updateButtons();
	},
	
	_showTooltip : function(e){		
		var item = e.grid.getItem(e.rowIndex);
		
		// Shows a tooltip when the transcoding table is empty
		// shows up by hovering the flag-icon
		if (e.cell.name == this.msg.DATAMAPNGSPG_6) { 
			if(e.cellNode.getElementsByTagName("img")[0] != undefined){
			
				e.cellNode.getElementsByTagName("img")[0].onmouseover = dojo.hitch(this, function(event) {
				 var condition = this.dataMappingsGrid.store.getValue(item, "TABLECONDITION");
				 switch(condition){
				 case (31):
					 message = this.msg.DATAMAPNGSPG_17;
				 break;			
				
				 case (60):
					 message = this.msg.DATAMAPNGSPG_16;
				 break;	
				 default:
					 message = "";
				 break;
				 }
				 if(this.dataMappingsGrid.store.getValue(item, "IsTTInCWDB")==false){
					 	message = this.msg.DATAMAPNGSPG_17;
				}
				 dijit.showTooltip(message, e.cellNode.getElementsByTagName("img")[0]);          
			 });
			 
				//hide tooltip
				 e.cellNode.getElementsByTagName("img")[0].onmouseout = function(event) {
					 dijit.hideTooltip(e.cellNode.getElementsByTagName("img")[0]);
				 };
			 }
        }

		// Shows a tooltip with which mapping the selected row is in conflict
		// pops up by hovering a "Conflict"-status
		 if (e.cell.name == this.msg.DATAMAPNGSPG_8) {	
             if(e.grid.store.getValue(item, e.cell.field)=="CONFLICT"){
            	 var conflict = this.dataMappingsGrid.store.getValue(item, "Conflicts");
           
            	 msg = "";			//the conflicting tables
            	 var preMsg = "";	//infotext
            	 var postMsg = "";	//infotext
            	 for (var x in conflict){
            		 if(conflict[x].STATUS != "NEW"){
            			 preMsg = this.msg.DATAMAPNGSPG_28;
            			 postMsg = this.msg.DATAMAPNGSPG_29;
            		 }
            		 else if(postMsg == ""){
            			 preMsg = this.msg.DATAMAPNGSPG_30;
            			 postMsg = this.msg.DATAMAPNGSPG_31;
            		 }
            		 msg +="- " +conflict[x].Name +" <br>";  
            	 }
            	 msg = preMsg + msg + postMsg; 
            	
            	 dijit.showTooltip(msg, e.cellNode);          
		 	}
         };  
     },
     
     _hideTooltipConflict : function(e) { 
         dijit.hideTooltip(e.cellNode); 
     },
     
	//check if there are 2 new mappings selected at the same time
    //if so, show message cancel import
     _checkImport : function(){
    	var conflictfree=true;
    	var alsoSelected= [];
    	var selectedItems = this.dataMappingsGrid.selection.getSelected();
    	
    	//checks if 2 conflicts of the same tt are selected
    	for (singleItem in selectedItems){
			item = selectedItems[singleItem];
			
			if (item.STATUS == "CONFLICT" && !item.DISABLED){
				var conflict = this.dataMappingsGrid.store.getValue(item, "Conflicts");
				for (var confElement in conflict){
					for (singleItem2 in selectedItems){
						item2 = selectedItems[singleItem2];
						if (item2.Name == conflict[confElement].Name){
								conflictfree=false;
								alsoSelected.push(item2);
						}
					}
				}
			}
    	}

    	if(selectedItems != null && conflictfree){
    		this._import();
		}
    	else{
    		if(!selectedItems){
    			idx.error({summary: this.msg.DATAMAPNGSPG_15});
    			return;
    		}
    		else{
    			var alsoSelectedItems = "";
    			for (x in alsoSelected){
    				if(x>0){
    					if(alsoSelected[x].TTName!=alsoSelected[x-1].TTName){
    						alsoSelectedItems += "<br>";
    					}
    					
    				}
    				alsoSelectedItems += alsoSelected[x].Name +"\n<br>";
    			}
    			idx.error({summary: this.msg.DATAMAPNGSPG_32 +"<br><br>"+ alsoSelectedItems});
    			return;
    		}    		
    	}
     },
			
     _import : function(){
		var importProgressDialog = new cwapp.tab_rdm.DataMappingImportProgressDialog();
		importProgressDialog.setStartingPage(this);
		importProgressDialog.startup();
		importProgressDialog.start();
 	},
 	
});

