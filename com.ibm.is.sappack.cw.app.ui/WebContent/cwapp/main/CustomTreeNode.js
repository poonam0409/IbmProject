dojo.provide("cwapp.main.CustomTreeNode");

dojo.require("dijit.Tree");

dojo.require("dojo.parser");

dojo.declare("cwapp.main.CustomTreeNode", [ dijit._TreeNode ], {
	
	// public functions
	constructor : function(args) {  
		if (args) {
			dojo.mixin(this, args);
		}
	},
	
	postCreate : function() {
		
		// set the image background for the icon node element
		this._setIconNodeBackground();

		this.inherited(arguments);
	},
	
	// private functions
	_setIconNodeBackground : function() {
		if (this.item) {
			switch (this.item.approvalStatus) {
				case BdrApprovalStatusCodes.APPROVED : {
					
					// check the field usage status and reflect it in the icon node element
					switch (this.item.fieldUsageStatus) {
						case BdrFieldUsageStatusCodes.REQUIRED_BUT_NEVER_WRITTEN :
						case BdrFieldUsageStatusCodes.READ_BUT_NEVER_WRITTEN :
						case BdrFieldUsageStatusCodes.MULTIPLE_WRITES : {
							dojo.setAttr(this.iconNode, "src", Util.getCwAppImageUrl() + "/bdr/overlay_approved_warning_16.png");
							break;
						}
						case BdrFieldUsageStatusCodes.REQUIRED_BUT_FOLLOWUP :
						case BdrFieldUsageStatusCodes.REQUIRED_BUT_NOT_IN_SCOPE :
						case BdrFieldUsageStatusCodes.REQUIRED_BUT_BLANK : {
							dojo.setAttr(this.iconNode, "src", Util.getCwAppImageUrl() + "/bdr/overlay_approved_error_16.png");
							break;
						}
						default : {
							dojo.setAttr(this.iconNode, "src", Util.getCwAppImageUrl() + "/bdr/overlay_approved_16.png");
							break;
						}
					}
					
					break;
				}
				default : {
					// check the field usage status and reflect it in the icon node element
					switch (this.item.fieldUsageStatus) {
						case BdrFieldUsageStatusCodes.REQUIRED_BUT_NEVER_WRITTEN :
						case BdrFieldUsageStatusCodes.READ_BUT_NEVER_WRITTEN :
						case BdrFieldUsageStatusCodes.MULTIPLE_WRITES : {
							dojo.setAttr(this.iconNode, "src", Util.getCwAppImageUrl() + "/bdr/overlay_warning_16.png");
							break;
						}
						case BdrFieldUsageStatusCodes.REQUIRED_BUT_FOLLOWUP :
						case BdrFieldUsageStatusCodes.REQUIRED_BUT_NOT_IN_SCOPE :
						case BdrFieldUsageStatusCodes.REQUIRED_BUT_BLANK : {
							dojo.setAttr(this.iconNode, "src", Util.getCwAppImageUrl() + "/bdr/overlay_error_16.png");
							break;
						}
						default : {
							break;
						}
					}

					break;
				}
			}
		}
	},
});