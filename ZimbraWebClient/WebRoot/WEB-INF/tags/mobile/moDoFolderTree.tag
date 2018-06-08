<%--
 * 
--%>
<%@ tag body-content="empty" %>
<%@ attribute name="parentid" rtexprvalue="true" required="false" type="java.lang.String" %>
<%@ attribute name="skiproot" rtexprvalue="true" required="true" %>
<%@ attribute name="skipsystem" rtexprvalue="true" required="true" %>
<%@ attribute name="skiptopsearch" rtexprvalue="true" required="false" %>
<%@ taglib prefix="zm" uri="com.zimbra.zm" %>
<%@ taglib prefix="mo" uri="com.zimbra.mobileclient" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<c:set var="context_url" value="${requestScope.baseURL!=null?requestScope.baseURL:'zmain'}"/><c:set var="count" value="${0}"/>
<zm:forEachFolder var="folder" skiproot="${skiproot}" parentid="${parentid}" skipsystem="${skipsystem}"  skiptopsearch="${skiptopsearch}">
    <c:if test="${count lt sessionScope.F_LIMIT and !folder.isSystemFolder and (folder.isNullView or folder.isUnknownView or folder.isMessageView or folder.isConversationView)}">
        <c:if test="${!folder.isSearchFolder}">
            <mo:overviewFolder base="${context_url}" folder="${folder}" types="${folder.types}"/>
        </c:if>
        <c:if test="${folder.isSearchFolder and folder.depth gt 0}">
            <mo:overviewSearchFolder folder="${folder}"/>
        </c:if>
        <c:set var="count" value="${count+1}"/>
    </c:if>
</zm:forEachFolder>
