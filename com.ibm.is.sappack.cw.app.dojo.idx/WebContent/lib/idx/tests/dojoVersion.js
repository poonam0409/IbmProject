var dojoVersions = {
  "1.6.1": {label: 'Dojo 1.6.1', 
	  		name: 'Dojo 1.6.1', 
	  		code: "1.6.1", 
	  		async: false,
	  		path: 'dojo_1.6.1', 
	  		dohPath: 'util/doh/',
	  		dohRunnerPath: "dojo_1.6.1/util/doh/runner.html",
	  		dohBootPath: "../../../dojo/dojo.js",
	  		dojoConfig: {
	  		      isDebug: true,
	  		      parseOnLoad: false,
	  		      	modulePaths: {
	  		    	    "idx" : "../../ibmjs/idx/",
	  			        "ibm" : "../../ibmjs/ibm/",
	  			        "doh" : "../util/doh",
	  			        "doh.robot" : "../InternalUse_DoNotDistribute_DOHRobot/util/doh/robot",
  				        "dijit.robot" : "../InternalUse_DoNotDistribute_DOHRobot/dijit/robot",
  				        "dijit.robotx" : "../InternalUse_DoNotDistribute_DOHRobot/dijit/robotx",
  				        "dojo.robot" : "../InternalUse_DoNotDistribute_DOHRobot/dojo/robot",
  			            "dojo.robotx" : "../InternalUse_DoNotDistribute_DOHRobot/dojo/robotx",
  			            "dojox.robot.recorder" : "../InternalUse_DoNotDistribute_DOHRobot/dojox/robot/recorder"	  			        	
	  		      	}
	  		}
  },
  "1.7.2-sync": {label: 'Dojo 1.7.2 (sync)\u200e', 
	  		name: 'Dojo 1.7.2 (sync)\u200e', 
	  		code: "1.7.2-sync", 
	  		async: false,
	  		path: 'dojo_1.7.2',
	  		dohPath: 'InternalUse_DoNotDistribute_DOHRobot/util/doh',
	  		dohRunnerPath: "dojo_1.7.2/InternalUse_DoNotDistribute_DOHRobot/util/doh/runner.html",
	  		dohBootPath: "../../../dojo/dojo.js",
	  		dojoConfig: {
		  	      tlmSiblingOfDojo: false,
		  	      async: false,
		  	      parseOnLoad: false,
		  	      has: {
		  	      	"dojo-firebug": false, //setting to true causes IE7 to explode 
		  	      	"dojo-debug-messages": true
		  	      },
		  	      aliases: [
				  	         ["dijit/robot", "internal/dijit/robot"],
				  	         ["dijit/robotx", "internal/dijit/robotx"],
				  	         ["dojo/robot", "internal/dojo/robot"],
				  	         ["dojo/robotx", "internal/dojo/robotx"],
				  	         ["dojox/robot/recorder", "internal/dojox/robot/recorder"]
				  	      ],
		  	      packages: [
		  	      	{name: "dojo", location: "./" },
		  	      	{name: "dijit", location: "../dijit" },
		  	      	{name: "dojox", location: "../dojox" },
		  	      	{name: "idx", location: "../../ibmjs/idx" },
		  	      	{name: "ibm", location: "../../ibmjs/ibm" },
		  	      	{name: "doh", location: "../InternalUse_DoNotDistribute_DOHRobot/util/doh/"},
		  	      	{name: "doh/robot", location: "../InternalUse_DoNotDistribute_DOHRobot/util/doh/robot" },
		  	      	{name: "internal/dijit", location: "../InternalUse_DoNotDistribute_DOHRobot/dijit"},
		  	      	{name: "internal/dojo", location: "../InternalUse_DoNotDistribute_DOHRobot/dojo"},
		  	      	{name: "internal/dojox/robot", location: "../InternalUse_DoNotDistribute_DOHRobot/dojox/robot"}
		  	      	]
	  		}
  },
  "1.7.2-amd": {label: 'Dojo 1.7.2 (AMD)\u200e', 
		  		name: 'Dojo 1.7.2 (AMD)\u200e', 
		  		code: "1.7.2-amd",
		  		async: true,
		  		path: 'dojo_1.7.2',
		  		dohPath: 'InternalUse_DoNotDistribute_DOHRobot/util/doh',
		  		dohRunnerPath: "dojo_1.7.2/InternalUse_DoNotDistribute_DOHRobot/util/doh/runner.html",
		  		dohBootPath: "../../../dojo/dojo.js",
		  		dojoConfig: {
			  	      tlmSiblingOfDojo: false,
			  	      parseOnLoad: false,
			  	      async: true,
			  	      has: {
			  	      	"dojo-firebug": true,
			  	      	"dojo-debug-messages": true
			  	      },
			  	      aliases: [
			  	         ["dijit/robot", "internal/dijit/robot"],
			  	         ["dijit/robotx", "internal/dijit/robotx"],
			  	         ["dojo/robot", "internal/dojo/robot"],
			  	         ["dojo/robotx", "internal/dojo/robotx"],
			  	         ["dojox/robot/recorder", "internal/dojox/robot/recorder"]
			  	      ],
			  	      packages: [
		  		  	      	{name: "dojo", location: "./" },
		  		  	      	{name: "dijit", location: "../dijit" },
		  		  	      	{name: "dojox", location: "../dojox" },
		  		  	      	{name: "idx", location: "../../ibmjs/idx" },
		  		  	      	{name: "ibm", location: "../../ibmjs/ibm" },
		  		  	      	{name: "doh", location: "../InternalUse_DoNotDistribute_DOHRobot/util/doh/" },
				  	      	{name: "doh/robot", location: "../InternalUse_DoNotDistribute_DOHRobot/util/doh/robot" },
				  	      	{name: "internal/dijit", location: "../InternalUse_DoNotDistribute_DOHRobot/dijit"},
				  	      	{name: "internal/dojo", location: "../InternalUse_DoNotDistribute_DOHRobot/dojo"},
				  	      	{name: "internal/dojox/robot", location: "../InternalUse_DoNotDistribute_DOHRobot/dojox/robot"}
				  	      	]
			  		}
  			},
  "custom": {label: 'custom', 
	  		name: 'custom', 
	  		code: "custom", 
	  		async: true,
	  		path: 'custom',
	  		
	  		dojoConfig: {
		  	      tlmSiblingOfDojo: false,
		  	      async: false,
		  	      parseOnLoad: false,
		  	      has: {
		  	      	"dojo-firebug": true,
		  	      	"dojo-debug-messages": true
		  	      }
		  	   
	  		}
  }
};

