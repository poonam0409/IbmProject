<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<!--
 * IBM Confidential
 * OCO Source Materials
 * (C) Copyright IBM Corp. 2010, 2011
 * The source code for this program is not published 
 * or otherwise divested of its trade secrets, irrespective
 * of what has been deposited with the U.S. Copyright Office.
-->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" import="com.ibm.is.sappack.cw.app.ui.util.UserLocale"%>
<html lang="<%= UserLocale.print(request) %>">

<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
    <meta http-equiv="Cache-Control" content="max-age=0">
	<meta http-equiv="Cache-Control" content="post-check=0, pre-check=0">
    <meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">

    <title id="appTitle"></title>
    <link rel="SHORTCUT ICON" href="public/favicon.png"/>
    
    <script type="text/javascript">
	    var CONTEXT_ROOT = '<%= request.getContextPath() %>';
    
         //Setup the application's config.  Some of this ends up in djConfig.
         //Some of it can be overridden via query params
         //Other stuff ends up being used to config logging and theming
         myApp = {
        	config : {
        		"locale": '<%= UserLocale.print(request) %>',
        		"parseOnLoad": "true",                    //djConfig, overrideable
        		"debugAtAllCosts": "false",                //djConfig, overrideable
        	    "dojoScript": "dojo.js",                  //dojo loading of uncompressed or not, Required
        	    "isDebug": "false",                        //dojo loading of uncompressed or not
        	    "logLevel": "OFF",                      //Logging - Level is ignored right now, but OFF will turn it off
        	    "dojoLocation": "/DojoRuntime/dojo",     //Where to import dojo from, Required
        	    "idxLocation": "/DojoRuntime/ibmjs",     //djConfig - IDX module paths, Required
        		"cwAppLocation": CONTEXT_ROOT + "/cwapp",
        	    "theme": "vienna",                        //Theme applied to body and other css paths, Required
        	    "logInterval": "5000",                    //log "upload" interval
        	    "logURL": "/ibm/iis/isf/admin/"           //logging api location, Required (use for base REST as well)
        	}		 
         };
    </script>

    <script type="text/javascript" id="insertDojoHere" >
        //Based on the config, loader will insert dojo right after this location
    </script>
    
    <style type="text/css" id="insertStylesHere">
        /*
         * Loader will insert style imports right after this location
         */
    </style>    
     
    <script type="text/javascript" src="loader.js">
        //Based on the config, loader will insert a script loading element
        //for dojo with an appropriate djConfig
    </script>
    
    <script type="text/javascript">
        //import your css dynamically based on config
		dojoCSSImport("@dojopath@/dojo/resources/dojo.css", "insertStylesHere");
		dojoCSSImport("@dojopath@/dijit/themes/dijit.css", "insertStylesHere");
		dojoCSSImport("@dojopath@/dijit/themes/claro/claro.css", "insertStylesHere");
		dojoCSSImport("@dojopath@/dojox/grid/resources/Grid.css", "insertStylesHere");
		dojoCSSImport("@dojopath@/dojox/grid/resources/claroGrid.css", "insertStylesHere");
		dojoCSSImport("@dojopath@/dojox/grid/enhanced/resources/claro/EnhancedGrid.css");
		dojoCSSImport("@dojopath@/dojox/grid/enhanced/resources/EnhancedGrid_rtl.css");
		dojoCSSImport("@dojopath@/dojox/layout/resources/ResizeHandle.css", "insertStylesHere");
		dojoCSSImport("@dojopath@/dojox/form/resources/UploaderFileList.css", "insertStylesHere");
	    dojoCSSImport("@idxpath@/idx/themes/@theme@/@theme@.css", "insertStylesHere");
	    dojoCSSImport("@idxpath@/idx/resources/claro_idx.css", "insertStylesHere");
	    dojoCSSImport("@idxpath@/idx/resources/@theme@_idx.css", "insertStylesHere");
	    dojoCSSImport("@cwAppPath@/css/CwApp.css", "insertStylesHere");
	</script>
	
    <style type="text/css">
		body, html, #mainDiv { 
		    width:100%; 
		    height:100%; 
		    margin:0; 
		    padding:0;
		    border:0;
		    overflow:hidden;
		}
    </style>
	
	<script type="text/javascript" src="cwapp/util/Constants.js">
	</script>

	<script type="text/javascript" src="cwapp/util/Util.js">
	</script>

    <script type="text/javascript">
    	dojo.require("dojo.hash");
    	dojo.require("idx.app.WorkspaceType");
		dojo.requireLocalization("cwapp", "CwApp");
		
		var msg = dojo.i18n.getLocalization("cwapp", "CwApp");
		
		function translate() {
			dojo.byId("appTitle").text = msg.MAIN_1;
			dijit.byId("appMarquee").set("appName", msg.MAIN_1);
			dojo.byId("welcomeSpan").innerHTML = msg.MAIN_2;
			dojo.byId("helpSpan").innerHTML = msg.MAIN_3;
			dijit.byId("onlineHelp").set("label", msg.MAIN_4);
			dijit.byId("ibmSupportHelp").set("label", msg.MAIN_5);
			dojo.byId("logoutLink").innerHTML = msg.LOG_OUT;
            dijit.byId("panicDialog").set("title", msg.LOGIN_DB_SETUP_ERROR_TITLE);
			dojo.byId("panicMessage").innerHTML = msg.LOGIN_DB_SETUP_ERROR;
			dijit.byId("panicButton").set("label", msg.LOG_OUT);
		}
		
		function connects() {
			dojo.connect(dijit.byId("onlineHelp"), "onClick", function() {
				window.open(Help.ONLINEURL);
			});
			
			dojo.connect(dijit.byId("ibmSupportHelp"), "onClick", function() {
				window.open(Help.IBMSUPPORTURL);
			});
		}
		
		function applyTheme() {
        	dojo.addClass(dojo.query("body")[0], myApp.config.theme);
		}
		
		dojo.addOnLoad(translate);
		dojo.addOnLoad(connects);
    	dojo.addOnLoad(applyTheme);
    </script>
    
    <script type="text/javascript">
    	function logout() {
    		document.getElementById("logoutForm").submit();
    	};
    </script>
    
    <script type="text/javascript">
    	function getI18nMessage(messageKey) {
    		return msg[messageKey];
    	};
    </script>
    
	<script type="text/javascript">
		dojo.require("idx.dialogs");
		dojo.require("idx.app.WorkspaceType");
		dojo.requireLocalization("cwapp", "CwApp");
		var msg = dojo.i18n.getLocalization("cwapp", "CwApp");
		var maxInactiveInterval = ${pageContext.session.maxInactiveInterval};
		sessionCheck = null;
		messageCheck = null;
		// Session expiration time in seconds
	    var maxInactiveInterval = ${pageContext.session.maxInactiveInterval};
	    // Time to show up warning dialog before session times out (seconds)
	    var timeBeforeExpire = 300;
	    		
		function startTimeoutTimer(){
		sessionCheck = window.setTimeout(function() {
	    			document.getElementById("logoutForm").submit();
	    		}, maxInactiveInterval*1000);
		}
		function startMessageTimer(){
		messageCheck = window.setTimeout(function() {
					var now = new Date();
		    		var then = new Date(now.getTime() + (timeBeforeExpire * 1000));
	    			idx.info(Util.formatMessage(msg.CWAPP_11, then.toLocaleTimeString()));
	    		}, ((maxInactiveInterval - timeBeforeExpire) * 1000));
		}
		
		function handleSessionTimeout() {
	    	// Save standard XHR handler
	    	var plainXhr = dojo.xhr;
	    	startTimeoutTimer();
	    	startMessageTimer();
	    	// XHR augment for session handling
	    	dojo.xhr = function(method, args, hasBody) {
	    		if (sessionCheck != null) {
					window.clearInterval(sessionCheck);
					window.clearInterval(messageCheck);
	    		}
	    		startTimeoutTimer();
	    		startMessageTimer();
	    		// Fire standard XHR function
	    		return plainXhr(method, args, hasBody);
	    	};
		}
		
		function cwDbConfigCheck() {
	        dojo.xhrGet({
	            url : "/com.ibm.is.sappack.cw.app.services/jaxrs/configCheck",
	            handleAs : "text",
	            sync : true,
	            load : dojo.hitch(this, function(response) {
	                if (response != "0") {
	                    dojo.destroy(dojo.byId("appFrame"));
	                    dijit.byId("panicDialog").show();
	                }
	            }),
	            error : function(error) {
	                cwapp.main.Error.handleError(error);
	            }
	        });
	    }

        dojo.addOnLoad(cwDbConfigCheck);
        dojo.addOnLoad(handleSessionTimeout);
    </script>
   
    <script type="text/javascript">
		dojo.require("dojo.parser");
		dojo.require("dojo.hash");
		dojo.require("idx.app.A11yPrologue");
		dojo.require("idx.app.AppFrame");
		dojo.require("idx.app.AppMarquee");
		dojo.require("idx.app.WorkspaceType");
		dojo.require("cwapp.main.AddressableTabMenuLauncher");
		dojo.require("idx.form.DropDownLink");
		dojo.require("idx.form.Link");
		dojo.require("dijit.Menu");
		dojo.require("dijit.MenuItem");
		dojo.require("dijit.MenuSeparator");
		dojo.require("cwapp.main.AboutDialog");
		
		// Disable dojo animations
		dijit.defaultDuration = 1;
		
		Util.getUserRole();
	</script>
