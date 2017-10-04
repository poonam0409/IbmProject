require([
	'dojo/_base/lang',
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/Memory',
	'idx/gridx/tests/support/TestPane',
	'idx/gridx/tests/support/modules',
	'dijit/form/Button',
	'dojo/domReady!'
], function(lang, Grid, Cache, dataSource, storeFactory, TestPane, mods){

	function create(id, container, size, layoutIdx, args){
		var g = new Grid(lang.mixin({
			id: id,
			cacheClass: Cache,
			store: storeFactory({
				path: './support/stores',
				dataSource: dataSource,
				size: size
			}),
			selectRowTriggerOnCell: true,
			modules: [
				mods.Focus,
				mods.FilterBar,
				mods.ExtendedSelectRow,
				mods.ExtendedSelectColumn,
				mods.MoveRow,
				mods.MoveColumn,
				mods.DndRow,
				mods.DndColumn,
//                mods.SingleSort,
//                mods.NestedSort,
				mods.VirtualVScroller
			],
			structure: dataSource.layouts[layoutIdx]
		}, args));
		g.placeAt(container);
		g.startup();
		return g;
	}

	create('grid', 'gridContainer', 100, 0, {});
});
