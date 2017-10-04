require([
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/ItemFileWriteStore',
	'idx/gridx/tests/support/modules',
	'dojo/domReady!'
], function(Grid, Cache, dataSource, storeFactory, modules){

	window.Cache = Cache;
	window.store = storeFactory({
		dataSource: dataSource,
		size: 100
	});
	window.dataSource = dataSource;
	window.modules = modules;
});
