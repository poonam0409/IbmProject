dojo.provide("cwapp.main.AddressableTabMenuLauncher");

dojo.require("idx.app.TabMenuLauncher");
dojo.require("cwapp.main.AddressableWidget");

dojo.declare("cwapp.main.AddressableTabMenuLauncher", [ idx.app.TabMenuLauncher, cwapp.main.AddressableWidget ], {
	
	constructor : function() {
		var hash = dojo.hash();
		if (hash) {
			var hashObject = dojo.queryToObject(hash);
			arguments[0].defaultWorkspaceTypeID = hashObject.workspace;
		}
		this.addressParameters = [ "workspace" ];
		this.inherited(arguments);
	},
	
	/*
	 * This is a workaround to prevent the loading of the first child of the
	 * TabMenuLauncher, as the idx implementation does not take care of it.
	 */
	startup : function() {
		this._createDummyWorkspace();
		this.inherited(arguments);
		this._removeDummyWorkspace();
		
		tabs = this.tabDock.tabs;
		dojo.forEach(tabs, dojo.hitch(this,function(tab){
		    dojo.connect(tab, "onMouseDown", this, "_onHideWorkspaceAttempt");
		}));
		
		// only the admin must be able to see the configuration tab
		if (!General.ISBDRSUPERUSER){
		    // the config tab ist always the last tab
		    var tabsCount = tabs.length;

		    var configWidget = dojo.byId("idx_app_WorkspaceTab_" + tabsCount.toString());
		    configWidget.style.display = 'none';  
		}
	},
	
	onWorkspaceSelected : function(workspace, previousWorkspace) {
		this.inherited(arguments);
		this.setHash({
			workspace : workspace.workspaceTypeID
		});
		if (previousWorkspace.isLoaded) {
			var previousWorkspaceIndex = this._getWorkspaceIndex(previousWorkspace);
			/*
			 * As the idx TabMenuLauncher architecture is a little bit uncommon,
			 * we need to go through these steps to get the widget inside the
			 * selected tab. A little explanation: the first getChildren gives
			 * us the TabMenuDock, the ContentStack and the "metadata" from the
			 * workspaces, so we need the ContentStack. The second one is to get
			 * the correct Workspace. And the third one is to get the widget
			 * inside the showing tab.
			 */
			this.getChildren()[1].getChildren()[previousWorkspaceIndex].getChildren()[0].onHide();
		}
		if (workspace.isLoaded) {
			var workspaceIndex = this.tabDock._selectedIndex;
			// This is the same case as above
			this.getChildren()[1].getChildren()[workspaceIndex].getChildren()[0].onShow();
		}
	},
	
	_onHideWorkspaceAttempt : function(event) {
	    var workspace = this._selectedWorkspace;
	    var widgetInWorkspace = dijit.getEnclosingWidget(workspace.domNode.firstElementChild);
	    if(widgetInWorkspace && widgetInWorkspace.declaredClass == "cwapp.tab_bdr.BDRTab"){
		widgetInWorkspace.onHideAttempt(event);
	    }
	},
	
	getCurrentAddress : function() {
		return {
			workspace : this.getWorkspaces()[this.tabDock._selectedIndex].workspaceTypeID
		};
	},
	
	getDefaultAddress : function() {
		return {
			workspace : this.defaultWorkspaceTypeID
		};
	},
	
	handleHash : function(hashObject) {
		if (hashObject && hashObject.workspace) {
			var workspaceTypeID = hashObject.workspace;
			this._selectWorkspace(workspaceTypeID);
		} else {
			this.setHash(this.getDefaultAddress());
		}
	},
	
	_getWorkspaceIndex : function(workspace) {
		var workspaces = this.getWorkspaces();
		for ( var i = 0; i < workspaces.length; i++) {
			if (workspaces[i] == workspace) {
				return i;
			}
		}
	},
	
	_selectWorkspace : function(workspaceTypeID) {
		this.selectWorkspace(this.getWorkspaces(workspaceTypeID)[0]);
	},
	
	_createDummyWorkspace : function() {
		var dummyWorkspace = new idx.app.WorkspaceType();
		this.tabDock.addWorkspace(dummyWorkspace);
		this.contentStack.addChild(dummyWorkspace);
	},
	
	/*
	 * This is to remove the dummy workspace we created before The idx
	 * implementation of the removeWorkspace function does not work.
	 */
	_removeDummyWorkspace : function() {
		this.tabDock.tabs[0].destroyRecursive();
		this.tabDock.tabs.splice(0, 1);
		this.tabDock._workspaces.splice(0, 1);
		this.contentStack.removeChild(this.contentStack.getChildren()[0]);
		this.tabDock._selectedIndex = this.tabDock._selectedIndex - 1;
		this.tabDock.applyTabStyles();
	}
});