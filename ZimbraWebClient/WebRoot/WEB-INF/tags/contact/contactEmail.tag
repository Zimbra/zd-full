<%--
 * 
--%>
<%@ tag body-content="empty" %>

<%@ attribute name="email" rtexprvalue="true" required="true" %>
<%@ attribute name="label" rtexprvalue="true" required="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<c:if test="${!empty email}">
        <fmt:message key="${label}" var="elabel"/>
		<c:set var="escapedEmail">${fn:escapeXml(email)}</c:set>
        <tr><c:if test="${!empty label}"><td class="contactLabel">${fn:escapeXml(elabel)}:</td></c:if><td class="contactOutput"><a href="/h/search?action=compose&to=${escapedEmail}">${escapedEmail}</a></td></tr>
</c:if>
