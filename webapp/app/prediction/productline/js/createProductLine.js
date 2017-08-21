/**
 * 
 * 生成产线
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
						defaultType : 'textfield',
						border : false,
						items : [new Ext.form.ComboBox({
									hiddenName : 'plCode',
									fieldLabel : '产线',
									emptyText : '',
									triggerAction : 'all',
									store : new Ext.data.SimpleStore({
												fields : ['name',
														'code'],
												data : [['--查询时请选择--', ''],
												        ['酸洗', 'SX'],
														['单机架', 'ZJ'],
														['彩涂', 'CT'],
														['1#镀锌', 'DA'],
														['2#镀锌', 'DB']
													   ]
											}),
									displayField : 'name',
									valueField : 'code',
									mode : 'local',
									forceSelection : false, // 选中内容必须为下拉列表的子项
									editable : false,
									typeAhead : true,
									value:'',
									resizable : true,
									anchor : '100%'
								})]
					},{
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
											listeners: {
												// 添加日期选择事件
												"select": function () {
													var m = Ext.getCmp('predictionMonth').getValue();
													var pm = Ext.util.Format.date(m, 'Y-m');
													//alert("你选择了日期~!"+pm);
													var id_btn_print_text = '生成<span style="color:red"><b>'+pm+'</b></span>所有产线';
													var id_btn_imp_text = '导入<span style="color:red"><b>'+pm+'</b></span>所有产线数据';
													Ext.getCmp('id_btn_print').setText(id_btn_print_text);
													Ext.getCmp('id_btn_imp').setText(id_btn_imp_text);
													
													var importProductLineData_text = '导入产线数据（按产线导入）';
													Ext.getCmp('importProductLineData').setText(importProductLineData_text);
													var importProductData_text = '导入产品及主投料数据';
													Ext.getCmp('importProductData').setText(importProductData_text);
													var importInputData_text = '导入其他消耗数据';
													Ext.getCmp('importInputData').setText(importInputData_text);
													queryData();
												}
											},
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
						}, {
							text : '生成所有产线',
							id : 'id_btn_print',
							iconCls : 'database_refreshIcon',
							tooltip: "生成选择预测月份的所有产线",
							handler : function() {
								createProductLineData();
							}
						}, {
							text : '导入所有产线数据',
							id : 'id_btn_imp',
							iconCls : 'uploadIcon',
							tooltip: "可分别导入选择预测月份的<br>产品主投料数据和其他消耗数据",
							handler : function() {
								impProductLineAllData();
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
		hidden : true, // 隐藏列
		width : 120
		},{
		header : '产线预测编号', // 列标题
		dataIndex : 'pl_prediction_code', // 数据索引:和Store模型对应
		sortable : true,// 是否可排序
		width : 150
		}, {
		header : '预测月份',
		dataIndex : 'prediction_month',
		hidden : false, // 隐藏列
		sortable : true,
		width : 150
			// 列宽
		}, {
		header : '产线编码',
		dataIndex : 'pl_code',
		width : 80,
		sortable : true
	}, {
		header : '产线描述',
		dataIndex : 'pl_desc',
		width : 200
	}, 
	
//	{
//		header : '数据导入',
//		dataIndex : 'is_imp',
//		width : 100
//	},  
	
	{
		header : '总产量',
		dataIndex : 'pl_amount',
		width : 100
	},  {
		header : '商品量',
		dataIndex : 'goods_amount',
		width : 100
	}, {
		header : '主投料费用',
		dataIndex : 'main_input_cost',
		hidden : true, 
		width : 100
	},{
		header : '消耗费用',
		dataIndex : 'item_input_cost',
		hidden : true,
		width : 100
	},
	{
		header : '总销售费用',
		dataIndex : 'sale_cost',
		hidden : true,
		width : 100
	},  
	
	{
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
							url : 'productline.do?reqCode=queryProductLineData'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'TOTALCOUNT', // 记录总数
							root : 'ROOT' // Json中的列表数据根节点
						}, [ 	{
									name : 'id'
								}, {
									name : 'pl_prediction_code'
								}, {
									name : 'prediction_month'
								}, {
									name : 'pl_code'
								}, {
									name : 'pl_desc'
								}, {
									name : 'is_imp'
								},  {
									name : 'pl_amount'
								},  {
									name : 'goods_amount'
								},  {
									name : 'main_input_cost'
								},  {
									name : 'item_input_cost'
								},  {
									name : 'sale_cost'
								}, {
									name : 'create_by'
								}, {
									name : 'create_dt'
								}, {
									name : 'update_by'
								}, {
									name : 'update_dt'
								}])
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
							text : '导入产线数据（按产线导入）',
							id:'importProductLineData',
							iconCls : 'uploadIcon',
							handler : function() {
								//Ext.Msg.alert('提示:', '弹出导入数据界面');
								// 弹出导入数据窗体
								doImportProductLineData();
							}
						}]
			});

	// 表格实例
	var grid = new Ext.grid.GridPanel({
				// 表格面板标题,默认为粗体，我不喜欢粗体，这里设置样式将其格式为正常字体
				title : '<span class="commoncss">产线数据 </span>',
				height : 500,
				id : 'id_grid_product_line',
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
		//Ext.Msg.alert('提示:', '单击行监听事件');
		var record = grid.getSelectionModel().getSelected();
		var importProductLineData_text = '导入<span style="color:red"><b>'+record.data.prediction_month+'月'+record.data.pl_desc+'</b></span>产线数据';
		Ext.getCmp('importProductLineData').setText(importProductLineData_text);
		var importProductData_text = '导入<span style="color:red"><b>'+record.data.prediction_month+'月'+record.data.pl_desc+'产品及主投料</b></span>数据';
		Ext.getCmp('importProductData').setText(importProductData_text);
		var importInputData_text = '导入<span style="color:red"><b>'+record.data.prediction_month+'月'+record.data.pl_desc+'消耗</b></span>数据';
		Ext.getCmp('importInputData').setText(importInputData_text);
		
		//Ext.getCmp('product_excel_window_impbtn').setText("导入（"+record.data.prediction_month+'月'+record.data.pl_desc+'产品及主投料数据');
		
		
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
	 * 生成产线
	 */
	function createProductLineData() {
		// 判断预测月份是否为空
		var m = Ext.getCmp('predictionMonth').getValue();
		
		if(m==''){
			Ext.Msg.alert('提示:', '请先选择预测月份');
			return;
		}
		var pm = Ext.util.Format.date(m, 'Y-m');
		//Ext.Msg.alert('提示:', pm);
		//return;
		Ext.Msg.confirm(
				'请确认',
				'确认要生成预测月份为<span style="color:red"><b>'+pm+'</b></span>月份的产线数据吗？<br>',
				function(btn, text) {
					if (btn == 'yes') {
						if (runMode == '0') {
							Ext.Msg.alert('提示','系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
							return;
						}
						showWaitMsg();
						Ext.Ajax.request({
							url : './productline.do?reqCode=createProductLineData',
							params : {
								predictionMonth : pm
							},
							success : function(response) {
								var resultArray = Ext.util.JSON.decode(response.responseText);
								
								store.reload({
									params : {
										predictionMonth :pm
									}
								});
								Ext.Msg.alert('提示', resultArray.msg);
							},
							failure : function(response) {
								hideWaitMsg();
								var resultArray = Ext.util.JSON.decode(response.responseText);
								Ext.Msg.alert('提示', resultArray.msg);
							}
						});
					}
				});
	}

	

	/**
	 * 全量导入
	 */
	function impProductLineAllData() {
		// 判断预测月份是否为空
		var m = Ext.getCmp('predictionMonth').getValue();
		
		if(m==''){
			Ext.Msg.alert('提示:', '请先选择预测月份');
			return;
		}
		var pm = Ext.util.Format.date(m, 'Y-m');
		//Ext.Msg.alert('提示:', pm);
		//return;
//		Ext.Msg.confirm(
//				'请确认',
//				'确认要导入预测月份为<span style="color:red"><b>'+pm+'</b></span>月份的产品及消耗数据吗？<br>',
//				function(btn, text) {
//					if (btn == 'yes') {
						if (runMode == '0') {
							Ext.Msg.alert('提示','系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
							return;
						}
						// 弹出导入窗口
						pl_file_form.get('pl_importInputMonth').setValue(pm);
						pl_excel_window.show();
//					}
//				});
	}	
	

	/**
	 * 修改项目
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

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	var select_record = grid.getSelectionModel().getSelected();
		
	/**
	 * 导入产品-主投料数据
	 * 
	 */
	// 复选框
	var product_sm = new Ext.grid.CheckboxSelectionModel();

	// 定义自动当前页行号
	var product_rownum = new Ext.grid.RowNumberer({
				header : '序号',
				width : 35
			});

	// 定义列模型
	var product_cm = new Ext.grid.ColumnModel([product_rownum, {
			header : 'ID', // 列标题
			dataIndex : 'id', // 数据索引:和Store模型对应
			sortable : true,// 是否可排序
			hidden : true, // 隐藏列
			width : 120
		},{
			header : '预测编号', 
			dataIndex : 'pl_prediction_code', 
			sortable : true,
			hidden : true,
			width : 120
		}, {
			header : '预测月份', 
			dataIndex : 'prediction_month',
			sortable : true,
			hidden : true,
			width : 120
		}, 
		{
			header : '产成品标识',
			dataIndex : 'output_type_desc',
			hidden : false,
			sortable : true,
			width : 80
		}, {
			header : '成品物料编码',
			dataIndex : 'output_mat_code',
			width : 80,
			sortable : true
		}, {
			header : '成品物料描述',
			dataIndex : 'output_mat_desc',
			width : 120
		}, {
			header : '成品产量',
			dataIndex : 'output_amount',
			width : 100
		}, {
			header : '产品单位',
			dataIndex : 'output_unit',
			width : 70
		},{
			header : '销售价格',
			dataIndex : 'output_sale_price',
			width : 100
		},{
			header : '原料物料编码',
			dataIndex : 'feed_mat_code',
			width : 100
		},{
			header : '原料物料描述',
			dataIndex : 'feed_mat_desc',
			width : 100
		},{
			header : '原料消耗量',
			dataIndex : 'feed_amount',
			width : 100
		},{
			header : '原料单位',
			dataIndex : 'feed_unit',
			width : 70
		},{
			header : '原料价格',
			dataIndex : 'feed_sale_price',
			width : 100
		}
		, {
			header : '创建人',
			dataIndex : 'create_by',
			hidden : true, // 隐藏列
			width : 100
		}, {
			header : '创建时间',
			dataIndex : 'create_dt',
			hidden : true, // 隐藏列
			width : 100
		}, {
			header : '更新人',
			dataIndex : 'update_by',
			hidden : true, // 隐藏列
			width : 100
		}, {
			header : '更新时间',
			dataIndex : 'update_dt',
			hidden : true, // 隐藏列
			width : 160
		}
		
		]);
	
	/**
	 * 数据存储
	 */
	var product_store = new Ext.data.Store({
				// 获取数据的方式
				proxy : new Ext.data.HttpProxy({
							url : 'productline.do?reqCode=queryProductData'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'TOTALCOUNT', // 记录总数
							root : 'ROOT' // Json中的列表数据根节点
						}, [ 	{
									name : 'id'
								}, {
									name : 'pl_prediction_code'
								}, {
									name : 'prediction_month'
								}, {
									name : 'output_type_desc'
								}, {
									name : 'output_mat_code'
								}, {
									name : 'output_mat_desc'
								}, {
									name : 'output_amount'
								}, {
									name : 'output_unit'
								}, {
									name : 'output_sale_price'
								}, {
									name : 'feed_mat_code'
								}, {
									name : 'feed_mat_desc'
								}, {
									name : 'feed_amount'
								}, {
									name : 'feed_unit'
								}, {
									name : 'feed_sale_price'
								}, 
								
								{
									name : 'create_by'
								}, {
									name : 'create_dt'
								}, {
									name : 'update_by'
								}, {
									name : 'update_dt'
								}])
			});


	product_store.on('beforeload', function() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示:', '请先选中具体产线！');
			return;
		}
//		//Ext.Msg.alert('提示:',record.data.pl_prediction_code+","+record.data.prediction_month+","+record.data.pl_code);
//		this.baseParams.plPredictionCode =record.data.pl_prediction_code;
//		this.baseParams.predictionmMonth=record.data.prediction_month;
//		this.baseParams.plCode=record.data.pl_code;
		//alert(this.baseParams.plPredictionCode);
	});
	// 每页显示条数下拉选择框
	var product_pagesize_combo = new Ext.form.ComboBox({
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
	var product_number = parseInt(product_pagesize_combo.getValue());
	// 改变每页显示条数reload数据
	product_pagesize_combo.on("select", function(comboBox) {
				product_bbar.pageSize = parseInt(comboBox.getValue());
				product_number = parseInt(comboBox.getValue());
				product_store.reload({
							params : {
								start : 0,
								limit : product_bbar.pageSize
							}
						});
			});

	// 分页工具栏
	var product_bbar = new Ext.PagingToolbar({
				pageSize : product_number,
				store : product_store,
				displayInfo : true,
				displayMsg : '显示{0}条到{1}条,共{2}条',
				plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
				emptyMsg : "没有符合条件的记录",
				items : ['-', '&nbsp;&nbsp;', product_pagesize_combo]
			});

	var product_file_form = new Ext.form.FormPanel({
		id : 'id_product_file_form',
		name : 'id_product_file_form',
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
					fieldLabel : '', 
					id : 'importPlCode',
					name : 'pl_code', 
					readOnly: true,
					hidden : true,
					value : '',
					anchor : '100%'
				},{
					fieldLabel : '预测月份',
					id : 'importProductMonth',
					name : 'prediction_month',
					readOnly: true,
					value : '',
					anchor : '100%' 
				},{
					fieldLabel : '产线', 
					id : 'importPlDesc',
					name : 'pl_desc', 
					readOnly: true,
					value : '',
					anchor : '100%'
				},{
					fieldLabel : '',
					id : 'importPlPredictionCode',
					name : 'pl_prediction_code',
					readOnly: true,
					hidden : true,
					value : '',
					anchor : '100%'
				}]
	});

	var product_excel_window = new Ext.Window({
		layout : 'fit',
		id:'id_product_excel_window',
		width : 380,
		height : 300,
		resizable : false,
		draggable : true,
		closeAction : 'hide',
		//title : '<span style="color:red">导入产品及主投料数据Excel</span>',
		modal : false,
		collapsible : true,
		titleCollapse : true,
		maximizable : false,
		buttonAlign : 'right',
		border : false,
		animCollapse : true,
		animateTarget : Ext.getBody(),
		constrain : true,
		items : [product_file_form],
		buttons : [{
					text : '导入（产品及主投料数据）',
					id:'product_excel_window_impbtn',
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
						product_file_form.form.submit({
									url : 'productline.do?reqCode=importProductExcel',
									waitTitle : '提示',
									method : 'POST',
									waitMsg : '正在处理数据,请稍候...',
									success : function(form, action) {
										var msg = action.result.msg;
										Ext.MessageBox.alert('提示', msg);
										var pl_prediction_code = product_file_form.get('importPlPredictionCode').getValue();
										product_store.load({
											params : {
												plPredictionCode :pl_prediction_code
											}
										});
										product_excel_window.hide();
						
									},
									failure : function(form, action) {
										var msg = action.result.msg;
										Ext.MessageBox.alert('提示', '数据导入失败:<br>' + msg);
									}
								});
		
					}
				}, {
					text : '关闭',
					id : 'product_btnReset',
					iconCls : 'deleteIcon',
					handler : function() {
						product_excel_window.hide();
					}
				}]
	});
	var product_tbar = new Ext.Toolbar({
				items : [
				         '-',
				         {
							text : '导入产品',
							id:'importProductData',
							iconCls : 'page_excelIcon',
							handler : function() {
								// Ext.Msg.alert('提示:', '弹出导入EXCEL数据界面');
								// 弹出导入数据窗体
								
								var record = grid.getSelectionModel().getSelected();
								if (Ext.isEmpty(record)) {
									Ext.Msg.alert('提示:', '请先选中具体产线！');
									return;
								}
								//Ext.Msg.alert('提示:',record.data.pl_prediction_code+","+record.data.prediction_month+","+record.data.pl_code);
								product_file_form.get('importProductMonth').setValue(record.data.prediction_month);
								product_file_form.get('importPlCode').setValue(record.data.pl_code);
								product_file_form.get('importPlDesc').setValue(record.data.pl_desc);
								product_file_form.get('importPlPredictionCode').setValue(record.data.pl_prediction_code);
								product_excel_window.show();
							}
						}]
			});
	// 表格实例
	var product_grid = new Ext.grid.GridPanel({
				// 表格面板标题,
				title : '<span class="commoncss">产品及主料数据</span>',
//				width : '100%',
				height : 200,
				id : 'id_product_grid',
				autoScroll : true,
				frame : true,
				region : 'center', // 和VIEWPORT布局模型对应，充当center区域布局
				margins : '3 3 3 3',
				store : product_store, // 数据存储
				stripeRows : true, // 斑马线
				cm : product_cm, // 列模型
				tbar:product_tbar,
				//bbar : product_bbar,// 分页工具栏
				viewConfig : {
					// 不产横向生滚动条, 各列自动扩展自动压缩, 适用于列数比较少的情况
					//forceFit : true
				},
				loadMask : {
					msg : '正在加载表格数据,请稍等...'
				}
			});
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 导入其他消耗数据
	 * 
	 */

	// 复选框
	var input_sm = new Ext.grid.CheckboxSelectionModel();

	// 定义自动当前页行号
	var input_rownum = new Ext.grid.RowNumberer({
				header : '序号',
				width : 35
			});

	// 定义列模型
	var input_cm = new Ext.grid.ColumnModel([input_rownum, {
			header : 'ID', 
			dataIndex : 'id', 
			sortable : true,
			hidden : true,
			width : 120
		},{
			header : '产线预测编号', 
			dataIndex : 'pl_prediction_code', 
			sortable : true,
			hidden : true, 
			width : 120
		}, {
			header : '预测月份',
			dataIndex : 'prediction_month',
			hidden : true,
			sortable : true,
			width : 80
		},  {
			header : '产线编码',
			dataIndex : 'pl_code',
			hidden : true,
			sortable : true,
			width : 80
		}, {
			header : '消耗类型标识',
			dataIndex : 'input_type_code',
			hidden : true,
			width : 80,
			sortable : true
		}, {
			header : '消耗类型',
			dataIndex : 'input_type_desc',
			width : 150
		}, {
			header : '物料编码',
			dataIndex : 'mat_code',
			width : 150
		},   
		{
			header : '物料描述',
			dataIndex : 'mat_desc',
			width : 250
		}, {
			header : '消耗量',
			dataIndex : 'amount',
			width : 130
		}, {
			header : '单位',
			dataIndex : 'unit',
			width : 130
		},  {
			header : '消耗单价',
			dataIndex : 'price',
			width : 130
		}, {
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
			width : 160
		}]);

	/**
	 * 数据存储
	 */
	var input_store = new Ext.data.Store({
				// 获取数据的方式
				proxy : new Ext.data.HttpProxy({
							url : 'productline.do?reqCode=queryInputData'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'TOTALCOUNT', // 记录总数
							root : 'ROOT' // Json中的列表数据根节点
						}, [ 	{
									name : 'id'
								}, {
									name : 'pl_prediction_code'
								}, {
									name : 'prediction_month'
								},  {
									name : 'pl_code'
								}, {
									name : 'input_type_code'
								}, {
									name : 'input_type_desc'
								}, {
									name : 'mat_code'
								}, {
									name : 'mat_desc'
								}, {
									name : 'amount'
								},  {
									name : 'unit'
								}, {
									name : 'price'
								}, {
									name : 'create_by'
								}, {
									name : 'create_dt'
								}, {
									name : 'update_by'
								}, {
									name : 'update_dt'
								}])
			});


	input_store.on('beforeload', function() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示:', '请先选中具体产线！');
			return;
		}
