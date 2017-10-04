require([
	'dojo/_base/lang',
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/Memory',
	'idx/oneui/gridx/tests/support/modules',
	'dojo/domReady!'
], function(lang, Grid, Cache, dataSource, storeFactory, modules){

	var create = function(id, container, size, isDD, props){
		var grid = new Grid(lang.mixin({
			id: id,
			cacheClass: Cache,
			store: storeFactory({
				dataSource: dataSource, 
				size: size 
			}),
			structure: dataSource.layouts[5],
			modules: [
				modules.ExtendedSelectRow,
				modules.VirtualVScroller,
				modules.Pagination,
				isDD ? modules.PaginationBarDD : modules.OneUIPaginationBar
			],
			selectRowTriggerOnCell: true
		}, props || {}));
		grid.placeAt(container);
		grid.startup();
		return grid;
	};
	
	create('grid1', 'grid1Container', 100, false, {
		paginationBarGotoButton: true
	});
	create('grid2', 'grid2Container', 100, false, {
		paginationBarPosition: 'both',
		paginationBarGotoButton: false,
		paginationBarDescription: 'bottom',
		paginationBarSizeSwitch: 'top'
	});
	create('grid3', 'grid3Container', 100, true, {
	});
});



