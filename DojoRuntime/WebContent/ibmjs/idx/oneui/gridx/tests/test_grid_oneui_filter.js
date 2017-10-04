require([
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/ItemFileWriteStore',
	'idx/oneui/gridx/tests/support/modules',
	'dojo/domReady!'
], function(Grid, Cache, dataSource, storeFactory, modules){
	grid = new Grid({
		id: 'grid',
		cacheClass: Cache,
		store: storeFactory({
			dataSource: dataSource, 
			size: 100
		}),
		structure: dataSource.layouts[5],
		modules: [
			modules.Focus,
			modules.ExtendedSelectRow,
			modules.ToolBar,
			modules.Filter,
			modules.QuickFilter,
			modules.FilterBar,
			modules.ColumnResizer,
			modules.VirtualVScroller
		],
		paginationBarGotoButton: false,
		selectRowTriggerOnCell: true
	});
	grid.placeAt('gridContainer');
	grid.startup();
	
	grid.toolBar.widget.addChild(new dijit.form.Button({
		label: '',
		"iconClass": 'gridxIconNew',
		style: 'padding: 0;',
		title: 'New',
		onClick: function(){
			alert('New Button Clicked!');
		}
	}));
	
	grid.toolBar.widget.addChild(new dijit.form.Button({
		label: '',
		"iconClass": 'gridxIconEdit',
		style: 'padding: 0;',
		title: 'Edit',
		onClick: function(){
			alert('Edit Button Clicked!');
		}
	}));
	
	grid.toolBar.widget.addChild(new dijit.form.Button({
		label: '',
		"iconClass": 'gridxIconDelete',
		style: 'padding: 0;',
		title: 'Delete',
		onClick: function(){
			alert('Delete Button Clicked!');
		}
	}));
});



