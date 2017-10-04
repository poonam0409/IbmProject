dojo.provide("idx.tests.layout.module");

try{
	var userArgs = "?" + window.location.search.match(/[\?&](dojo|mode)=[^&]*/g).join("").substring(1);
	doh.registerUrl("idx.tests.layout.test_TitlePane", dojo.moduleUrl("idx", "tests/layout/test_TitlePane.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.test_ContentPane", dojo.moduleUrl("idx", "tests/layout/test_ContentPane.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.test_BreadcrumbController", dojo.moduleUrl("idx", "tests/layout/test_BreadcrumbController.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.test_HeaderPane", dojo.moduleUrl("idx", "tests/layout/test_HeaderPane.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.test_AccordionTabContainer", dojo.moduleUrl("idx", "tests/layout/test_AccordionTabContainer.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.test_BorderContainer", dojo.moduleUrl("idx", "tests/layout/test_BorderContainer.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.test_CollapsibleTabContainer", dojo.moduleUrl("idx", "tests/layout/test_CollapsibleTabContainer.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.test_DockContainer", dojo.moduleUrl("idx", "tests/layout/test_DockContainer.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.test_ECMTitlePane", dojo.moduleUrl("idx", "tests/layout/test_ECMTitlePane.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.test_MoveableTabContainer", dojo.moduleUrl("idx", "tests/layout/test_MoveableTabContainer.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.testButtonBar", dojo.moduleUrl("idx", "tests/layout/test_ButtonBar.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.layout.test_OpenMenuTabContainer", dojo.moduleUrl("idx", "tests/layout/test_OpenMenuTabContainer.html"+userArgs), 999999);
}catch(e){
	doh.debug(e);
}
