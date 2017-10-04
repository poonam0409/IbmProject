/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2011 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

(function() {

var version = (window["dojo"] && dojo.version);
if (version && version.major == 1 && version.minor == 6) {	
	dojo.provide("ibm.mygroup.myproduct.util");

} else {
	define(["dojo/_base/lang"], function(dLang) {
		return dLang.getObject("ibm.mygroup.myproduct.util", true);
	});
}

})();