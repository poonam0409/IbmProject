require([
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Sync',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/ItemFileWriteStore',
	'idx/gridx/tests/support/modules',
	'dojo/domReady!'
], function(Grid, Cache, dataSource, storeFactory, modules, TestPane){

	grid = new Grid({
		id: 'grid',
		cacheClass: Cache,
		store: storeFactory({
			dataSource: dataSource, 
			size: 1000
		}),
		modules:[modules.SingleSort, modules.SelectRow, modules.VirtualVScroller],
		structure: dataSource.layouts[4]
	});
	grid.placeAt('gridContainer');
	grid.startup();
});
