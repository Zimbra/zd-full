<%--
 * 
--%>
<%@ tag body-content="empty" %>

<%@ attribute name="var" required="true" type="java.lang.String" rtexprvalue="false" %>
<%@ variable name-from-attribute="var" alias="reqheader" variable-class="java.lang.Object" scope="AT_END" %>

<%@ attribute name="display" rtexprvalue="true" required="false" %>
<%@ attribute name="msg" rtexprvalue="true" required="false" type="com.zimbra.cs.taglib.bean.ZMessageBean" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="app" uri="com.zimbra.htmlclient" %>

<%-- Dont change var attribute --%>
<c:set var="reqheader" value="${''}"/>
<c:set var="reqvalue" value="${''}"/>
