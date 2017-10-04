require([
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/ItemFileWriteStore',
	'idx/gridx/tests/support/TestPane',
	'idx/gridx/tests/support/modules',
	'idx/gridx/modules/Focus',
	'idx/gridx/modules/filter/Filter',
	'idx/gridx/modules/filter/FilterBar',
	'dojo/domReady!'
], function(Grid, Cache, dataSource, storeFactory, TestPane, mods, focus, filter, filterBar){

	grid = new Grid({
		id: 'grid',
		cacheClass: Cache,
		store: storeFactory({
			dataSource: dataSource, 
			size: 100
		}),
		selectRowTriggerOnCell: true,
		modules: [
			filter,
			mods.SingleSort,
			mods.VirtualVScroller,
			mods.Filter,
			{
				moduleClass: filterBar,
				maxRuleCount: 2,
				ruleCountToConfirmClearFilter: 2
			}
		],
		structure: dataSource.layouts[1]
	});
	grid.placeAt('gridContainer');
	grid.startup();

	//Test functions, must be global
	window.setStore = function(){
		grid.setStore(storeFactory({
			dataSource: dataSource,
			size: 50 + parseInt(Math.random() * 200, 10)
		}));
	};

	//Test buttons
	
	var tp = new TestPane({});
	tp.placeAt('ctrlPane');
	
	fb = grid.filterBar;
	applyFilter = function(){
		var filterData = {"type":"any","conditions":[{"colId":"id","condition":"greater","value":95,"type":"Number"},{"colId":"Artist","condition":"contain","value":"Jimi Hendrix","type":"Text"}]};
		fb.applyFilter(filterData);
	};
	clear = function(){
		fb.clearFilter();
	};
	clearWithoutConfirm = function(){
		fb.clearFilter(true);
	};
	showOrHide = function(){
		var isHiding = fb.domNode.style.display == "none";
		fb[isHiding ? "show" : "hide"]();
		this.set("label", isHiding ? "Hide fiter bar" : "Show filter bar");
	};
	showFilterDialog = function(){
		fb.showFilterDialog();
	};
	setIdentityFilterable = function(){
		var c = grid.column("id", true);
		c.setFilterable(!c.isFilterable());
		this.set("label", c.isFilterable() ? 'Set "Identity" column un-filterable' : 'Set "Identity" column filterable');
	};
	getConditions = function(){
		var c = grid.column("Download Date", true);
		alert(dojo.toJson(c.filterConditions()));
	};

	tp.addTestSet('Filter Actions', [
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: applyFilter">Apply a given filter</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: clear">Clear current filter</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: clearWithoutConfirm">Clear current filter without confirm</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: showOrHide">Hide filter bar</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: showFilterDialog">Show filter dialog</div><br/>',
	''].join(''));
	tp.addTestSet('Column Extended Actions', [
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: setIdentityFilterable">Set "Identity" column un-filterable</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: getConditions">Get the available conditions of column "Download Date"</div><br/>',
	''].join(''));
	tp.startup();

	
});
