dojo.provide("idx.tests.grid.module");

try{
	var userArgs = "?" + window.location.search.match(/[\?&](dojo|mode)=[^&]*/g).join("").substring(1);
	doh.registerUrl("idx.tests.grid.test_PropertyFormatter", dojo.moduleUrl("idx", "tests/grid/test_PropertyFormatter.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.grid.test_PropertyGrid", dojo.moduleUrl("idx", "tests/grid/test_PropertyGrid.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.grid.test_DataGridMixin", dojo.moduleUrl("idx", "tests/grid/test_DataGridMixin.html"+userArgs), 999999);
	doh.registerUrl("idx.tests.grid.test_cells", dojo.moduleUrl("idx", "tests/grid/test_cells.html"+userArgs), 999999);
}catch(e){
	doh.debug(e);
}