var defaultDojoVersion = dojoVersions["1.7.2-sync"];
var currentDojoVersion = defaultDojoVersion;

//permission denied issue for iframe cross domain
try{
	if ((window.parent) && (window.parent.currentDojoVersion)){
		currentDojoVersion = window.parent.currentDojoVersion;
	}
}catch(e){
	//console.log(e.message);
}


(function() {
	function extractQueryParam(url, paramName) {
		//url toString issue
		var currentURL = "" + url;
		var queryStart  = currentURL.indexOf('?');
		var queryLength = currentURL.length - (queryStart + 1);
		var anchorIndex = currentURL.indexOf('#');
		if (anchorIndex >= 0) {
			if (anchorIndex < queryStart) {
				queryStart  = -1;
				queryLength = 0;
			} else {
				queryLength = anchorIndex - (queryStart+1);
			}
		}
		if ((queryStart > 0) && (queryStart < (currentURL.length - 1))) {
			var queryString = currentURL.substr(queryStart+1,queryLength);
			var paramStart = queryString.indexOf("&" + paramName + "=");
			if ((paramStart < 0) && (queryString.indexOf(paramName + "=") == 0)) {
				paramStart = paramName.length;
			} else if (paramStart >= 0) {
				paramStart += (("&" + paramName + "=").length - 1); 
			}
			if ((paramStart >= 0) && (paramStart < (queryString.length - 1))) {
				var suffix 		= queryString.substr(paramStart+1);
				var valueLength = suffix.length;
				var ampIndex 	= suffix.indexOf("&");
				if (ampIndex >= 0) {
					valueLength = ampIndex;
				}
				return suffix.substr(0, valueLength);
			}
		}
		return null;
	}

	var dojoVerToken = extractQueryParam(document.location, "dojo");
	//Also check the parent's url for the dojo ver - used by tests since
	//runTests.html redirects to runner.html and passes dojo version on the 
	//query string
	try{
		if (!dojoVerToken && window.parent) {
			dojoVerToken = extractQueryParam(window.parent.location, "dojo");
		}
	}catch(e){
		//console.log(e.message);
	}

	if (dojoVerToken) {
		if (dojoVersions[dojoVerToken] == null) {
			console.log("WARNING UNRECOGNIZED DOJO VERSION: " + dojoVerToken);
		} else {
			currentDojoVersion = dojoVersions[dojoVerToken];
		}
	} else {
		currentDojoVersion = defaultDojoVersion;
	}
	
	var firebugFlag = extractQueryParam(document.location, "firebug");
	if (firebugFlag == "true") {
		var importStatement = "<scr" + "ipt type='text/javascript' src='" 
			+ "https://getfirebug.com/firebug-lite.js'></script>";
		document.write(importStatement);
	}
})();

