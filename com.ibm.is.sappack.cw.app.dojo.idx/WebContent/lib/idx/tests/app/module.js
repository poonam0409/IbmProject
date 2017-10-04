dojo.provide("idx.tests.app.module");

try{
	var userArgs = "?" + window.location.search.match(/[\?&](dojo|mode)=[^&]*/g).join("").substring(1);
	doh.registerUrl("idx.tests.app.test_AppFrame", dojo.moduleUrl("idx", "tests/app/test_AppFrame.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.app.test_AppMarquee", dojo.moduleUrl("idx", "tests/app/test_AppMarquee.html"+userArgs), 999999);
    doh.registerUrl("idx.tests.app.test_TabMenuLauncher", dojo.moduleUrl("idx", "tests/app/test_TabMenuLauncher.html"+userArgs), 999999);
    doh.registerUrl("idx.tests.app.test_WorkspaceType", dojo.moduleUrl("idx", "tests/app/test_WorkspaceType.html"+userArgs), 999999);
    doh.registerUrl("idx.tests.app.test_registry", dojo.moduleUrl("idx", "tests/app/test_registry.html"+userArgs), 999999);
}catch(e){
	doh.debug(e);
}
