require([
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/ItemFileWriteStore',
	'idx/gridx/tests/support/modules',
	'idx/gridx/tests/support/TestPane',
	'dojo/domReady!'
], function(Grid, Cache, dataSource, storeFactory, mods, TestPane){

	grid = new Grid({
		id: 'grid',
		cacheClass: Cache,
		store: storeFactory({
			dataSource: dataSource, 
			size: 50
		}),
		structure: dataSource.layouts[1],
		autoHeight: true,
		autoWidth: true,
		modules: [
			mods.Focus,
			mods.ColumnResizer,
//            mods.VirtualVScroller,
			mods.Pagination,
			mods.PaginationBar
		]
	});
	grid.placeAt('gridContainer');
	grid.startup();
});
