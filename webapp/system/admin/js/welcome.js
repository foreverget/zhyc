/**
 * 欢迎页面
 * 
 */
Ext.onReady(function() {
//			new Ext.ux.TipWindow({
//						title : '<span class=commoncss>提示</span>',
//						html : '您有[0]条未读信息. ',
//						iconCls : 'commentsIcon'
//					}).show(Ext.getBody());
		});

Ext.onReady(function() {
	Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

	var tools = [{
				id : 'maximize',
				handler : function(e, target, panel) {
				}
			}];


	var viewport = new Ext.Viewport({
		layout : 'border',
		items : [{
			xtype : 'portal',
			region : 'center',
			margins : '3 3 3 3',
			items : [{
						columnWidth : .99,
						style : 'padding:8px 0px 8px 8px',
						items : [{
									title : '',
									layout : 'fit',
									html:'<img src="./resource/image/welcome.jpg" width="80%" height="86%" />'
								}]
					}]
		}]
	});
});
