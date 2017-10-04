require([
	'dojo/date/locale',
	'idx/gridx/Grid',
	'idx/gridx/core/model/cache/Async',
	'idx/gridx/tests/support/data/MusicData',
	'idx/gridx/tests/support/stores/ItemFileWriteStore',
	'idx/gridx/tests/support/modules',
	'idx/gridx/tests/support/TestPane',
	'dijit/form/ComboButton',
	'dijit/Menu',
	'dijit/MenuItem',
	'dijit/ProgressBar',
	'dijit/form/Button',
	'dijit/form/CheckBox',
	'dijit/form/DropDownButton',
	'dijit/TooltipDialog',
	'dojo/domReady!'
], function(locale, Grid, Cache, dataSource, storeFactory, modules, TestPane,ComboButton,Menu,MenuItem){

	var progressDecorator = function(){
		return [
			"<div data-dojo-type='dijit.ProgressBar' data-dojo-props='maximum: 1' class='idx/gridxHasGridCellValue' style='width: 100%;'></div>"
		].join('');
	};

	var artistDecorator = function(){
		return [
			'<button data-dojo-type="dijit.form.Button" data-dojo-attach-point="btn" onClick="alert(123)"></button>'
		].join('');
	};

	var artistSetCellValue = function(data){
		this.btn.set('label', data);
	};

	var albumDecorator = function(){
		return [
			'<span data-dojo-type="dijit.form.CheckBox" data-dojo-attach-point="cb"></span>',
			'<label data-dojo-attach-point="lbl"></label>'
		].join('');
	};
	
	

	var albumSetCellValue = function(data){
		this.lbl.innerHTML = data;
		this.cb.set('value', data.length % 2);
	};
	
	var comboButtonDecorator = function(){
		return [
			'<div data-dojo-type="dijit.form.ComboButton"',
        'data-dojo-props="',
            'optionsTitle:\'Save Options\',',
            'iconClass:\'dijitIconFile\',',
            'onClick:function(){ console.log(\'Clicked ComboButton\'); }">',
        '<span>Combo</span>',
        '<div  data-dojo-type="dijit.Menu">',
            '<div data-dojo-type="dijit.MenuItem"',
                'data-dojo-props="',
                    'iconClass:\'dijitEditorIcon dijitEditorIconSave\',',
                    'onClick:function(){ console.log(\'Save\'); }">',
                'Save',
            '</div>',
            '<div data-dojo-type="dijit.MenuItem"',
                'data-dojo-props="onClick:function(){ console.log(\'Save As\'); }">',
                'Save As',
            '</div></div> </div>'
        
   
			
		].join('');
	};
	
	

	var comboButtonSetCellValue = function(data){
	
		
		
	};
	
	
	var radioButtonDecorator = function(){
		return [
		'<input data-dojo-type="dijit.form.RadioButton" data-dojo-attach-point="rb"</input>',
        '<label data-dojo-attach-point="radioLabel"></label>'
		
		].join('');
	};
	
	var radioButtonSetCellValue = function(data){
		this.rb.value=data;
		this.radioLabel.innerHTML=data;
	};
	
	var comboBoxDecorator = function(){
		return [
		'<select name="stateSelect" data-dojo-type="dijit.form.ComboBox"',
        'data-dojo-props="value: \'\',placeHolder: \'Select a State\'">',
        '<option value="AL">Alabama</option>',
        '<option value="AK">Alaska</option>',
        '<option value="AZ">Arizona</option>',
        '<option value="AR">Arkansas</option>',
        '<option value="CA">California</option>',
		'</select>'
		
		
		].join('');
	
	};
	
	var dropDownButtonDecorator = function(){
		return [
		 '<div data-dojo-type="dijit.form.DropDownButton"',
        'data-dojo-props="iconClass:\'dijitIconApplication\'">',
        '<span>DropDown</span>',
        '<div data-dojo-type="dijit.TooltipDialog" data-dojo-attach-point="ttd">',
		'hihi',
        ' </div></div>'
	
		
		].join('');
	};
	
	var dropDownButtonSetCellValue = function(data){
		this.ttd.containerNode.innerHTML=data;
		this.ttd.set('value', data);
	};
	

	var structure = [
		{ field: "id", name:"Index", width: '50px',dataType:'number'},
		{ field: "Progress", name:"Progress", dataType:'number',
			widgetsInCell: true, 
			decorator: progressDecorator
		},
		{ field: "Artist", name:"Artist", 
			widgetsInCell: true,
			navigable:true,			
			decorator: artistDecorator,
			setCellValue: artistSetCellValue
		},
		{ field: "Album", name:"Album", 
			widgetsInCell: true,
			decorator: albumDecorator,
			setCellValue: albumSetCellValue
		},
		
		 { field: "Genre", name:"Genre", 
			widgetsInCell: true,
			decorator: radioButtonDecorator,
			setCellValue: radioButtonSetCellValue
		
		}, 
		{ field: "State", name:"State", 
			widgetsInCell: true, 
			navigable:true,
			decorator: comboBoxDecorator
		
		},
		{ field: "Name", name:"Name", 
			widgetsInCell: true, 
			navigable:true,
			decorator: comboButtonDecorator,
			setCellValue: comboButtonSetCellValue
		},
		{ field: "Composer", name:"Composer", 
			widgetsInCell: true, 
			navigable:true,
			decorator: dropDownButtonDecorator,
			setCellValue: dropDownButtonSetCellValue
		}
		
	];

	grid = new Grid({
		id: 'grid',
		cacheClass: Cache,
		store: storeFactory({
			dataSource: dataSource, 
			size: 1000
		}),
		structure: structure,
		modules: [
			modules.Focus,
			modules.CellWidget,
			modules.ColumnResizer,
			modules.Filter,
			modules.FilterBar,
			modules.VirtualVScroller
		]
	});
	grid.placeAt('gridContainer');
	grid.startup();


	//Test buttons
	/*var tp = new TestPane({});
	tp.placeAt('ctrlPane');

	tp.addTestSet('Core Functions', [
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: beginEdit2_3">Begin edit cell(2,3)</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: applyEdit2_3">Apply edit cell(2,3)</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: cancelEdit2_3">Cancel edit cell(2,3)</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: isEditing2_3">Is cell(2,3) editing</div><br/>',
		'<div data-dojo-type="dijit.form.Button" data-dojo-props="onClick: setEditor3">set the "Year" column\'s editor to a TextBox</div><br/>'
	].join(''));

	tp.startup();
	*/
});
