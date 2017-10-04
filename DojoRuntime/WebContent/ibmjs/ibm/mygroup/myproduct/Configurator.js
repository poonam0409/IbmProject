/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2011 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
dojo.provide("ibm.mygroup.myproduct.Configurator");

dojo.require("dijit._Templated");
dojo.require("dijit._Widget");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TimeTextBox");
dojo.require("dijit.form.TextBox");
dojo.require("dijit.form.HorizontalSlider");
dojo.require("dijit.form.HorizontalRule");
dojo.require("dijit.form.HorizontalRuleLabels");
dojo.require("dijit.form.Select");

/**
* @name ibm.mygroup.myproduct.Configurator
* @class Configuration Widget Template
* @augments dijit._Widget
* @augments dijit._Templated
* @private
*/
dojo.declare("ibm.mygroup.myproduct.Configurator", 
             [dijit._Widget,dijit._Templated],
     		/**@lends ibm.mygroup.myproduct.Configurator#*/	
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
	 * @default "mygroup/myproduct/templates/Configurator.html"
	 */
  templatePath: dojo.moduleUrl(
    "ibm",
    "mygroup/myproduct/templates/Configurator.html")

});