function dojoCSSImport(path, beforeID) {
	try {
		document.write("<link type='text/css' rel='stylesheet' href='"
				+ path.replace("@dojopath@", currentDojoVersion.path)
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
		cssNode.href = path.replace("@dojopath@", currentDojoVersion.path); 
	    cssNode.media = "all";
	    if (placeHolder) {
	    	headNode.insertBefore(cssNode, placeHolder);
	    } else {
	    	headNode.appendChild(cssNode);
	    }		
	}
}

function dojoScriptImport(path, beforeID,attrs) {
	try{
		var attrStr = "";
		for (attr in attrs) {
			var attrVal = attrs[attr];
			if (attrVal.indexOf('"') >= 0) {
				attrStr = attrStr + " " + attr + "='" + attrs[attr] + "'";
			} else {
				attrStr = attrStr + " " + attr + '="' + attrs[attr] + '"';
			}
		}
		var importStatement = "<scr" + "ipt type='text/javascript' src='" 
				+ path.replace("@dojopath@", currentDojoVersion.path)
				+ "'" + attrStr + "></script>";
		document.write(importStatement);
	} catch (e) {
		var headNode = document.getElementsByTagName("head")[0];
		var scriptNode = document.createElement("script");
		var placeHolder = null;
		if (beforeID) {
			placeHolder = document.getElementById(beforeID);
		}
		scriptNode.type = "text/javascript";
		scriptNode.src  = path.replace("@dojopath@", currentDojoVersion.path);
		scriptNode.onload = function () { };
		
		if (placeHolder) {
			headNode.insertBefore(scriptNode, placeHolder);
		} else {
			headNode.appendChild(scriptNode);
		}
	}
}

var _post_1_7_modules = {
		"doh": true
};

/***
 * Handles requiring of modules and i18n modules and does it either using the legacy
 * loader or the AMD loader depending the third paramter.  The first parameter is
 * an array of module names.  Modules names can be given in two forms:
 *   - legacy dot notation (e.g.: "idx.foo.bar")
 *   - AMD slash notation (e.g.: "idx/foo/bar")
 * 
 * Any modules given with dot notation will be loaded for both legacy and AMD loaders,
 * however, the dot notation will be converted to slash notation for the AMD loader.
 * Any modules specified with slash notation will ONLY be loaded for the AMD loader.
 * 
 * The second parameter is the i18n modules array and must be an array of objects with
 * two fields each:
 * 	  - package: The package containing the bundle specified in dot notation
 *    - bundle: The bundle name to be loaded.
 * 
 * When using the legacy loader, the i18n modules are loaded via "dojo.requireLocalization"
 * and then obtained via "dojo.i18n.getLocalization".
 * 
 * The third parameter is either true or false to indicate if we should use the AMD asynchronous
 * loader or the legacy synchronous loader, respectively.  Only the legacy synchronous loader
 * is supported for Dojo 1.6.x.
 * 
 * The fourth and fifth parameters specify callback functions to call before and after parsing
 * or a boolean flag indicating if parsing should be skipped and then a single callback function
 * to call.  The callback functions (if specified) take a single parameter that is an 
 * associative array of module names to the loaded module.  The module names are normalized
 * to the AMD loader format using slashes.  For i18n modules, the names are in the form:
 *    "dojo/i18n!" + package + "/nls/" + bundle
 * Where package is the slash-notation version of the "package" field from the respective
 * i18n module that was specified.  The fourth parameter is referred to as "preParse" because
 * it is typically called PRIOR to parsing the document (assuming the document will be parsed
 * by this function).
 * 
 * @param modules
 * @param i18nModules
 * @param async
 * @param preParse
 * @param postParse
 */
function dojoRequireModules(modules, i18nModules, async, preParse, postParse) {
	// check if "pre-parse" is being used to indicate if parsing should be skipped
	var skipParsing = false;
	if ((preParse === true) || (preParse === false)) {
		skipParsing = preParse;
		preParse = null;
	}
	if (!modules) modules = [];
	if (!i18nModules) i18nModules = [];
	if (!async) {
		var requires = ["dojo.parser"];
		var postModules = {"dojo/parser": null};
		var keys     = [ ];
		if (modules) {
			for (var index = 0; index < modules.length; index++) {
				requires.push(modules[index]);
			}
		}
		var pre1_7 = ((dojo.version.major == 1) && (dojo.version.minor < 7));
		var post1_7 = ((dojo.version.major > 1) || ((dojo.version.major == 1) && (dojo.version.minor >= 7)));
		for (var index = 0; index < requires.length; index++) {
			var dependency = requires[index];
			var dependencyKey = dependency.replace(/\./g,"/");
			if (dependency.indexOf("/") < 0) {
				if ((post1_7) || ((pre1_7) && (!(dependency in _post_1_7_modules)))) {
					var reqresult = dojo["require"](dependency);
					postModules[dependencyKey] = dojo.getObject(dependency, false);
				}
			}
		}
		for (var index = 0; index < i18nModules.length; index++) {
			var dependency = i18nModules[index];
			var dependencyKey = "dojo/i18n!" + dependency.package.replace(/\./g,"/") + "/nls/" + dependency.bundle;
			dojo["requireLocalization"](dependency.package, dependency.bundle);
			var bundle = dojo.i18n.getLocalization(dependency.package, dependency.bundle);
			postModules[dependencyKey] = bundle;
		}
		dojo.ready(function() {	
			if (preParse) {
				preParse.call(null, postModules);
			}
			if (!skipParsing) dojo.parser.parse();
			if (postParse) {
				postParse.call(null, postModules);
			}
		});
	} else {
		var requires = ["dojo/parser","dojo/_base/declare","dojo/domReady!"];
		var offset = requires.length;
		var i18nArgs = "";
		var postModules = { };
		for (var index = 0; index < requires.length; index++) {
			postModules[requires[index]] = null;
		}
		for (var index = 0; index < modules.length; index++) {
			var dependency = modules[index];
			dependency = dependency.replace(/\./g,"/");
			if (! (dependency in postModules)) {
				requires.push(dependency);
				postModules[dependency] = null;
			}
		}
		var i18nOffset = requires.length;
		for (var index = 0; index < i18nModules.length; index++) {
			var dependency = i18nModules[index];
			dependency = "dojo/i18n!" + dependency.package.replace(/\./g,"/") + "/nls/" + dependency.bundle;
			if (! (dependency in postModules)) {
				requires.push(dependency);
				postModules[dependency] = null;
			}
		}
		require(requires, function(parser) {
				for (var index = 0; index < requires.length; index++) {
					postModules[requires[index]] = arguments[index];
				}	
				if (preParse) {
					preParse.call(null, postModules);
				}
				if (!skipParsing) parser.parse();
				if (postParse) {
					postParse.call(null, postModules);
				}
		});
	}
}
