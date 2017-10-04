dojo.provide("cwapp.main.CustomCellRelLength");

dojo.require("dojox.grid.cells._base");

dojo.require("dojo.parser");

dojo.declare("cwapp.main.CustomCellRelLength", [ dojox.grid.cells.Cell ], {
	
	constructor: function(args){
		
		this.maxLength = Number(this.maxLength);
	},
	maxLength: null,
	
	formatEditing: function(inDatum, inRowIndex) {
		this.needFormatNode(inDatum, inRowIndex);
		
		var input = '<input class="dojoxGridInput" type="text" value="' + inDatum + '">';
		if(this.maxLength != null) {
			var posTagClose = input.indexOf(">");
			var stringBeforeTagClose = input.slice(0, posTagClose);
			var stringAfterTagClose = input.slice(posTagClose, input.length);
			stringBeforeTagClose += ' maxlength="'+this.maxLength+'"';
			input = stringBeforeTagClose + stringAfterTagClose;
		}
		return input;
	},
});