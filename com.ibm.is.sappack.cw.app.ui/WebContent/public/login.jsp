<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<meta http-equiv="Pragma" content="no-cache">
		<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
		<meta http-equiv="Cache-Control" content="max-age=0">
		<meta http-equiv="Cache-Control" content="post-check=0, pre-check=0">
    	<meta http-equiv="Expires" content="Sat, 01 Dec 2001 00:00:00 GMT">
		
		<title>IBM InfoSphere Conversion Workbench Application</title>
		
		<link rel="SHORTCUT ICON" href="<%= request.getContextPath() %>/public/favicon.png"/>
		
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/dojo/dojo/resources/dojo.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/dojo/dijit/themes/dijit.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/dojo/dijit/themes/claro/claro.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/dojo/dojox/grid/resources/Grid.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/dojo/dojox/grid/resources/claroGrid.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/dojo/dojox/layout/resources/ExpandoPane.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/dojo/dojox/grid/enhanced/resources/EnhancedGrid.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/dojo/dojox/grid/enhanced/resources/claro/EnhancedGrid.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/dojo/dojox/grid/enhanced/resources/EnhancedGrid_rtl.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/ibmjs/idx/themes/vienna/grid/viennaGrid.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/ibmjs/idx/themes/vienna/vienna.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/ibmjs/idx/resources/claro_idx.css"></link>
		<link rel="stylesheet" type="text/css" href="/DojoRuntime/ibmjs/idx/resources/vienna_idx.css"></link>
		
		<style type="text/css">
			body, html { 
			    width:100%; 
			    height:100%; 
			    margin:0; 
			    padding:0; 
			    overflow:hidden;
			}
			
			div.idxLoginBoxWithShadow {
				height: 420px !important;
				width: 320px !important;
				margin-top: -240px !important;
			}
			
			div.idxLoginBoxWithShadow .dijitTextBox {
				width: 275px;
			}
			
			div.idxLoginBoxInner {
				text-align: justify;
			}
			
			div.idxLoginSubTitle {
				margin-bottom: 20px !important;
			}
			
			div.idxFieldLabel {
				margin-bottom: 5px !important;
			}
		</style>
		
		<script type="text/javascript">
		    var CONTEXT_ROOT = '<%= request.getContextPath() %>';
			djConfig = {
		      isDebug: false,
		      parseOnLoad: true,
		      modulePaths: {
		      	idx : "/DojoRuntime/ibmjs/idx/",
		        cwapp : CONTEXT_ROOT + "/public/cwapp"
		      }
		    };
		</script>
		
		<script type="text/javascript" src="/DojoRuntime/dojo/dojo/dojo.js"></script>
		<script type="text/javascript">
			dojo.require("cwapp.widgets.LoginPage");
		</script>
		
		<% if (request.getParameter("error") != null) { %>
		<script type="text/javascript">
			dojo.ready(function() {
				var msg = 'LOGIN_<%= request.getParameter("error") %>';
				dijit.byId('loginFrame').showErrorMessage(msg);
			});
		</script>
		<% } %>
	</head>
	<body class="claro vienna" role="application">
		<div style="overflow: hidden; width: 100%; height: 100%;">
		    <div dojoType="cwapp.widgets.LoginPage" id="loginFrame"></div> 
		</div>
	</body>
</html>
