/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2012 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

dojo.provide("idx.oneui.tests.messaging.module");

try{
	var userArgs = "?" + window.location.search.match(/[\?&](dojo|mode)=[^&]*/g).join("").substring(1);
		
	doh.registerUrl("idx.oneui.tests.messaging.SingleMessage", dojo.moduleUrl("idx","tests/oneui/messaging/doh/SingleMessage.html"+userArgs));
	//doh.registerUrl("idx.oneui.tests.messaging.ModalDialog", dojo.moduleUrl("oneui","tests/messaging/doh/ModalDialog.html"+userArgs));
}catch(e){
	doh.debug(e);
}
