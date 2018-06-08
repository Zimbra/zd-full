<%--
 * 
--%>
<%@ tag body-content="empty" %>
<%@ attribute name="label" rtexprvalue="true" required="false" %>
<%@ attribute name="address" rtexprvalue="true" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<c:if test="${!empty address}">
    <fmt:message key="${label}" var="elabel"/>
    <tr><c:if test="${!empty label}"><td nowrap="nowrap" class="contactLabel">${fn:escapeXml(elabel)}:</td></c:if><td nowrap="nowrap" class="contactOutput">${fn:escapeXml(address)}</td></tr>
</c:if>
