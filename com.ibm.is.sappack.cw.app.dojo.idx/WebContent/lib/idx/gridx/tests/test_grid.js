require([
	'dojo/_base/array',
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/Memory',
	'idx/gridx/tests/support/modules',
	'idx/gridx/tests/support/TestPane',
	'dojo/domReady!'
], function(array, Grid, Cache, dataSource, storeFactory, modules, TestPane){

	var columnSetIdx = 0;

	destroy = function(){
		if(window.grid){
			grid.destroy();
			window.grid = undefined;
		}
	};

	create = function(){
		if(!window.grid){
			var store = storeFactory({
				dataSource: dataSource, 
				size: 200
			}); 
			var layout = dataSource.layouts[columnSetIdx];
			var t1 = new Date().getTime();
			grid = new Grid({
				id: 'grid',
				cacheClass: Cache,
				//cacheSize: 0,
				store: store,
				structure: layout,
				modules:[
//                    modules.SingleSort,
//                    modules.ExtendedSelectRow,
//                    modules.DndRow,
//                    modules.FilterBar,
					modules.Focus,
					modules.RowHeader,
//                    modules.ColumnResizer,
					modules.VirtualVScroller
				],
				selectRowTriggerOnCell: true
			});
			var t2 = new Date().getTime();
			grid.placeAt('gridContainer');
			var t3 = new Date().getTime();
			grid.startup();
			var t4 = new Date().getTime();
			console.log('grid', t2 - t1, t3 - t2, t4 - t3, ' total:', t4 - t1);
		}
	};

	create();
	
	//Test Functions, must be global
	setStore = function(){
		grid.setStore(storeFactory({
			dataSource: dataSource,
			size: 50 + parseInt(Math.random() * 200, 10)
		}));
	};
	setColumns = function(){
		columnSetIdx = columnSetIdx == 4 ? 0 : 4;
		var columns = dataSource.layouts[columnSetIdx];
		grid.setColumns(columns);
	};
	var idcnt = 10000;
	newRow = function(){
		grid.store.add({
			id: idcnt++
		});
	};

	setRow = function(){
		var item = grid.row(0).item();
		item.Year = parseInt(Math.random() * 1000 + 1000, 10);
//        grid.store.put(item, 'Year', parseInt(Math.random() * 1000 + 1000, 10));
		grid.store.put(item);
	};

	deleteRow = function(){
		grid.store.remove(grid.row(0).id);
//        var item = grid.row(0).item();
//        grid.store.deleteItem(item);
	};

	//Test buttons
	var tp = new TestPane({});
	tp.placeAt('ctrlPane');

	tp.addTestSet('Tests', [
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: setColumns">Change column structure</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: setStore">Change store</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: newRow">And an empty new row</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: setRow">Set Year of the first row</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: deleteRow">Delete the first row</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: destroy">Destroy</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: create">Create</div><br/>'
	].join(''));

	tp.startup();
});
