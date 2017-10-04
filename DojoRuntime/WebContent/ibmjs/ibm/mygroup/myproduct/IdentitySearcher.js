/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2011 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
dojo.provide("ibm.mygroup.myproduct.IdentitySearcher");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.DropDownButton");
dojo.require("dijit.Menu");
dojo.require("dijit.MenuItem");
dojo.require("dijit.form.DateTextBox");
dojo.require("dijit.form.TextBox");

/**
* @name ibm.mygroup.myproduct.IdentitySearcher
* @class Identity Searcher Widget Template
* @augments dijit._Widget
* @augments dijit._Templated
* @private
*/
dojo.declare("ibm.mygroup.myproduct.IdentitySearcher", 
             [dijit._Widget,dijit._Templated],
      		/**@lends ibm.mygroup.myproduct.IdentitySearcher#*/	
             {

	/**
 	 * Constant to indicate if there is an HTML file with this widget
 	 * @private
 	 * @constant
 	 * @type boolean
 	 * @default true
 	 */
  widgetsInTemplate: true,

	/**
	 * The path to the widget template for the dijit._Templated base class.
	 * @constant
	 * @type String
	 * @private
	 * @default "mygroup/myproduct/templates/IdentitySearcher.html"
	 */
  templatePath: dojo.moduleUrl(
    "ibm",
    "mygroup/myproduct/templates/IdentitySearcher.html")

});
