/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2012 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

define([
	"dojo/_base/declare",
	"dojo/_base/window",
	"dojo/dom-geometry",
	"dijit/_Widget",
	"dijit/_TemplatedMixin",
	"dijit/_WidgetsInTemplateMixin",
	"dijit/_CssStateMixin",
	"idx/oneui/MenuBar",
	"dijit/MenuBarItem",
	"dojo/text!./_PageFooter.html"
], function(declare, win, geom, _Widget, _TemplatedMixin, _WidgetsInTemplateMixin, _CssStateMixin, MenuBar, MenuBarItem, template){
	return declare("idx/oneui/tests/_pagefooter/_PageFooter", [_Widget, _TemplatedMixin, _WidgetsInTemplateMixin, _CssStateMixin], {
		templateString: template,
		widgetsInTemplate: true,
		baseClass: "_testPageFooter",
		
		postCreate: function(){
			win.body().appendChild(this.domNode);
			if(geom.isBodyLtr()){
				this.rtl.set("disabled", false);
				this.ltr.set("disabled", true);
			}else{
				this.rtl.set("disabled", true);
				this.ltr.set("disabled", false);
			}
		},
		
		switchDir: function(rtl){
			var query = location.search, index1 = query.indexOf("dir"),
				href = window.location.href, index2 = href.indexOf("?"),
				subQuery = query.substr(index1), index3 = subQuery.indexOf("&");
			if(index1 > -1){
				href = href.substring(0, index2) + query.substring(0, index1 + 4) + rtl;
				if(index3 > -1){
					href += subQuery.substr(index3);
				}
				window.location.href = href;
			}else if(query.length == 1){
				window.location.href = href + "dir=" + rtl;
			}else{
				window.location.href = href + "?dir=" + rtl;
			}
		}
	});
});
