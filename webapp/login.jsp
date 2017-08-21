 <%@ page contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include/taglib.jsp"%>
<G4Studio:html title="${sysTitle}" showLoading="false" exportParams="true"
	isSubPage="false">
<G4Studio:import src="/system/admin/js/login.js" />
<head>
<style type="text/css">
body {
	background-image: url(./resource/image/login_bk.jpg);
	background-repeat: no-repeat;
	background-size: 100%;  
    background-position:center;
	
}
</style>
</head>
<G4Studio:body >
	<div id="hello-win" class="x-hidden">
	<div id="hello-tabs"><img border="0" width="450" height="70"
		src="<%=request.getAttribute("bannerPath") == null ? request.getContextPath()
							+ "/resource/image/login_banner.png" : request.getAttribute("bannerPath")%>" />
	</div>
	</div>
	<div id="aboutDiv" class="x-hidden"
		style='color: black; padding-left: 10px; padding-top: 10px; font-size: 12px'>
	
	</div>
	<div id="infoDiv" class="x-hidden"
		style='color: black; padding-left: 10px; padding-top: 10px; font-size: 12px; line-height:25px'>
	登录帐户[用户名/密码]<br>
	[zs/111111]<br>
	</div>
</G4Studio:body>
</G4Studio:html>