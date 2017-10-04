require([
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/Memory',
	'idx/oneui/gridx/tests/support/modules',
	'dojo/domReady!'
], function(Grid, Cache, dataSource, storeFactory, modules){

	create = function(id, cls, container, size){
		var store = storeFactory({
			dataSource: dataSource, 
			size: 200
		}); 
		var layout = dataSource.layouts[4];
		var grid = new Grid({
			id: id,
			'class': cls,
			cacheClass: Cache,
			store: store,
			structure: layout,
			modules:[
//                modules.Sort,
				modules.ExtendedSelectRow,
				modules.ColumnResizer,
				modules.VirtualVScroller
			],
			selectRowTriggerOnCell: true
		});
		grid.placeAt(container);
		grid.startup();
		return grid;
	};

	create('grid1', '', 'grid1Container', 100);
	create('grid2', 'gridxAlternatingRows gridxWholeRow', 'grid2Container', 100);
	create('grid3', 'compact', 'grid3Container', 100);
	create('grid4', 'compact gridxAlternatingRows gridxWholeRow', 'grid4Container', 100);
});
