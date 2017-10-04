require([
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/ItemFileWriteStore',
	'idx/gridx/tests/support/modules',
	'idx/gridx/tests/support/TestPane',
	'idx/gridx/modules/extendedSelect/Row',
	'idx/gridx/modules/IndirectSelect',
	'idx/gridx/modules/SummaryBar',
	'dojo/domReady!'
], function(Grid, Cache, dataSource, storeFactory, modules, TestPane, SelectRow, IndirectSelect, SummaryBar){
	grid = new Grid({
		id: 'grid',
		cacheClass: Cache,
		store: storeFactory({
			dataSource: dataSource, 
			size: 100
		}),
		structure: dataSource.layouts[0],
		modules: [
			{
				moduleClass:SummaryBar
			},
			{
				moduleClass: SelectRow,
				triggerOnCell: true
			},
			modules.RowHeader,
			IndirectSelect,
			//modules.select.row,
			modules.VirtualVScroller
		]
	});
	grid.placeAt('gridContainer');
	grid.startup();

	//Test buttons
//	var tp = new TestPane({});
//	tp.placeAt('ctrlPane');
//
//	tp.addTestSet('Summary Bar', [
//	].join(''));
//
//	tp.startup();
});



