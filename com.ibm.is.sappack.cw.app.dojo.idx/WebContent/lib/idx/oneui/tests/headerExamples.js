/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2012 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

define(["idx/oneui/Header", "dojo/_base/lang", "idx/oneui/Menu", "dijit/MenuItem", "idx/oneui/MenuBar", "dijit/MenuBarItem", "dijit/PopupMenuBarItem", "dijit/layout/StackContainer", "dijit/layout/ContentPane"],
       function(Header, lang, Menu, MenuItem0, MenuBar, MenuBarItem0, PopupMenuBarItem, StackContainer, ContentPane0){
	   
	var MenuItem = function(obj){
		if(obj.label && !obj.onClick){
			obj.onClick = new Function("alert('Item \"" + obj.label.split("<")[0] + "\" has been clicked.');");
		}
		
		return new MenuItem0(obj);
	}
	 
	var MenuBarItem = function(obj){
		if(obj.label && !obj.onClick){
			obj.onClick = new Function("alert('Item \"" + obj.label.split("<")[0] + "\" has been clicked.');");
		}
		
		return new MenuBarItem0(obj);
	}
	 
	var ContentPane = function(obj){
		if(obj.title && !obj.content){
			obj.content = '<span dir="ltr">This is the content panel for "' + obj.title.split("<")[0] + '".</span>';
		}
		
		obj.doLayout = false;
		return new ContentPane0(obj);
	}

	var titles = {
		count: 5,
		0: { template: "test examples",
			 examples: {
				count: 1,
				1: "Fully loaded" } },
		1: { template: "Single Banner without navigation",
			 examples: {
				count: 5,
				1: "Fluid-width, includes absolute minimum required elements",
				2: "Fixed-width with a menu of global actions",
				3: "Fluid-width, includes user thumbnail and a global actions menu",
				4: "Shorter, fluid-width header with global actions",
				5: "Fluid-width header with search" } },
		2: { template: "Single Banner with Navigation",
			 examples: {
				count: 4,
				1: "Fluid-width with link-based navigation",
				2: "Fixed-width with a both link- and menu-based navigation and a global actions menu",
				3: "Fluid-width, links and menus, includes user thumbnail and menu arrows",
				4: "Link- and menu-based navigation with a badge; also includes a search control" } },
		3: { template: "Double Banner with Navigation in Second-Level Banner Only",
			 examples: {
				count: 4,
				1: "Fluid-width, app-defined tab-based navigation with blue styling; small user thumbnail",
				2: "Fixed-width, app-defined tab-based navigation with gray styling; large user thumbnail",
				3: "Fluid-width with a menu and user-controlled tab-based navigation; search in top banner",
				4: "Fixed-width with a menu and user-controlled tab-based navigation; search in top banner" } },
		4: { template: "Double Banner with Navigation in Top Banner Only",
			 examples: {
				count: 4,
				1: "Fluid-width with link- and menu-based navigation and search in the top banner; blue styling",
				2: "Fluid-width, includes a user thumbnail; search in a blue second-level banner",
				3: "Fluid-width; link-based actions in a blue second-level banner",
				4: "Fixed-width; link-based actions in a white second-level banner" } },
		5: { template: "Double Banner with Two Levels of Navigation",
			 examples: {
				count: 8,
				1: "Fluid-width, with link-based navigation in the top banner and system-defined tab-based navigation in a blue second-level banner",
				2: "Fixed-width with gray styling and a global actions menu",
				3: "Link- and menu-based navigation in the top banner; context title and search in second-level banner",
				4: "Badge and global actions menu in top banner; context icon precedes title in second-level banner",
				5: "Long second-level context title necessitates a two-row second-level blue banner",
				6: "Long second-level context title and indeterminate tabs in a two-row second-level gray banner",
				7: "Global context switcher in top banner; menu-based navigation in a two-row second-level banner",
				8: "Global context switcher in top banner; menu-based navigation in a two-row second-level banner" } }
	};

	var ht = {
	
		getNumberOfTemplates: function(){
			return titles.count;
		},
		
		getNumberOfExamples: function(template){
			return titles[template] ? titles[template].examples.count : 0;
		},
	
		getTitle: function(template, example){
			// This function returns the title for a template or
			// (if specified) and example within a template.
			
			return titles[template] ? (example ? (titles[template].examples[example] || "") : titles[template].template) : "";
		},
		
		getHeaderFactory: function(template, example){
			// This function returns a function which, when executed, will
			// create and return a header instance configured as per the
			// specified template and example. The returned function takes
			// two parameter, which are ids of the DOM node where the header
			// is to be placed and the the DOM node which is to be used as a
			// content container (if required). The function has a property
			// 'hasContent' which is true if the second parameter is required.
			
			var result;
			
			switch((10 * template) + example){
			
				case 1:
					result = function(__id__, __contentcontainer__, overrides){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01", currentPage: true }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02" }));
		
		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 6" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 7" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 8" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 9" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 10" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 11" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 12" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 13" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 14" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 15" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 16" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 17" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 18" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 19" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 20" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 21" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 22" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));
		
		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_01 }));
		
		navigationMenu.addChild(new MenuBarItem({ label: "Link 03" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 04" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 05" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 06" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 07" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 08" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 09" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 10" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 11" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 12" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 13" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 14" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 15" }));
		
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var contentMenu_01 = new Menu();
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 6" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 7" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 8" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 9" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 10" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 11" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 12" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 13" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 14" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 15" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 16" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 17" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 18" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 19" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 20" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 21" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 22" }));
		
		var contentMenu_02 = new Menu();
		contentMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
	
		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Link 01" }));
		content.addChild(new ContentPane({ title: "Menu 01", popup: contentMenu_01, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Closeable Menu 01", popup: contentMenu_02, alwaysShowMenu: true, closable: true }));
		content.addChild(new ContentPane({ title: "Closeable Link 01", closable: true }));
		content.addChild(new ContentPane({ title: "Closeable Link 02", closable: true }));
		content.addChild(new ContentPane({ title: "Link 02" }));
		content.addChild(new ContentPane({ title: "Link 03" }));
		content.addChild(new ContentPane({ title: "Link 04" }));
		content.addChild(new ContentPane({ title: "Link 05" }));
		content.addChild(new ContentPane({ title: "Link 06" }));
		content.addChild(new ContentPane({ title: "Link 07" }));
		content.addChild(new ContentPane({ title: "Link 08" }));
		content.addChild(new ContentPane({ title: "Link 09" }));
		content.addChild(new ContentPane({ title: "Link 10" }));
		content.addChild(new ContentPane({ title: "Link 11" }));
		content.addChild(new ContentPane({ title: "Link 12" }));
		content.addChild(new ContentPane({ title: "Link 13" }));
		content.addChild(new ContentPane({ title: "Link 14" }));
		content.addChild(new ContentPane({ title: "Link 15" }));
		content.addChild(new ContentPane({ title: "Link 16" }));
		content.addChild(new ContentPane({ title: "Link 17" }));
		content.addChild(new ContentPane({ title: "Link 18" }));

		var header = new Header(lang.delegate((overrides || {}), {
			primaryTitle: "One UI Header &ldquo;fully loaded&rdquo; example with limit-stretching values",
			navigation: navigationMenu,
			primarySearch: {
				entryPrompt: "Ask, and it shall be given you; seek, and ye shall find; knock, and it shall be opened unto you",
				onSubmit: function(value){ alert('Search for "' + value + '" requested'); }
			},
			user: {
				displayName: "Eyitope Oyinkan St. Matthew-Daniel"
			},
			settings: settingsMenu,
			help: helpMenu,
			secondaryTitle: "A long context title value with a subtitle which follows here:",
			secondarySubtitle: "Change and Configuration Management (/ccm)",
	 		contentContainer: __contentcontainer__,
			secondarySearch: {
				entryPrompt: "Ask, and it shall be given you; seek, and ye shall find; knock, and it shall be opened unto you",
				onSubmit: function(value){ alert('Search for "' + value + '" requested'); }
			}
		}),
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
				case 11:
					result = function(__id__){
//START
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			user: {
				displayName: "User Name"
			},
			help: helpMenu,
			primaryBannerType: "thick"
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
				case 12:
					result = function(__id__){
//START
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
			layoutType: "fixed",
			primaryBannerType: "thick"
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
				case 13:
					result = function(__id__){
//START
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			user: {
				displayName: "User Name",
				displayImage: "http://w3.ibm.com/bluepages/api/BluePagesPhoto.jsp?CNUM=074773866",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
			primaryBannerType: "thick"
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
				case 14:
					result = function(__id__){
//START
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context Long Name in the Top Banner",
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
				case 15:
					result = function(__id__){
//START
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			primarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			help: helpMenu,
			primaryBannerType: "thick"
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
			
				case 21:
					result = function(__id__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01", currentPage: true }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 03" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 04" }));
		
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			primarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			help: helpMenu,
			primaryBannerType: "thick"
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
			
				case 22:
					result = function(__id__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01", currentPage: true }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02" }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02 }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
			primaryBannerType: "thick",
			layoutType: "fixed"
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
			
				case 23:
					result = function(__id__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01", currentPage: true }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02" }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02 }));
		
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				displayImage: "http://w3.ibm.com/bluepages/api/BluePagesPhoto.jsp?CNUM=074773866",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
			primaryBannerType: "thick"
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
			
				case 24:
					result = function(__id__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01", currentPage: true }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02" }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02 }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			primarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			help: helpMenu,
			primaryBannerType: "thick"
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
			
				case 31:
					result = function(__id__, __contentcontainer__){
//START
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));
		
		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Link 01" }));
		content.addChild(new ContentPane({ title: "Link 02" }));
		content.addChild(new ContentPane({ title: "Link 03" }));
		content.addChild(new ContentPane({ title: "Link 04" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context Long Name in the Top Banner",
			user: {
				displayName: "User Name",
				displayImage: "http://w3.ibm.com/bluepages/api/BluePagesPhoto.jsp?CNUM=074773866",
				actions: actionsMenu
			},
			help: helpMenu,
	 		contentContainer: __contentcontainer__,
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			}
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;

					
				case 32:
					result = function(__id__, __contentcontainer__){
//START
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));
		
		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Link 01" }));
		content.addChild(new ContentPane({ title: "Link 02" }));
		content.addChild(new ContentPane({ title: "Link 03" }));
		content.addChild(new ContentPane({ title: "Link 04" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context Long Name in the Top Banner",
			user: {
				displayImage: "http://w3.ibm.com/bluepages/api/BluePagesPhoto.jsp?CNUM=074773866",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
	 		contentContainer: __contentcontainer__,
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			secondaryBannerType: "lightGray",
			layoutType: "fixed"
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			
				case 33:
					result = function(__id__, __contentcontainer__){
//START
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));
		
		var contentMenu_01 = new Menu();
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
		
		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Menu 01", popup: contentMenu_01, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Closeable Link 01", closable: true }));
		content.addChild(new ContentPane({ title: "Closeable Link 02", closable: true }));
		content.addChild(new ContentPane({ title: "Closeable Link 03", closable: true }));

		var header = new Header({
			primaryTitle: "IBM Product/Context Long Name in the Top Banner",
			primarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			help: helpMenu,
	 		contentContainer: __contentcontainer__
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			
				case 34:
					result = function(__id__, __contentcontainer__){
//START
		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));
		
		var contentMenu_01 = new Menu();
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
		
		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Menu 01", popup: contentMenu_01, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Closeable Link 01", closable: true }));
		content.addChild(new ContentPane({ title: "Closeable Link 02", closable: true }));
		content.addChild(new ContentPane({ title: "Closeable Link 03", closable: true }));

		var header = new Header({
			primaryTitle: "IBM Product/Context Long Name in the Top Banner",
			primarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			help: helpMenu,
	 		contentContainer: __contentcontainer__,
			secondaryBannerType: "lightGray",
			layoutType: "fixed"
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			
				case 41:
					result = function(__id__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02", currentPage: true }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02 }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			primarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			help: helpMenu,
			secondaryTitle: "Link 02"
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
			
				case 42:
					result = function(__id__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02<span class='idxBadgeInformation'>4</span>" }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02, currentPage: true }));

		var navigationMenu_03 = new Menu();
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 1" }));
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 2" }));
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 3" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 03", popup: navigationMenu_03 }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			help: helpMenu,
			secondaryTitle: "Menu 02 Item",
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			}
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
			
				case 43:
					result = function(__id__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02" }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02, currentPage: true }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			help: helpMenu,
			secondaryTitle: "Menu 02 Item",
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			}
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
			
				case 44:
					result = function(__id__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01", currentPage: true }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02<span class='idxBadgeInformation'>4</span>" }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02 }));

		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
			secondaryTitle: "Menu 02 Item",
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			primaryBannerType: "thick",
			secondaryBannerType: "white",
			layoutType: "fixed"
		},
		__id__);
//END
					};
					result.hasContent = false;
					break;
			
			
				case 51:
					result = function(__id__, __contentcontainer__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 03", currentPage: true }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 04" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 05" }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Sub-Link 01" }));
		content.addChild(new ContentPane({ title: "Sub-Link 02" }));
		content.addChild(new ContentPane({ title: "Sub-Link 03" }));
		content.addChild(new ContentPane({ title: "Sub-Link 04" }));
		content.addChild(new ContentPane({ title: "Sub-Link 05" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				displayImage: "http://w3.ibm.com/bluepages/api/BluePagesPhoto.jsp?CNUM=074773866",
				actions: actionsMenu
			},
			help: helpMenu,
	 		contentContainer: __contentcontainer__
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			
				case 52:
					result = function(__id__, __contentcontainer__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 03", currentPage: true }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 04" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 05" }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Sub-Link 01" }));
		content.addChild(new ContentPane({ title: "Sub-Link 02" }));
		content.addChild(new ContentPane({ title: "Sub-Link 03" }));
		content.addChild(new ContentPane({ title: "Sub-Link 04" }));
		content.addChild(new ContentPane({ title: "Sub-Link 05" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				displayImage: "http://w3.ibm.com/bluepages/api/BluePagesPhoto.jsp?CNUM=074773866",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
			primaryBannerType: "thick",
	 		contentContainer: __contentcontainer__,
			secondaryBannerType: "lightGray",
			layoutType: "fixed"
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			
				case 53:
					result = function(__id__, __contentcontainer__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01", currentPage: true }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02" }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02 }));

		var navigationMenu_03 = new Menu();
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 1" }));
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 2" }));
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 3" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 03", popup: navigationMenu_03 }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Sub-Link 01" }));
		content.addChild(new ContentPane({ title: "Sub-Link 02" }));
		content.addChild(new ContentPane({ title: "Sub-Link 03" }));
		content.addChild(new ContentPane({ title: "Sub-Link 04" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			help: helpMenu,
			secondaryTitle: "Menu 02 Item",
	 		contentContainer: __contentcontainer__,
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			contentTabsInline: true
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			
				case 54:
					result = function(__id__, __contentcontainer__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02<span class='idxBadgeInformation'>4</span>" }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02, currentPage: true }));

		var navigationMenu_03 = new Menu();
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 1" }));
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 2" }));
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 3" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 03", popup: navigationMenu_03 }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Sub-Link 01" }));
		content.addChild(new ContentPane({ title: "Sub-Link 02" }));
		content.addChild(new ContentPane({ title: "Sub-Link 03" }));
		content.addChild(new ContentPane({ title: "Sub-Link 04" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
			primaryBannerType: "thick",
			secondaryTitle: "Menu 02 Item",
	 		contentContainer: __contentcontainer__,
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			secondaryBannerType: "lightGray",
			layoutType: "fixed",
			contentTabsInline: true
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			
				case 55:
					result = function(__id__, __contentcontainer__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 01" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 02<span class='idxBadgeInformation'>1</span>" }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02, currentPage: true }));

		var navigationMenu_03 = new Menu();
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 1" }));
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 2" }));
		navigationMenu_03.addChild(new MenuItem({ label: "Menu 03 Item 3" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 03", popup: navigationMenu_03 }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Sub-Link 01" }));
		content.addChild(new ContentPane({ title: "Sub-Link 02" }));
		content.addChild(new ContentPane({ title: "Sub-Link 03" }));
		content.addChild(new ContentPane({ title: "Sub-Link 04" }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			help: helpMenu,
			secondaryTitle: "Really Long Item Name From Menu 02",
	 		contentContainer: __contentcontainer__,
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			}
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			
				case 56:
					result = function(__id__, __contentcontainer__){
//START
		var navigationMenu = new MenuBar();
		navigationMenu.addChild(new MenuBarItem({ label: "Link 1" }));
		navigationMenu.addChild(new MenuBarItem({ label: "Link 2<span class='idxBadgeInformation'>59</span>" }));

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 01", popup: navigationMenu_01 }));

		var navigationMenu_02 = new Menu();
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 1" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 2" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 3" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 4" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 5" }));
		navigationMenu_02.addChild(new MenuItem({ label: "Menu 02 Item 6" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "Menu 02", popup: navigationMenu_02, currentPage: true }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Sub-Link 01", closable: true }));
		content.addChild(new ContentPane({ title: "Sub-Link 02", closable: true }));
		content.addChild(new ContentPane({ title: "Sub-Link 03", closable: true }));
		content.addChild(new ContentPane({ title: "Sub-Link 04", closable: true }));
		content.addChild(new ContentPane({ title: "Sub-Link 05", closable: true }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
			primaryBannerType: "thick",
			secondaryTitle: "Really Long Item Name from Menu 02",
	 		contentContainer: __contentcontainer__,
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			secondaryBannerType: "lightGray",
			layoutType: "fixed"
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			
				case 57:
					result = function(__id__, __contentcontainer__){
//START
		var navigationMenu = new MenuBar();

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Home Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Home Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Home Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Home Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "", popup: navigationMenu_01 }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var contentMenu_01 = new Menu();
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
	
		var contentMenu_02 = new Menu();
		contentMenu_02.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
	
		var contentMenu_03 = new Menu();
		contentMenu_03.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_03.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_03.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_03.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_03.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
	
		var contentMenu_04 = new Menu();
		contentMenu_04.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_04.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_04.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_04.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_04.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
	
		var contentMenu_05 = new Menu();
		contentMenu_05.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_05.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_05.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_05.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_05.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
	
		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Link 01" }));
		content.addChild(new ContentPane({ title: "Menu 01", popup: contentMenu_01, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Menu 02", popup: contentMenu_02, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Menu 03", popup: contentMenu_03, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Menu 04", popup: contentMenu_04, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Menu 05", popup: contentMenu_05, alwaysShowMenu: true }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
			secondaryTitle: "Context",
			secondarySubtitle: "Sub-Context",
	 		contentContainer: __contentcontainer__,
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			}
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			
				case 58:
					result = function(__id__, __contentcontainer__){
//START
		var navigationMenu = new MenuBar();

		var navigationMenu_01 = new Menu();
		navigationMenu_01.addChild(new MenuItem({ label: "Home Item 1" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Home Item 2" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Home Item 3" }));
		navigationMenu_01.addChild(new MenuItem({ label: "Home Item 4" }));
		navigationMenu.addChild(new PopupMenuBarItem({ label: "", popup: navigationMenu_01 }));

		var actionsMenu = new Menu();
		actionsMenu.addChild(new MenuItem({ label: "Edit Profile" }));
		actionsMenu.addChild(new MenuItem({ label: "Sign Out" }));
		
		var settingsMenu = new Menu();
		settingsMenu.addChild(new MenuItem({ label: "Edit Settings" }));
		settingsMenu.addChild(new MenuItem({ label: "Manage Users" }));

		var helpMenu = new Menu();
		helpMenu.addChild(new MenuItem({ label: "Help Center" }));
		helpMenu.addChild(new MenuItem({ label: "About" }));

		var contentMenu_01 = new Menu();
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_01.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
	
		var contentMenu_02 = new Menu();
		contentMenu_02.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_02.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
	
		var contentMenu_03 = new Menu();
		contentMenu_03.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_03.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_03.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_03.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_03.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
	
		var contentMenu_04 = new Menu();
		contentMenu_04.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_04.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_04.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_04.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_04.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
	
		var contentMenu_05 = new Menu();
		contentMenu_05.addChild(new MenuItem({ label: "Menu 01 Item 1" }));
		contentMenu_05.addChild(new MenuItem({ label: "Menu 01 Item 2" }));
		contentMenu_05.addChild(new MenuItem({ label: "Menu 01 Item 3" }));
		contentMenu_05.addChild(new MenuItem({ label: "Menu 01 Item 4" }));
		contentMenu_05.addChild(new MenuItem({ label: "Menu 01 Item 5" }));
	
		var content = new StackContainer({style: "height: 100%; width: 100%;"}, __contentcontainer__);
		content.addChild(new ContentPane({ title: "Link 01" }));
		content.addChild(new ContentPane({ title: "Menu 01", popup: contentMenu_01, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Menu 02", popup: contentMenu_02, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Menu 03", popup: contentMenu_03, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Menu 04", popup: contentMenu_04, alwaysShowMenu: true }));
		content.addChild(new ContentPane({ title: "Menu 05", popup: contentMenu_05, alwaysShowMenu: true }));

		var header = new Header({
			primaryTitle: "IBM Product/Context",
			navigation: navigationMenu,
			user: {
				displayName: "User Name",
				actions: actionsMenu
			},
			settings: settingsMenu,
			help: helpMenu,
			secondaryTitle: "Context",
			secondarySubtitle: "Sub-Context",
	 		contentContainer: __contentcontainer__,
			secondarySearch: {
				entryPrompt: "Search",
				onSubmit: function(value){ alert('Search for "' + value + '" requested.'); }
			},
			secondaryBannerType: "lightGray",
			layoutType: "fixed"
		},
		__id__);

		content.startup();
//END
					};
					result.hasContent = true;
					break;
			
			}
			
			return result;
		}
	};

	return ht;
});