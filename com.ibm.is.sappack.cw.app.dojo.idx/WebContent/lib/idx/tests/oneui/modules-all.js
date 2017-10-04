/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2012 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

dojo.provide("oneui.tests.modules-all");

try{
	dojo.require("idx.tests.oneui.module");
	dojo.require("idx.tests.oneui.checkboxtree.module");
	dojo.require("idx.tests.oneui.form.module");
	dojo.require("idx.tests.oneui.messaging.module");
}catch(e){
	doh.debug(e);
}