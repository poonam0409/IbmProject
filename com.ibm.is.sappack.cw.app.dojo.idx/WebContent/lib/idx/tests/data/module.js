dojo.provide("idx.tests.data.module");

try{
	var userArgs = "?" + window.location.search.match(/[\?&](dojo|mode)=[^&]*/g).join("").substring(1);
	
	doh.registerUrl("idx.tests.data.test_JsonStore", dojo.moduleUrl("idx", "tests/data/test_JsonStore.html"+userArgs), 999999);
}catch(e){
	doh.debug(e);
}
