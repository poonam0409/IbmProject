/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2012 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

dojo.provide("ibm.oneui");

dojo.require("idx.oneui.MenuBar");
dojo.require("dijit.MenuBarItem");
dojo.require("dijit.PopupMenuBarItem");
dojo.require("idx.oneui.Menu");
dojo.require("dijit.MenuItem");
dojo.require("dijit.PopupMenuItem");

dojo.require("dijit.layout.TabContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.form.TextBox");

ibm.oneui = function(){
	var cpArr = [];
	var cps;
	var pMenuBar;

    return {
        init : function(menuBarItems, contentPanes){
    		this.initMenuBar(menuBarItems);
    		this.initContentPanes(contentPanes);
    		this.initTextBox();
        },
        
        initMenuBar : function(menuBarItems){
            pMenuBar = new idx.oneui.MenuBar();
        	
	        for(var i=0; i<menuBarItems.length; i++){
	        	var mbItem = menuBarItems[i].menuItem;
	        	var pSubMenu = new idx.oneui.Menu({});
	        	
	        	if(typeof mbItem !== "undefined"){
		        	for(var j=0; j<mbItem.length; j++){
		        		var mItem = mbItem[j];
		                pSubMenu.addChild(new dijit.MenuItem({
		                    label: mItem.label
		                }));	        		
		        	}	        		
	        	}
	        	
	        	pMenuBar.addChild(new dijit.PopupMenuBarItem({
	                label: menuBarItems[i].label,
	                popup: pSubMenu
	            }));
	        }
	        
	        var nav = dojo.query(".idxHeaderContainer .idxHeaderPrimary")[0];
            pMenuBar.placeAt(nav);
            pMenuBar.startup();
        },  
        
        initContentPanes : function(contentPanes){
    		if(typeof contentPanes !== "undefined"){
    			cps = contentPanes;
    		} else {
    			return false;
    		}
    	
			var tc = new dijit.layout.TabContainer({
	            style: "height: 100%; width: 100%;"
	        },
	        "tc");

			dojo.forEach(cps, function(contentPane, i){
				cpArr[i] = new dijit.layout.ContentPane({
					id : contentPane.id,
					title: contentPane.title,
                    closable: contentPane.closable !== undefined ? contentPane.closable : true,
                    onClose: function() {
                        return confirm("Do you really want to Close " + contentPane.title + " ContentPane?");
                    }
				})
				tc.addChild(cpArr[i]);
			});
			tc.startup();

			//this.initCloseBtns();
			this.initArrowBtns();
        },
        
        initTextBox : function(){
	    	var productSearch = new dijit.form.TextBox({
	    	    name: "firstname",
	    	    value: "",
	    	    placeHolder: "Search"
	    	}, "productSearch");        	
        },

        initCloseBtns : function(){
	        var that = this;
			this.setCloseBtns(0);

			var dijitTabs = dojo.query("#tc .nowrapTabStrip .dijitTab");
			dijitTabs.onmouseenter(function(evt){
				if(!dojo.hasClass(this, "dijitTabChecked")){
		            var closeBtn = dojo.query(".dijitInline.dijitTabCloseButton.dijitTabCloseIcon", this)[0];
		            dojo.style(closeBtn, "visibility", "visible");
				}
	        }).onmouseleave(function(evt){
				if(!dojo.hasClass(this, "dijitTabChecked")){
		            var closeBtn = dojo.query(".dijitInline.dijitTabCloseButton.dijitTabCloseIcon", this)[0];
		            dojo.style(closeBtn, "visibility", "hidden");
				}
	        }).onmousedown(function(evt){
				if(!dojo.hasClass(this, "dijitTabChecked")){
					var widgetids = this.getAttribute("widgetid").split("_");
					var widgetid = widgetids[widgetids.length-1];
					var widgetIndex = that.getWidgetIndex(widgetid);
		            that.setCloseBtns(widgetIndex);
				}							
		    });
        },
        
        initArrowBtns : function(){
        	var arrowBtns = dojo.query(".dijitInline.dijitTabArrowButton.dijitTabArrowIcon");
        	var separators = dojo.query(".dijitTabContent .idxTabSeparator");
        	dojo.forEach(arrowBtns, function(arrowBtn, i){
            	if(typeof cps[i].arrowdown !== 'undefined' && cps[i].arrowdown === false){
                	dojo.addClass(arrowBtn, "hide");
                	dojo.addClass(separators[i], "hide");
            	}
        	})
        },

        setCloseBtns : function(index){
			var closeBtns = dojo.query(".dijitTab .dijitInline.dijitTabCloseButton.dijitTabCloseIcon");
			dojo.forEach(closeBtns, function(closeBtn, i){
				if(i === index){
					dojo.style(closeBtn, "visibility", "visible");
				} else {
					dojo.style(closeBtn, "visibility", "hidden");
				}							
			});
        },

        getWidgetIndex : function(widgetid){
			for(var i=0; i<cps.length; i++){
				if(cps[i].id === widgetid){
					return i;
				}
			}
        }
    };
}();


