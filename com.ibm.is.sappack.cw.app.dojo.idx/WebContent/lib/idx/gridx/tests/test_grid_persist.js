require([
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Sync',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/ItemFileWriteStore',
	'idx/gridx/tests/support/modules',
	'idx/gridx/tests/support/TestPane',
	'dojo/domReady!'
], function(Grid, Cache, dataSource, storeFactory, modules, TestPane){

	store = storeFactory({
		dataSource: dataSource, 
		size: 100
	});

	createGrid = function(){
		if(!window.grid){
			grid = new Grid({
				id: 'grid',
				cacheClass: Cache,
				store: store,
				structure: dataSource.layouts[4],
				modules: [
					modules.Persist,
					modules.SelectColumn,
					modules.MoveColumn,
					modules.DndColumn,
					modules.NestedSort
				]
			});
			grid.placeAt('gridContainer');
			grid.startup();
		}
	};
	createGrid();

	destroyGrid = function(){
		if(window.grid){
			grid.destroy();
			window.grid = null;
		}
	};

	enablePersist = function(){
		if(window.grid){
			grid.persist.enabled = true;
		}
	};

	disablePersist = function(){
		if(window.grid){
			grid.persist.enabled = false;
		}
	};

	//Test buttons
	var tp = new TestPane({});
	tp.placeAt('ctrlPane');

	tp.addTestSet('Pesistent Actions', [
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: createGrid">Create Grid</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: destroyGrid">Destroy Grid</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: enablePersist">Enable Persist</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: disablePersist">Disable Persist</div><br/>',
	''].join(''));

	tp.startup();
});
