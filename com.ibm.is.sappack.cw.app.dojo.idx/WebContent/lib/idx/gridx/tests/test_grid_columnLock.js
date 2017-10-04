require([
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/ItemFileWriteStore',
	'idx/gridx/tests/support/modules',
	'idx/gridx/tests/support/TestPane',
	'dijit/form/NumberSpinner',
	'dojo/domReady!'
], function(Grid, Cache, dataSource, storeFactory, modules, TestPane){
	grid = new Grid({
		id: 'grid',
		cacheClass: Cache,
		store: storeFactory({
			dataSource: dataSource, 
			size: 100
		}),
		structure: dataSource.layouts[0],
		modules: [
			modules.VirtualVScroller,
			modules.ColumnLock,
//            modules.ExtendedSelectCell,
			modules.ExtendedSelectRow,
			modules.ExtendedSelectColumn,
			modules.CellWidget,
			modules.Edit,
//			modules.NestedSort,
			modules.ColumnResizer
		],
		selectRowTriggerOnCell: 1,
		columnLockCount: 1
	});
	grid.placeAt('gridContainer');
	grid.startup();

	//Test buttons
	var tp = new TestPane({});
	tp.placeAt('ctrlPane');

	tp.addTestSet('Lock/Unlock Columns', [
		'<label for="integerspinner">Columns to lock:</label><input id="integerspinner1" data-dojo-type="dijit.form.NumberSpinner" data-dojo-props="constraints:{max:10,min: 1},name:\'integerspinner1\', value: 1"/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: lockColumns">Lock Columns</div>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: unlockColumns">Unlock</div>'
	].join(''));

	tp.startup();
});

function lockColumns(){
	var c = dijit.byId('integerspinner1').get('value');
	grid.columnLock.lock(c);
}

function unlockColumns(){
	grid.columnLock.unlock();
}

