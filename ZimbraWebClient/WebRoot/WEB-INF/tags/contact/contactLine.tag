<%--
 * 
--%>
<%@ tag body-content="empty" %>
<%@ attribute name="line" rtexprvalue="true" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${!empty line}">
<c:set var="newline" value="
"/>
${fn:replace(fn:escapeXml(line),newline,"<br/>")}<br/>
</c:if>
