/**
 * 
 * 产线预测报表
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
												data : [['---查询时请选择---', ''],
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
//													var m = Ext.getCmp('predictionMonth').getValue();
//													var pm = Ext.util.Format.date(m, 'Y-m');
//													alert("你选择了日期~!"+pm);
												}
											},
											anchor : '100%' // 宽度百分比
										}],
							}]
				}],
				buttons : [{
							text : '查询',
							iconCls : 'previewIcon',
							handler : function() {
								queryData();
							}
						}, {
							text : '生成报表',
							id : 'id_btn_print',
							iconCls : 'database_refreshIcon',
							handler : function() {
								createPlReport();
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
	var cm = new Ext.grid.ColumnModel([{
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
			// 列宽
		}, {
		header : '产线编码',
		dataIndex : 'pl_code',
		width : 80,
		hidden : true, 
		sortable : true
	}, {
		header : '产线',
		dataIndex : 'pl_desc',
		width : 120
	}, {
		header : '产量',
		dataIndex : 'pl_amount',
		width : 100
	},  {
		header : '商品量',
		dataIndex : 'goods_amount',
		width : 100
	},  {
		header : '成本',
		dataIndex : 'unit_cost',
		width : 100
	},  {
		header : '售价',
		dataIndex : 'sell_price',
		width : 100 
	},  {
		header : '价差',
		dataIndex : 'diff_price',
		width : 100 
	},  {
		header : '效益(元)',
		dataIndex : 'benefit_1',
		width : 100 
	},  {
		header : '效益(万元)',
		dataIndex : 'benefit_2',
		width : 100 
	},  {
		header : '效益(不含2#镀锌)',
		dataIndex : 'benefit_3',
		width : 130 
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
							url : 'plreport.do?reqCode=queryPlReportData'
						}),
				reader : new Ext.data.JsonReader({
							totalProperty : 'TOTALCOUNT', // 记录总数
							root : 'ROOT' // Json中的列表数据根节点
						}, [ 	{
									name : 'id'
								},{
									name : 'prediction_month'
								}, {
									name : 'pl_code'
								}, {
									name : 'pl_desc'
								}, {
									name : 'pl_amount'
								},  {
									name : 'goods_amount'
								},  {
									name : 'unit_cost'
								},  {
									name : 'sell_price'
								},  {
									name : 'diff_price'
								},  {
									name : 'benefit_1'
								},  {
									name : 'benefit_2'
								},  {
									name : 'benefit_3'
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
							text : '导出报表',
							id:'exportPlReportData',
							//tooltip : '以仿Ajax方式导出,界面无刷新',
							iconCls : 'page_excelIcon',
							handler : function() {
								var m = Ext.getCmp('predictionMonth').getValue();
								if(m==''){
									Ext.Msg.alert('提示:', '请先选择预测月份！');
									return;
								}
								var pm = Ext.util.Format.date(m, 'Y-m');
								exportExcel('plreport.do?reqCode=exportExcel&pm='+pm);
								
						}
					}]
			});

	// 表格实例
	var grid = new Ext.grid.GridPanel({
				// 表格面板标题,默认为粗体，我不喜欢粗体，这里设置样式将其格式为正常字体
				title : '<span class="commoncss">预测报表</span>',
				height : 500,
				id : 'id_grid_pl_report',
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
	 * 生成报表
	 */
	function createPlReport() {
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
				'确认要生成预测月份为<span style="color:red"><b>'+pm+'</b></span>月份的预测报表数据吗？<br>',
				function(btn, text) {
					if (btn == 'yes') {
						if (runMode == '0') {
							Ext.Msg.alert('提示','系统正处于演示模式下运行,您的操作被取消!该模式下只能进行查询操作!');
							return;
						}
						showWaitMsg();
						Ext.Ajax.request({
							url : './plreport.do?reqCode=createPlReport',
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

});