dojo.provide("cwapp.main.Error");

dojo.require("dojo.i18n");
dojo.require("dojo.parser");

dojo.require("idx.dialogs");

dojo.requireLocalization("cwapp", "CwApp");

cwapp.main.Error.handleError = function(error) {
	var msg = {};
	dojo.mixin(msg, dojo.i18n.getLocalization("cwapp", "CwApp"));
	var message;
	switch (error.status) {
	case 500:
		message = msg.INTERNAL_ERROR;
		break;
	case 404:
		message = msg.NOT_FOUND_ERROR;
		break;
	default:
		message = msg.INTERNAL_ERROR;
		break;
	}
	if (error.status) {
		message += "<br>" + msg.ERROR_CODE + error.status;
	}
	console.log("An unexpected error occurred: " + error);
	idx.error({
		summary : message 
	});
};