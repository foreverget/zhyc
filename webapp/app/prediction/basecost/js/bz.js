/**
 * 
 * 包装费
 */
Ext.onReady(function() {
	var qForm = new Ext.form.FormPanel({
		region : 'north',
		margins : '3 3 3 3',
		title : '<span class="commoncss">条件<span>',
		collapsible : true,
		border : true,
		labelWidth : 50, // 标签宽度
		// frame : true, //是否渲染表单面板背景色
		labelAlign : 'right', // 标签对齐方式
		bodyStyle : 'padding:3 5 0', // 表单元素和表单面板的边距
		buttonAlign : 'left',
		height : 100,
		items : [{
			layout : 'column',
			border : false,
			items : [{
						columnWidth : .2,
						layout : 'form',
						labelWidth : 60, // 标签宽度
						defaultType : 'datefield',
						
						border : false,
						items : [{
									fieldLabel : '预测月份', // 标签
									id : 'predictionMonth',
									name : 'predictionMonth', // name:后台根据此name属性取值
									format:'Y-m', //日期格式化
									plugins:'monthPickerPlugin',
									editable : false,
									anchor : '100%' // 宽度百分比
								}]
					}]
		}],
		buttons : [{
					text : '查询',
					iconCls : 'previewIcon',
					handler : function() {
						queryData();
					}
				}]
	});

	

	// 复选框
	var sm = new Ext.grid.CheckboxSelectionModel();

	// 定义自动当前页行号
	var rownum = new Ext.grid.RowNumberer({
				header : '序号',
				width : 35
			});

	// 定义列模型
	var cm = new Ext.grid.ColumnModel([rownum, sm,{
			header : 'ID', // 列标题
			dataIndex : 'id', // 数据索引:和Store模型对应
			sortable : true,// 是否可排序
			hidden : true,
			width : 120
		},{
			header : '预测月份',
			dataIndex : 'prediction_month',
			hidden : false, // 隐藏列
			sortable : true,
			width : 80
		}, {
			header : '工序编码',
			dataIndex : 'pl_code',
			hidden : true,
			width : 80,
			sortable : true
		}, {
			header : '工序',
			dataIndex : 'pl_desc',
			width : 300
		}, {
			header : '费用',
			dataIndex : 'bz_cost',
			width : 120
		},{
			header : '创建人',
			dataIndex : 'create_by',
			hidden : true,
			width : 100
		}, {
			header : '创建时间',
			dataIndex : 'create_dt',
			hidden : true,
			width : 100
		}, {
			header : '更新人',
			dataIndex : 'update_by',
			hidden : true,
			width : 100
		}, {
			header : '更新时间',
			dataIndex : 'update_dt',
			hidden : true,
			width : 100
		}]);

	/**
	 * 数据存储
	 */
	var store = new Ext.data.Store({
				// 获取数据的方式
				proxy : new Ext.data.HttpProxy({
							url : 'basecost.do?reqCode=queryBzData'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'TOTALCOUNT', // 记录总数
							root : 'ROOT' // Json中的列表数据根节点
						}, [ 	{
									name : 'id'
								}, {
									name : 'prediction_month'
								}, {
									name : 'pl_code'
								}, {
									name : 'pl_desc'
								}, {
									name : 'bz_cost'
								},{
									name : 'create_by'
								}, {
									name : 'create_dt'
								}, {
									name : 'update_by'
								}, {
									name : 'update_dt'
								}])
			});
	


	var bz_file_form = new Ext.form.FormPanel({
		id : 'id_bz_file_form',
		name : 'id_bz_file_form',
		defaultType : 'textfield',
		labelAlign : 'right',
		labelWidth : 99,
		frame : true,
		fileUpload : true,
		items : [{
					fieldLabel : '请选择导入文件',
					name : 'file1',
					id : 'file1',
					inputType : 'file',
					allowBlank : true,
					anchor : '99%'
				}, {
					fieldLabel : '预测月份',
					id : 'importBzMonth',
					name : 'prediction_month',
					readOnly: true,
					value : '',
					anchor : '100%' 
				}]
	});

	var bz_excel_window = new Ext.Window({
		layout : 'fit',
		width : 380,
		height : 300,
		resizable : false,
		draggable : true,
		closeAction : 'hide',
		title : '导入Excel',
		modal : false,
		collapsible : true,
		titleCollapse : true,
		maximizable : false,
		buttonAlign : 'right',
		border : false,
		animCollapse : true,
		animateTarget : Ext.getBody(),
		constrain : true,
		items : [bz_file_form],
		buttons : [{
					text : '导入',
					iconCls : 'acceptIcon',
					handler : function() {
						var theFile = Ext.getCmp('file1').getValue();
						if (Ext.isEmpty(theFile)) {
							Ext.Msg.alert('提示', '请先选择您要导入的xls文件...');
							return;
						}
						if (theFile.substring(theFile.length - 4, theFile.length) != ".xls") {
							Ext.Msg.alert('提示', '您选择的文件格式不对,只能导入.xls文件!');
							return;
						}

						if (runMode == '0') {
							Ext.Msg.alert('提示','系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
							return;
						}
						bz_file_form.form.submit({
									url : 'basecost.do?reqCode=importBzExcel',
									waitTitle : '提示',
									method : 'POST',
									waitMsg : '正在处理数据,请稍候...',
									success : function(form, action) {
										var predictionMonth = bz_file_form.get('importBzMonth').getValue();
										var msg = action.result.msg;
										//Ext.MessageBox.alert('提示', msg);
										store.load({
											params : {
												predictionMonth :predictionMonth
											}
										});
										bz_excel_window.hide();
						
									},
									failure : function(form, action) {
										var msg = action.result.msg;
										Ext.MessageBox.alert('提示', '数据导入失败:<br>' + msg);
									}
								});
		
					}
				}, {
					text : '关闭',
					id : 'bz_btnReset',
					iconCls : 'deleteIcon',
					handler : function() {
						bz_excel_window.hide();
					}
				}]
	});

	/**
	 * 翻页排序时候的参数传递
	 */
	// 翻页排序时带上查询条件
	store.on('beforeload', function() {
		this.baseParams = qForm.getForm().getValues();
	});
	// 每页显示条数下拉选择框
	var pagesize_combo = new Ext.form.ComboBox({
				name : 'pagesize',
				triggerAction : 'all',
				mode : 'local',
				store : new Ext.data.ArrayStore({
							fields : ['value', 'text'],
							data : [[10, '10条/页'], [20, '20条/页'],
									[50, '50条/页'], [100, '100条/页'],
									[250, '250条/页'], [500, '500条/页']]
						}),
				valueField : 'value',
				displayField : 'text',
				value : '20',
				editable : false,
				width : 85
			});
	var number = parseInt(pagesize_combo.getValue());
	// 改变每页显示条数reload数据
	pagesize_combo.on("select", function(comboBox) {
				bbar.pageSize = parseInt(comboBox.getValue());
				number = parseInt(comboBox.getValue());
				store.reload({
							params : {
								start : 0,
								limit : bbar.pageSize
							}
						});
			});

	// 分页工具栏
	var bbar = new Ext.PagingToolbar({
				pageSize : number,
				store : store,
				displayInfo : true,
				displayMsg : '显示{0}条到{1}条,共{2}条',
				plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
				emptyMsg : "没有符合条件的记录",
				items : ['-', '&nbsp;&nbsp;', pagesize_combo]
			});

	// 表格工具栏
	var tbar = new Ext.Toolbar({
				items : [{
							text : '导入',
							id:'importBzData',
							iconCls : 'uploadIcon',
							handler : function() {
								// 判断预测月份是否为空
								var m = Ext.getCmp('predictionMonth').getValue();
								
								if(m==''){
									Ext.Msg.alert('提示:', '请先选择预测月份');
									return;
								}
								var pm = Ext.util.Format.date(m, 'Y-m');

								Ext.Msg.confirm(
										'请确认',
										'确认要导入预测月份为<span style="color:red"><b>'+pm+'</b></span>月份的包装费用数据吗？<br>',
										function(btn, text) {
											if (btn == 'yes') {
												bz_file_form.get('importBzMonth').setValue(pm);
												bz_excel_window.show();
											}
										});
							}
						}]
			});

	// 表格实例
	var grid = new Ext.grid.GridPanel({
				// 表格面板标题,默认为粗体，我不喜欢粗体，这里设置样式将其格式为正常字体
				title : '<span class="commoncss">包装费用数据 </span>',
				height : 500,
				id : 'id_grid_bz',
				autoScroll : true,
				frame : true,
				region : 'center', // 和VIEWPORT布局模型对应，充当center区域布局
				margins : '3 3 3 3',
				store : store, // 数据存储
				stripeRows : true, // 斑马线
				cm : cm, // 列模型
				tbar : tbar, // 表格工具栏
				bbar : bbar,// 分页工具栏
				viewConfig : {
				// 不产横向生滚动条, 各列自动扩展自动压缩, 适用于列数比较少的情况
				// forceFit : true
				},
				loadMask : {
					msg : '正在加载表格数据,请稍等...'
				}
			});

	// 监听行选中事件
	grid.on('rowclick', function(pGrid, rowIndex, event) {

	});

	// 布局
	// 如果把form作为center区域的话,其Height属性将失效。
	var viewport = new Ext.Viewport({
				layout : 'border',
				items : [qForm, grid]
			});

	/**
	 * 查询数据列表
	 */
	function queryData() {
		var params = qForm.getForm().getValues();
		params.start = 0;
		params.limit = bbar.pageSize;
		store.load({params : params});
	}

	function dateFormat(value){ 
	    if(null != value){ 
	        return Ext.Date.format(new Date(value),'Y-m'); 
	    }else{ 
	        return null; 
	    } 
	} 

	/**
	 * 修改
	 */
	function updateRecord() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示:', '请先选中项目');
			return;
		}
		Ext.Msg.alert('提示:', '修改');
	}

	/**
	 * 删除
	 */
	function deleteRecord() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示:', '请先选中项目');
			return;
		}
		Ext.MessageBox.confirm('请确认', '确认删除吗?', function(btn, text) {
					if (btn == 'yes') {
						if (runMode == '0') {
							Ext.Msg.alert('提示',
									'系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
							return;
						}
						Ext.MessageBox.alert('提示', '删除');
					}
				})
	}



});