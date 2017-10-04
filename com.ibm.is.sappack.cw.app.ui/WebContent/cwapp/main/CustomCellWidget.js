dojo.provide("cwapp.main.CustomCellWidget");

dojo.require("dojox.grid.cells.dijit");

dojo.require("dojo.parser");

dojo.declare("cwapp.main.CustomCellWidget", [ dojox.grid.cells._Widget ], {
	
	// public functions
	constructor : function(args) {  
		if (args) {
			dojo.mixin(this, args);
		}
	},
	
	// override createWidget to account for the required widget.startup() function
	// (so this is basically a workaround for a bug in dojox.grid.cells._Widget)
	createWidget: function(inNode, inDatum, inRowIndex){
		var widget = new this.widgetClass(this.getWidgetProps(inDatum), inNode);
		widget.startup();
		
		return widget;
	},
});