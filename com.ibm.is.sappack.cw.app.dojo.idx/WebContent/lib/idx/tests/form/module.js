dojo.provide("idx.tests.form.module");

try{
	var userArgs = "?" + window.location.search.match(/[\?&](dojo|mode)=[^&]*/g).join("").substring(1);
	doh.registerUrl("idx.tests.form.test_ComboLink", dojo.moduleUrl("idx", "tests/form/test_ComboLink.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.form.test_DatePicker", dojo.moduleUrl("idx", "tests/form/test_DatePicker.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.form.test_DateTimePicker", dojo.moduleUrl("idx", "tests/form/test_DateTimePicker.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.form.test_DropDownLink", dojo.moduleUrl("idx", "tests/form/test_DropDownLink.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.form.test_DropDownMultiSelect", dojo.moduleUrl("idx", "tests/form/test_DropDownMultiSelect.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.form.test_DropDownSelect", dojo.moduleUrl("idx", "tests/form/test_DropDownSelect.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.form.test_Link", dojo.moduleUrl("idx", "tests/form/test_Link.html"+userArgs), 999999);
 	// having trouble with robot tests
	//	doh.registerUrl("idx.tests.form.robot.test_Link", dojo.moduleUrl("idx", "tests/form/robot/test_Link.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.form.test_Select", dojo.moduleUrl("idx", "tests/form/test_Select.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.form.test_TimePicker", dojo.moduleUrl("idx", "tests/form/test_TimePicker.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.form.test_TriStateCheckBox", dojo.moduleUrl("idx", "tests/form/test_TriStateCheckBox.html"+userArgs), 999999);
}catch(e){
	doh.debug(e);
}