//		//Ext.Msg.alert('提示:',record.data.pl_prediction_code+","+record.data.prediction_month+","+record.data.pl_code);
		this.baseParams.plPredictionCode =record.data.pl_prediction_code;
		this.baseParams.predictionmMonth=record.data.prediction_month;
		this.baseParams.plCode=record.data.pl_code;
	});
	// 每页显示条数下拉选择框
	var input_pagesize_combo = new Ext.form.ComboBox({
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
	var input_number = parseInt(input_pagesize_combo.getValue());
	// 改变每页显示条数reload数据
	input_pagesize_combo.on("select", function(comboBox) {
				input_bbar.pageSize = parseInt(comboBox.getValue());
				input_number = parseInt(comboBox.getValue());
				input_store.reload({
							params : {
								start : 0,
								limit : input_bbar.pageSize
							}
						});
			});

	// 分页工具栏
	var input_bbar = new Ext.PagingToolbar({
				pageSize : input_number,
				store : input_store,
				displayInfo : true,
				displayMsg : '显示{0}条到{1}条,共{2}条',
				plugins : new Ext.ux.ProgressBarPager(), // 分页进度条
				emptyMsg : "没有符合条件的记录",
				items : ['-', '&nbsp;&nbsp;', input_pagesize_combo]
			});
	
	var input_mat_type = new Ext.form.ComboBox({
		hiddenName : 'input_type',
		fieldLabel : '消耗物料种类',
		emptyText : '可选择消耗类型进行分类查询',
		triggerAction : 'all',
		store : new Ext.data.SimpleStore({
					fields : ['name','code'],
					data : [['物料回收－原料', 'HS'],
					        ['物料消耗－原料', 'YL'],
							['辅材', 'FC'],
							['燃料', 'RL'],
							['动力', 'DL'],
							['制造费', 'ZZ']
						   ]
		}),
		listeners : {
			'select' : function(combo, record, opts) {
				//Ext.Msg.alert('提示:', '会刷新消耗数据,功能待完善！');
				//alert(combo.getValue());
				input_store.load({
					params : {
						inputTypeCode :combo.getValue(),
					    plPredictionCode : record.data.pl_prediction_code
					}
				});
			}
		},
		displayField : 'name',
		valueField : 'code',
		mode : 'local',
		forceSelection : false, // 选中内容必须为下拉列表的子项
		editable : false,
		typeAhead : true,
		// value:'0002',
		resizable : true,
		anchor : '100%'
	})

	var input_file_form = new Ext.form.FormPanel({
				id : 'id_input_file',
				name : 'id_input_file',
				defaultType : 'textfield',
				labelAlign : 'right',
				labelWidth : 99,
				frame : true,
				fileUpload : true,
				items : [{
							fieldLabel : '请选择导入文件',
							name : 'file2',
							id : 'file2',
							inputType : 'file',
							allowBlank : true,
							anchor : '99%'
						}, {
							fieldLabel : '', 
							id : 'importInputPlCode',
							name : 'pl_code', 
							readOnly: true,
							hidden : true,
							value : '',
							anchor : '100%'
						},{
							fieldLabel : '预测月份',
							id : 'importInputMonth',
							name : 'prediction_month',
							readOnly: true,
							value : '',
							anchor : '100%' 
						},{
							fieldLabel : '产线', 
							id : 'importInputPlDesc',
							name : 'pl_desc', 
							readOnly: true,
							value : '',
							anchor : '100%'
						},{
							fieldLabel : '',
							id : 'importInputPlPredictionCode',
							name : 'pl_prediction_code',
							readOnly: true,
							hidden : true,
							value : '',
							anchor : '100%'
						}]
			});

	
	var input_excel_window = new Ext.Window({
		layout : 'fit',
		width : 380,
		height : 300,
		resizable : false,
		draggable : true,
		closeAction : 'hide',
		//title : '<span style="color:red">导入消耗数据Excel</span>',
		modal : false,
		collapsible : true,
		titleCollapse : true,
		maximizable : false,
		buttonAlign : 'right',
		border : false,
		animCollapse : true,
		animateTarget : Ext.getBody(),
		constrain : true,
		items : [input_file_form],
		buttons : [{
					text : '导入（消耗数据）',
					id:'input_excel_window_impbtn',
					iconCls : 'acceptIcon',
					handler : function() {
						var theFile = Ext.getCmp('file2').getValue();
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
						input_file_form.form.submit({
									url : 'productline.do?reqCode=importInputExcel',
									waitTitle : '提示',
									method : 'POST',
									waitMsg : '正在处理数据,请稍候...',
									success : function(form, action) {

										var msg = action.result.msg;
										Ext.MessageBox.alert('提示', msg);
										var pl_prediction_code = input_file_form.get('importInputPlPredictionCode').getValue();
										input_store.reload({
											params : {
												plPredictionCode :pl_prediction_code
											}
										});
										input_excel_window.hide();
						
									},
									failure : function(form, action) {
										var msg = action.result.msg;
										Ext.MessageBox.alert('提示', '数据导入失败:<br>' + msg);
									}
								});

					}
				}, {
					text : '关闭',
					id : 'input_btnReset',
					iconCls : 'deleteIcon',
					handler : function() {
						input_excel_window.hide();
					}
				}]
	});

	var input_tbar = new Ext.Toolbar({
				items : [
				         input_mat_type,
				         '&nbsp;&nbsp;&nbsp;&nbsp;',
				         '-',
				         {
							text : '导入消耗',
							id:'importInputData',
							iconCls : 'page_excelIcon',
							handler : function() {
								//Ext.Msg.alert('提示:', '弹出导入EXCEL数据界面');
								// 弹出导入数据窗体
								var record = grid.getSelectionModel().getSelected();
								if (Ext.isEmpty(record)) {
									Ext.Msg.alert('提示:', '请先选中具体产线！');
									return;
								}
								//Ext.Msg.alert('提示:',record.data.pl_prediction_code+","+record.data.prediction_month+","+record.data.pl_code);
								input_file_form.get('importInputMonth').setValue(record.data.prediction_month);
								input_file_form.get('importInputPlCode').setValue(record.data.pl_code);
								input_file_form.get('importInputPlDesc').setValue(record.data.pl_desc);
								input_file_form.get('importInputPlPredictionCode').setValue(record.data.pl_prediction_code);
								input_excel_window.show();
							}
							
						}]
			});
	
	// 表格实例
	var input_grid = new Ext.grid.GridPanel({
				// 表格面板标题,
				title : '<span class="commoncss">其他消耗数据 </span>',
				//width : '100%',
				height : 320,
				id : 'id_input_grid',
				autoScroll : true,
				frame : true,
				region : 'center', // 和VIEWPORT布局模型对应，充当center区域布局
				margins : '3 3 3 3',
				store : input_store, // 数据存储
				stripeRows : true, // 斑马线
				cm : input_cm, // 列模型
				tbar:input_tbar,
				//bbar : input_bbar,// 分页工具栏
				viewConfig : {
				// 不产横向生滚动条, 各列自动扩展自动压缩, 适用于列数比较少的情况
				// forceFit : true
				},
				loadMask : {
					msg : '正在加载表格数据,请稍等...'
				}
				,
				buttons : [{
							text : '关闭',
							iconCls : 'deleteIcon',
							handler : function() {
								importProductLineDataWindow.hide();
							}
						}],
				buttonAlign: 'left'
			});

	var importProductLineDataWindow = new Ext.Window({
		//title : '<span style="color:red">注意：导入时，请务必认真确认您选择要导入的文件是否正确后再点击导入按钮！</span>', // 窗口标题
		//layout : 'fit', // 设置窗口布局模式
		//width : '100%', // 窗口宽度
		height : '100%', // 窗口高度
		autoScroll: true,
		closable : true, // 是否可关闭
		closeAction: 'hide', //close 关闭  hide  隐藏  ,
		collapsible : true, // 是否可收缩
		maximizable : false, // 设置是否可以最大化
		border : false, // 边框线设置
		constrain : true, // 设置窗口是否可以溢出父容器
		animateTarget : Ext.getBody(),
		items : [product_grid,input_grid]
	});

	

	
	/**
	 * 弹出导入产线数据窗口
	 */
	function doImportProductLineData() {
		var record = grid.getSelectionModel().getSelected();
		if (Ext.isEmpty(record)) {
			Ext.Msg.alert('提示:', '请先选择产线！');
			return;
		}
//		//Ext.Msg.alert('提示:',record.data.pl_prediction_code+","+record.data.prediction_month+","+record.data.pl_code);
		product_store.load({
			params : {
				plPredictionCode :record.data.pl_prediction_code
			}
		});
		input_store.load({
			params : {
				plPredictionCode :record.data.pl_prediction_code
			}
		});
		importProductLineDataWindow.show();
		importProductLineDataWindow.maximize();

		
	}
	
	var pl_file_form = new Ext.form.FormPanel({
			id : 'id_pl_file_form',
			name : 'id_pl_file_form',
			defaultType : 'textfield',
			labelAlign : 'right',
			labelWidth : 220,
			frame : true,
			fileUpload : true,
			margin:'0 0 0 20',
			items : [{
						fieldLabel : '请选择<span style="color:red">【产品及主投料数据】</span>导入文件',
						name : 'file3',
						id : 'file3',
						inputType : 'file',
						allowBlank : true,
						anchor : '99%'
					},{
						fieldLabel : '预测月份',
						id : 'pl_importInputMonth',
						name : 'prediction_month',
						readOnly: true,
						value : '',
						anchor : '52%' 
					},{
						fieldLabel : '请选择<span style="color:red">【其他消耗数据】</span>导入文件',
						name : 'file4',
						id : 'file4',
						inputType : 'file',
						allowBlank : true,
						anchor : '99%'
					}]
		});
	
	
	// 全量导入
	var pl_excel_window = new Ext.Window({
		layout : 'fit',
		width : 580,
		height : 300,
		resizable : false,
		draggable : true,
		closeAction : 'hide',
		//title : '<span style="color:red">导入消耗数据Excel</span>',
		modal : false,
		collapsible : true,
		titleCollapse : true,
		maximizable : false,
		buttonAlign : 'right',
		border : false,
		animCollapse : true,
		animateTarget : Ext.getBody(),
		constrain : true,
		items : [pl_file_form],
		buttons : [{
					text : '确认导入',
					id:'pl_excel_window_impbtn',
					iconCls : 'acceptIcon',
					handler : function() {
						var theFile3 = Ext.getCmp('file3').getValue();
						var theFile4 = Ext.getCmp('file4').getValue();
						if (Ext.isEmpty(theFile3)) {
							Ext.Msg.alert('提示', '请先选择您要导入【产品及主投料数据】的.xls文件！');
							return;
						}
						if (Ext.isEmpty(theFile4)) {
							Ext.Msg.alert('提示', '请先选择您要导入【其他消耗数据】的.xls文件！');
							return;
						}
						if (theFile3.substring(theFile3.length - 4, theFile3.length) != ".xls") {
							Ext.Msg.alert('提示', '您选择的【产品及主投料数据】文件格式不支持,只支持导入.xls文件！');
							return;
						}
						if (theFile4.substring(theFile4.length - 4, theFile4.length) != ".xls") {
							Ext.Msg.alert('提示', '您选择的【其他消耗数据】文件格式不支持,只支持导入.xls文件！');
							return;
						}

						if (runMode == '0') {
							Ext.Msg.alert('提示','系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
							return;
						}
						pl_file_form.form.submit({
									url : 'productline.do?reqCode=impProductAndInputExcel',
									waitTitle : '提示',
									method : 'POST',
									waitMsg : '正在处理数据,请稍候...',
									success : function(form, action) {
										var msg = action.result.msg;
										Ext.MessageBox.alert('提示', msg);
										// 查询数据
										queryData();
									},
									failure : function(form, action) {
										var msg = action.result.msg;
										Ext.MessageBox.alert('提示', '数据导入失败:<br>' + msg);
									}
								});

					}
				}, {
					text : '关闭',
					id : 'pl_input_btnReset',
					iconCls : 'deleteIcon',
					handler : function() {
						pl_excel_window.hide();
					}
				}]
	});
	

});