<%--
 * 
--%><%@ tag body-content="empty" dynamic-attributes="dynattrs"
%><%@ attribute name="value" rtexprvalue="true" required="true"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="zm" uri="com.zimbra.zm"
%><%@ taglib prefix="fmt" uri="com.zimbra.i18n"
%><%@ taglib prefix="app" uri="com.zimbra.htmlclient"
%><app:imginfo var="info" value="${value}" /><c:url value="${zm:getImagePath(pageContext, info.src)}" />