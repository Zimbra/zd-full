<%--
 * 
--%>
<%@ tag body-content="empty" %>
<%@ attribute name="base" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ attribute name="text" required="true" rtexprvalue="true" type="java.lang.String" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%-- NOTE: getLocale is a non-standard tag --%>
<fmt:getLocale var="locale" />
<c:choose>
    <%-- NOTE: Currently Japanese only for bug 52823 --%>
    <%-- TODO: Use for all languages? only asian languages? based on COS? pref? etc? --%>
    <c:when test="${locale.language eq 'ja' and not empty base and not empty text}">
        <ruby><rb>${zm:cook(base)}</rb><rp>(</rp><rt>${zm:cook(text)}</rt><rp>)</rp></ruby>
    </c:when>
    <c:when test="${not empty base}">
       ${zm:cook(base)}
    </c:when>
    <c:when test="${not empty text}">
         ${zm:cook(text)}
    </c:when>
</c:choose>