/*
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2011
 * The source code for this program is not published 
 * or otherwise divested of its trade secrets, irrespective
 * of what has been deposited with the U.S. Copyright Office.
 */

/**
 * Parse the URL query parms and add to "myApp.config"
 * @param loc - Window location/URL with entire query string
 */
function parseQueryParams(loc) {
	var params = loc.search.substring(1);
	params = params.split("&");
	for (var i = 0; i < params.length; i++) {
		var parts = params[i].split("=");
		myApp.config[parts[0]] = parts[1];
	}
}

/**
 * Loads Dojo, using the app config settings
 * Sets up djConfig from myApp.config values
 */
function loadDojo() {
	parseQueryParams(window.location);
	if (!window.dojoConfig) {
		dojoConfig = {
	      isDebug: myApp.config.isDebug || true,
	      parseOnLoad: myApp.config.parseOnLoad || true,
	      debugAtAllCosts: myApp.config.debugAtAllCosts || false,
	      packages: [
		      {
		    	  name: "idx",
		    	  location: myApp.config.idxLocation + "/idx/"
		      },
		      {
		    	  name: "cwapp",
		    	  location: myApp.config.cwAppLocation
		      }
	      ],
	      sendMethod: 'xhrPost', 
	      sendInterval: myApp.config.logInterval || 10000, 
	      analyticsUrl: myApp.config.logURL,
	    };
	}
	
	//import dojo
	dojoScriptImport(myApp.config.dojoLocation + "/dojo/" + myApp.config.dojoScript, "insertDojoHere");
	//import a set of JS Modules
	//dojoScriptImport(myApp.config.dojoLocation + "/dojo/" + myApp.config.mainScript, "insertDojoHere");
	//dojoScriptImport(myApp.config.dojoLocation + "/dojo/" + myApp.config.homeScript, "insertDojoHere");
	//dojoScriptImport(myApp.config.dojoLocation + "/dojo/" + myApp.config.referenceScript, "insertDojoHere");
}
/**
 * 
 * Based on the config, loader will insert dojo right after specified ID location
 * @param path - Path to use to locate dojo
 * @param beforeID - ID in HTML file to insert before
 * @param attrs - tag used for replacement "insertDojoHere"
 */
function dojoScriptImport(path, beforeID,attrs) {
	try{
		var importStatement = "<scr" + "ipt type='text/javascript' src='" 
				+ path	+ "'></scr" + "ipt>";
		document.write(importStatement);
	} catch (e) {
		var headNode = document.getElementsByTagName("head")[0];
		var scriptNode = document.createElement("script");
		var placeHolder = null;
		if (beforeID) {
			placeHolder = document.getElementById(beforeID);
		}
		scriptNode.type = "text/javascript";
		scriptNode.src  = path;
		scriptNode.onload = function () { };
		
		if (placeHolder) {
			headNode.insertBefore(scriptNode, placeHolder);
		} else {
			headNode.appendChild(scriptNode);
		}
	}
}

/**
 * Inserts style imports right after this location specified
 * Replaces dojo path, idx path and theme tags in main
 * app HTML load page with configuration settings.
 * @param path - Path to use to locate css
 * @param beforeID - ID in HTML file to insert before
 */
function dojoCSSImport(path, beforeID) {
	var substPath = path.replace("@dojopath@", myApp.config.dojoLocation);
	substPath = substPath.replace("@idxpath@", myApp.config.idxLocation);
	substPath = substPath.replace(/@theme@/g, myApp.config.theme);
	substPath = substPath.replace("@cwAppPath@", myApp.config.cwAppLocation);
	
	try {
		document.write("<link type='text/css' rel='stylesheet' href='"
				+ substPath
				+ "' media='all'></link>");
	} catch(e) {
		var headNode = document.getElementsByTagName("head")[0];
		var cssNode = document.createElement("link");
		var placeHolder = null;
		if (beforeID) {
			placeHolder = document.getElementById(beforeID);
		}
		cssNode.type = "text/css";
		cssNode.rel  = "stylesheet";
		cssNode.href = substPath; 
	    cssNode.media = "all";
	    if (placeHolder) {
	    	headNode.insertBefore(cssNode, placeHolder);
	    } else {
	    	headNode.appendChild(cssNode);
	    }		
	}
}

loadDojo();
