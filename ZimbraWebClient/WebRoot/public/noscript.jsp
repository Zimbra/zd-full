<%@ page buffer="8kb" session="false" autoFlush="true" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*,javax.naming.*,com.zimbra.cs.zclient.ZAuthResult" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlclient" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<app:skinAndRedirect />
<%
	// Set to expire far in the past.
	response.setHeader("Expires", "Tue, 24 Jan 2000 17:46:50 GMT");

	// Set standard HTTP/1.1 no-cache headers.
	response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");

	// Set standard HTTP/1.0 no-cache header.
	response.setHeader("Pragma", "no-cache");
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<fmt:setBundle basename="/messages/ZmMsg" scope="request"/>
<html>
<head>
<!--
 noscript.jsp
 * 
-->
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
<title><fmt:message key="zimbraTitle"/></title>
<link rel="stylesheet" type="text/css" href="<c:url value='/css/images,common,dwt,msgview,login,zm,spellcheck,wiki,skin' />">
<fmt:message key="favIconUrl" var="favIconUrl"/>
</head>
<body>

<!-- BEGIN ERROR SCREEN -->
<div class="LoginScreen">
<div id='skin_container_splash_screen' class='SplashScreen'>
	<script language='javascript'>
		function showCompanyUrl() {
			window.open(ZmMsg.splashScreenCompanyURL, '_blank');
		}
	</script>
	

<div class="center">
        <div class="ImgAltBanner"></div>    
		<h1><div id='ZLoginBannerImage' class='ImgLoginBanner' onclick='showCompanyUrl()'></div></h1>
		<h2><script>document.write(ZmMsg.splashScreenAppName)</script></h2>

		<div id="#ZSplashBodyContainer" class="content">
			<div class="offline" id='ZLoginLoadingPanel'>
			 <div class="spacer">	
				<fmt:message key="errorJavaScriptRequired">
					<fmt:param>
						<c:url value='/'></c:url>
					</fmt:param>
					<fmt:param>
						<c:url value='/h/'></c:url>
					</fmt:param>
				</fmt:message>
			 </div>
			</div>
			<div class="switch"></div>
			<div class="copyright" id='ZLoginLicenseContainer'><fmt:message key="splashScreenCopyright" /></div>
		<div> 
		<div class="decor1"></div>
	</div>
	
  </div>
</div>

</body>
</html>
