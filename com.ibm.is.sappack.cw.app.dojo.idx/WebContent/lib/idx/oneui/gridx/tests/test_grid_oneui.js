require([
	'dojo/_base/lang',
	'dijit/form/Button',
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/ComputerData',
	'idx/gridx/tests/support/stores/Memory',
	'idx/oneui/gridx/tests/support/modules',
	'dojo/domReady!'
], function(lang, Button, Grid, Cache, dataSource, storeFactory, modules){

	function create(id, container, size, layout, mods, attrs){
		var grid = new Grid(lang.mixin({
			id: id,
			cacheClass: Cache,
			store: storeFactory({
				dataSource: dataSource,
				size: size 
			}),
			structure: dataSource.layouts[layout],
			modules: [
				modules.Focus,
				modules.Filter,
				modules.QuickFilter,
				modules.CellWidget,
				modules.ExtendedSelectRow,
				modules.Sort,
				modules.ColumnResizer,
				modules.VirtualVScroller
			].concat(mods),
			autoWidth: true,
			selectRowTriggerOnCell: true
		}, attrs));
		grid.placeAt(container);
		grid.startup();
		return grid;
	}

	var grid1 = create('grid1', 'grid1Container', 100, 0, [
		modules.ToolBar,
		modules.Pagination,
		modules.OneUIPaginationBar
	], {
		paginationBarGotoButton: true
	});
	grid1.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCut'
	}));
	grid1.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCopy'
	}));
	grid1.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonPaste'
	}));


	var grid2 = create('grid2', 'grid2Container', 100, 0, [
		modules.ToolBar,
		modules.Pagination,
		modules.PaginationBarDD
	], {
	});
	grid2.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCut'
	}));
	grid2.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCopy'
	}));
	grid2.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonPaste'
	}));


	var grid3 = create('grid3', 'grid3Container', 100, 0, [
		modules.ToolBar,
		modules.SummaryBar
	], {
	});
	grid3.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCut'
	}));
	grid3.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCopy'
	}));
	grid3.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonPaste'
	}));


	var grid4 = create('grid4', 'grid4Container', 100, 0, [
		modules.ToolBar,
		modules.Pagination,
		modules.OneUIPaginationBar
	], {
		paginationBarPosition: 'both',
		paginationBarGotoButton: false,
		paginationBarDescription: 'bottom',
		paginationBarSizeSwitch: 'top'
	});
	grid4.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCut'
	}));
	grid4.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCopy'
	}));
	grid4.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonPaste'
	}));


	var grid5 = create('grid5', 'grid5Container', 100, 0, [
		modules.ToolBar,
		modules.SummaryBar
	], {
		'class': 'gridxAlternatingRows'
	});
	grid5.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCut'
	}));
	grid5.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCopy'
	}));
	grid5.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonPaste'
	}));


	var grid6 = create('grid6', 'grid6Container', 100, 0, [
		modules.ToolBar
	], {
		'class': 'compact'
	});
	grid6.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCut'
	}));
	grid6.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonCopy'
	}));
	grid6.toolBar.widget.addChild(new Button({
		showLabel: false,
		iconClass: 'testToolbarButtonPaste'
	}));
});