</head>

<body class="claro vienna" role="application">
	<div dojoType="idx.app.A11yPrologue"></div>
	<div dojoType="idx.app.AppFrame" id="appFrame" class="CwApp_appFrame">
		<div id="appMarquee" dojoType="idx.app.AppMarquee" region="marquee" vendorLogo="cwapp/img/ibm_logo_white.gif" vendorName="IBM &#0174;">
			<span id="welcomeSpan"></span>
		    <span><%= request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "" %></span>
		   	<span class="CwApp_marqueeSpan">|</span>
		   	<div style="display: inline-block;"	dojoType="cwapp.main.AboutDialog" id="cwappAboutDialog"></div>
		   	<span class="CwApp_marqueeSpan">|</span>
		   	<span id="helpSpan"></span>			
		   	<div dojoType="idx.form.DropDownLink" id="helpMenu">
		   		<div dojoType="dijit.Menu">
		   			<div id="onlineHelp" dojoType="dijit.MenuItem"></div>
		   			<div dojoType="dijit.MenuSeparator"> </div>
		   			<div id="ibmSupportHelp" dojoType="dijit.MenuItem"></div>
		   		</div>
		   	</div>
		   	<span class="CwApp_marqueeSpan">|</span>	
			<span id="logoutLink" dojoType="idx.form.Link" onClick="logout"></span>
		  	<form method="POST" id="logoutForm" action="logout" style="display: none;">
		  		<input type="hidden" name="logoutExitPage" value="/index.jsp">
		  	</form>
		</div>
		<div id="menuLauncherContainer"></div>
		<!-- the jsp include statement makes a call to our CWAppConfigurationServlet to figure out which tabs
		     are to be displayed and then gets replaced by the returned html code which comprises the tab structure -->
		<jsp:include page="/config">
			<jsp:param name="showBdrUi" value="" />
		</jsp:include>
	</div>
 	<div dojoType="dijit.Dialog" id="panicDialog" class="errorDialog" style="display: none">
        <div class="dijitDialogPaneContentArea">
             <div class="dijitInline imageSpacer">
                 <div class="ImageError_48"></div>
             </div>
             <div class="dijitInline imageSpacer" id="panicMessage"></div>
        </div>
        <div class="dijitDialogPaneActionBar">
             <form method="POST" id="panicLogoutForm" action="logout" style="display: none;">
                <input type="hidden" name="logoutExitPage" value="/index.jsp">
             </form>
             <div id="panicButton" dojoType="dijit.form.Button" type="button" onClick="dojo.byId('panicLogoutForm').submit();"></div>
        </div>
    </div>
</body>
</html>
