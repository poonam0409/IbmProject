/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2012 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

dojo.provide("idx.oneui.tests.checkboxtree.module");

try{
	var userArgs = "?" + window.location.search.match(/[\?&](dojo|mode)=[^&]*/g).join("").substring(1);

	//doh.registerUrl("idx.oneui.tests.form.CheckBoxTreeRobot", dojo.moduleUrl("idx","tests/oneui/checkboxtree/doh/robot/CheckBoxTree.html"+userArgs), 999999);
	doh.registerUrl("idx.oneui.tests.form.CheckBoxTree", dojo.moduleUrl("idx","tests/oneui/checkboxtree/doh/CheckBoxTree.html"+userArgs, 999999));
}catch(e){
	doh.debug(e);
}
