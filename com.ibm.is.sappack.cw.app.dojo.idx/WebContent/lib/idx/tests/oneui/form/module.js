/*
 * Licensed Materials - Property of IBM
 * (C) Copyright IBM Corp. 2010, 2012 All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

dojo.provide("idx.oneui.tests.form.module");

try{
	var userArgs = "?" + window.location.search.match(/[\?&](dojo|mode)=[^&]*/g).join("").substring(1);
	
	doh.registerUrl("idx.oneui.tests.form.CheckBox", dojo.moduleUrl("idx","tests/oneui/form/doh/CheckBox.html"+userArgs));
	doh.registerUrl("idx.oneui.tests.form.CheckBoxList", dojo.moduleUrl("idx","tests/oneui/form/doh/CheckBoxList.html"+userArgs));
	doh.registerUrl("idx.oneui.tests.form.CheckBoxSelect", dojo.moduleUrl("idx","tests/oneui/form/doh/CheckBoxSelect.html"+userArgs));
	doh.registerUrl("idx.oneui.tests.form.ComboBox", dojo.moduleUrl("idx","tests/oneui/form/doh/ComboBox.html"+userArgs));
	doh.registerUrl("idx.oneui.tests.form.Select", dojo.moduleUrl("idx","tests/oneui/form/doh/Select.html"+userArgs));
	doh.registerUrl("idx.oneui.tests.form.TriStateCheckBox", dojo.moduleUrl("idx","tests/oneui/form/doh/TriStateCheckBox.html"+userArgs));
	
	doh.registerUrl("idx.oneui.tests.form.TextBox", dojo.moduleUrl("idx","tests/oneui/form/doh/TextBox.html"+userArgs));
	doh.registerUrl("idx.oneui.tests.form.DateTextBox", dojo.moduleUrl("idx","tests/oneui/form/doh/DateTextBox.html"+userArgs));
//	doh.registerUrl("idx.oneui.tests.form.FileInput", dojo.moduleUrl("idx","tests/oneui/form/doh/FileInput.html"+userArgs));
	doh.registerUrl("idx.oneui.tests.form.FilteringSelect", dojo.moduleUrl("idx","tests/oneui/form/doh/FilteringSelect.html"+userArgs));
	doh.registerUrl("idx.oneui.tests.form.Slider", dojo.moduleUrl("idx","tests/oneui/form/doh/Slider.html"+userArgs));
	doh.registerUrl("idx.oneui.tests.form.NumberTextBox", dojo.moduleUrl("idx","tests/oneui/form/doh/NumberTextBox.html"+userArgs));
//	doh.registerUrl("idx.oneui.tests.form.FormLayout", dojo.moduleUrl("idx","tests/oneui/form/doh/FormLayout.html"+userArgs));
}catch(e){
	doh.debug(e);
}
