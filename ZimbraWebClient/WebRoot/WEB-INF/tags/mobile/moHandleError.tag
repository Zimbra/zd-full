<%--
 * 
--%>
<%@ tag body-content="scriptless" %>
<%@ taglib prefix="mo" uri="com.zimbra.mobileclient" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>

<c:catch var="actionException">
    <jsp:doBody/>
</c:catch>
<c:if test="${!empty actionException}">
    <zm:getException var="error" exception="${actionException}"/>
    <c:choose>
        <c:when test="${error.code eq 'ztaglib.SERVER_REDIRECT'}">
            <c:redirect url="${not empty requestScope.SERVIER_REDIRECT_URL ? requestScope.SERVIER_REDIRECT_URL : '/'}"/>
        </c:when>
        <c:when test="${error.code eq 'service.AUTH_EXPIRED' or error.code eq 'service.AUTH_REQUIRED'}">
            <c:choose>
                <c:when test="${not empty (paramValues.ajax[0]||param.ajax)}">
                    <script type="text/javascript">
                        var logouturl = "<c:url value="/?loginOp=relogin&client=mobile&loginErrorCode=${error.code}"/>";
                        window.location.href = logouturl;
                    </script>
                </c:when>
                <c:otherwise>
                    <c:redirect url="/?loginOp=relogin&client=mobile&loginErrorCode=${error.code}"/>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <mo:status style="Critical">
                <fmt:message key="${error.code}"/>
            </mo:status>
            <!-- ${fn:escapeXml(error.id)} -->
        </c:otherwise>
    </c:choose>
</c:if>
