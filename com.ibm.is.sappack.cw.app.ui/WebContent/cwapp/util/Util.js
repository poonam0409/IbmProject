var Util = {
	
	// This modifies the grid's _resize() method to stop keeping the last parent box size in an instance variable.
	// This fixes some grid sizing issues in 
	fixGridResizing : function(grid) {
        var originalFunction = dojo.hitch(grid, grid._resize);
        
        grid._resize = dojo.hitch(grid, function(changeSize, resultSize) {
            this._parentContentBoxHeight = undefined;
            return originalFunction(changeSize, resultSize);
        });
	},
	
	cloneLegacySystem : function(legacySystem) {
		var newLegacySystem = new Object();
		
		newLegacySystem.legacyId = legacySystem.legacyId;
		newLegacySystem.description = legacySystem.description;
		newLegacySystem.sapClient = legacySystem.sapClient;
		newLegacySystem.sapGroupName = legacySystem.sapGroupName;
		newLegacySystem.sapLanguage = legacySystem.sapLanguage;
		newLegacySystem.sapHost = legacySystem.sapHost;
		newLegacySystem.sapLegacyId = legacySystem.sapLegacyId;
		newLegacySystem.sapMessageServer = legacySystem.sapMessageServer;
		newLegacySystem.sapPassword = legacySystem.sapPassword;
		newLegacySystem.sapRouterString = legacySystem.sapRouterString;
		newLegacySystem.sapSystemId = legacySystem.sapSystemId;
		newLegacySystem.sapSystemNumber = legacySystem.sapSystemNumber;
		newLegacySystem.sapUseLoadBalancing = legacySystem.sapUseLoadBalancing;
		newLegacySystem.sapUser = legacySystem.sapUser;
		
		return newLegacySystem;
	},

	// NLS message formatting function which will insert an arbitrary
	// number of strings into the original NLS message via {} placeholders
	//
	// function parameters are not fixed (an infinite number is possible)
	// but the ordering of the parameters is important
	// parameter one: NLS message coming from the catalog (with {} placeholders)
	// parameter two...: string replacements for the message placeholders
	// e.g. formatMessage("This is the {0} translated string in {1}", "first", "German")
	formatMessage : function() {
		var regexpStart = "\\{";
		var regexpEnd = "\\}";
		
		// if there are no parameters at all we simply return an empty string
		if (arguments.length == 0) {
			return "";
		}
		
		// if there is only one parameter (the NLS message) we simple return it
		if (arguments.length == 1) {
			return arguments[0];
		}
		
		// get the first parameter which is the NLS message
		var messageFromCatalog = arguments[0];
		
		// go through the number of parameters
		for(var i = 0; i < arguments.length; i++) {
			
			// construct the regular expression for the n-th replacement string
			var regexpString = regexpStart + i + regexpEnd;
			var regexp = new RegExp(regexpString);
			
			if (messageFromCatalog.match(regexp) != null) {
				
				// important: do not replace with arguments[i] as the first
				// replacement string is at argument position [1] while the
				// placefolder numbering actually starts at [0]
				messageFromCatalog = messageFromCatalog.replace(regexp, arguments[i + 1]);
			}
			else {
				break;
			}
		}

		return messageFromCatalog;
	},
	
    formatDate : function(data, rowIndex) {
    	if (data) {
    		var loaded = new Date(data);
            return dojo.date.locale.format(loaded, {
                selector: "date",
                datePattern: General.DATE_FORMAT});
    	} else {
    		return "";
    	}
    },
    
	formatReferenceTableType : function(value, msgCatalog) {
		switch (value) {
		case "GENERIC_TABLE":
			return msgCatalog.GENERIC_TABLE_TYPE;
		case "DOMAIN_TABLE":
			return msgCatalog.DOMAIN_TABLE_TYPE;
		case "CHECK_TABLE":
			return msgCatalog.CHECK_TABLE_TYPE;
		default:
			return msgCatalog.GENERIC_TABLE_TYPE;
		}
	},
    
    // Retrieves the RDM connection settings (host, port, user).
	// Returns false if any setting is missing (also checks for the language but doesn't return it).
    getRdmParameters : function() {
		var rdmHost = "";
		var rdmPort = "";
		var rdmUser = "";
		var rdmLanguage = "";
		
		// check if we have the RDM URL and user name
		dojo.xhrGet({
			url: Services.SETTINGRESTURL,
			handleAs: "json",
			preventCache: true,
			sync: true,
			load: dojo.hitch(this, function(settings) {
				// Look for the RDM settings
				for (var i = 0; i < settings.length; i++) {
					var s = settings[i];
					if (s.name == Settings.RDM_HOST_NAME) {
						rdmHost = s.value;
					}
					if (s.name == Settings.RDM_PORT_NAME) {
						rdmPort = s.value;
					}
					if (s.name == Settings.RDM_USER_NAME) {
						rdmUser = s.value;
					}
					if (s.name == Settings.RDM_LANGUAGE_NAME) {
						rdmLanguage = s.value;
					}
				}
			}),
		});

		if (!rdmHost || !rdmPort || !rdmUser || !rdmLanguage) {
			return false;
		}
		return {rdmHost:rdmHost, rdmPort:rdmPort, rdmUser:rdmUser};
    },
    
	// Makes sure we have all RDM settings and the RDM password is set in the session
    // By opening the RDM password dialog if necessary and calls the follow-up function
    // Note: the follow-up function must be passed as a dojo.hitch() call so that the context remains the original one
	prepareRDM: function(context, nextStep) {
		var rdmParams = Util.getRdmParameters();
		if (!rdmParams) {
			// No setting(s), display error and bail
			idx.error({summary: context.msg.CWAPP_7});
			return;
		}

		var pwDeferred = dijit.byId("rdmPasswordDialog").getPassword(rdmParams.rdmHost, rdmParams.rdmPort, rdmParams.rdmUser);
		pwDeferred.then(function(success) {
			if (success) {
				// Password was successfully obtained
				nextStep();
			}
		});
	},
    
    // filters an array by retrieving only unique array items
    // and putting them into a new array
	filterArray : function(array) {
		var unique = {};
		
		return dojo.filter(array, function(item) {
			if (!unique[item.legacyId]) {
				unique[item.legacyId] = true;
				
				return true;
			}
			
			return false;
		}).sort();	
	},
	
	// Removes an array item by its id attribute
	removeItemById : function(array, id) {
		for (var i in array) {
			if (array[i].id == id) {
				array.splice(i,1);
				break;
			}
		}
	},
	
	getTimeoutError: function() {
		var result = new Error();
		result.status = 300;
		return result;
	},
	
	getHashObject: function () {
		return dojo.queryToObject(dojo.hash());
	},
	
	setHashObject: function (hashObject) {
		dojo.hash(dojo.objectToQuery(hashObject));
	},
	
	// Asks the server if the current user has the administrator role and saves the result in a global variable
	getUserRole : function() {
		if (General.ISBDRSUPERUSER === undefined) {
			dojo.xhrGet({
				url : Services.BDR_USER_HAS_REQUIRED_ROLE,
				handleAs : "json",
				sync : true,
				load : function(data) {
					General.ISBDRSUPERUSER = data.isAdmin;
					General.ISREADONLY = data.isReadOnly;
				}
			});
		}
	},
	
	// returns the node icon depending on the item type
	// return value is the class name
	bdrIconFunc : function(item) {
		if(item.type == BdrTypes.PROCESS)
			return "BPH_TreeIconProcess";
		if(item.type == BdrTypes.BO)
			return "BPH_TreeIconBusinessObject";
		if(item.type == BdrTypes.PROCESS_STEP)
			return "BPH_TreeIconProcessStep";
		if(item.type == BdrTypes.TABLE)
			return "BPH_TreeIconTable";
		if(item.type == BdrTypes.TABLE_USAGE)
			return "BPH_TreeIconTableUsage";
	},
	
	getCwAppImageUrl : function() {
		var currentLocationParts = window.location.pathname.split('/');
		
		// filter out any references to 'index.jsp' in the window.location.pathname string
		if (currentLocationParts[currentLocationParts.length - 1].indexOf(".jsp") !== -1) {
			var fixedLocation = undefined;

			for (var i = 0; i < currentLocationParts.length - 1; i++) {
				if (i == 0) {
					fixedLocation = currentLocationParts[i];
				}
				else {
					fixedLocation = fixedLocation + currentLocationParts[i];
				}
				
				fixedLocation = fixedLocation + '/';
			}
			
			return fixedLocation + "cwapp/img";
		}
		else {
			return window.location.pathname + "cwapp/img";
		}
	},
	
	getNumberOfBusinessObjectsAndTablesDefined : function() {
		var deferred = new dojo.Deferred();
		
		deferred = dojo.xhrGet({
			url : Services.BDR_GETNUMBEROFBOSANDTABLES,
			handleAs : "text",
			sync : true,
			load : dojo.hitch(this, function(data) {
				deferred.resolve(data);
			}),
			error : dojo.hitch(this, function(error) {
				deferred.errback(error);
			})
		});
		
		return deferred;
	},
	
	changeWidgetsDisabledState : function(widgets, state) {
	    widgets.forEach(dojo.hitch(this, function(widget){
	    	widget.set('disabled', state);
	    }));
	}
};
