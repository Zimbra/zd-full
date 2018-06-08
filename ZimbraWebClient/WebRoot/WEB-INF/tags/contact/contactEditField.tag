<%--
 * 
--%>
<%@ tag body-content="empty" %>
<%@ attribute name="label" rtexprvalue="true" required="false" %>
<%@ attribute name="contact" rtexprvalue="true" required="true" type="com.zimbra.cs.taglib.bean.ZContactBean"%>
<%@ attribute name="field" rtexprvalue="true" required="true" %>
<%@ attribute name="hint" rtexprvalue="true" required="false" %>
<%@ attribute name="address" rtexprvalue="true" required="false" %>
<%@ attribute name="tabindex" rtexprvalue="true" required="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="com.zimbra.i18n" %>
<c:if test="${empty label}">
    <c:set var="label" value="AB_FIELD_${field}" />
</c:if>
<fmt:message var="label" key="${label}" />
<c:set var="value" value="${not empty contact ? contact.attrs[field] : ''}"/>
<td valign="${address ? 'top' : 'middle'}" class="editContactLabel">
    <%-- TODO: The colon should be part of the message text!!! --%>
    <label for="${field}">${fn:escapeXml(label)}:</label>
</td>
<td><c:choose>
        <c:when test="${address}">
            <textarea name='${field}' id='${field}' cols=32 rows='2' tabindex="${tabindex}">${fn:escapeXml(value)}</textarea>
        </c:when>
        <c:otherwise>
            <input name='${field}' id='${field}' type='text' autocomplete='off' size='35' value="${fn:escapeXml(value)}" tabindex="${tabindex}">
            <c:if test="${not empty hint}">
                <span class="ZOptionsHint">(<fmt:message key="${hint}" />)</span>
            </c:if>
        </c:otherwise>
    </c:choose>
</td>
