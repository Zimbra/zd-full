<%--
 * 
--%>
<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<%@ taglib prefix="zdf" uri="com.zimbra.cs.offline.jsp" %>
<%@ attribute name="localeId" required="true" %>

<c:if test="${not zdf:checkAuthToken(pageContext.request)}">
    <c:redirect url="/desktop/reject.jsp">
        <c:if test="${not empty localeId}">
            <c:param name="localeId" value="${localeId}"></c:param>
        </c:if>
    </c:redirect>
</c:if>
