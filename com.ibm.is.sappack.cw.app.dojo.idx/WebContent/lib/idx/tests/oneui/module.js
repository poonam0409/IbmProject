/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2012 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

dojo.provide("idx.tests.oneui.module");

try{
	var userArgs = "?" + window.location.search.match(/[\?&](dojo|mode)=[^&]*/g).join("").substring(1);

	doh.registerUrl("idx.tests.oneui.Header", dojo.moduleUrl("idx", "tests/oneui/doh/Header.html"+userArgs), 30000);
	doh.registerUrl("idx.tests.oneui.Menu", dojo.moduleUrl("idx", "tests/oneui/doh/Menu.html"+userArgs), 30000);
	doh.registerUrl("idx.tests.oneui.MenuBar", dojo.moduleUrl("idx", "tests/oneui/doh/MenuBar.html"+userArgs), 30000);
	doh.registerUrl("idx.tests.oneui.MenuDialog", dojo.moduleUrl("idx", "tests/oneui/doh/MenuDialog.html"+userArgs), 30000);
	doh.registerUrl("idx.tests.oneui.MenuHeading", dojo.moduleUrl("idx", "tests/oneui/doh/MenuHeading.html"+userArgs), 30000);
//	doh.registerUrl("idx.tests.oneui.Dialog", dojo.moduleUrl("idx", "tests/oneui/doh/Dialog.html"+userArgs));
	doh.registerUrl("idx.tests.oneui.HoverHelpTooltip", dojo.moduleUrl("idx", "tests/oneui/doh/HoverHelpTooltip.html"+userArgs), 30000);
//	doh.registerUrl("idx.tests.oneui.form.DateTextBox", dojo.moduleUrl("idx", "tests/oneui/form/doh/DateTextBox.html"+userArgs));
//	doh.registerUrl("idx.tests.oneui.form.TextBox", dojo.moduleUrl("idx", "tests/oneui/form/doh/TextBox.html"+userArgs));
//	doh.registerUrl("idx.tests.oneui.form.Slider", dojo.moduleUrl("idx", "tests/oneui/form/doh/Slider.html"+userArgs));
//	doh.registerUrl("idx.tests.oneui.messaging.ModalDialog", dojo.moduleUrl("idx", "tests/oneui/messaging/doh/ModalDialog.html"+userArgs));
//	doh.registerUrl("idx.tests.dijit.form.Button", dojo.moduleUrl("idx", "tests/oneui/form/doh/Button.html"+userArgs));
}catch(e){
	doh.debug(e);
}
